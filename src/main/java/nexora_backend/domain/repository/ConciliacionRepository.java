package nexora_backend.domain.repository;

import nexora_backend.domain.model.ConciliacionRegistrada;
import nexora_backend.domain.model.ResultadoConciliacion;

import java.util.List;

// Puerto de salida para guardar y consultar el historial de conciliaciones.
public interface ConciliacionRepository {

    ConciliacionRegistrada guardar(String codigoMedioPago, ResultadoConciliacion resultado);

    List<ConciliacionRegistrada> listar();
}
