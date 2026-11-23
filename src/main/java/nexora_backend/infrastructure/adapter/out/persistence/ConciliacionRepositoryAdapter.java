package nexora_backend.infrastructure.adapter.out.persistence;

import nexora_backend.domain.model.ConciliacionRegistrada;
import nexora_backend.domain.model.ResultadoConciliacion;
import nexora_backend.domain.repository.ConciliacionRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

// Adaptador de salida: guarda los resultados del motor en la tabla conciliacion.
@Component
public class ConciliacionRepositoryAdapter implements ConciliacionRepository {

    private final ConciliacionJpaRepository jpa;
    private final MedioPagoJpaRepository mediosJpa;

    public ConciliacionRepositoryAdapter(ConciliacionJpaRepository jpa, MedioPagoJpaRepository mediosJpa) {
        this.jpa = jpa;
        this.mediosJpa = mediosJpa;
    }

    @Override
    public ConciliacionRegistrada guardar(String codigoMedioPago, ResultadoConciliacion r) {
        MedioPagoEntity medio = mediosJpa.getReferenceById(codigoMedioPago);
        ConciliacionEntity guardada = jpa.save(new ConciliacionEntity(
                medio, r.montoVenta(), r.montoAbonado(), r.comision(), r.retencion(),
                r.diferenciaNoExplicada(), r.estado(), LocalDateTime.now()));
        return aDominio(guardada, codigoMedioPago);
    }

    @Override
    public List<ConciliacionRegistrada> listar() {
        return jpa.findAllByOrderByIdDesc().stream()
                .map(e -> aDominio(e, e.getMedioPago().getCodigo()))
                .toList();
    }

    private ConciliacionRegistrada aDominio(ConciliacionEntity e, String codigoMedioPago) {
        ResultadoConciliacion r = new ResultadoConciliacion(
                e.getMontoVenta(), e.getMontoAbonado(), e.getComision(), e.getRetencion(),
                e.getDiferenciaNoExplicada(), e.getEstado());
        return new ConciliacionRegistrada(e.getId(), codigoMedioPago, e.getFecha(), r);
    }
}
