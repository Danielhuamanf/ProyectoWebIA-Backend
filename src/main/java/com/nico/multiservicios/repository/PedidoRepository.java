package com.nico.multiservicios.repository;

import com.nico.multiservicios.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByProveedorId(Long proveedorId);
    List<Pedido> findByEstado(String estado);
    // Sumar el total de todos los pedidos
    @Query("SELECT COALESCE(SUM(p.total), 0) FROM Pedido p")
    Integer sumTotalPedidos();
}
