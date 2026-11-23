package nexora_backend.application.usecase;

import nexora_backend.domain.model.ConciliacionRegistrada;
import nexora_backend.domain.model.MedioPago;
import nexora_backend.domain.model.ResultadoConciliacion;
import nexora_backend.domain.repository.ConciliacionRepository;
import nexora_backend.domain.repository.MedioPagoRepository;
import nexora_backend.domain.service.MotorConciliacion;

import java.math.BigDecimal;
import java.util.List;

// Caso de uso: coordina el dominio (motor) con los puertos (repositorios).
// No sabe nada de HTTP ni de PostgreSQL: solo conoce interfaces.
public class ConciliarVentaUseCase {

    private final MotorConciliacion motor;
    private final MedioPagoRepository mediosPago;
    private final ConciliacionRepository conciliaciones;

    public ConciliarVentaUseCase(MotorConciliacion motor,
                                 MedioPagoRepository mediosPago,
                                 ConciliacionRepository conciliaciones) {
        this.motor = motor;
        this.mediosPago = mediosPago;
        this.conciliaciones = conciliaciones;
    }

    // Solo calcula, no guarda nada.
    public ResultadoConciliacion calcular(BigDecimal venta, BigDecimal abono,
                                          BigDecimal tasaComision, BigDecimal tasaRetencion) {
        return motor.conciliar(venta, abono, tasaComision, tasaRetencion);
    }

    // Busca las tarifas del medio de pago, concilia y guarda el resultado.
    public ConciliacionRegistrada conciliarYGuardar(BigDecimal venta, BigDecimal abono, String codigoMedioPago) {
        MedioPago medio = mediosPago.buscarPorCodigo(codigoMedioPago)
                .orElseThrow(() -> new IllegalArgumentException("No existe el medio de pago: " + codigoMedioPago));

        ResultadoConciliacion resultado = motor.conciliar(venta, abono, medio.tasaComision(), medio.tasaRetencion());
        return conciliaciones.guardar(medio.codigo(), resultado);
    }

    public List<ConciliacionRegistrada> historial() {
        return conciliaciones.listar();
    }

    public List<MedioPago> mediosDePago() {
        return mediosPago.listar();
    }
}
