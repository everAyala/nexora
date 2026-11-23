package nexora_backend.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConciliacionJpaRepository extends JpaRepository<ConciliacionEntity, Long> {

    // Spring Data arma la consulta a partir del nombre del metodo.
    List<ConciliacionEntity> findAllByOrderByIdDesc();
}
