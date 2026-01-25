package org.example.controller;

import org.example.model.Usuario;
import org.example.repository.RepositorioUsuarios;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final RepositorioUsuarios repositorioUsuarios;
    private final PasswordEncoder passwordEncoder;
    // Inyectaremos el JWT util más adelante
    // private final JwtUtil jwtUtil;

    public AuthController(RepositorioUsuarios repositorioUsuarios, PasswordEncoder passwordEncoder) {
        this.repositorioUsuarios = repositorioUsuarios;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, String> credenciales) {
        String nombre = credenciales.get("nombre");
        String password = credenciales.get("password");

        if (nombre == null || password == null || nombre.trim().isEmpty() || password.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Nombre de usuario y contraseña son obligatorios."));
        }

        if (repositorioUsuarios.findByNombre(nombre).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("mensaje", "El nombre de usuario ya existe."));
        }

        String hashedPassword = passwordEncoder.encode(password);
        Usuario nuevoUsuario = new Usuario(nombre, hashedPassword);

        repositorioUsuarios.save(nuevoUsuario);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("mensaje", "Usuario registrado exitosamente."));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> credenciales) {
        String nombre = credenciales.get("nombre");
        String password = credenciales.get("password");

        Optional<Usuario> usuarioOpt = repositorioUsuarios.findByNombre(nombre);

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("mensaje", "Usuario no encontrado."));
        }

        Usuario usuario = usuarioOpt.get();

        if (passwordEncoder.matches(password, usuario.getPassword())) {
            // Generaremos un token JWT en el siguiente paso
            // String token = jwtUtil.generateToken(usuario.getNombre());
            String token = "placeholder-jwt-token-for-" + usuario.getNombre();
            return ResponseEntity.ok(Map.of("token", token));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("mensaje", "Contraseña incorrecta."));
        }
    }
}

