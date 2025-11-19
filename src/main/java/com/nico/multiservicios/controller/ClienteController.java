package com.nico.multiservicios.controller;

import com.nico.multiservicios.model.Cliente;
import com.nico.multiservicios.repository.ClienteRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.sql.Timestamp;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "https://frontend-integrador-ikmdiqdep-huahuaccapas-projects.vercel.app"
})
public class ClienteController {

    private final ClienteRepository clienteRepository;

    public ClienteController(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @GetMapping("/clientes")
    public ResponseEntity<List<Cliente>> listarClientes() {
        List<Cliente> clientes = clienteRepository.findAll();
        return ResponseEntity.ok(clientes);
    }

    @PostMapping("/clientes")
    public ResponseEntity<?> crearCliente(@RequestBody Cliente cliente) {
        if (cliente.getNombre() == null || cliente.getNombre().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("El nombre es obligatorio");
        }
        if (cliente.getEmail() == null || cliente.getEmail().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("El email es obligatorio");
        }
        if (clienteRepository.existsByEmail(cliente.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Ya existe un cliente con ese email");
        }

        cliente.setFechaRegistro(new Timestamp(System.currentTimeMillis()));
        Cliente clienteGuardado = clienteRepository.save(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteGuardado);
    }

    @PutMapping("/clientes/{id}")
    public ResponseEntity<?> actualizarCliente(@PathVariable Long id, @RequestBody Cliente clienteActualizado) {
        if (clienteActualizado.getNombre() == null || clienteActualizado.getNombre().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("El nombre es obligatorio");
        }
        if (clienteActualizado.getEmail() == null || clienteActualizado.getEmail().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("El email es obligatorio");
        }

        return clienteRepository.findById(id)
                .map(cliente -> {
                    if (!cliente.getEmail().equals(clienteActualizado.getEmail()) && 
                        clienteRepository.existsByEmail(clienteActualizado.getEmail())) {
                        return ResponseEntity.status(HttpStatus.CONFLICT)
                                .body("Ya existe un cliente con ese email");
                    }

                    cliente.setNombre(clienteActualizado.getNombre());
                    cliente.setApellidos(clienteActualizado.getApellidos());
                    cliente.setDireccion(clienteActualizado.getDireccion());
                    cliente.setEmail(clienteActualizado.getEmail());
                    cliente.setTelefono(clienteActualizado.getTelefono());
                    cliente.setIdentificacion(clienteActualizado.getIdentificacion());
                    
                    Cliente clienteActualizadoDB = clienteRepository.save(cliente);
                    return ResponseEntity.ok(clienteActualizadoDB);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/clientes/{id}")
    public ResponseEntity<?> eliminarCliente(@PathVariable Long id) {
        return clienteRepository.findById(id)
                .map(cliente -> {
                    try {
                        // Esto eliminará en cascada las ventas y sus detalles
                        clienteRepository.delete(cliente);
                        return ResponseEntity.ok().build();
                    } catch (DataIntegrityViolationException e) {
                        // Manejo alternativo si la eliminación en cascada falla
                        return ResponseEntity.status(HttpStatus.CONFLICT)
                                .body("No se pudo eliminar el cliente. Hay datos asociados.");
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/clientes/total")
    public ResponseEntity<Long> contarClientes() {
        long cantidadClientes = clienteRepository.count();
        return ResponseEntity.ok(cantidadClientes);
    }

}