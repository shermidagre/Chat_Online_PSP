# Anexo: API REST del Backend

Este anexo detalla los principales endpoints de la API REST expuestos por el microservicio Spring Boot. La API está diseñada para la gestión de usuarios y la interacción con un modelo de lenguaje (Gemini), además de otras funcionalidades de utilidad. Toda la API está documentada de forma interactiva usando **Swagger (Springdoc OpenAPI)**, accesible en la ruta `/chat-psp` del servidor.

## Acceso a la Documentación de Swagger

La documentación interactiva de Swagger está disponible en:
`http://localhost:8081/chat-psp` (o la URL base de tu despliegue)

## Endpoints Principales

A continuación, se describen los grupos de endpoints más relevantes:

### 1. Gestión de Usuarios (`/api/usuarios`)

Estos endpoints se utilizan para administrar la información de los usuarios y para el proceso de autenticación en el sistema web de login.

*   **`POST /api/usuarios`**
    *   **Descripción:** Crea un nuevo usuario en el sistema.
    *   **Cuerpo de la Petición (`PeticionUsuariosDTO`):**
        ```json
        {
          "nombre": "string",
          "tipoUsuario": "string"
        }
        ```
    *   **Respuesta (`RespuestaUsuariosDTO`):** Detalles del usuario creado.
    *   **Códigos de Estado:** `201 Created`
*   **`PUT /api/usuarios/{id}`**
    *   **Descripción:** Actualiza la información de un usuario existente por su ID.
    *   **Cuerpo de la Petición (`PeticionUsuariosDTO`):**
        ```json
        {
          "nombre": "string",
          "tipoUsuario": "string"
        }
        ```
    *   **Respuesta (`RespuestaUsuariosDTO`):** Detalles del usuario actualizado.
    *   **Códigos de Estado:** `200 OK`, `404 Not Found`
*   **`GET /api/usuarios`**
    *   **Descripción:** Obtiene una lista de todos los usuarios registrados.
    *   **Respuesta (`List<RespuestaUsuariosDTO>`):**
        ```json
        [
          {
            "idUsuario": 0,
            "nombre": "string",
            "tipoUsuario": "string"
          }
        ]
        ```
    *   **Códigos de Estado:** `200 OK`
*   **`GET /api/usuarios/{id}`**
    *   **Descripción:** Obtiene los detalles de un usuario específico por su ID.
    *   **Respuesta (`RespuestaUsuariosDTO`):** Detalles del usuario.
    *   **Códigos de Estado:** `200 OK`, `404 Not Found`
*   **`DELETE /api/usuarios/{id}`**
    *   **Descripción:** Elimina un usuario del sistema por su ID.
    *   **Respuesta (`RespuestaUsuariosDTO`):** Detalles del usuario eliminado.
    *   **Códigos de Estado:** `200 OK`, `404 Not Found`
*   **`GET /api/usuarios/check/{nombre}`**
    *   **Descripción:** Verifica si un usuario existe y si ya tiene una contraseña establecida (para el flujo de login).
    *   **Respuesta (`Map<String, Object>`):**
        ```json
        {
          "idUsuario": 0,
          "nombre": "string",
          "rol": "string",
          "tieneContrasena": true
        }
        ```
    *   **Códigos de Estado:** `200 OK`, `404 Not Found`
*   **`POST /api/usuarios/login`**
    *   **Descripción:** Gestiona el login de usuario, incluyendo el establecimiento de contraseña la primera vez y la verificación posterior.
    *   **Cuerpo de la Petición (`PeticionLoginDTO`):**
        ```json
        {
          "idUsuario": 0,
          "contrasena": "string"
        }
        ```
    *   **Respuesta (`Map<String, String>`):**
        ```json
        {
          "estado": "ok" | "error",
          "mensaje": "string"
        }
        ```
    *   **Códigos de Estado:** `200 OK`, `401 Unauthorized`
*   **`PUT /api/usuarios/{id}/reset-password`**
    *   **Descripción:** Resetea la contraseña de un usuario, poniéndola a NULL para que pueda crear una nueva al siguiente login.
    *   **Respuesta:** `200 OK` con mensaje, `404 Not Found`
*   **`POST /api/usuarios/latido/{nombre}`**
    *   **Descripción:** Registra un "latido" de un usuario, marcándolo como online.
    *   **Códigos de Estado:** `200 OK`
*   **`GET /api/usuarios/online`**
    *   **Descripción:** Obtiene una lista de los nombres de usuario que han enviado un latido en los últimos 5 minutos.
    *   **Respuesta (`List<String>`):**
        ```json
        [
          "string"
        ]
        ```
    *   **Códigos de Estado:** `200 OK`

### 2. Interacción con Chatbot Gemini (`/api/chatbot`)

Estos endpoints permiten comunicarse con el modelo Gemini para obtener respuestas basadas en inteligencia artificial.

*   **`POST /api/chatbot/chat`**
    *   **Descripción:** Envía un mensaje al chatbot Gemini y recibe su respuesta.
    *   **Cuerpo de la Petición (`PeticionChat`):**
        ```json
        {
          "mensaje": "string"
        }
        ```
    *   **Respuesta (`RespuestaChat`):**
        ```json
        {
          "respuesta": "string"
        }
        ```
    *   **Códigos de Estado:** `200 OK`

## Seguridad de la API REST

*   **CORS:** Configurado para permitir peticiones desde cualquier origen (`*`), facilitando el desarrollo frontend.
*   **Swagger UI:** Protegido con autenticación básica (usuario/contraseña `admin`/`elvan.2021` por defecto), configurado en `ConfiguracionSeguridad`.
