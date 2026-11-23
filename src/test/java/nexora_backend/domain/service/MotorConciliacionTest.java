package nexora_backend.domain.service;

import nexora_backend.domain.model.EstadoConciliacion;
import nexora_backend.domain.model.ResultadoConciliacion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MotorConciliacionTest {

    private final MotorConciliacion motor = new MotorConciliacion();

    @Test
    @DisplayName("Venta de $250.000 con abono de $241.500: explica $7.500 de comisión y $1.000 de retención")
    void explicaComisionYRetencion() {
        ResultadoConciliacion r = motor.conciliar(
                new BigDecimal("250000"), new BigDecimal("241500"),
                new BigDecimal("0.03"), new BigDecimal("0.004"));

        assertEquals(new BigDecimal("7500.00"), r.comision());
        assertEquals(new BigDecimal("1000.00"), r.retencion());
        assertEquals(0, r.diferenciaNoExplicada().signum());
        assertEquals(EstadoConciliacion.DIFERENCIA_EXPLICADA, r.estado());
    }

    @Test
    @DisplayName("Pago en efectivo sin comisiones: queda CONCILIADA")
    void ventaSinComisionesQuedaConciliada() {
        ResultadoConciliacion r = motor.conciliar(
                new BigDecimal("100000"), new BigDecimal("100000"),
                BigDecimal.ZERO, BigDecimal.ZERO);

        assertEquals(EstadoConciliacion.CONCILIADA, r.estado());
    }

    @Test
    @DisplayName("Si faltan $2.000 que no explica ninguna comisión: INCONSISTENTE")
    void detectaDineroFaltante() {
        ResultadoConciliacion r = motor.conciliar(
                new BigDecimal("250000"), new BigDecimal("239500"),
                new BigDecimal("0.03"), new BigDecimal("0.004"));

        assertEquals(new BigDecimal("2000.00"), r.diferenciaNoExplicada());
        assertEquals(EstadoConciliacion.INCONSISTENTE, r.estado());
    }
}