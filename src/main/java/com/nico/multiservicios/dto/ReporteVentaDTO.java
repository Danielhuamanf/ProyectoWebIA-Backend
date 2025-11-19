// com.nico.multiservicios.dto.ReporteVentaDTO
package com.nico.multiservicios.dto;

import com.nico.multiservicios.model.MetodoPago;
import java.math.BigDecimal;

public class ReporteVentaDTO {
    private String id;
    private String cliente;
    private BigDecimal total;
    private MetodoPago metodo;
    private String fecha;

    public ReporteVentaDTO(String id, String cliente, BigDecimal total, MetodoPago metodo, String fecha) {
        this.id = id;
        this.cliente = cliente;
        this.total = total;
        this.metodo = metodo;
        this.fecha = fecha;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public MetodoPago getMetodo() { return metodo; }
    public void setMetodo(MetodoPago metodo) { this.metodo = metodo; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
}