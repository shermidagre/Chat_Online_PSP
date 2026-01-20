package org.example.chat;

import org.example.logging.SecurityLogger;
import org.example.model.Usuario; // Importar el modelo Usuario
import org.example.repository.UsuarioRepository; // Importar el repositorio de Usuario
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder; // Importar PasswordEncoder
import org.springframework.stereotype.Component;

import java.security.MessageDigest; // Todavía necesario para generar SHA-256 para el log (si es necesario)
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class GestorUsuarios {

    private static final Logger registrador = LoggerFactory.getLogger(GestorUsuarios.class);

    private final UsuarioRepository repositorioUsuarios; // Repositorio de usuarios
    private final PasswordEncoder codificadorContrasenas; // Codificador de contraseñas

    // Almacena los ManejadorCliente de usuarios conectados, mapeados por su nombre de usuario
    private final ConcurrentMap<String, ManejadorCliente> usuariosOnline = new ConcurrentHashMap<>();
    // Almacena los intentos fallidos de login por dirección IP
    private final ConcurrentMap<String, Integer> intentosFallidosLogin = new ConcurrentHashMap<>();
    private static final int MAX_INTENTOS_LOGIN = 3;

    private final SecurityLogger registradorSeguridad;

    public GestorUsuarios(UsuarioRepository repositorioUsuarios, PasswordEncoder codificadorContrasenas, SecurityLogger registradorSeguridad) {
        this.repositorioUsuarios = repositorioUsuarios;
        this.codificadorContrasenas = codificadorContrasenas;
        this.registradorSeguridad = registradorSeguridad;
    }

    public boolean autenticar(String nombreUsuario, String contrasena, String ipCliente, ManejadorCliente manejador) {
        // Incrementa los intentos fallidos para esta IP
        intentosFallidosLogin.merge(ipCliente, 1, Integer::sum);

        if (intentosFallidosLogin.getOrDefault(ipCliente, 0) > MAX_INTENTOS_LOGIN) {
            registrador.warn("Intentos de login excedidos para IP: {}", ipCliente);
            registradorSeguridad.registrarIntentoLoginFallido(nombreUsuario, ipCliente, "Intentos excedidos");
            return false;
        }

        Optional<Usuario> usuarioOpt = repositorioUsuarios.findByNombre(nombreUsuario);

        if (usuarioOpt.isEmpty()) {
            registrador.warn("Intento de login fallido: Usuario '{}' no encontrado desde IP: {}", nombreUsuario, ipCliente);
            registradorSeguridad.registrarIntentoLoginFallido(nombreUsuario, ipCliente, "Usuario no encontrado");
            return false;
        }

        Usuario usuario = usuarioOpt.get();

        // CASO A: PRIMERA VEZ -> GUARDAR CONTRASEÑA ENCRIPTADA (si no tiene)
        if (usuario.obtenerContrasena() == null || usuario.obtenerContrasena().isEmpty()) {
            String contrasenaHasheada = codificadorContrasenas.encode(contrasena);
            usuario.establecerContrasena(contrasenaHasheada);
            repositorioUsuarios.save(usuario);
            registrador.info("Contraseña establecida para usuario '{}' desde IP: {}", nombreUsuario, ipCliente);
            registradorSeguridad.registrarLoginExitoso(nombreUsuario, ipCliente, "Contraseña establecida");
            usuariosOnline.put(nombreUsuario, manejador);
            manejador.establecerUsuarioLogueado(nombreUsuario);
            manejador.establecerRolUsuario(usuario.obtenerTipoUsuario());
            intentosFallidosLogin.remove(ipCliente);
            return true;
        } else {
            // CASO B: LOGIN NORMAL -> VERIFICAR CON MATCHES
            if (codificadorContrasenas.matches(contrasena, usuario.obtenerContrasena())) {
                registrador.info("Login exitoso para usuario '{}' desde IP: {}", nombreUsuario, ipCliente);
                registradorSeguridad.registrarLoginExitoso(nombreUsuario, ipCliente, "Login exitoso");
                usuariosOnline.put(nombreUsuario, manejador);
                manejador.establecerUsuarioLogueado(nombreUsuario);
                manejador.establecerRolUsuario(usuario.obtenerTipoUsuario());
                intentosFallidosLogin.remove(ipCliente);
                return true;
            } else {
                registrador.warn("Login fallido: Contraseña incorrecta para usuario '{}' desde IP: {}", nombreUsuario, ipCliente);
                registradorSeguridad.registrarIntentoLoginFallido(nombreUsuario, ipCliente, "Contraseña incorrecta");
                return false;
            }
        }
    }

    public void usuarioLogueado(String nombreUsuario, ManejadorCliente manejador) {
        usuariosOnline.put(nombreUsuario, manejador);
        registradorSeguridad.registrarCambioEstadoUsuario(nombreUsuario, manejador.obtenerSocketCliente().getInetAddress().getHostAddress(), "LOGIN");
    }

    public void usuarioDesconectado(String nombreUsuario, String ipCliente) {
        usuariosOnline.remove(nombreUsuario);
        registradorSeguridad.registrarCambioEstadoUsuario(nombreUsuario, ipCliente, "LOGOUT");
    }

    public boolean esUsuarioOnline(String nombreUsuario) {
        return usuariosOnline.containsKey(nombreUsuario);
    }

    public ManejadorCliente obtenerManejadorCliente(String nombreUsuario) {
        return usuariosOnline.get(nombreUsuario);
    }

    public ConcurrentMap<String, ManejadorCliente> obtenerUsuariosOnline() {
        return usuariosOnline;
    }

    public boolean esAdmin(String nombreUsuario) {
        Optional<Usuario> usuarioOpt = repositorioUsuarios.findByNombre(nombreUsuario);
        return usuarioOpt.map(usuario -> "ADMIN".equalsIgnoreCase(usuario.obtenerTipoUsuario())).orElse(false);
    }

    public void resetearIntentosFallidos(String ipCliente) {
        intentosFallidosLogin.remove(ipCliente);
        registrador.info("Intentos fallidos reseteados para IP: {}", ipCliente);
    }

    // Mantener para el log de errores SHA-256 si es necesario, aunque no para autenticación directa.
    // O eliminar si no hay otros usos. Por ahora lo dejamos por si acaso.
    private String generarSha256(String texto) {
        try {
            MessageDigest resumen = MessageDigest.getInstance("SHA-256");
            byte[] hash = resumen.digest(texto.getBytes());
            StringBuilder cadenaHex = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) cadenaHex.append('0');
                cadenaHex.append(hex);
            }
            return cadenaHex.toString();
        } catch (NoSuchAlgorithmException e) {
            registrador.error("Error al generar SHA-256: " + e.getMessage(), e);
            return null;
        }
    }
}
