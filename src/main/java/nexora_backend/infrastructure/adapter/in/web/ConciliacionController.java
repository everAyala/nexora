package nexora_backend.infrastructure.adapter.in.web;

import nexora_backend.application.dto.ConciliarRequest;
import nexora_backend.application.usecase.ConciliarVentaUseCase;
import nexora_backend.domain.model.ConciliacionRegistrada;
import nexora_backend.domain.model.MedioPago;
import nexora_backend.domain.model.ResultadoConciliacion;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

// Adaptador de entrada: recibe las peticiones HTTP y llama al caso de uso.
@RestController
@RequestMapping("/api")
public class ConciliacionController {

    private final ConciliarVentaUseCase conciliarVenta;

    public ConciliacionController(ConciliarVentaUseCase conciliarVenta) {
        this.conciliarVenta = conciliarVenta;
    }

    // Solo calcula. Ejemplo:
    // GET /api/conciliacion?venta=250000&abono=241500&comision=0.03&retencion=0.004
    @GetMapping("/conciliacion")
    public ResultadoConciliacion calcular(@RequestParam BigDecimal venta,
                                          @RequestParam BigDecimal abono,
                                          @RequestParam(defaultValue = "0") BigDecimal comision,
                                          @RequestParam(defaultValue = "0") BigDecimal retencion) {
        return conciliarVenta.calcular(venta, abono, comision, retencion);
    }

    // Concilia con las tarifas del medio de pago y guarda el resultado en PostgreSQL.
    @PostMapping("/conciliacion")
    public ConciliacionRegistrada conciliarYGuardar(@RequestBody ConciliarRequest req) {
        return conciliarVenta.conciliarYGuardar(req.venta(), req.abono(), req.medioPago());
    }

    @GetMapping("/conciliacion/historial")
    public List<ConciliacionRegistrada> historial() {
        return conciliarVenta.historial();
    }

    @GetMapping("/medios-pago")
    public List<MedioPago> mediosDePago() {
        return conciliarVenta.mediosDePago();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> datoInvalido(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
}
