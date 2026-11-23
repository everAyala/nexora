package nexora_backend.infrastructure.adapter.out.persistence;

import nexora_backend.domain.model.MedioPago;
import nexora_backend.domain.repository.MedioPagoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

// Adaptador de salida: implementa el puerto del dominio usando JPA.
// Convierte entre la entidad (tabla) y el modelo del dominio.
@Component
public class MedioPagoRepositoryAdapter implements MedioPagoRepository {

    private final MedioPagoJpaRepository jpa;

    public MedioPagoRepositoryAdapter(MedioPagoJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<MedioPago> buscarPorCodigo(String codigo) {
        return jpa.findById(codigo).map(this::aDominio);
    }

    @Override
    public List<MedioPago> listar() {
        return jpa.findAll().stream().map(this::aDominio).toList();
    }

    private MedioPago aDominio(MedioPagoEntity e) {
        return new MedioPago(e.getCodigo(), e.getNombre(), e.getTasaComision(), e.getTasaRetencion());
    }
}
