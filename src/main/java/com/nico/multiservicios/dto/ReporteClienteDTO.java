// com.nico.multiservicios.dto.ReporteClienteDTO
package com.nico.multiservicios.dto;

import com.nico.multiservicios.model.MetodoPago;

public class ReporteClienteDTO {
    private String ruc;
    private String nombre;
    private String metodoPago; // CAMBIA de MetodoPago a String
    private String fechaUltimaCompra;
    private Long comprasTotales;

    public ReporteClienteDTO(String ruc, String nombre, String metodoPago, String fechaUltimaCompra, Long comprasTotales) {
        this.ruc = ruc;
        this.nombre = nombre;
        this.metodoPago = metodoPago; // ya no se convierte aquí
        this.fechaUltimaCompra = fechaUltimaCompra;
        this.comprasTotales = comprasTotales;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getFechaUltimaCompra() {
        return fechaUltimaCompra;
    }

    public void setFechaUltimaCompra(String fechaUltimaCompra) {
        this.fechaUltimaCompra = fechaUltimaCompra;
    }

    public Long getComprasTotales() {
        return comprasTotales;
    }

    public void setComprasTotales(Long comprasTotales) {
        this.comprasTotales = comprasTotales;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}