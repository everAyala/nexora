// Nexora - logica del frontend.
// Es una sola pagina: segun el "#" de la URL se dibuja una seccion distinta.

const API = {
    medios: '/api/medios-pago',
    historial: '/api/conciliacion/historial',
    conciliar: '/api/conciliacion'
};

const datos = { medios: [], historial: [] };
const vista = document.getElementById('vista');

const NOMBRE_ESTADO = {
    CONCILIADA: 'Conciliada',
    DIFERENCIA_EXPLICADA: 'Diferencia explicada',
    INCONSISTENTE: 'Inconsistente'
};

// ---------- Utilidades ----------

const pesos = valor => new Intl.NumberFormat('es-CO', {
    style: 'currency', currency: 'COP', minimumFractionDigits: 0, maximumFractionDigits: 2
}).format(Number(valor));

const porcentaje = tasa => (Number(tasa) * 100).toLocaleString('es-CO', { maximumFractionDigits: 2 }) + ' %';

const fecha = texto => new Date(texto).toLocaleString('es-CO', {
    day: '2-digit', month: 'short', hour: '2-digit', minute: '2-digit'
});

// Evita que un texto se interprete como HTML
const esc = texto => String(texto).replace(/[&<>"']/g, c =>
    ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));

const etiqueta = estado => `<span class="etiqueta ${estado}">${NOMBRE_ESTADO[estado]}</span>`;

const nombreMedio = codigo => (datos.medios.find(m => m.codigo === codigo) || { nombre: codigo }).nombre;

async function pedir(url, opciones) {
    const resp = await fetch(url, opciones);
    if (!resp.ok) throw new Error('Error ' + resp.status);
    return resp.json();
}

async function cargarDatos() {
    [datos.medios, datos.historial] = await Promise.all([pedir(API.medios), pedir(API.historial)]);
}

// ---------- Seccion: Inicio (resumen en lenguaje claro) ----------

function vistaInicio() {
    const h = datos.historial;
    if (h.length === 0) {
        return `
            <h1>Hola</h1>
            <p class="intro">Aun no hay conciliaciones registradas.</p>
            <div class="tarjeta vacio">
                <p>Registra tu primera venta para ver aqui el resumen de tu negocio.</p>
                <a class="boton" href="#conciliar" style="display:inline-block;text-decoration:none">Conciliar una venta</a>
            </div>`;
    }

    const suma = campo => h.reduce((total, c) => total + Number(c.resultado[campo]), 0);
    const ventas = suma('montoVenta');
    const abonado = suma('montoAbonado');
    const comisiones = suma('comision');
    const retenciones = suma('retencion');
    const inconsistentes = h.filter(c => c.resultado.estado === 'INCONSISTENTE');
    const sinExplicar = inconsistentes.reduce((t, c) => t + Math.abs(Number(c.resultado.diferenciaNoExplicada)), 0);
    const cuenta = estado => h.filter(c => c.resultado.estado === estado).length;
    const cobrado = ventas > 0 ? (abonado / ventas * 100).toLocaleString('es-CO', { maximumFractionDigits: 1 }) : '0';

    const alerta = inconsistentes.length > 0
        ? `<p>Hay <strong>${pesos(sinExplicar)}</strong> sin explicacion en ${inconsistentes.length}
           ${inconsistentes.length === 1 ? 'conciliacion' : 'conciliaciones'}. Revisalas en el historial.</p>`
        : `<p>Todas las diferencias estan explicadas. No falta dinero.</p>`;

    return `
        <h1>Resumen</h1>
        <p class="intro">Asi va tu negocio, explicado en palabras sencillas.</p>

        <div class="tarjeta resumen">
            <p>Al banco llego el</p>
            <p class="grande">${cobrado} % de tus ventas</p>
            <p>De ${pesos(ventas)} vendidos llegaron ${pesos(abonado)}. Se descontaron
               ${pesos(comisiones)} en comisiones y ${pesos(retenciones)} en retenciones.</p>
            ${alerta}
        </div>

        <div class="indicadores">
            <div class="indicador"><span>Ventas</span><strong>${pesos(ventas)}</strong></div>
            <div class="indicador"><span>Abonado</span><strong>${pesos(abonado)}</strong></div>
            <div class="indicador"><span>Comisiones</span><strong>${pesos(comisiones)}</strong></div>
            <div class="indicador"><span>Retenciones</span><strong>${pesos(retenciones)}</strong></div>
        </div>

        <h2>Estado de las conciliaciones</h2>
        <div class="estados">
            <div class="estado-caja CONCILIADA"><strong>${cuenta('CONCILIADA')}</strong><span>Conciliadas</span></div>
            <div class="estado-caja DIFERENCIA_EXPLICADA"><strong>${cuenta('DIFERENCIA_EXPLICADA')}</strong><span>Explicadas</span></div>
            <div class="estado-caja INCONSISTENTE"><strong>${cuenta('INCONSISTENTE')}</strong><span>Inconsistentes</span></div>
        </div>

        <h2>Ultimos movimientos</h2>
        ${h.slice(0, 3).map(tarjetaMovimiento).join('')}
        <a class="boton boton-sec" href="#historial" style="display:block;text-align:center;text-decoration:none">Ver todo el historial</a>`;
}

