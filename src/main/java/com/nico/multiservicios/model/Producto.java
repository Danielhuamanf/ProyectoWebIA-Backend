package com.nico.multiservicios.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_producto", nullable = false) //nombre del producto
    private String nombreProducto;


    @Column(name = "categoria")
    private String categoria;//categoria (Accesorios / Repuestos)

    @Column(name = "precio_compra")
    private Double precioCompra; //

    @Column(name = "precio_venta", nullable = false)
    private Double precioVenta;

    @Column(name = "stock_disponible", nullable = false) //cantidad
    private Integer stock;

    @Column(name = "stock_minimo")
    private Integer stockMinimo;

    @Column(name = "stock_maximo")
    private Integer stockMaximo;

    @Column(name = "marca")
    private String marca;

    @Column(name = "estado", nullable = false)//activo o inactivo
    private String estado;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "codigo", unique = true)
    private String codigo;

    @Column(name = "fecha_adquisicion")
    private LocalDate fechaAdquisicion;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "producto_imagenes",
            joinColumns = @JoinColumn(name = "producto_id")
    )
    @Column(name = "imagen_url", length = 1000)
    private List<String> imagenes = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "proveedor_id")
    private Proveedor proveedor;



    // Getters y Setters
    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public String getNombreProducto() {return nombreProducto;}
    public void setNombreProducto(String nombreProducto) {this.nombreProducto = nombreProducto;}

    public String getCategoria() {return categoria;}
    public void setCategoria(String categoria) {this.categoria = categoria;}

    public Double getPrecioCompra() {return precioCompra;}
    public void setPrecioCompra(Double precioCompra) {this.precioCompra = precioCompra;}

    public Double getPrecioVenta() {return precioVenta;}
    public void setPrecioVenta(Double precioVenta) {this.precioVenta = precioVenta;}

    public Integer getStock() {return stock;}
    public void setStock(Integer stock) {this.stock = stock;}

    public Integer getStockMinimo() {return stockMinimo;}
    public void setStockMinimo(Integer stockMinimo) {this.stockMinimo = stockMinimo;}

    public Integer getStockMaximo() {return stockMaximo;}
    public void setStockMaximo(Integer stockMaximo) {this.stockMaximo = stockMaximo;}

    public String getMarca() {return marca;}
    public void setMarca(String marca) {this.marca = marca;}

    public String getEstado() {return estado;}
    public void setEstado(String estado) {this.estado = estado; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getCodigo() { return codigo;}
    public void setCodigo(String codigo) { this.codigo = codigo;}

    public LocalDate getFechaAdquisicion() { return fechaAdquisicion;}
    public void setFechaAdquisicion(LocalDate fechaAdquisicion) {this.fechaAdquisicion = fechaAdquisicion;}

    public List<String> getImagenes() {return imagenes;}

    public void setImagenes(List<String> imagenes) {this.imagenes = imagenes;}

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }
    public String toString() {
        return "Producto{" +
                "id=" + id +
                ", nombre='" + nombreProducto + '\'' +
                ", Categoria='" + categoria + '\'' +
                ", precioCompra='" + precioCompra + '\'' +
                ", precioVenta='" + precioVenta + '\'' +
                ", Stock='" + stock + '\'' +
                ", StockMinimo='" + stockMinimo + '\'' +
                ", StockMaximo=" + stockMaximo + '\'' +
                ", marca=" + marca + '\'' +
                ", Estado=" + estado + '\'' +
                ", Descripcion=" + descripcion + '\'' +
                ", codigo=" + codigo + '\'' +
                ", Fecha de Aquisicion=" + fechaAdquisicion +
                '}';
    }
}
