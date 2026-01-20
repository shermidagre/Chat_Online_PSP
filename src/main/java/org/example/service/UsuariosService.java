package org.example.service;

import jakarta.transaction.Transactional;
import org.example.dto.Login.LoginRequestDTO;
import org.example.dto.Usuarios.UsuariosRequestDTO;
import org.example.model.Usuarios;
import org.example.repository.UsuariosRepository;
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
public class UsuariosService {

    private final UsuariosRepository usuariosRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; 

    @Autowired
    public UsuariosService(UsuariosRepository usuariosRepository) {
        this.usuariosRepository = usuariosRepository;
    }

    @Transactional
    public Usuarios crearUsuario(UsuariosRequestDTO dto) {

        Usuarios nuevoUsuario = new Usuarios();
        nuevoUsuario.setNombre(dto.getNombre());
        nuevoUsuario.setTipoUsuario(dto.getTipoUsuario());

        nuevoUsuario.setIdUsuario(null);

        return usuariosRepository.save(nuevoUsuario);
    }

    // 2. PUT: ACTUALIZAR Usuario
    @Transactional
    public Optional<Usuarios> actualizarUsuario(Long id, UsuariosRequestDTO dto) {

        return usuariosRepository.findById(id).map(usuarioExistente -> {

            if (dto.getNombre() != null) {
                usuarioExistente.setNombre(dto.getNombre());
            }
            if (dto.getTipoUsuario() != null) {
                usuarioExistente.setTipoUsuario(dto.getTipoUsuario());
            }

            return usuariosRepository.save(usuarioExistente);
        });
    }

    public List<Usuarios> obtenerUsuarios() {
        return usuariosRepository.findAll();
    }

    public Optional<Usuarios> buscarUsuarioPorId(Long id) {
        return usuariosRepository.findById(id);
    }

    public Usuarios borrarUsuarioPorId(Long id) {
        Usuarios usuarios = usuariosRepository.findById(id).orElse(null);
        if (usuarios != null) {
            usuariosRepository.delete(usuarios);
        }
        return usuarios;
    }
    // Lógica 1: Verificar si el usuario existe y si ya tiene contraseña
    public Map<String, Object> verificarEstadoUsuario(String nombre) {
        Optional<Usuarios> usuarioOpt = usuariosRepository.findByNombre(nombre);

        if (usuarioOpt.isEmpty()) {
            return null; // O lanzar excepción, pero null nos sirve para el 404 del controller
        }

        Usuarios usuario = usuarioOpt.get();
        Map<String, Object> respuesta = new HashMap<>();

        respuesta.put("idUsuario", usuario.getIdUsuario());
        respuesta.put("rol", usuario.getTipoUsuario());
        // Verificamos si la columna password tiene algo
        boolean tienePassword = (usuario.getPassword() != null && !usuario.getPassword().isEmpty());
        respuesta.put("tienePassword", tienePassword);

        return respuesta;
    }


    public String gestionarLogin(LoginRequestDTO loginDto) throws Exception {
        Usuarios usuario = usuariosRepository.findById(loginDto.getIdUsuario())
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        // CASO A: Crear
        if (usuario.getPassword() == null || usuario.getPassword().isEmpty()) {
            // ENCODE
            usuario.setPassword(passwordEncoder.encode(loginDto.getPassword()));
            usuariosRepository.save(usuario);
            return "CONTRASEÑA_CREADA";
        }

        // CASO B: Verificar
        // MATCHES
        if (passwordEncoder.matches(loginDto.getPassword(), usuario.getPassword())) {
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
