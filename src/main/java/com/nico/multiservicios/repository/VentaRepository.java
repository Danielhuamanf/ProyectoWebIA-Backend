package com.nico.multiservicios.repository;

import com.nico.multiservicios.model.Venta;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {

    @Override
    @EntityGraph(attributePaths = {
            "detalles",
            "cliente",
            "detalles.producto",
            "detalles.producto.imagenes"  // ✅ Necesario para evitar el error
    })
    List<Venta> findAll();

    @Query("SELECT COALESCE(SUM(v.total), 0) FROM Venta v WHERE v.estado <> 'ANULADA'")
    BigDecimal sumTotalIngresos();

    @Query("SELECT COUNT(v) FROM Venta v WHERE DATE(v.fechaVenta) = CURRENT_DATE")
    long countVentasHoy();

}