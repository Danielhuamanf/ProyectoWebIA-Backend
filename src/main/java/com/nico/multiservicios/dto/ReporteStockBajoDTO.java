// com.nico.multiservicios.dto.ReporteStockBajoDTO
package com.nico.multiservicios.dto;

public class ReporteStockBajoDTO {
    private String id;
    private String producto;
    private Double precioVenta;
    private Integer stock;
    private Integer stockMinimo;
    private String proveedor;

    public ReporteStockBajoDTO(String id, String producto, Double precioVenta, Integer stock,
                               Integer stockMinimo, String proveedor) {
        this.id = id;
        this.producto = producto;
        this.precioVenta = precioVenta;
        this.stock = stock;
        this.stockMinimo = stockMinimo;
        this.proveedor = proveedor;
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
    public Integer getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(Integer stockMinimo) { this.stockMinimo = stockMinimo; }
    public String getProveedor() { return proveedor; }
    public void setProveedor(String proveedor) { this.proveedor = proveedor; }
}