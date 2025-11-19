package com.nico.multiservicios.controller;

import com.nico.multiservicios.model.DetallePedido;
import com.nico.multiservicios.repository.DetallePedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/detalles-pedidos")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "https://frontend-integrador-ikmdiqdep-huahuaccapas-projects.vercel.app"
})
public class DetallePedidoController {

    @Autowired
    private DetallePedidoRepository detallePedidoRepository;

    @GetMapping
    public List<DetallePedido> getAllDetallesPedidos() {
        return detallePedidoRepository.findAll();
    }

    // 2. Obtener un detalle por ID
    @GetMapping("/{id}")
    public ResponseEntity<DetallePedido> getDetalleById(@PathVariable Long id) {
        DetallePedido detalle = detallePedidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Detalle no encontrado"));
        return ResponseEntity.ok(detalle);
    }

    @PostMapping
    public ResponseEntity<DetallePedido> createDetalle(@RequestBody DetallePedido nuevoDetalle) {
        DetallePedido guardado = detallePedidoRepository.save(nuevoDetalle);
        return new ResponseEntity<>(guardado, HttpStatus.CREATED);
    }

    // 4. Actualizar un detalle existente
    @PutMapping("/{id}")
    public ResponseEntity<DetallePedido> updateDetalle(@PathVariable Long id, @RequestBody DetallePedido detallesActualizados) {
        DetallePedido existente = detallePedidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Detalle no encontrado"));

        existente.setModelo(detallesActualizados.getModelo());
        existente.setPrecioUnitario(detallesActualizados.getPrecioUnitario());
        existente.setCantidad(detallesActualizados.getCantidad());
        existente.setDescripcion(detallesActualizados.getDescripcion());

        return ResponseEntity.ok(detallePedidoRepository.save(existente));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDetalle(@PathVariable Long id) {
        DetallePedido existente = detallePedidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Detalle no encontrado"));

        detallePedidoRepository.delete(existente);
        return ResponseEntity.noContent().build();

    }

    // Agrega este nuevo endpoint al DetallePedidoController
    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<List<DetallePedido>> getDetallesByPedidoId(@PathVariable Long pedidoId) {
        List<DetallePedido> detalles = detallePedidoRepository.findByPedidoId(pedidoId);
        if (detalles.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(detalles);
    }

}
