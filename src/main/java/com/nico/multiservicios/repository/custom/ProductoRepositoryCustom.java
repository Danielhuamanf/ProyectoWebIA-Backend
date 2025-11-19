// com.nico.multiservicios.repository.custom.ProductoRepositoryCustom
package com.nico.multiservicios.repository.custom;

import com.nico.multiservicios.dto.ReporteInventarioDTO;
import com.nico.multiservicios.dto.ReporteStockBajoDTO;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface ProductoRepositoryCustom {
    List<ReporteInventarioDTO> findInventarioReporte(Date fechaInicio, Date fechaFin);
    List<ReporteStockBajoDTO> findProductosStockBajo();
    BigDecimal getTotalInventario();
    Long getCantidadProductos();
}