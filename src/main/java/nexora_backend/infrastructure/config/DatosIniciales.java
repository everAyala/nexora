package nexora_backend.infrastructure.config;

import nexora_backend.infrastructure.adapter.out.persistence.MedioPagoEntity;
import nexora_backend.infrastructure.adapter.out.persistence.MedioPagoJpaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

// Carga los medios de pago la primera vez que arranca la app (si la tabla esta vacia).
@Configuration
public class DatosIniciales {

    @Bean
    public CommandLineRunner cargarMediosDePago(MedioPagoJpaRepository repo) {
        return args -> {
            if (repo.count() == 0) {
                repo.saveAll(List.of(
                        new MedioPagoEntity("DATAFONO", "Datafono", new BigDecimal("0.0300"), new BigDecimal("0.0040")),
                        new MedioPagoEntity("NEQUI", "Nequi", new BigDecimal("0.0150"), new BigDecimal("0.0040")),
                        new MedioPagoEntity("PASARELA", "Pasarela web", new BigDecimal("0.0350"), new BigDecimal("0.0040")),
                        new MedioPagoEntity("EFECTIVO", "Efectivo", new BigDecimal("0.0000"), new BigDecimal("0.0000"))
                ));
            }
        };
    }
}
