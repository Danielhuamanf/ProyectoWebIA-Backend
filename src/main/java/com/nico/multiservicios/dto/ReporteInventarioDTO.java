// com.nico.multiservicios.dto.ReporteInventarioDTO
package com.nico.multiservicios.dto;


public class ReporteInventarioDTO {
    private String id;
    private String producto;
    private Double precioVenta;
    private Integer stock;
    private String categoria;
    private String proveedor;
    private String fecha;

    public ReporteInventarioDTO(String id, String producto, Double precioVenta, Integer stock,
                                String categoria, String proveedor, String fecha) {
        this.id = id;
        this.producto = producto;
        this.precioVenta = precioVenta;
        this.stock = stock;
        this.categoria = categoria;
        this.proveedor = proveedor;
        this.fecha = fecha;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getProducto() { return producto; }
    public void setProducto(String producto) { this.producto = producto; }
    public Double getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(Double precioVenta) { this.precioVenta = precioVenta; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getProveedor() { return proveedor; }
    public void setProveedor(String proveedor) { this.proveedor = proveedor; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
}