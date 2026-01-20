# Anexo: Protocolo de Comunicación del Chat

Este anexo describe el protocolo de comunicación utilizado entre el cliente web (JavaScript) y el servidor de chat (Java) a través de WebSockets. La comunicación se basa en mensajes estructurados en formato JSON.

## Estructura General del Mensaje

Todos los mensajes intercambiados siguen una estructura JSON básica, definida por el DTO `MensajeProtocolo` en el backend:

```json
{
  "tipo": "AUTENTICACION" | "MENSAJE" | "COMANDO" | "NOTIFICACION",
  "remitente": "nombreDeUsuario" | "Sistema",
  "contenido": "texto del mensaje o comando",
  "fechaHora": "2026-01-20T15:30:00Z"
}
```

*   **`tipo` (String):** Indica la categoría del mensaje. Valores posibles:
    *   `"AUTENTICACION"`: Usado para el proceso de inicio de sesión en el chat.
    *   `"MENSAJE"`: Un mensaje de chat estándar enviado por un usuario.
    *   `"COMANDO"`: Una instrucción especial enviada por el usuario (ej., `/list`).
    *   `"NOTIFICACION"`: Mensajes del sistema al cliente (ej., "usuario se ha unido").
*   **`remitente` (String):** El nombre de usuario que envía el mensaje. Para mensajes del sistema, suele ser `"Sistema"`.
*   **`contenido` (String):** El cuerpo principal del mensaje, ya sea el texto de chat, el comando con sus argumentos, o detalles de una notificación.
*   **`fechaHora` (String - ISO 8601):** Marca de tiempo del mensaje en formato ISO 8601.

## Mensajes de Control del Servidor (Texto Plano)

Durante el proceso de autenticación o para ciertos estados críticos, el servidor puede enviar mensajes de control en formato de texto plano (no JSON) antes de que la comunicación JSON esté completamente establecida o en casos específicos. El cliente JavaScript está preparado para manejar estos mensajes.

Ejemplos de mensajes de control en texto plano:

*   `AUTENTICACION_REQUERIDA`: El servidor solicita al cliente que envíe sus credenciales.
*   `AUTENTICACION_EXITOSA`: Las credenciales fueron aceptadas.
*   `AUTENTICACION_FALLIDA`: Las credenciales no son válidas.
*   `AUTENTICACION_BLOQUEADA`: El cliente ha excedido el número de intentos de login.
*   `ERROR_FORMATO: ...`: El formato del mensaje enviado por el cliente es incorrecto.
*   `ADIOS: Desconectando...`: Notificación de que el cliente será desconectado.

## Flujo de Autenticación Detallado (WebSocket)

1.  **Conexión Inicial:** El cliente establece una conexión WebSocket (`wss://`) con el servidor.
2.  **Solicitud de Credenciales:** El servidor envía el mensaje de control `AUTENTICACION_REQUERIDA` y una indicación de formato (`Introduce tu nombre de usuario y contraseña (ej: usuario:contraseña):`).
3.  **Envío de Credenciales por el Cliente:** El cliente, utilizando el nombre de usuario y la contraseña obtenidos del formulario de login web, envía un `MensajeProtocolo` con `tipo: "AUTENTICACION"` y el `contenido` en formato `usuario:contraseña` (ej., `{"tipo":"AUTENTICACION", "remitente":"alice", "contenido":"alice:mipassword"}`).
4.  **Verificación del Servidor:** El servidor verifica estas credenciales contra la base de datos.
    *   Si son válidas, envía `AUTENTICACION_EXITOSA`.
    *   Si son inválidas, envía `AUTENTICACION_FALLIDA`.
    *   Si se excede el número de intentos, envía `AUTENTICACION_BLOQUEADA` y cierra la conexión.
5.  **Entrada al Chat:** Una vez `AUTENTICACION_EXITOSA`, la comunicación JSON fluye normalmente.

## Tipos de Mensajes JSON (ejemplos)

### Mensaje de Chat Estándar (tipo: "MENSAJE")

Enviado por un usuario para comunicarse con los demás.

```json
{
  "tipo": "MENSAJE",
  "remitente": "alice",
  "contenido": "¡Hola a todos en el chat!",
  "fechaHora": "2026-01-20T15:35:10Z"
}
```

### Comando de Usuario (tipo: "COMANDO")

Enviado para ejecutar una acción específica en el servidor.

```json
{
  "tipo": "COMANDO",
  "remitente": "bob",
  "contenido": "/list",
  "fechaHora": "2026-01-20T15:36:05Z"
}
```

### Notificación del Sistema (tipo: "NOTIFICACION" o texto plano)

Mensajes generados por el servidor para informar a los clientes. En el contexto actual del proyecto, la mayoría de las notificaciones del sistema (ej., "usuario se ha unido") se manejan directamente como texto plano difundido por el `DifusorMensajes`, o a través de los mensajes de control de texto plano mencionados anteriormente.
```json
{
  "tipo": "NOTIFICACION",
  "remitente": "Sistema",
  "contenido": "Nuevo usuario 'carlos' se ha unido al chat.",
  "fechaHora": "2026-01-20T15:40:00Z"
}
```
(Nota: Aunque el DTO `MensajeProtocolo` permite `NOTIFICACION`, en la implementación actual del `DifusorMensajes`, los mensajes de unión/salida de usuario se envían como texto plano preformateado).
