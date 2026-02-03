package com.example.chat.service;

import com.example.chat.model.MensajeChat;
import com.example.chat.model.Usuario;
import com.example.chat.repository.RepositorioMensajeChat; // Importa el repositorio renombrado
import com.example.chat.repository.RepositorioUsuario; // Importa el repositorio renombrado
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio principal para la lógica de negocio del chat.
 * Gestiona operaciones relacionadas con usuarios y mensajes, interactuando con los repositorios.
 */
@Service
public class ServicioChat {

    private final RepositorioUsuario repositorioUsuario;
    private final RepositorioMensajeChat repositorioMensajeChat;

    /**
     * Constructor para la inyección de dependencias de los repositorios.
     * @param repositorioUsuario El repositorio para la entidad {@link Usuario}.
     * @param repositorioMensajeChat El repositorio para la entidad {@link MensajeChat}.
     */
    public ServicioChat(RepositorioUsuario repositorioUsuario, RepositorioMensajeChat repositorioMensajeChat) {
        this.repositorioUsuario = repositorioUsuario;
        this.repositorioMensajeChat = repositorioMensajeChat;
    }

    /**
     * Registra un nuevo usuario o actualiza su estado de 'última vez visto' si ya existe.
     * @param username El nombre de usuario a registrar o actualizar.
     * @return El {@link Usuario} registrado o actualizado.
     */
    @Transactional
    public Usuario registerUser(String username) {
        // Busca un usuario existente o crea uno nuevo
        return repositorioUsuario.findByUsername(username)
                .map(usuario -> {
                    usuario.setLastSeen(LocalDateTime.now()); // Actualiza la última vez visto si ya existe
                    return repositorioUsuario.save(usuario);
                })
                .orElseGet(() -> repositorioUsuario.save(new Usuario(username)));
    }

    /**
     * Actualiza la marca de tiempo 'lastSeen' de un usuario específico.
     * @param username El nombre de usuario cuyo estado 'lastSeen' se debe actualizar.
     */
    @Transactional
    public void updateLastSeen(String username) {
        repositorioUsuario.findByUsername(username).ifPresent(usuario -> {
            usuario.setLastSeen(LocalDateTime.now());
            repositorioUsuario.save(usuario);
        });
    }

    /**
     * Envía y persiste un nuevo mensaje de chat.
     * @param username El nombre de usuario del remitente.
     * @param content El contenido del mensaje.
     * @return El {@link MensajeChat} persistido.
     * @throws IllegalArgumentException Si el usuario remitente no se encuentra.
     */
    @Transactional
    public MensajeChat sendMessage(String username, String content) {
        Usuario sender = repositorioUsuario.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + username));
        
        MensajeChat mensaje = new MensajeChat(content, sender);
        return repositorioMensajeChat.save(mensaje);
    }

    /**
     * Recupera los mensajes de chat más recientes.
     * Actualmente recupera los últimos 20 mensajes.
     * @param limit El número máximo de mensajes a recuperar (actualmente ignorado y fijo a 20).
     * @return Una lista de {@link MensajeChat} recientes.
     */
    @Transactional
    public List<MensajeChat> getRecentMessages(int limit) {
        // Usa la consulta personalizada para obtener los últimos 20 mensajes
        return repositorioMensajeChat.findTop20ByOrderByTimestampDesc();
    }

    /**
     * Obtiene una lista de nombres de usuario de los usuarios considerados 'online'.
     * Un usuario se considera online si su 'lastSeen' fue hace menos de 5 minutos.
     * @return Una lista de {@link String} con los nombres de usuario online.
     */
    @Transactional
    public List<String> getOnlineUsers() {
        // Los usuarios activos en los últimos 5 minutos se consideran online
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);
        return repositorioUsuario.findAll().stream()
                .filter(usuario -> usuario.getLastSeen() != null && usuario.getLastSeen().isAfter(fiveMinutesAgo))
                .map(Usuario::getUsername)
                .collect(Collectors.toList());
    }

    /**
     * Elimina un usuario y todos sus mensajes asociados.
     * @param username El nombre de usuario a eliminar.
     * @throws IllegalArgumentException Si el usuario no se encuentra.
     */
    @Transactional
    public void deleteUser(String username) {
        repositorioUsuario.findByUsername(username).ifPresent(usuario -> {
            // Primero, elimina los mensajes de chat asociados para evitar restricciones de clave foránea
            repositorioMensajeChat.deleteAll(repositorioMensajeChat.findAll().stream()
                .filter(msg -> msg.getSender().getId().equals(usuario.getId()))
                .collect(Collectors.toList()));
            repositorioUsuario.delete(usuario);
        });
    }
}
