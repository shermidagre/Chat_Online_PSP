# Arquitectura de Microservicios: Chat Corporativo Seguro

## 1. Visión General

Esta arquitectura desacopla la aplicación monolítica en dos servicios especializados e independientes: `servicio-usuarios` y `servicio-chat`. Esta separación de responsabilidades mejora la escalabilidad, el mantenimiento y la seguridad del sistema.

-   **`servicio-usuarios`**: Actúa como el servicio de identidad, gestionando usuarios, autenticación y autorización.
-   **`servicio-chat`**: Gestiona la comunicación en tiempo real, las salas de chat y los mensajes.

## 2. Diagrama de Arquitectura

```mermaid
graph TD
    subgraph "Cliente (Navegador/App)"
        A[Interfaz de Usuario]
    end

    subgraph "Infraestructura"
        LB[Load Balancer / API Gateway]
    end

    subgraph "Microservicios"
        B[servicio-usuarios <br> (API REST)]
        C[servicio-chat <br> (WebSockets)]
    end

    subgraph "Base de Datos (Supabase/PostgreSQL)"
        DB[(Base de Datos)]
    end

    A -- "1. Login (HTTP)" --> LB -- "/api/auth/**" --> B
    B -- "2. Valida credenciales" --> DB
    DB -- "3. Datos de usuario" --> B
    B -- "4. Emite JWT Token" --> A

    A -- "5. Conexión (WebSocket) con JWT" --> LB -- "/chat" --> C
    C -- "6. Valida JWT con servicio-usuarios (HTTP)" --> B
    B -- "7. Respuesta de validación" --> C
    C -- "8. Conexión establecida" --> A

    A -- "9. Envía/Recibe Mensajes" <--> C
    C -- "Guarda historial (opcional)" --> DB

    linkStyle 0,3,4,8,9 stroke-width:2px,stroke:green;
    linkStyle 1,5 stroke-width:2px,stroke:blue;
    linkStyle 2,6,7 stroke-width:2px,stroke:orange;
```

## 3. Definición de Servicios

### 3.1. `servicio-usuarios`

-   **Descripción**: Gestiona la identidad, autenticación y autorización de los usuarios. Es el único servicio con acceso directo a la tabla `usuarios`.
-   **Pila Tecnológica**: Java 17, Spring Boot, Spring Data JPA, Spring Security, PostgreSQL, Maven.

#### Endpoints de la API (OpenAPI 3):

-   `POST /api/auth/register`
    -   **Descripción**: Registra un nuevo usuario.
    -   **Request Body**: `{"nombre": "usuario", "password": "..."}`
    -   **Response 201**: `{"id": 1, "nombre": "usuario"}`
-   `POST /api/auth/login`
    -   **Descripción**: Autentica a un usuario y devuelve un token JWT.
    -   **Request Body**: `{"nombre": "usuario", "password": "..."}`
    -   **Response 200**: `{"token": "ey..."}`
-   `GET /api/auth/validate`
    -   **Descripción**: Endpoint **interno** para que otros servicios validen un token.
    -   **Headers**: `Authorization: Bearer <token>`
    -   **Response 200**: `{"valid": true, "username": "usuario", "roles": ["USER"]}`
    -   **Response 401**: `{"valid": false}`

### 3.2. `servicio-chat`

-   **Descripción**: Gestiona la comunicación en tiempo real a través de WebSockets.
-   **Pila Tecnológica**: Java 17, Spring Boot, Spring WebSockets, Maven.

#### Endpoint WebSocket:

-   `WS /chat`
    -   **Descripción**: Endpoint principal para la comunicación del chat.
    -   **Protocolo de Conexión**: El cliente debe conectarse pasando el token JWT, por ejemplo, a través de un protocolo secundario (`Sec-WebSocket-Protocol`) o un mensaje de autorización inicial.

#### Protocolo de Mensajes (JSON sobre WebSocket):

-   **Mensaje del Cliente al Servidor (chat):**
    ```json
    { "type": "MESSAGE", "content": "Hola a todos!" }
    ```
-   **Mensaje del Servidor al Cliente (broadcast):**
    ```json
    { "type": "NEW_MESSAGE", "sender": "nombre_usuario", "content": "Hola a todos!", "timestamp": "..." }
    ```

## 4. Esquema de Base de Datos (PostgreSQL)

-   **Tabla `usuarios`**:
    -   `id` (SERIAL, PK)
    -   `nombre` (VARCHAR, UNIQUE, NOT NULL)
    -   `password` (VARCHAR, NOT NULL) - *Se almacenará el hash de la contraseña*
    -   `rol` (VARCHAR, NOT NULL, DEFAULT 'USER')
    -   `created_at` (TIMESTAMP, DEFAULT NOW())

-   **Tabla `mensajes`** (Opcional, para persistencia de historial):
    -   `id` (SERIAL, PK)
    -   `id_usuario_emisor` (INTEGER, FK a `usuarios.id`)
    -   `contenido` (TEXT)
    -   `timestamp` (TIMESTAMP, DEFAULT NOW())

## 5. Flujo de Autenticación y Conexión

1.  El cliente solicita al `servicio-usuarios` el login con sus credenciales.
2.  `servicio-usuarios` valida las credenciales contra la BD y genera un token JWT de corta duración.
3.  El cliente recibe el token JWT.
4.  El cliente inicia una conexión WebSocket con el `servicio-chat`, enviando el token JWT.
5.  `servicio-chat` recibe el token y lo valida haciendo una petición interna al endpoint `/api/auth/validate` del `servicio-usuarios`.
6.  Si la validación es exitosa, la conexión WebSocket se establece y el usuario puede empezar a enviar y recibir mensajes.
