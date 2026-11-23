package nexora_backend.domain.model;

import java.math.BigDecimal;

public record ResultadoConciliacion(
        BigDecimal montoVenta,
        BigDecimal montoAbonado,
        BigDecimal comision,
        BigDecimal retencion,
        BigDecimal diferenciaNoExplicada,
        EstadoConciliacion estado
) {}