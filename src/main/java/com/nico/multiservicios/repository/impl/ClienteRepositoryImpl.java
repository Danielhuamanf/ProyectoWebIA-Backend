// com.nico.multiservicios.repository.impl.ClienteRepositoryImpl
package com.nico.multiservicios.repository.impl;

import com.nico.multiservicios.dto.ReporteClienteDTO;
import com.nico.multiservicios.repository.custom.ClienteRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import java.util.List;

public class ClienteRepositoryImpl implements ClienteRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ReporteClienteDTO> findClientesReporte(){
        String sql = "SELECT " +
                "c.identificacion as ruc, " +
                "(SELECT v.metodo_pago FROM ventas v WHERE v.cliente_id = c.id ORDER BY v.fecha_venta DESC LIMIT 1) as metodoPago, " +
                "(SELECT DATE_FORMAT(MAX(v.fecha_venta), '%d/%m/%Y') FROM ventas v WHERE v.cliente_id = c.id) as fechaUltimaCompra, " +
                "(SELECT COUNT(*) FROM ventas v WHERE v.cliente_id = c.id) as comprasTotales, " +
                "c.nombre " +
                "FROM clientes c";
        Query query = entityManager.createNativeQuery(sql, "ReporteClienteMapping");
        return query.getResultList();
    }
    @Override
    public Long countClientesRegistrados() {
        return entityManager.createQuery("SELECT COUNT(c) FROM Cliente c", Long.class)
                .getSingleResult();
    }
}