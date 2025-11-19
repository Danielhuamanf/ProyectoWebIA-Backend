package com.nico.multiservicios.repository;

import com.nico.multiservicios.model.Producto;
import com.nico.multiservicios.model.TipoProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByStockGreaterThan(int stock);
    List<Producto> findByNombreProductoContaining(String nombre);
    List<Producto> findByNombreProductoContainingIgnoreCase(String nombre); // Añade este método
    List<Producto> findByEstado(String estado);

    List<Producto> findByStockLessThanEqual(Integer stock);
    @Query("SELECT p FROM Producto p LEFT JOIN FETCH p.proveedor")
    List<Producto> findAllWithProveedor();

    @Query("SELECT p FROM Producto p LEFT JOIN FETCH p.proveedor WHERE p.id = :id")
    Optional<Producto> findByIdWithProveedor(@Param("id") Long id);

    @Query("SELECT COALESCE(SUM(p.stock), 0) FROM Producto p WHERE p.estado = 'ACTIVO'")
    Integer sumTotalStock();

    @Query("SELECT COUNT(p) FROM Producto p WHERE p.stock <= p.stockMinimo AND p.estado = 'ACTIVO'")
    long countProductosConStockBajo();

    @Query("SELECT MONTH(CURRENT_DATE), SUM(p.stock) FROM Producto p WHERE p.estado = 'ACTIVO'")
    Object[] getInventarioActual();

    @Query("SELECT m.mes, COALESCE(SUM(p.stock), 0) " +
            "FROM (SELECT 1 as mes UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 " +
            "      UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 " +
            "      UNION SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12) m " +
            "LEFT JOIN Producto p ON MONTH(p.fechaAdquisicion) = m.mes AND p.estado = 'ACTIVO' " +
            "GROUP BY m.mes ORDER BY m.mes")
    List<Object[]> getInventarioPorMeses();
}

