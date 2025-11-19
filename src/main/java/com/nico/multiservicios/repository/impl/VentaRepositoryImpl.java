// com.nico.multiservicios.repository.impl.VentaRepositoryImpl
package com.nico.multiservicios.repository.impl;

import com.nico.multiservicios.dto.ReporteVentaDTO;
import com.nico.multiservicios.repository.custom.VentaRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class VentaRepositoryImpl implements VentaRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ReporteVentaDTO> findVentasReporte(Date fechaInicio, Date fechaFin) {
        String queryStr = "SELECT new com.nico.multiservicios.dto.ReporteVentaDTO(" +
                "CAST(v.id AS string), COALESCE(c.nombre, 'Sin cliente'), v.total, v.metodoPago, " +
                "DATE_FORMAT(v.fechaVenta, '%d/%m/%Y')) " +
                "FROM Venta v LEFT JOIN v.cliente c " +
                "WHERE (:fechaInicio IS NULL OR v.fechaVenta >= :fechaInicio) " +
                "AND (:fechaFin IS NULL OR v.fechaVenta <= :fechaFin)";

        return entityManager.createQuery(queryStr, ReporteVentaDTO.class)
                .setParameter("fechaInicio", fechaInicio)
                .setParameter("fechaFin", fechaFin)
                .getResultList();
    }

    @Override
    public BigDecimal getTotalVentas(Date fechaInicio, Date fechaFin) {
        String queryStr = "SELECT COALESCE(SUM(v.total), 0) FROM Venta v " +
                "WHERE (:fechaInicio IS NULL OR v.fechaVenta >= :fechaInicio) " +
                "AND (:fechaFin IS NULL OR v.fechaVenta <= :fechaFin)";

        return entityManager.createQuery(queryStr, BigDecimal.class)
                .setParameter("fechaInicio", fechaInicio)
                .setParameter("fechaFin", fechaFin)
                .getSingleResult();
    }

    @Override
    public Long getCantidadVentas(Date fechaInicio, Date fechaFin) {
        String queryStr = "SELECT COUNT(v) FROM Venta v " +
                "WHERE (:fechaInicio IS NULL OR v.fechaVenta >= :fechaInicio) " +
                "AND (:fechaFin IS NULL OR v.fechaVenta <= :fechaFin)";

        return entityManager.createQuery(queryStr, Long.class)
                .setParameter("fechaInicio", fechaInicio)
                .setParameter("fechaFin", fechaFin)
                .getSingleResult();
    }
}