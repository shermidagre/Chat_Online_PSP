package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.example.dto.Login.PeticionLoginDTO; // Usar PeticionLoginDTO
import org.example.dto.Usuarios.PeticionUsuariosDTO; // Usar PeticionUsuariosDTO
import org.example.dto.Usuarios.RespuestaUsuariosDTO; // Usar RespuestaUsuariosDTO
import org.example.model.Usuario; // Usar Usuario
import org.example.repository.RepositorioUsuarios; // Usar RepositorioUsuarios
import org.example.service.ServicioUsuarios;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;

@RestController
@CrossOrigin(origins = "*")
@Tag(name = "Usuarios", description = "Endpoints para interactuar con los usuarios")
public class ControladorUsuarios {

    public static final String MAPPING = "/api";

    @Autowired
    private PasswordEncoder codificadorContrasenas;

    @Autowired
    private RepositorioUsuarios repositorioUsuarios;
    @Autowired
    private ServicioUsuarios servicioUsuarios;

    @Operation(summary = "Crear un nuevo usuario")
    @PostMapping("/usuarios")
    public ResponseEntity<RespuestaUsuariosDTO> crearUsuario(@RequestBody PeticionUsuariosDTO usuarioDto) { // Usar RespuestaUsuariosDTO, PeticionUsuariosDTO
        Usuario nuevoUsuario = servicioUsuarios.crearUsuario(usuarioDto); // Usar Usuario
        RespuestaUsuariosDTO responseDto = new RespuestaUsuariosDTO(nuevoUsuario); // Usar RespuestaUsuariosDTO
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    // 2. PUT: ACTUALIZAR Usuario
    @Operation(summary = "Actualizar un usuario existente por ID")
    @PutMapping("/usuarios/{id}")
    public ResponseEntity<RespuestaUsuariosDTO> actualizarUsuario(@PathVariable Long id,
            @RequestBody PeticionUsuariosDTO usuarioDto) { // Usar RespuestaUsuariosDTO, PeticionUsuariosDTO

        return servicioUsuarios.actualizarUsuario(id, usuarioDto)
                .map(RespuestaUsuariosDTO::new)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Obtener todos los usuarios")
    @GetMapping("/usuarios")
    public List<RespuestaUsuariosDTO> obtenerUsuarios() { // Usar RespuestaUsuariosDTO
        return servicioUsuarios.obtenerUsuarios().stream()
                .map(RespuestaUsuariosDTO::new)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Obtener usuario por id")
    @GetMapping("/usuarios/{id}")
    public ResponseEntity<RespuestaUsuariosDTO> buscarUsuarioPorId(@PathVariable Long id) { // Usar RespuestaUsuariosDTO
        return servicioUsuarios.buscarUsuarioPorId(id)
                .map(RespuestaUsuariosDTO::new)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar usuario por id")
    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<RespuestaUsuariosDTO> borrarUsuarioPorId(@PathVariable Long id) { // Usar RespuestaUsuariosDTO
        Usuario usuarioBorrado = servicioUsuarios.borrarUsuarioPorId(id); // Usar Usuario

        if (usuarioBorrado != null) {
            return ResponseEntity.ok(new RespuestaUsuariosDTO(usuarioBorrado));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Backend, verificar si un usuario existe y si tiene contraseña")
    @GetMapping("/usuarios/check/{nombre}")
    public ResponseEntity<Map<String, Object>> verificarEstadoUsuario(@PathVariable String nombre) {
        Optional<Usuario> usuarioOpt = repositorioUsuarios.findByNombre(nombre); // Usar Usuario

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Usuario usuario = usuarioOpt.get(); // Usar Usuario
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("idUsuario", usuario.getIdUsuario());
        respuesta.put("nombre", usuario.getNombre());
        respuesta.put("rol", usuario.getTipoUsuario());

        // Si es null o vacío, es "usuario virgen" (sin contraseña)
        boolean tieneContrasena = (usuario.getContrasena() != null && !usuario.getContrasena().isEmpty());
        respuesta.put("tieneContrasena", tieneContrasena);

        return ResponseEntity.ok(respuesta);
    }

    @Operation(summary = "Backend, login de usuario con manejo de primera vez y contraseñas hasheadas")
    @PostMapping("/usuarios/login")
    public ResponseEntity<?> login(@RequestBody PeticionLoginDTO loginDto) { // Usar PeticionLoginDTO

        Usuario usuario = repositorioUsuarios.findById(loginDto.getIdUsuario()) // Usar Usuario
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // CASO A: PRIMERA VEZ -> GUARDAR CONTRASEÑA ENCRIPTADA
        if (usuario.getContrasena() == null || usuario.getContrasena().isEmpty()) { // Usar getContrasena()

            String contrasenaHasheada = codificadorContrasenas.encode(loginDto.getContrasena()); // Usar getContrasena()
            usuario.setContrasena(contrasenaHasheada); // Usar setContrasena()

            repositorioUsuarios.save(usuario);
            return ResponseEntity.ok(Map.of("estado", "ok", "mensaje", "Contraseña creada correctamente"));
        }

        // CASO B: LOGIN NORMAL -> VERIFICAR CON MATCHES
        if (codificadorContrasenas.matches(loginDto.getContrasena(), usuario.getContrasena())) { // Usar getContrasena(), getContrasena()
            return ResponseEntity.ok(Map.of("estado", "ok", "mensaje", "Login correcto"));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("estado", "error", "mensaje", "Contraseña incorrecta"));
        }
    }
    @Operation(summary = "Resetear la contraseña de un usuario (ponerla a NULL)")
    @PutMapping("/usuarios/{id}/reset-password")
    public ResponseEntity<?> resetearContrasena(@PathVariable Long id) {
        Usuario usuario = repositorioUsuarios.findById(id).orElse(null); // Usar Usuario
        if(usuario != null) {
            usuario.setContrasena(null); // Usar setContrasena()
            repositorioUsuarios.save(usuario);
            return ResponseEntity.ok("Contraseña reseteada. El usuario la creará al entrar.");
        }
        return ResponseEntity.notFound().build();
    }
    @Operation(summary = "Registrar latido de un usuario para marcarlo como online")
    @PostMapping("/usuarios/latido/{nombre}")
    public ResponseEntity<Void> recibirLatido(@PathVariable String nombre) {
        servicioUsuarios.registrarLatido(nombre);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Ver la lista de usuarios online (han enviado latido en los últimos 5 minutos)")
    @GetMapping("/usuarios/online")
    public ResponseEntity<List<String>> verUsuariosOnline() {
        return ResponseEntity.ok(servicioUsuarios.obtenerUsuariosOnline());
    }
}
