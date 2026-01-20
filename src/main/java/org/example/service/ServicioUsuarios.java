package org.example.service;

import jakarta.transaction.Transactional;
import org.example.dto.Login.PeticionLoginDTO;
import org.example.dto.Usuarios.PeticionUsuariosDTO;
import org.example.model.Usuario;
import org.example.repository.RepositorioUsuarios; // Importar RepositorioUsuarios
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class ServicioUsuarios { // Renombrado a ServicioUsuarios

    private final RepositorioUsuarios repositorioUsuarios; // Renombrado a repositorioUsuarios

    @Autowired
    private PasswordEncoder codificadorContrasenas; // Renombrado a codificadorContrasenas

    @Autowired
    public ServicioUsuarios(RepositorioUsuarios repositorioUsuarios) { // Renombrado a RepositorioUsuarios
        this.repositorioUsuarios = repositorioUsuarios;
    }

    @Transactional
    public Usuario crearUsuario(PeticionUsuariosDTO dto) {

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(dto.getNombre()); // Usar getNombre()
        nuevoUsuario.setTipoUsuario(dto.getTipoUsuario()); // Usar getTipoUsuario()

        // Este parece ser un bug original: establecer nombre a null. Lo mantengo por fidelidad.
        nuevoUsuario.setNombre(null); // Usar setNombre()

        return repositorioUsuarios.save(nuevoUsuario);
    }

    // 2. PUT: ACTUALIZAR Usuario
    @Transactional
    public Optional<Usuario> actualizarUsuario(Long id, PeticionUsuariosDTO dto) {

        return repositorioUsuarios.findById(id).map(usuarioExistente -> {

            if (dto.getNombre() != null) { // Usar getNombre()
                usuarioExistente.setNombre(dto.getNombre()); // Usar setNombre(), getNombre()
            }
            if (dto.getTipoUsuario() != null) { // Usar getTipoUsuario()
                usuarioExistente.setTipoUsuario(dto.getTipoUsuario()); // Usar setTipoUsuario(), getTipoUsuario()
            }

            return repositorioUsuarios.save(usuarioExistente);
        });
    }

    public List<Usuario> obtenerUsuarios() {
        return repositorioUsuarios.findAll();
    }

    public Optional<Usuario> buscarUsuarioPorId(Long id) {
        return repositorioUsuarios.findById(id);
    }

    public Usuario borrarUsuarioPorId(Long id) {
        Usuario usuario = repositorioUsuarios.findById(id).orElse(null); // Renombrado a usuario
        if (usuario != null) {
            repositorioUsuarios.delete(usuario);
        }
        return usuario;
    }
    // Lógica 1: Verificar si el usuario existe y si ya tiene contrasena
    public Map<String, Object> verificarEstadoUsuario(String nombre) {
        Optional<Usuario> usuarioOpt = repositorioUsuarios.findByNombre(nombre);

        if (usuarioOpt.isEmpty()) {
            return null; // O lanzar excepción, pero null nos sirve para el 404 del controller
        }

        Usuario usuario = usuarioOpt.get();
        Map<String, Object> respuesta = new HashMap<>();

        respuesta.put("idUsuario", usuario.getIdUsuario()); // Usar getIdUsuario()
        respuesta.put("nombre", usuario.getNombre()); // Usar getNombre()
        respuesta.put("rol", usuario.getTipoUsuario()); // Usar getTipoUsuario()

        // Si es null o vacío, es "usuario virgen" (sin contrasena)
        boolean tieneContrasena = (usuario.getContrasena() != null && !usuario.getContrasena().isEmpty()); // Usar getContrasena()
        respuesta.put("tieneContrasena", tieneContrasena);

        return respuesta;
    }


    public String gestionarLogin(PeticionLoginDTO loginDto) throws Exception {
        Usuario usuario = repositorioUsuarios.findById(loginDto.getIdUsuario()) // Usar getIdUsuario()
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        // CASO A: Crear
        if (usuario.getContrasena() == null || usuario.getContrasena().isEmpty()) { // Usar getContrasena()
            // ENCODE
            usuario.setContrasena(codificadorContrasenas.encode(loginDto.getContrasena())); // Usar setContrasena(), getContrasena()
            repositorioUsuarios.save(usuario);
            return "CONTRASEÑA_CREADA";
        }

        // CASO B: Verificar
        // MATCHES
        if (codificadorContrasenas.matches(loginDto.getContrasena(), usuario.getContrasena())) { // Usar getContrasena()
            return "LOGIN_OK";
        } else {
            throw new Exception("CONTRASEÑA_INCORRECTA");
        }
    }

    private final Map<String, LocalDateTime> usuariosOnlineMap = new ConcurrentHashMap<>();

    // MÉTODO 1: Recibir el latido
    public void registrarLatido(String nombreUsuario) {
        usuariosOnlineMap.put(nombreUsuario, LocalDateTime.now());
    }

    // MÉTODO 2: Devolver quiénes siguen vivos (han dado señal en los últimos 2 min)
    public List<String> obtenerUsuariosOnline() {
        LocalDateTime haceDosMinutos = LocalDateTime.now().minus(2, ChronoUnit.MINUTES);

        // Filtramos el mapa: Solo devolvemos los que su fecha sea posterior a hace 2 min
        return usuariosOnlineMap.entrySet().stream()
                .filter(entry -> entry.getValue().isAfter(haceDosMinutos))
                .map(Map.Entry::getKey) // Solo queremos el nombre
                .collect(Collectors.toList());
    }
}