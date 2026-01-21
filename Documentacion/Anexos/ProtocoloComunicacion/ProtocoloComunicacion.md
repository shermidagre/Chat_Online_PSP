# Anexo: Protocolo de Comunicación del Chat

Este anexo describe el protocolo de comunicación simplificado utilizado entre un cliente de consola (ej. Netcat) y el `Servidor` de chat. La comunicación se basa exclusivamente en **mensajes de texto plano** separados por saltos de línea.

## Conexión Inicial y Nickname

1.  **Conexión:** El cliente establece una conexión TCP estándar con el servidor en el puerto `8081`.
2.  **Solicitud de Nick:** Inmediatamente después de la conexión, el servidor envía el siguiente mensaje al cliente:
    ```
    Bienvenido al chat. Por favor, introduce tu nick:
    ```
3.  **Envío de Nick:** El cliente debe responder con una única línea de texto que será su `nick` para la sesión.
4.  **Confirmación:** El servidor notifica a todos los usuarios la entrada del nuevo cliente y envía un mensaje de bienvenida al propio cliente.
    ```
    SISTEMA: <nick> se ha unido al chat.
    Conectado a la sala. Escribe /bye para salir.
    ```

## Flujo de Mensajes

### Mensajes de Usuario

*   **Formato:** Cualquier línea de texto que **no** comience con `/`.
*   **Funcionamiento:** Cuando un cliente envía un mensaje de texto, el servidor lo recibe y lo reenvía (difunde) a **todos los demás clientes** conectados, precedido por el nick del remitente.
*   **Ejemplo:**
    1.  El cliente con nick "Samuel" envía el texto: `Hola a todos`.
    2.  El servidor recibe el mensaje.
    3.  El servidor envía a todos los demás clientes la línea: `Samuel: Hola a todos`.

### Comandos de Usuario

Los comandos son instrucciones especiales que el cliente puede enviar al servidor. Siempre comienzan con el carácter `/`.

*   **/list**:
    *   **Descripción:** Solicita al servidor una lista de los nicks de todos los usuarios conectados actualmente.
    *   **Respuesta del Servidor:**
        ```
        Usuarios online: Samuel Ana Pedro 
        ```

*   **/ping**:
    *   **Descripción:** Comando de diagnóstico para comprobar la conexión con el servidor.
    *   **Respuesta del Servidor:** Devuelve la palabra "PONG" seguida de la fecha y hora actual del servidor.
        ```
        PONG 2026-01-21T10:30:00.123456
        ```

*   **/bye**:
    *   **Descripción:** Desconecta al cliente del servidor.
    *   **Funcionamiento:** El cliente envía el comando y el servidor cierra la conexión para ese cliente. Además, notifica al resto de usuarios que el cliente ha abandonado el chat.
    *   **Notificación a los demás:**
        ```
        SISTEMA: Samuel ha salido del chat.
        ```

### Mensajes del Sistema

Son mensajes informativos que el servidor envía a los clientes para notificar eventos. Suelen ir prefijados con `SISTEMA:`.

*   **Ejemplos:**
    *   `SISTEMA: <nick> se ha unido al chat.`
    *   `SISTEMA: <nick> ha salido del chat.`
    *   `Comando no reconocido: /comando_invalido`