// ---------- Seccion: Conciliar (con explicacion paso a paso) ----------

function vistaConciliar() {
    const opciones = datos.medios.map(m =>
        `<option value="${esc(m.codigo)}">${esc(m.nombre)} (comision ${porcentaje(m.tasaComision)})</option>`).join('');

    return `
        <h1>Conciliar una venta</h1>
        <p class="intro">Ingresa lo que vendiste y lo que llego al banco. Nexora te explica la diferencia.</p>
        <div class="dos-columnas">
            <form id="form-conciliar" class="tarjeta">
                <label class="campo"><span>Medio de pago</span>
                    <select id="medio" required>${opciones}</select>
                </label>
                <label class="campo"><span>Valor de la venta</span>
                    <input id="venta" type="number" inputmode="decimal" min="0" step="0.01" value="250000" required>
                </label>
                <label class="campo"><span>Valor que llego al banco</span>
                    <input id="abono" type="number" inputmode="decimal" min="0" step="0.01" value="241500" required>
                </label>
                <button class="boton" type="submit">Conciliar y guardar</button>
                <div id="error" class="error"></div>
            </form>
            <div id="resultado"></div>
        </div>`;
}

function explicacion(registro) {
    const r = registro.resultado;
    const medio = datos.medios.find(m => m.codigo === registro.medioPago);
    const esperado = Number(r.montoVenta) - Number(r.comision) - Number(r.retencion);
    const diferencia = Number(r.diferenciaNoExplicada);

    const textos = {
        CONCILIADA: ['Todo cuadra', 'Lo que vendiste es exactamente lo que llego al banco.'],
        DIFERENCIA_EXPLICADA: ['La diferencia esta explicada',
            `Llego menos dinero, pero se debe a la comision y la retencion de ${esc(medio ? medio.nombre : registro.medioPago)}.`],
        INCONSISTENTE: [diferencia > 0 ? 'Falta dinero' : 'Llego dinero de mas',
            `Hay ${pesos(Math.abs(diferencia))} que no se explican con comisiones ni retenciones. Conviene revisarlo con el banco.`]
    };
    const [titulo, texto] = textos[r.estado];

    return `
        <div class="tarjeta">
            <h2 style="margin-top:0">Explicacion paso a paso</h2>
            <ol class="pasos">
                <li><span>1. Venta registrada</span><span>${pesos(r.montoVenta)}</span></li>
                <li><span>2. Comision ${medio ? '(' + porcentaje(medio.tasaComision) + ')' : ''}</span><span class="menos">- ${pesos(r.comision)}</span></li>
                <li><span>3. Retencion ${medio ? '(' + porcentaje(medio.tasaRetencion) + ')' : ''}</span><span class="menos">- ${pesos(r.retencion)}</span></li>
                <li class="total"><span>4. Deberia llegar al banco</span><span>${pesos(esperado)}</span></li>
                <li class="total"><span>5. Llego al banco</span><span>${pesos(r.montoAbonado)}</span></li>
            </ol>
            <div class="veredicto ${r.estado}">
                <strong>${titulo}</strong>${texto}
            </div>
        </div>`;
}

function activarConciliar() {
    const form = document.getElementById('form-conciliar');
    form.addEventListener('submit', async e => {
        e.preventDefault();
        const boton = form.querySelector('button');
        const error = document.getElementById('error');
        error.textContent = '';
        boton.disabled = true;
        try {
            const registro = await pedir(API.conciliar, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    venta: document.getElementById('venta').value,
                    abono: document.getElementById('abono').value,
                    medioPago: document.getElementById('medio').value
                })
            });
            datos.historial.unshift(registro);
            document.getElementById('resultado').innerHTML = explicacion(registro);
            document.getElementById('resultado').scrollIntoView({ behavior: 'smooth', block: 'start' });
        } catch (err) {
            error.textContent = 'No se pudo conciliar. Revisa los datos o la conexion.';
        } finally {
            boton.disabled = false;
        }
    });
}

