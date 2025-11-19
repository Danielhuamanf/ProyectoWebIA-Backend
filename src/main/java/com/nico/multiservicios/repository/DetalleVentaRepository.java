package com.nico.multiservicios.repository;

import com.nico.multiservicios.model.DetalleVenta;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {
    List<DetalleVenta> findByVentaId(Long ventaId);

    @Query("SELECT dv.producto.nombreProducto, SUM(dv.cantidad) as totalVendidos " +
            "FROM DetalleVenta dv " +
            "GROUP BY dv.producto.nombreProducto " +
            "ORDER BY totalVendidos DESC")
    List<Object[]> findTopProductosVendidos(Pageable pageable);
}