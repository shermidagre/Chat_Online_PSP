package org.example.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.logging.SecurityLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class GestorUsuarios { // Renombrado a GestorUsuarios

    private static final Logger registrador = LoggerFactory.getLogger(GestorUsuarios.class); // Renombrado a registrador

    // Clase interna para representar un usuario de users.json
    private record Usuario(String nombre, String password_sha256, String tipoUsuario) {} // Renombrado a Usuario

    private final Map<String, Usuario> usuariosAutorizados; // Renombrado a usuariosAutorizados

    // Almacena los ManejadorCliente de usuarios conectados, mapeados por su nombre de usuario
    private final ConcurrentMap<String, ManejadorCliente> usuariosOnline = new ConcurrentHashMap<>(); // Renombrado a usuariosOnline y ManejadorCliente
    // Almacena los intentos fallidos de login por dirección IP
    private final ConcurrentMap<String, Integer> intentosFallidosLogin = new ConcurrentHashMap<>(); // Renombrado a intentosFallidosLogin
    private static final int MAX_INTENTOS_LOGIN = 3; // Renombrado a MAX_INTENTOS_LOGIN

    private final SecurityLogger registradorSeguridad; // Renombrado a registradorSeguridad

    public GestorUsuarios(SecurityLogger registradorSeguridad) { // Renombrado a GestorUsuarios y registradorSeguridad
        this.usuariosAutorizados = cargarUsuariosDesdeJson(); // Renombrado a cargarUsuariosDesdeJson
        this.registradorSeguridad = registradorSeguridad;
    }

    private Map<String, Usuario> cargarUsuariosDesdeJson() { // Renombrado a cargarUsuariosDesdeJson
        ObjectMapper mapeador = new ObjectMapper(); // Renombrado a mapeador
        try (InputStream is = new ClassPathResource("users.json").getInputStream()) {
            List<Usuario> usuarios = Arrays.asList(mapeador.readValue(is, Usuario[].class)); // Renombrado a Usuario y usuarios
            return usuarios.stream().collect(Collectors.toMap(Usuario::nombre, Function.identity())); // Renombrado a Usuario
        } catch (IOException e) {
            registrador.error("Error al cargar users.json: " + e.getMessage(), e);
            return new ConcurrentHashMap<>();
        }
    }

    public boolean autenticar(String nombreUsuario, String contrasena, String ipCliente, ManejadorCliente manejador) { // Renombrado a autenticar, nombreUsuario, contrasena, ipCliente, manejador
        // Incrementa los intentos fallidos para esta IP
        intentosFallidosLogin.merge(ipCliente, 1, Integer::sum);

        if (intentosFallidosLogin.getOrDefault(ipCliente, 0) > MAX_INTENTOS_LOGIN) {
            registrador.warn("Intentos de login excedidos para IP: {}", ipCliente);
            registradorSeguridad.logFailedLoginAttempt(nombreUsuario, ipCliente, "Intentos excedidos");
            return false;
        }

        Usuario usuario = usuariosAutorizados.get(nombreUsuario);

        if (usuario == null) {
            registrador.warn("Intento de login fallido: Usuario '{}' no encontrado desde IP: {}", nombreUsuario, ipCliente);
            registradorSeguridad.logFailedLoginAttempt(nombreUsuario, ipCliente, "Usuario no encontrado");
            return false;
        }

        String contrasenaHasheadaIntento = generarSha256(contrasena); // Renombrado a contrasenaHasheadaIntento y generarSha256

        if (contrasenaHasheadaIntento != null && contrasenaHasheadaIntento.equals(usuario.password_sha256())) {
            registrador.info("Login exitoso para usuario '{}' desde IP: {}", nombreUsuario, ipCliente);
            registradorSeguridad.logSuccessfulLogin(nombreUsuario, ipCliente, "Login exitoso");
            usuariosOnline.put(nombreUsuario, manejador);
            manejador.establecerUsuarioLogueado(nombreUsuario); // Renombrado a establecerUsuarioLogueado
            manejador.establecerRolUsuario(usuario.tipoUsuario()); // Renombrado a establecerRolUsuario
            intentosFallidosLogin.remove(ipCliente);
            return true;
        } else {
            registrador.warn("Login fallido: Contraseña incorrecta para usuario '{}' desde IP: {}", nombreUsuario, ipCliente);
            registradorSeguridad.logFailedLoginAttempt(nombreUsuario, ipCliente, "Contraseña incorrecta");
            return false;
        }
    }

    public void usuarioLogueado(String nombreUsuario, ManejadorCliente manejador) { // Renombrado a usuarioLogueado, nombreUsuario, manejador
        usuariosOnline.put(nombreUsuario, manejador);
        registradorSeguridad.logUserStatusChange(nombreUsuario, manejador.obtenerSocketCliente().getInetAddress().getHostAddress(), "LOGIN"); // obtenerSocketCliente
    }

    public void usuarioDesconectado(String nombreUsuario, String ipCliente) { // Renombrado a usuarioDesconectado, nombreUsuario, ipCliente
        usuariosOnline.remove(nombreUsuario);
        registradorSeguridad.logUserStatusChange(nombreUsuario, ipCliente, "LOGOUT");
    }

    public boolean esUsuarioOnline(String nombreUsuario) { // Renombrado a esUsuarioOnline
        return usuariosOnline.containsKey(nombreUsuario);
    }

    public ManejadorCliente obtenerManejadorCliente(String nombreUsuario) { // Renombrado a obtenerManejadorCliente
        return usuariosOnline.get(nombreUsuario);
    }

    public ConcurrentMap<String, ManejadorCliente> obtenerUsuariosOnline() { // Renombrado a obtenerUsuariosOnline
        return usuariosOnline;
    }

    public boolean esAdmin(String nombreUsuario) { // Renombrado a esAdmin
        Usuario usuario = usuariosAutorizados.get(nombreUsuario);
        return usuario != null && "ADMIN".equalsIgnoreCase(usuario.tipoUsuario());
    }

    // Método para resetear los intentos fallidos de una IP (por ejemplo, si el admin la desbloquea)
    public void resetearIntentosFallidos(String ipCliente) { // Renombrado a resetearIntentosFallidos
        intentosFallidosLogin.remove(ipCliente);
        registrador.info("Intentos fallidos reseteados para IP: {}", ipCliente);
    }

    // Helper para generar SHA-256
    private String generarSha256(String texto) { // Renombrado a generarSha256, texto
        try {
            MessageDigest resumen = MessageDigest.getInstance("SHA-256"); // Renombrado a resumen
            byte[] hash = resumen.digest(texto.getBytes());
            StringBuilder cadenaHex = new StringBuilder(); // Renombrado a cadenaHex
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