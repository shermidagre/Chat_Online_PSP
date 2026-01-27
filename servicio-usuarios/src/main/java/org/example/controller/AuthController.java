import org.example.model.Usuario;
import org.example.repository.RepositorioUsuarios;
import org.example.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final RepositorioUsuarios repositorioUsuarios;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(RepositorioUsuarios repositorioUsuarios, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.repositorioUsuarios = repositorioUsuarios;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
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
            String token = jwtUtil.generateToken(usuario.getNombre());
            return ResponseEntity.ok(Map.of("token", token));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("mensaje", "Contraseña incorrecta."));
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestParam("token") String token) {
        if (jwtUtil.validateToken(token)) {
            String username = jwtUtil.extractUsername(token);
            return ResponseEntity.ok(Map.of("valid", true, "username", username));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("valid", false, "message", "Token inválido o expirado."));
        }
    }
}

