package nexora_backend.infrastructure.config;

import nexora_backend.application.usecase.ConciliarVentaUseCase;
import nexora_backend.domain.repository.ConciliacionRepository;
import nexora_backend.domain.repository.MedioPagoRepository;
import nexora_backend.domain.service.MotorConciliacion;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Aqui Spring crea los objetos del dominio y del caso de uso.
// Asi el dominio sigue siendo Java puro, sin anotaciones de Spring.
@Configuration
public class BeanConfig {

    @Bean
    public MotorConciliacion motorConciliacion() {
        return new MotorConciliacion();
    }

    @Bean
    public ConciliarVentaUseCase conciliarVentaUseCase(MotorConciliacion motor,
                                                       MedioPagoRepository mediosPago,
                                                       ConciliacionRepository conciliaciones) {
        return new ConciliarVentaUseCase(motor, mediosPago, conciliaciones);
    }
}
