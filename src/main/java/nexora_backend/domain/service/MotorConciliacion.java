package nexora_backend.domain.service;

import nexora_backend.domain.model.EstadoConciliacion;
import nexora_backend.domain.model.ResultadoConciliacion;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MotorConciliacion {

    public ResultadoConciliacion conciliar(BigDecimal montoVenta, BigDecimal montoAbonado,
                                           BigDecimal tasaComision, BigDecimal tasaRetencion) {

        BigDecimal comision  = montoVenta.multiply(tasaComision).setScale(2, RoundingMode.HALF_UP);
        BigDecimal retencion = montoVenta.multiply(tasaRetencion).setScale(2, RoundingMode.HALF_UP);

        BigDecimal abonoEsperado = montoVenta.subtract(comision).subtract(retencion);
        BigDecimal diferenciaNoExplicada = abonoEsperado.subtract(montoAbonado);

        EstadoConciliacion estado;
        if (diferenciaNoExplicada.signum() != 0) {
            estado = EstadoConciliacion.INCONSISTENTE;
        } else if (comision.add(retencion).signum() == 0) {
            estado = EstadoConciliacion.CONCILIADA;
        } else {
            estado = EstadoConciliacion.DIFERENCIA_EXPLICADA;
        }

        return new ResultadoConciliacion(montoVenta, montoAbonado, comision,
                retencion, diferenciaNoExplicada, estado);
    }
}