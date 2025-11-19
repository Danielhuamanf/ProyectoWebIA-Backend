package com.nico.multiservicios.controller;

import com.nico.multiservicios.model.Cliente;
import com.nico.multiservicios.model.DetalleVenta;
import com.nico.multiservicios.model.EstadoVenta;
import com.nico.multiservicios.model.MetodoPago;
import com.nico.multiservicios.model.Producto;
import com.nico.multiservicios.model.TipoComprobante;
import com.nico.multiservicios.model.Venta;
import com.nico.multiservicios.repository.ClienteRepository;
import com.nico.multiservicios.repository.DetalleVentaRepository;
import com.nico.multiservicios.repository.ProductoRepository;
import com.nico.multiservicios.repository.VentaRepository;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ventas")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "https://frontend-integrador-ikmdiqdep-huahuaccapas-projects.vercel.app"
})
public class VentaController {

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final ProductoRepository productoRepository;
    private final ClienteRepository clienteRepository;

    public VentaController(VentaRepository ventaRepository,
                          DetalleVentaRepository detalleVentaRepository,
                          ProductoRepository productoRepository,
                          ClienteRepository clienteRepository) {
        this.ventaRepository = ventaRepository;
        this.detalleVentaRepository = detalleVentaRepository;
        this.productoRepository = productoRepository;
        this.clienteRepository = clienteRepository;
    }

    @PostMapping
    public ResponseEntity<?> crearVenta(@RequestBody VentaRequest ventaRequest) {
        try {
            // Validar cliente si existe
            Cliente cliente = null;
            if (ventaRequest.getClienteId() != null) {
                cliente = clienteRepository.findById(ventaRequest.getClienteId())
                        .orElse(null);
            }

            // Validar productos y stock
            for (DetalleVentaRequest detalle : ventaRequest.getDetalles()) {
                Producto producto = productoRepository.findById(detalle.getProductoId())
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + detalle.getProductoId()));

                if (producto.getStock() < detalle.getCantidad()) {
                    return ResponseEntity.badRequest().body("Stock insuficiente para el producto: " + producto.getNombreProducto());
                }
            }

            // Crear venta
            Venta venta = new Venta();
            venta.setCliente(cliente);
            venta.setFechaVenta(Timestamp.valueOf(LocalDateTime.now()));
            venta.setMetodoPago(ventaRequest.getMetodoPago());
            venta.setTipoComprobante(ventaRequest.getTipoComprobante());
            venta.setMontoPagado(ventaRequest.getMontoPagado());
            venta.setEstado(ventaRequest.getMontoPagado().compareTo(ventaRequest.getTotal()) < 0 ?
                    EstadoVenta.PENDIENTE_PAGO : EstadoVenta.COMPLETADA);

            // Calcular total
            BigDecimal total = ventaRequest.getDetalles().stream()
                    .map(d -> new BigDecimal(d.getPrecioUnitario()).multiply(new BigDecimal(d.getCantidad())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            venta.setTotal(total);

            Venta ventaGuardada = ventaRepository.save(venta);

            // Crear detalles de venta y actualizar stock
            for (DetalleVentaRequest detalle : ventaRequest.getDetalles()) {
                Producto producto = productoRepository.findById(detalle.getProductoId()).get();

                DetalleVenta detalleVenta = new DetalleVenta();
                detalleVenta.setVenta(ventaGuardada);
                detalleVenta.setProducto(producto);
                detalleVenta.setCantidad(detalle.getCantidad());
                detalleVenta.setPrecioUnitario(new BigDecimal(detalle.getPrecioUnitario()));
                detalleVenta.setSubtotal(new BigDecimal(detalle.getPrecioUnitario()).multiply(new BigDecimal(detalle.getCantidad())));
                detalleVentaRepository.save(detalleVenta);

                // Actualizar stock
                producto.setStock(producto.getStock() - detalle.getCantidad());
                productoRepository.save(producto);
            }

            return ResponseEntity.ok(ventaGuardada);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al crear venta: " + e.getMessage());
        }
    }

    @Transactional
    @GetMapping
    public ResponseEntity<List<Venta>> listarVentas() {
        List<Venta> ventas = ventaRepository.findAll();
        return ResponseEntity.ok(ventas);
    }

    @PutMapping("/{id}/anular")
    public ResponseEntity<?> anularVenta(@PathVariable Long id) {
        try {
            Venta venta = ventaRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

            if (venta.getEstado() == EstadoVenta.ANULADA) {
                return ResponseEntity.badRequest().body("La venta ya está anulada");
            }

            // Revertir stock
            List<DetalleVenta> detalles = detalleVentaRepository.findByVentaId(id);
            for (DetalleVenta detalle : detalles) {
                Producto producto = detalle.getProducto();
                producto.setStock(producto.getStock() + detalle.getCantidad());
                productoRepository.save(producto);
            }

            venta.setEstado(EstadoVenta.ANULADA);
            ventaRepository.save(venta);

            return ResponseEntity.ok("Venta anulada correctamente");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al anular venta: " + e.getMessage());
        }
    }

    @GetMapping("/productos-disponibles")
    public ResponseEntity<List<Producto>> listarProductosDisponibles() {
        List<Producto> productos = productoRepository.findByStockGreaterThan(0);
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/total-ventas")
    public ResponseEntity<Long> getTotalVentas() {
        long totalVentas = ventaRepository.count();
        return ResponseEntity.ok(totalVentas);
    }

    @GetMapping("/ingresos-totales")
    public ResponseEntity<BigDecimal> getIngresosTotales() {
        try {
            BigDecimal ingresosTotales = ventaRepository.sumTotalIngresos();
            return ResponseEntity.ok(ingresosTotales);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(BigDecimal.ZERO);
        }
    }

    @GetMapping("/ventas-hoy")
    public ResponseEntity<Long> getVentasHoy() {
        try {
            long ventasHoy = ventaRepository.countVentasHoy();
            return ResponseEntity.ok(ventasHoy);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(0L);
        }
    }

}

class VentaRequest {
    private Long clienteId;
    private MetodoPago metodoPago;
    private TipoComprobante tipoComprobante;
    private BigDecimal montoPagado;
    private BigDecimal total;
    private List<DetalleVentaRequest> detalles;

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public TipoComprobante getTipoComprobante() {
        return tipoComprobante;
    }

    public void setTipoComprobante(TipoComprobante tipoComprobante) {
        this.tipoComprobante = tipoComprobante;
    }

    public BigDecimal getMontoPagado() {
        return montoPagado;
    }

    public void setMontoPagado(BigDecimal montoPagado) {
        this.montoPagado = montoPagado;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public List<DetalleVentaRequest> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleVentaRequest> detalles) {
        this.detalles = detalles;
    }
}

class DetalleVentaRequest {
    private Long productoId;
    private Integer cantidad;
    private Double precioUnitario;

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(Double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
}