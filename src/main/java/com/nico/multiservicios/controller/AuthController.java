package com.nico.multiservicios.controller;

import com.nico.multiservicios.model.User;
import com.nico.multiservicios.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "https://frontend-integrador-ikmdiqdep-huahuaccapas-projects.vercel.app"
})
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        initializeDefaultUsers();
    }

    private void initializeDefaultUsers() {
        User admin = userRepository.findByUsername("admin");
        if (admin == null) {
            admin = new User();
            admin.setUsername("admin");
            admin.setPassword("admin123"); // Contraseña sin encriptar para el admin
            admin.setRole("ADMIN");
            admin.setEmail("admin@example.com");
            admin.setFirstLogin(false); // Admin no necesita cambiar contraseña
            admin.setPasswordChangeRequired(false);
            userRepository.save(admin);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        try {
            String username = loginData.get("username");
            String password = loginData.get("password");

            User user = userRepository.findByUsername(username);

            if (user == null || !password.equals(user.getPassword())) {
                return ResponseEntity.status(401).body("Credenciales incorrectas");
            }

            Map<String, Object> response = new HashMap<>();
            response.put("username", user.getUsername());
            response.put("role", user.getRole());
            response.put("email", user.getEmail());
            response.put("firstLogin", user.isFirstLogin());
            response.put("passwordChangeRequired", user.isPasswordChangeRequired());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error en el servidor");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String password = payload.get("password");
        String email = payload.get("email");

        if (username == null || username.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Nombre de usuario es requerido");
        }
        if (password == null || password.length() < 6) {
            return ResponseEntity.badRequest().body("Contraseña debe tener al menos 6 caracteres");
        }

        if (userRepository.findByUsername(username) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Usuario ya existe");
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password); // Sin encriptar por simplicidad
        newUser.setEmail(email);
        newUser.setRole("EMPLEADO");
        newUser.setFirstLogin(true); // Nuevos usuarios deben cambiar contraseña
        newUser.setPasswordChangeRequired(true);
        userRepository.save(newUser);

        return ResponseEntity.ok("Usuario creado exitosamente");
    }
    @GetMapping("/profile/{username}")
    public ResponseEntity<?> getProfile(@PathVariable String username) {
        try {
            User user = userRepository.findByUsername(username);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            Map<String, String> profile = new HashMap<>();
            profile.put("username", user.getUsername());
            profile.put("email", user.getEmail());
            profile.put("role", user.getRole());
            profile.put("createdAt", user.getCreatedAt() != null ? user.getCreatedAt().toString() : "N/A");

            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al obtener perfil");
        }
    }


    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> payload,
                                            @RequestParam String username) {
        String newPassword = payload.get("password");
        if (newPassword == null || newPassword.length() < 6) {
            return ResponseEntity.badRequest().body("Contraseña debe tener al menos 6 caracteres");
        }

        User user = userRepository.findByUsername(username);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        user.setPassword(newPassword); // Actualizamos la contraseña sin encriptar
        user.setFirstLogin(false);
        user.setPasswordChangeRequired(false);
        userRepository.save(user);

        return ResponseEntity.ok("Contraseña actualizada");
    }
}