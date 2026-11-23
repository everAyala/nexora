package nexora_backend.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

// Spring Data genera la implementacion (findById, findAll, save...) automaticamente.
public interface MedioPagoJpaRepository extends JpaRepository<MedioPagoEntity, String> {
}
