# Anexo: Estructura del Proyecto

Este anexo describe la estructura de directorios y la organización principal del código fuente del proyecto "Chat Corporativo Seguro". La estructura sigue las convenciones de un proyecto Spring Boot típico, complementada con directorios específicos para la lógica del chat y la documentación.

```
.
├── Documentacion/
│   ├── Anexos/
│   │   ├── API_REST/
│   │   │   └── API_REST.md
│   │   ├── EstructuraProyecto/
│   │   │   └── EstructuraProyecto.md (este archivo)
│   │   ├── FlujoUsuario/
│   │   │   └── FlujoUsuario.md
│   │   ├── ProtocoloComunicacion/
│   │   │   └── ProtocoloComunicacion.md
│   │   └── SSL_TLS/
│   │       └── SSL_TLS.md
│   ├── Anexos.md
│   ├── DescripcionModulo/
│   ├── Enunciado/
│   └── MetodologiasEmpleadas/
│       └── MetodologiasEmpleadas.md
├── pom.xml
└── src/
    ├── main/
    │   ├── java/
    │   │   └── org/
    │   │       └── example/
    │   │           ├── chat/                  // Lógica del servidor de chat (sockets)
    │   │           │   ├── ManejadorCliente.java
    │   │           │   ├── DifusorMensajes.java
    │   │           │   ├── GestorUsuarios.java
    │   │           │   └── ServidorChat.java
    │   │           ├── chat/dto/              // DTOs para el protocolo de comunicación del chat
    │   │           │   └── MensajeProtocolo.java
    │   │           ├── config/                // Clases de configuración de Spring Boot
    │   │           │   ├── ConfiguracionApiAbierta.java (Swagger)
    │   │           │   ├── ConfiguracionCors.java
    │   │           │   ├── ConfiguracionSeguridad.java (Spring Security)
    │   │           │   └── ConfiguracionServidorChat.java (Inicio del ServidorChat)
    │   │           ├── controller/            // Controladores REST (APIs)
    │   │           │   ├── ControladorChatbot.java
    │   │           │   ├── ControladorServidor.java
    │   │           │   └── ControladorUsuarios.java
    │   │           ├── dto/                   // DTOs generales de la API REST
    │   │           │   ├── ChatBot/
    │   │           │   │   ├── Candidato.java
    │   │           │   │   ├── Contenido.java
    │   │           │   │   ├── Parte.java
    │   │           │   │   ├── PeticionChat.java
    │   │           │   │   ├── PeticionGemini.java
    │   │           │   │   └── RespuestaChat.java
    │   │           │   ├── Login/
    │   │           │   │   └── PeticionLoginDTO.java
    │   │           │   └── Usuarios/
    │   │           │       ├── PeticionUsuariosDTO.java
    │   │           │       └── RespuestaUsuariosDTO.java
    │   │           ├── exceptions/            // Clases de excepciones personalizadas
    │   │           │   ├── EntityNotFoundException.java
    │   │           │   └── InvalidOperationException.java
    │   │           ├── logging/               // Clases para el registro de eventos
    │   │           │   └── RegistradorSeguridad.java
    │   │           ├── model/                 // Modelos de datos (Entidades JPA)
    │   │           │   ├── Servidor.java
    │   │           │   └── Usuario.java
    │   │           ├── repository/            // Repositorios JPA
    │   │           │   ├── ServidorRepository.java
    │   │           │   └── UsuarioRepository.java
    │   │           ├── service/               // Servicios de lógica de negocio
    │   │           │   ├── ServicioChatbot.java
    │   │           │   ├── ServicioClima.java (Integración OpenWeatherMap)
    │   │           │   ├── ServicioServidor.java (Coordinador de servicios del servidor)
    │   │           │   └── ServicioUsuarios.java
    │   │           └── Main.java              // Clase principal de Spring Boot
    │   └── resources/
    │       ├── application.properties         // Configuración de la aplicación
    │       ├── keystore.jks                   // Almacén de claves para SSL/TLS
    │       └── static/                        // Recursos estáticos (Frontend)
    │           ├── Chat/                      // Interfaz de chat (HTML, CSS, JS)
    │           │   ├── chat.js
    │           │   ├── index.html
    │           │   └── styles.css
    │           ├── ComprobacionUsuarioConectado/
    │           │   └── latido.js
    │           ├── config.js/
    │           │   └── config.js
    │           └── Login/                     // Interfaz de login (HTML, CSS, JS)
    │               ├── login.css
    │               ├── login.html
    │               └── login.js
    └── test/                                  // Pruebas unitarias/integración
```

## Descripción de Componentes Clave

*   **`Documentacion/`**: Contiene toda la documentación del proyecto, incluyendo metodologías, anexos, enunciado y descripción modular.
*   **`src/main/java/org/example/chat/`**: Implementa el núcleo del servidor de chat basado en sockets. Aquí reside la lógica de manejo de clientes, difusión de mensajes y gestión de usuarios conectados.
*   **`src/main/java/org/example/chat/dto/`**: Define los objetos de transferencia de datos específicos para el protocolo de comunicación del chat (JSON).
*   **`src/main/java/org/example/config/`**: Contiene las clases de configuración de Spring Boot, incluyendo seguridad (Spring Security), CORS, OpenAPI (Swagger) y la inicialización del `ServidorChat`.
*   **`src/main/java/org/example/controller/`**: Define los controladores REST que exponen los endpoints de la API para la gestión de usuarios y la interacción con el chatbot Gemini.
*   **`src/main/java/org/example/dto/`**: Contiene los DTOs utilizados por la API REST para el intercambio de datos.
*   **`src/main/java/org/example/logging/`**: Clases para el registro centralizado de eventos de seguridad.
*   **`src/main/java/org/example/model/`**: Define las entidades de la base de datos (modelos JPA).
*   **`src/main/java/org/example/repository/`**: Define los interfaces de repositorios JPA para la persistencia de datos.
*   **`src/main/java/org/example/service/`**: Contiene la lógica de negocio principal, incluyendo servicios para usuarios, chatbot, clima y el coordinador de servicios del servidor.
*   **`src/main/resources/application.properties`**: Archivo central para la configuración de la aplicación Spring Boot, incluyendo la conexión a la base de datos, claves API y puertos.
*   **`src/main/resources/keystore.jks`**: Archivo binario que contiene el certificado y la clave privada utilizados para establecer conexiones SSL/TLS seguras para el servidor de chat.
*   **`src/main/resources/static/`**: Contiene todos los recursos estáticos del frontend, incluyendo las páginas HTML, hojas de estilo CSS y scripts JavaScript para las interfaces de login y chat.
*   **`pom.xml`**: El Project Object Model de Maven, que define las dependencias del proyecto, la configuración del build y otra información del proyecto.
