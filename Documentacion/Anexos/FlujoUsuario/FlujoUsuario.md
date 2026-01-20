# Anexo: Flujo de Usuario

Este documento describe el flujo de interacción del usuario con la aplicación, desde el acceso inicial hasta la participación en el chat.

## 1. Acceso a la Web (Página de Login)

El usuario inicia su interacción con la aplicación accediendo a la URL principal, que lo redirige automáticamente a la página de login:
`http://localhost:8081/Login/login.html` (o la URL configurada para el despliegue).

## 2. Proceso de Login y Registro (src/main/resources/static/Login/login.html)

La página de login presenta los siguientes elementos y funcionalidades:

*   **Buscador de Usuarios (lado derecho):**
    *   Un campo de texto permite buscar usuarios existentes en la base de datos.
    *   Al escribir, se filtra una lista de usuarios de la base de datos, mostrando "chips" con sus iniciales y nombre.
    *   Al hacer clic en un chip de usuario, el nombre de ese usuario se precarga en el campo de "Usuario" del formulario de login.
*   **Usuarios Online Recientes (lado derecho):**
    *   Muestra una lista de usuarios que han estado "online" (han enviado un latido en los últimos 5 minutos).
*   **Usuarios Recientes (lado izquierdo):**
    *   Lista de los últimos usuarios con los que el cliente ha iniciado sesión, guardados localmente.
    *   Permite hacer clic para precargar el nombre de usuario y borrar de la lista.
*   **Formulario de Login Central:**
    *   **Paso 1: Introducir Nombre de Usuario.**
        *   El usuario introduce su nombre de usuario.
        *   Al pulsar "Continuar" o "Enter", la aplicación verifica si el usuario existe en la base de datos (`/api/usuarios/check/{nombre}`).
        *   Si el usuario no existe, se muestra un mensaje de error.
        *   Si el usuario existe, la interfaz avanza al Paso 2.
    *   **Paso 2: Introducir/Crear Contraseña.**
        *   El campo de "Usuario" se deshabilita.
        *   Aparece un campo de "Contraseña" con un botón para mostrar/ocultar la contraseña.
        *   **Si el usuario no tiene contraseña (es "virgen"):** El botón principal cambia a "Guardar y Entrar", y se le solicita al usuario que cree una nueva contraseña. Esta se guardará hasheada en la base de datos.
        *   **Si el usuario ya tiene contraseña:** El botón principal cambia a "Entrar", y se le solicita la contraseña existente para verificarla.
        *   Tras introducir la contraseña y pulsar el botón, la aplicación envía las credenciales (`/api/usuarios/login`).
        *   Si la autenticación es exitosa, el usuario es redirigido. Si falla, se muestra un mensaje de error.

## 3. Redirección al Chat

Una vez que el usuario se ha autenticado con éxito, es redirigido a la página del chat principal:
`http://localhost:8081/Chat/index.html` (o la URL configurada).

La redirección es condicional según el rol del usuario, si bien el chat es el destino principal para usuarios estándar.
*   `usuario.rol === "TECNICO"` -> `../SeleccionarObra/SeleccionarObra.html` (no implementado en este contexto del chat)
*   `usuario.rol === "ADMINISTRADOR"` -> `../SeleccionAdministracion/SeleccionAdministrador.html` (no implementado en este contexto del chat)
*   Para `USUARIO` o cualquier otro rol no especificado en la lógica de redirección, el flujo natural es al `Chat/index.html`.

## 4. Interacción en el Chat (src/main/resources/static/Chat/index.html)

En la interfaz del chat, los usuarios pueden:

*   **Conexión Automática:** Al cargar la página, el cliente JavaScript intenta establecer una conexión WebSocket segura (`wss://`) con el `ServidorChat` en el puerto `8081`.
*   **Autenticación en el Chat:** Tras la conexión WebSocket, el servidor de chat solicitará una re-autenticación (enviando "AUTENTICACION_REQUERIDA"). El cliente enviará automáticamente el nombre de usuario y la contraseña con la que se logueó en la página de login.
*   **Envío de Mensajes:** Los usuarios pueden escribir mensajes en el campo de entrada y enviarlos al pulsar "Enviar" o "Enter". Estos mensajes son difundidos a todos los usuarios conectados.
*   **Uso de Comandos:**
    *   **`/list`**: Muestra una lista de todos los usuarios conectados al chat.
    *   **`/bye`**: Desconecta al usuario del chat.
    *   **`/ping`**: El servidor responde con "PONG" y la hora actual.
    *   **`/weather [ciudad]`**: Obtiene un resumen del tiempo para la ciudad especificada (ej., `/weather madrid`).
    *   **`/kick [nombre_usuario]` (Solo ADMIN):** Expulsa a un usuario específico del chat.
    *   **`/shutdown` (Solo ADMIN):** Apaga el servidor de chat de forma controlada.
*   **Estado de Conexión:** La interfaz muestra el estado actual de la conexión ("Conectado", "Desconectado") y el nombre de usuario.
*   **Registro de Eventos:** El servidor registra eventos de seguridad (logins, comandos admin, etc.) en `security.log`.
*   **Comunicación Cifrada:** Toda la comunicación a través del WebSocket está cifrada con SSL/TLS.
