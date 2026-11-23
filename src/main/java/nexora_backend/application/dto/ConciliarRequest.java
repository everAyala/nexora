package nexora_backend.application.dto;

import java.math.BigDecimal;

// Datos que llegan en el cuerpo del POST /api/conciliacion
public record ConciliarRequest(
        BigDecimal venta,
        BigDecimal abono,
        String medioPago
) {}
