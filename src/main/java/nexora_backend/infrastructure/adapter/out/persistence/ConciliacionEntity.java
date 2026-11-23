package nexora_backend.infrastructure.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import nexora_backend.domain.model.EstadoConciliacion;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Tabla conciliacion: guarda cada resultado del motor.
// Los montos son DECIMAL(14,2) para no perder centavos.
@Entity
@Table(name = "conciliacion")
public class ConciliacionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "medio_pago_codigo")
    private MedioPagoEntity medioPago;

    @Column(name = "monto_venta", nullable = false, precision = 14, scale = 2)
    private BigDecimal montoVenta;

    @Column(name = "monto_abonado", nullable = false, precision = 14, scale = 2)
    private BigDecimal montoAbonado;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal comision;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal retencion;

    @Column(name = "diferencia_no_explicada", nullable = false, precision = 14, scale = 2)
    private BigDecimal diferenciaNoExplicada;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoConciliacion estado;

    @Column(nullable = false)
    private LocalDateTime fecha;

    protected ConciliacionEntity() {
        // requerido por JPA
    }

    public ConciliacionEntity(MedioPagoEntity medioPago, BigDecimal montoVenta, BigDecimal montoAbonado,
                              BigDecimal comision, BigDecimal retencion, BigDecimal diferenciaNoExplicada,
                              EstadoConciliacion estado, LocalDateTime fecha) {
        this.medioPago = medioPago;
        this.montoVenta = montoVenta;
        this.montoAbonado = montoAbonado;
        this.comision = comision;
        this.retencion = retencion;
        this.diferenciaNoExplicada = diferenciaNoExplicada;
        this.estado = estado;
        this.fecha = fecha;
    }

    public Long getId() { return id; }
    public MedioPagoEntity getMedioPago() { return medioPago; }
    public BigDecimal getMontoVenta() { return montoVenta; }
    public BigDecimal getMontoAbonado() { return montoAbonado; }
    public BigDecimal getComision() { return comision; }
    public BigDecimal getRetencion() { return retencion; }
    public BigDecimal getDiferenciaNoExplicada() { return diferenciaNoExplicada; }
    public EstadoConciliacion getEstado() { return estado; }
    public LocalDateTime getFecha() { return fecha; }
}
