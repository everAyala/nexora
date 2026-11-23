package nexora_backend.infrastructure.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

// Tabla medio_pago en PostgreSQL.
@Entity
@Table(name = "medio_pago")
public class MedioPagoEntity {

    @Id
    @Column(length = 30)
    private String codigo;

    @Column(nullable = false, length = 80)
    private String nombre;

    @Column(name = "tasa_comision", nullable = false, precision = 6, scale = 4)
    private BigDecimal tasaComision;

    @Column(name = "tasa_retencion", nullable = false, precision = 6, scale = 4)
    private BigDecimal tasaRetencion;

    protected MedioPagoEntity() {
        // requerido por JPA
    }

    public MedioPagoEntity(String codigo, String nombre, BigDecimal tasaComision, BigDecimal tasaRetencion) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.tasaComision = tasaComision;
        this.tasaRetencion = tasaRetencion;
    }

    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public BigDecimal getTasaComision() { return tasaComision; }
    public BigDecimal getTasaRetencion() { return tasaRetencion; }
}
