// com.nico.multiservicios.repository.custom.ClienteRepositoryCustom
package com.nico.multiservicios.repository.custom;

import com.nico.multiservicios.dto.ReporteClienteDTO;
import java.util.List;

public interface ClienteRepositoryCustom {
    List<ReporteClienteDTO> findClientesReporte();
    Long countClientesRegistrados();
}