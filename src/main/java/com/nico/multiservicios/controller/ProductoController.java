package com.nico.multiservicios.controller;

import com.nico.multiservicios.dto.ProductoCreateDTO;
import com.nico.multiservicios.model.Producto;
import com.nico.multiservicios.model.Proveedor;
import com.nico.multiservicios.repository.ProductoRepository;

import com.nico.multiservicios.repository.ProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/productos")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "https://frontend-integrador-ikmdiqdep-huahuaccapas-projects.vercel.app"
})
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private ProveedorRepository proveedorRepository;
    // Crear un nuevo producto
    @PostMapping
    public ResponseEntity<?> crearProducto(@RequestBody ProductoCreateDTO productoDTO) {
        try {
            // Buscar el proveedor por ID
            Proveedor proveedor = proveedorRepository.findById(productoDTO.getProveedorId())
                    .orElseThrow(() -> new RuntimeException("Proveedor no encontrado con ID: " + productoDTO.getProveedorId()));

            // Crear el producto
            Producto producto = new Producto();
            producto.setNombreProducto(productoDTO.getNombreProducto());
            producto.setCategoria(productoDTO.getCategoria());
            producto.setPrecioCompra(productoDTO.getPrecioCompra());
            producto.setPrecioVenta(productoDTO.getPrecioVenta());
            producto.setStock(productoDTO.getStock());
            producto.setStockMinimo(productoDTO.getStockMinimo());
            producto.setStockMaximo(productoDTO.getStockMaximo());
            producto.setMarca(productoDTO.getMarca());
            producto.setEstado(productoDTO.getEstado());
            producto.setDescripcion(productoDTO.getDescripcion());
            producto.setCodigo(productoDTO.getCodigo());
            producto.setFechaAdquisicion(productoDTO.getFechaAdquisicion());
            producto.setImagenes(productoDTO.getImagenes());
            producto.setProveedor(proveedor); // ESTABLECER LA RELACIÓN

            Producto productoGuardado = productoRepository.save(producto);
            return ResponseEntity.ok(productoGuardado);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al crear producto: " + e.getMessage());
        }
    }

    // Obtener todos los productos
    @GetMapping
    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    // Obtener un producto por ID
    @GetMapping("/{id}")
    public Producto obtenerProducto(@PathVariable Long id) {
        return productoRepository.findById(id).orElse(null);
    }

    // Eliminar producto
    @DeleteMapping("/{id}")
    public void eliminarProducto(@PathVariable Long id) {
        productoRepository.deleteById(id);
    }

    // Editar producto
    @PutMapping("/{id}")
    public Producto actualizarProducto(@PathVariable Long id, @RequestBody Producto productoActualizado) {
        return productoRepository.findById(id).map(producto -> {
            producto.setCodigo(productoActualizado.getCodigo());
            producto.setNombreProducto(productoActualizado.getNombreProducto());
            producto.setCategoria(productoActualizado.getCategoria());
            producto.setStock(productoActualizado.getStock());
            producto.setStockMinimo(productoActualizado.getStockMinimo());
            producto.setStockMaximo(productoActualizado.getStockMaximo());
            producto.setPrecioCompra(productoActualizado.getPrecioCompra());
            producto.setPrecioVenta(productoActualizado.getPrecioVenta());



            //producto.setMarca(productoActualizado.getMarca());
            //producto.setEstado(productoActualizado.getEstado());
            //producto.setDescripcion(productoActualizado.getDescripcion());

            producto.setImagenes(productoActualizado.getImagenes());

            return productoRepository.save(producto);
        }).orElse(null);

    }
    @GetMapping("/proveedores")
    public List<Proveedor> listarProveedores() {
        return proveedorRepository.findAll();
    }
    @GetMapping("/stock-bajo")
    public List<Producto> obtenerProductosConStockBajo() {
        List<Producto> todosLosProductos = productoRepository.findAll();
        return todosLosProductos.stream()
                .filter(producto -> producto.getStock() <= producto.getStockMinimo())
                .collect(Collectors.toList());
    }

    @GetMapping("/total-stock")
    public ResponseEntity<Integer> getTotalStock() {
        try {
            Integer totalStock = productoRepository.sumTotalStock();
            return ResponseEntity.ok(totalStock);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(0);
        }
    }

    @GetMapping("/count/stock-bajo")
    public ResponseEntity<Long> contarProductosConStockBajo() {
        try {
            long cantidad = productoRepository.countProductosConStockBajo();
            return ResponseEntity.ok(cantidad);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(0L);
        }
    }

    @GetMapping("/inventario-mensual")
    public ResponseEntity<List<Map<String, Object>>> getInventarioMensual() {
        try {
            List<Object[]> resultados = productoRepository.getInventarioPorMeses();
            List<Map<String, Object>> inventario = new ArrayList<>();

            String[] nombresMeses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                    "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};

            for (Object[] resultado : resultados) {
                int mesNumero = ((Number) resultado[0]).intValue();
                String mesNombre = nombresMeses[mesNumero - 1];

                Map<String, Object> mes = new HashMap<>();
                mes.put("mes", mesNombre);
                mes.put("cantidad", ((Number) resultado[1]).intValue());
                inventario.add(mes);
            }

            return ResponseEntity.ok(inventario);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}
