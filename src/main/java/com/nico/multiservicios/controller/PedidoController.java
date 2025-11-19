package com.nico.multiservicios.controller;

import com.nico.multiservicios.model.DetallePedido;
import com.nico.multiservicios.model.Pedido;
import com.nico.multiservicios.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/v1/pedidos")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "https://frontend-integrador-ikmdiqdep-huahuaccapas-projects.vercel.app"
})
public class PedidoController {

    @Autowired
    private PedidoRepository pedidoRepository;

    // CREATE - Crear nuevo pedido
    @PostMapping
    public Pedido crearPedido(@RequestBody Pedido pedido) {
        if (pedido.getDetallePedido() != null) {
            for (DetallePedido detalle : pedido.getDetallePedido()) {
                detalle.setPedido(pedido); // ASOCIACIÓN BIDIRECCIONAL NECESARIA
            }
        }
        return pedidoRepository.save(pedido);
    }

    // READ ALL - Obtener todos los pedidos
    @GetMapping
    public ResponseEntity<List<Pedido>> obtenerTodosPedidos() {
        try {
            List<Pedido> pedidos = pedidoRepository.findAll();
            return ResponseEntity.ok(pedidos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // READ ONE - Obtener un pedido por ID
    @GetMapping("/{id}")
    public ResponseEntity<Pedido> obtenerPedidoPorId(@PathVariable Long id) {
        Optional<Pedido> pedido = pedidoRepository.findById(id);
        return pedido.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // UPDATE - Actualizar un pedido existente
    @PutMapping("/{id}")
    public ResponseEntity<Pedido> actualizarPedido(
            @PathVariable Long id,
            @RequestBody Pedido pedidoActualizado) {
        return pedidoRepository.findById(id)
                .map(pedido -> {
                    pedido.setProveedorId(pedidoActualizado.getProveedorId());
                    pedido.setEstado(pedidoActualizado.getEstado());
                    pedido.setMetodoPago(pedidoActualizado.getMetodoPago());
                    pedido.setFechaEntrega(pedidoActualizado.getFechaEntrega());
                    pedido.setTotal(pedidoActualizado.getTotal());
                    // Actualizar detalles si es necesario
                    if(pedidoActualizado.getDetallePedido() != null) {
                        pedido.getDetallePedido().clear();
                        pedido.getDetallePedido().addAll(pedidoActualizado.getDetallePedido());
                        pedido.getDetallePedido().forEach(d -> d.setPedido(pedido));
                    }
                    Pedido pedidoActualizadoDB = pedidoRepository.save(pedido);
                    return ResponseEntity.ok(pedidoActualizadoDB);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // NUEVO ENDPOINT - Actualizar solo el estado del pedido
    @PutMapping("/{id}/estado")
    public ResponseEntity<?> actualizarEstadoPedido(
            @PathVariable Long id,
            @RequestBody Map<String, String> estadoRequest) {

        try {
            String nuevoEstado = estadoRequest.get("estado");

            // Validar que se proporcione el estado
            if (nuevoEstado == null || nuevoEstado.trim().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "El estado es requerido");
                return ResponseEntity.badRequest().body(error);
            }

            // Buscar el pedido
            Optional<Pedido> pedidoOpt = pedidoRepository.findById(id);
            if (!pedidoOpt.isPresent()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Pedido no encontrado");
                return ResponseEntity.notFound().build();
            }

            Pedido pedido = pedidoOpt.get();
            String estadoActual = pedido.getEstado();

            // Validar transiciones de estado permitidas
            if (!esTransicionValida(estadoActual, nuevoEstado)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Transición de estado no válida de '" + estadoActual + "' a '" + nuevoEstado + "'");
                error.put("estadoActual", estadoActual);
                error.put("estadoSolicitado", nuevoEstado);
                return ResponseEntity.badRequest().body(error);
            }

            // Actualizar el estado
            pedido.setEstado(nuevoEstado);
            Pedido pedidoActualizado = pedidoRepository.save(pedido);

            // Respuesta exitosa
            Map<String, Object> response = new HashMap<>();
            response.put("id", pedidoActualizado.getId());
            response.put("estadoAnterior", estadoActual);
            response.put("estadoNuevo", nuevoEstado);
            response.put("message", "Estado actualizado correctamente");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error interno del servidor: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    // Método privado para validar transiciones de estado
    private boolean esTransicionValida(String estadoActual, String nuevoEstado) {
        if (estadoActual == null || nuevoEstado == null) {
            return false;
        }

        // Normalizar los valores eliminando espacios y convirtiendo a minúsculas
        estadoActual = estadoActual.trim().toLowerCase();
        nuevoEstado = nuevoEstado.trim().toLowerCase();

        // Validar transiciones permitidas
        switch (estadoActual) {
            case "pendiente":
                return nuevoEstado.equals("en tránsito");
            case "en tránsito":
                return nuevoEstado.equals("recibido");
            case "recibido":
                return false; // No se permite cambiar desde "recibido"
            default:
                return false;
        }
    }


    // DELETE - Eliminar un pedido
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPedido(@PathVariable Long id) {
        if (pedidoRepository.existsById(id)) {
            pedidoRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // ENDPOINTS ADICIONALES
    // Obtener pedidos por proveedor
    @GetMapping("/proveedor/{proveedorId}")
    public List<Pedido> obtenerPedidosPorProveedor(@PathVariable Long proveedorId) {
        return pedidoRepository.findByProveedorId(proveedorId);
    }

    // Obtener pedidos por estado
    @GetMapping("/estado/{estado}")
    public List<Pedido> obtenerPedidosPorEstado(@PathVariable String estado) {
        return pedidoRepository.findByEstado(estado);
    }

    // Valor total de todos los pedidos
    @GetMapping("/valor-total")
    public ResponseEntity<Integer> getValorTotalPedidos() {
        return ResponseEntity.ok(pedidoRepository.sumTotalPedidos());
    }
}