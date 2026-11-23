package nexora_backend.domain.model;

public enum EstadoConciliacion {
    CONCILIADA,             // venta y abono coinciden exactamente
    DIFERENCIA_EXPLICADA,   // no coinciden, pero comisión + retención lo explican
    INCONSISTENTE           // hay dinero que no se puede explicar
}