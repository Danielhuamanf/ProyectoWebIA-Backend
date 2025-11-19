// com.nico.multiservicios.repository.impl.ProductoRepositoryImpl
package com.nico.multiservicios.repository.impl;

import com.nico.multiservicios.dto.ReporteInventarioDTO;
import com.nico.multiservicios.dto.ReporteStockBajoDTO;
import com.nico.multiservicios.repository.custom.ProductoRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class ProductoRepositoryImpl implements ProductoRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ReporteInventarioDTO> findInventarioReporte(Date fechaInicio, Date fechaFin) {
        String sql = "SELECT " +
                "CAST(p.id AS CHAR), " +
                "p.nombre_producto, " +
                "p.precio_venta, " +
                "p.stock, " +
                "p.categoria, " +
                "(SELECT pr.nombre FROM proveedor pr WHERE pr.id = " +
                "(SELECT MIN(pe.proveedor_id) FROM pedido pe " +
                "JOIN detalle_pedido dp ON dp.pedido_id = pe.id " +
                "WHERE dp.modelo = p.nombre_producto) " +
                "), " +
                "DATE_FORMAT(p.fecha_adquisicion, '%d/%m/%Y') " +
                "FROM productos p " +
                "WHERE (:fechaInicio IS NULL OR p.fecha_adquisicion >= :fechaInicio) " +
                "AND (:fechaFin IS NULL OR p.fecha_adquisicion <= :fechaFin)";

        List<Object[]> results = entityManager.createNativeQuery(sql)
                .setParameter("fechaInicio", fechaInicio)
                .setParameter("fechaFin", fechaFin)
                .getResultList();

        return results.stream().map(row -> new ReporteInventarioDTO(
                (String) row[0],
                (String) row[1],
                ((Number) row[2]).doubleValue(),
                ((Number) row[3]).intValue(),
                (String) row[4],
                (String) row[5],
                (String) row[6]
        )).toList();
    }

    @Override
    public List<ReporteStockBajoDTO> findProductosStockBajo() {
        String queryStr = "SELECT new com.nico.multiservicios.dto.ReporteStockBajoDTO(" +
                "CAST(p.id AS string), p.nombreProducto, p.precioVenta, p.stock, p.stockMinimo, " +
                "(SELECT pr.nombre FROM Proveedor pr WHERE pr.id = " +
                "(SELECT MIN(pe.proveedorId) FROM Pedido pe JOIN pe.detallePedido dp WHERE dp.modelo = p.nombreProducto))) " +
                "FROM Producto p WHERE p.stock <= p.stockMinimo";

        return entityManager.createQuery(queryStr, ReporteStockBajoDTO.class)
                .getResultList();
    }

    @Override
    public BigDecimal getTotalInventario() {
        String queryStr = "SELECT COALESCE(SUM(p.precioCompra * p.stock), 0) FROM Producto p";
        return entityManager.createQuery(queryStr, BigDecimal.class)
                .getSingleResult();
    }

    @Override
    public Long getCantidadProductos() {
        return entityManager.createQuery("SELECT COUNT(p) FROM Producto p", Long.class)
                .getSingleResult();
    }
}