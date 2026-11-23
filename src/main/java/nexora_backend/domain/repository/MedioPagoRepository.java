package nexora_backend.domain.repository;

import nexora_backend.domain.model.MedioPago;

import java.util.List;
import java.util.Optional;

// Puerto de salida: el dominio dice QUE necesita, no COMO se guarda.
// La implementacion con PostgreSQL esta en infrastructure/adapter/out/persistence.
public interface MedioPagoRepository {

    Optional<MedioPago> buscarPorCodigo(String codigo);

    List<MedioPago> listar();
}