// ---------- Seccion: Historial ----------

let filtroActual = 'TODOS';

function tarjetaMovimiento(c) {
    const r = c.resultado;
    return `
        <div class="tarjeta">
            <div class="movimiento">
                <div>
                    <div class="medio">${esc(nombreMedio(c.medioPago))}</div>
                    <div class="fecha">#${c.id} - ${fecha(c.fecha)}</div>
                </div>
                <div class="montos">
                    <div>${pesos(r.montoVenta)}</div>
                    ${etiqueta(r.estado)}
                </div>
            </div>
            <div class="detalle">
                Llego ${pesos(r.montoAbonado)} - comision ${pesos(r.comision)} - retencion ${pesos(r.retencion)}
                ${Number(r.diferenciaNoExplicada) !== 0 ? ' - sin explicar ' + pesos(Math.abs(r.diferenciaNoExplicada)) : ''}
            </div>
        </div>`;
}

function vistaHistorial() {
    const filtros = ['TODOS', 'CONCILIADA', 'DIFERENCIA_EXPLICADA', 'INCONSISTENTE'];
    const lista = datos.historial.filter(c => filtroActual === 'TODOS' || c.resultado.estado === filtroActual);

    return `
        <h1>Historial</h1>
        <p class="intro">Todas las conciliaciones guardadas en la base de datos.</p>
        <div class="filtros">
            ${filtros.map(f => `<button class="filtro ${f === filtroActual ? 'activo' : ''}" data-filtro="${f}">
                ${f === 'TODOS' ? 'Todas' : NOMBRE_ESTADO[f]}</button>`).join('')}
        </div>
        ${lista.length ? lista.map(tarjetaMovimiento).join('') : '<div class="tarjeta vacio">No hay conciliaciones con este estado.</div>'}`;
}

function activarHistorial() {
    vista.querySelectorAll('.filtro').forEach(b => b.addEventListener('click', () => {
        filtroActual = b.dataset.filtro;
        mostrar('historial');
    }));
}

// ---------- Seccion: Medios de pago ----------

function vistaMedios() {
    const base = 100000;
    return `
        <h1>Medios de pago</h1>
        <p class="intro">Tarifas que cobra cada medio. Con ellas Nexora explica las diferencias.</p>
        <div class="lista-medios">
            ${datos.medios.map(m => {
                const recibe = base - base * m.tasaComision - base * m.tasaRetencion;
                return `
                <div class="tarjeta">
                    <div class="medio" style="font-weight:600;margin-bottom:6px">${esc(m.nombre)}</div>
                    <div class="medio-fila"><span>Comision</span><strong>${porcentaje(m.tasaComision)}</strong></div>
                    <div class="medio-fila"><span>Retencion</span><strong>${porcentaje(m.tasaRetencion)}</strong></div>
                    <div class="detalle">Por cada ${pesos(base)} vendidos llegan ${pesos(recibe)} al banco.</div>
                </div>`;
            }).join('')}
        </div>`;
}

// ---------- Navegacion ----------

const SECCIONES = {
    inicio: { dibujar: vistaInicio },
    conciliar: { dibujar: vistaConciliar, activar: activarConciliar },
    historial: { dibujar: vistaHistorial, activar: activarHistorial },
    medios: { dibujar: vistaMedios }
};

function mostrar(ruta) {
    const seccion = SECCIONES[ruta] || SECCIONES.inicio;
    vista.innerHTML = seccion.dibujar();
    if (seccion.activar) seccion.activar();
    document.querySelectorAll('.menu a').forEach(a =>
        a.classList.toggle('activo', a.dataset.ruta === (SECCIONES[ruta] ? ruta : 'inicio')));
}

async function navegar() {
    const ruta = location.hash.replace('#', '') || 'inicio';
    try {
        await cargarDatos();
        mostrar(ruta);
    } catch (e) {
        vista.innerHTML = `
            <div class="tarjeta vacio">
                <p>No se pudo conectar con el servidor de Nexora.</p>
                <button class="boton" onclick="navegar()">Reintentar</button>
            </div>`;
    }
    window.scrollTo(0, 0);
}

// ---------- Inicio de la app ----------

window.addEventListener('hashchange', navegar);

const avisoRed = document.getElementById('estado-red');
window.addEventListener('offline', () => avisoRed.hidden = false);
window.addEventListener('online', () => { avisoRed.hidden = true; navegar(); });

if ('serviceWorker' in navigator) {
    navigator.serviceWorker.register('/sw.js');
}

navegar();
