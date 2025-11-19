package com.nico.multiservicios.controller;

import com.nico.multiservicios.model.Proveedor;
import com.nico.multiservicios.repository.ProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/proveedores")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "https://frontend-integrador-ikmdiqdep-huahuaccapas-projects.vercel.app"
})
public class ProveedorController {

    @Autowired
    private ProveedorRepository proveedorRepository;

    @GetMapping
    public List<Proveedor> getAllProveedores()
    {
        return proveedorRepository.findAll();
    }

    @PostMapping
    public Proveedor createProveedor(@RequestBody Proveedor proveedor)
    {
        return (proveedorRepository.save(proveedor));
    }

    @PostMapping("/{id}")
    public ResponseEntity<Proveedor> getProveedorById(@PathVariable Long id)
    {
        return proveedorRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Proveedor> actualizar(@PathVariable Long id, @RequestBody Proveedor proveedorActualizado) {
        return proveedorRepository.findById(id)
                .map(proveedorExistente -> {
                    proveedorExistente.setRuc(proveedorActualizado.getRuc());
                    proveedorExistente.setNombre(proveedorActualizado.getNombre());
                    proveedorExistente.setTipoProveedor(proveedorActualizado.getTipoProveedor());
                    proveedorExistente.setCodigoPostal(proveedorActualizado.getCodigoPostal());
                    proveedorExistente.setPaisProveedor(proveedorActualizado.getPaisProveedor());
                    proveedorExistente.setCiudad(proveedorActualizado.getCiudad());
                    proveedorExistente.setDistrito(proveedorActualizado.getDistrito());
                    proveedorExistente.setDireccion(proveedorActualizado.getDireccion());
                    proveedorExistente.setRubro(proveedorActualizado.getRubro());
                    proveedorExistente.setTipoProducto(proveedorActualizado.getTipoProducto());
                    proveedorExistente.setTelefono(proveedorActualizado.getTelefono());
                    proveedorExistente.setCorreo(proveedorActualizado.getCorreo());
                    proveedorRepository.save(proveedorExistente);
                    return ResponseEntity.ok(proveedorExistente);
                })
                .orElse(ResponseEntity.notFound().build());
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (proveedorRepository.existsById(id)) {
            proveedorRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
