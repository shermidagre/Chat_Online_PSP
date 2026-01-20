package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.example.dto.Login.LoginRequestDTO;
import org.example.dto.Usuarios.UsuariosRequestDTO;
import org.example.dto.Usuarios.UsuariosResponseDTO;
import org.example.model.Usuarios;
import org.example.repository.UsuariosRepository;
import org.example.service.UsuariosService;
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
public class UsuariosController {

    public static final String MAPPING = "/api";

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsuariosRepository usuariosRepository;
    @Autowired
    private UsuariosService usuariosService;

    @Operation(summary = "Crear un nuevo usuario")
    @PostMapping("/usuarios")
    public ResponseEntity<UsuariosResponseDTO> crearUsuario(@RequestBody UsuariosRequestDTO usuarioDto) {
        Usuarios nuevoUsuario = usuariosService.crearUsuario(usuarioDto);
        UsuariosResponseDTO responseDto = new UsuariosResponseDTO(nuevoUsuario);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED); // 201 Created
    }

    // 2. PUT: ACTUALIZAR Usuario
    @Operation(summary = "Actualizar un usuario existente por ID")
    @PutMapping("/usuarios/{id}")
    public ResponseEntity<UsuariosResponseDTO> actualizarUsuario(@PathVariable Long id,
            @RequestBody UsuariosRequestDTO usuarioDto) {

        return usuariosService.actualizarUsuario(id, usuarioDto)
                .map(UsuariosResponseDTO::new) // Mapear la entidad a DTO de respuesta
                .map(ResponseEntity::ok) // Devolver 200 OK
                .orElseGet(() -> ResponseEntity.notFound().build()); // 404
    }

    @Operation(summary = "Obtener todos los usuarios")
    @GetMapping("/usuarios")
    public List<UsuariosResponseDTO> obtenerUsuarios() { // <--- Devuelve lista de DTOs
        return usuariosService.obtenerUsuarios().stream()
                .map(UsuariosResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Obtener usuario por id")
    @GetMapping("/usuarios/{id}")
    public ResponseEntity<UsuariosResponseDTO> buscarUsuarioPorId(@PathVariable Long id) { // <--- Devuelve DTO
        return usuariosService.buscarUsuarioPorId(id)
                .map(UsuariosResponseDTO::new)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar usuario por id")
    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<UsuariosResponseDTO> borrarUsuarioPorId(@PathVariable Long id) { // <--- Devuelve DTO del
                                                                                           // borrado
        Usuarios usuarioBorrado = usuariosService.borrarUsuarioPorId(id);

        if (usuarioBorrado != null) {
            return ResponseEntity.ok(new UsuariosResponseDTO(usuarioBorrado));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Bcekend, verificar si un usuario existe y si tiene contraseña")
    @GetMapping("/usuarios/check/{nombre}")
    public ResponseEntity<Map<String, Object>> verificarEstadoUsuario(@PathVariable String nombre) {
        Optional<Usuarios> usuarioOpt = usuariosRepository.findByNombre(nombre);

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(404).body(null); // Usuario no existe
        }

        Usuarios usuario = usuarioOpt.get();
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("idUsuario", usuario.getIdUsuario());
        respuesta.put("nombre", usuario.getNombre());
        respuesta.put("rol", usuario.getTipoUsuario());

        // Si es null o vacío, es "usuario virgen" (sin contraseña)
        boolean tienePassword = (usuario.getPassword() != null && !usuario.getPassword().isEmpty());
        respuesta.put("tienePassword", tienePassword);

        return ResponseEntity.ok(respuesta);
    }

    @Operation(summary = "Backend, login de usuario con manejo de primera vez y contraseñas hasheadas")
    @PostMapping("/usuarios/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginDto) {

        Usuarios usuario = usuariosRepository.findById(loginDto.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // CASO A: PRIMERA VEZ -> GUARDAR CONTRASEÑA ENCRIPTADA
        if (usuario.getPassword() == null || usuario.getPassword().isEmpty()) {

            String passwordHasheada = passwordEncoder.encode(loginDto.getPassword());
            usuario.setPassword(passwordHasheada);

            usuariosRepository.save(usuario);
            return ResponseEntity.ok(Map.of("status", "ok", "mensaje", "Contraseña creada correctamente"));
        }

        // CASO B: LOGIN NORMAL -> VERIFICAR CON MATCHES
        // passwordEncoder.matches( "lo que escribio el usuario", "el hash de la BD" )
        if (passwordEncoder.matches(loginDto.getPassword(), usuario.getPassword())) {
            return ResponseEntity.ok(Map.of("status", "ok", "mensaje", "Login correcto"));
        } else {
            return ResponseEntity.status(401).body(Map.of("status", "error", "mensaje", "Contraseña incorrecta"));
        }
    }
    @Operation(summary = "Resetear la contraseña de un usuario (ponerla a NULL)")
    @PutMapping("/usuarios/{id}/reset-password")
    public ResponseEntity<?> resetearPassword(@PathVariable Long id) {
        Usuarios usuario = usuariosRepository.findById(id).orElse(null);
        if(usuario != null) {
            usuario.setPassword(null); // ¡Volvemos a ponerlo a NULL!
            usuariosRepository.save(usuario);
            return ResponseEntity.ok("Contraseña reseteada. El usuario la creará al entrar.");
        }
        return ResponseEntity.notFound().build();
    }
    @Operation(summary = "Registrar latido de un usuario para marcarlo como online")
    @PostMapping("/usuarios/latido/{nombre}")
    public ResponseEntity<Void> recibirLatido(@PathVariable String nombre) {
        usuariosService.registrarLatido(nombre);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Ver la lista de usuarios online (han enviado latido en los últimos 5 minutos)")
    @GetMapping("/usuarios/online")
    public ResponseEntity<List<String>> verUsuariosOnline() {
        return ResponseEntity.ok(usuariosService.obtenerUsuariosOnline());
    }
}
