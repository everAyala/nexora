package nexora_backend.application.usecase;

import nexora_backend.domain.model.ResultadoConciliacion;
import nexora_backend.domain.service.MotorConciliacion;

import java.math.BigDecimal;

// Caso de uso: recibe la peticion desde afuera y se la pasa al dominio.
// No sabe nada de HTTP ni de base de datos.
public class ConciliarVentaUseCase {

    private final MotorConciliacion motor;

    public ConciliarVentaUseCase(MotorConciliacion motor) {
        this.motor = motor;
    }

    public ResultadoConciliacion ejecutar(BigDecimal venta, BigDecimal abono,
                                          BigDecimal tasaComision, BigDecimal tasaRetencion) {
        return motor.conciliar(venta, abono, tasaComision, tasaRetencion);
    }
}
