// com.nico.multiservicios.repository.custom.VentaRepositoryCustom
package com.nico.multiservicios.repository.custom;

import com.nico.multiservicios.dto.ReporteVentaDTO;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface VentaRepositoryCustom {
    List<ReporteVentaDTO> findVentasReporte(Date fechaInicio, Date fechaFin);
    BigDecimal getTotalVentas(Date fechaInicio, Date fechaFin);
    Long getCantidadVentas(Date fechaInicio, Date fechaFin);
}