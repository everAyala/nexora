package nexora_backend.domain.model;

import java.time.LocalDateTime;

// Una conciliacion que ya quedo guardada en la base de datos.
public record ConciliacionRegistrada(
        Long id,
        String medioPago,
        LocalDateTime fecha,
        ResultadoConciliacion resultado
) {}
