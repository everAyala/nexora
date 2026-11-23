package nexora_backend.infrastructure.adapter.in.web;

import nexora_backend.application.usecase.ConciliarVentaUseCase;
import nexora_backend.domain.model.ResultadoConciliacion;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

// Adaptador de entrada: recibe la peticion HTTP y llama al caso de uso.
// Ejemplo: GET /api/conciliacion?venta=250000&abono=241500&comision=0.03&retencion=0.004
@RestController
@RequestMapping("/api/conciliacion")
public class ConciliacionController {

    private final ConciliarVentaUseCase conciliarVenta;

    public ConciliacionController(ConciliarVentaUseCase conciliarVenta) {
        this.conciliarVenta = conciliarVenta;
    }

    @GetMapping
    public ResultadoConciliacion conciliar(@RequestParam BigDecimal venta,
                                           @RequestParam BigDecimal abono,
                                           @RequestParam(defaultValue = "0") BigDecimal comision,
                                           @RequestParam(defaultValue = "0") BigDecimal retencion) {
        return conciliarVenta.ejecutar(venta, abono, comision, retencion);
    }
}
