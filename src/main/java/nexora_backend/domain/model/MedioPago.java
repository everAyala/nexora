package nexora_backend.domain.model;

import java.math.BigDecimal;

// Medio por el que la pyme recibe el pago (datafono, Nequi, efectivo...)
// con las tarifas que cobra cada uno.
public record MedioPago(
        String codigo,
        String nombre,
        BigDecimal tasaComision,
        BigDecimal tasaRetencion
) {}
