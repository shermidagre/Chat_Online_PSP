# Metodologías y Tecnologías Empleadas en el Proyecto

## Introducción

Este documento detalla las metodologías y las principales tecnologías empleadas en el desarrollo del "Chat Corporativo Seguro". El proyecto se ha concebido siguiendo una arquitectura de microservicio, aprovechando la robustez y flexibilidad de un stack tecnológico moderno y ampliamente adoptado en la industria.

## Tecnologías Clave

### 1. Spring Boot (Backend - Java)

**Descripción:** Spring Boot es un framework de desarrollo de aplicaciones Java que simplifica la creación de aplicaciones stand-alone y de grado de producción, "justo para ejecutar". Se ha utilizado para construir el núcleo del microservicio de backend.

**Uso en el Proyecto:**
*   **Servicios RESTful:** Exposición de endpoints para la gestión de usuarios, autenticación y comunicación con APIs externas (como Gemini y OpenWeatherMap).
*   **Inyección de Dependencias:** Gestión eficiente de componentes y sus interacciones (ej., `ServicioUsuarios`, `ServicioServidor`).
*   **Configuración Simplificada:** Uso de `application.properties` para gestionar configuraciones de base de datos, claves API y puertos.
*   **Servidor Integrado:** Ejecución autónoma con un servidor Tomcat embebido, facilitando el despliegue.

### 2. Swagger (Springdoc OpenAPI)

**Descripción:** Swagger (implementado a través de Springdoc OpenAPI) es un conjunto de herramientas de código abierto que ayuda a diseñar, construir, documentar y consumir APIs RESTful. Genera automáticamente una documentación interactiva de la API.

**Uso en el Proyecto:**
*   **Documentación Interactiva:** Proporciona una interfaz web (`/chat-psp`) donde los desarrolladores pueden ver todos los endpoints del microservicio, sus parámetros, modelos de datos y probar las llamadas directamente. Esto es crucial para la integración frontend y para la futura evolución del microservicio.
*   **Contratos de API Claros:** Asegura que el comportamiento de la API esté bien definido y sea comprensible para cualquier consumidor.

### 3. Supabase (Base de Datos PostgreSQL)

**Descripción:** Supabase es una alternativa de código abierto a Firebase, que ofrece una base de datos PostgreSQL en la nube, autenticación, APIs instantáneas y más.

**Uso en el Proyecto:**
*   **Persistencia de Datos:** Utilizado como la base de datos relacional para almacenar información persistente del microservicio, como los detalles de los usuarios (nombres, roles, contraseñas hasheadas).
*   **Configuración a través de `application.properties`:** La conexión a Supabase se gestiona mediante las propiedades `spring.datasource.url`, `username` y `password` configuradas en `application.properties`, utilizando variables de entorno para mayor seguridad y flexibilidad.
*   **Autenticación del Servidor de Chat:** La autenticación de los usuarios que se conectan al servidor de chat se realiza contra la tabla `Usuario` en la base de datos de Supabase, garantizando un control de acceso centralizado y seguro.

### 4. Frontend (HTML, CSS, JavaScript)

**Descripción:** Estas tecnologías web estándar se utilizan para construir la interfaz de usuario del cliente, proporcionando una experiencia interactiva y dinámica.

**Uso en el Proyecto:**
*   **HTML:** Estructura las páginas web de login (`login.html`) y chat (`index.html`), definiendo los elementos de la interfaz.
*   **CSS:** Proporciona el estilo visual y el diseño de la aplicación, garantizando una presentación coherente y atractiva.
*   **JavaScript:** Implementa la lógica del lado del cliente, gestionando la interacción del usuario, la comunicación con el backend (tanto REST como WebSocket) y la actualización dinámica de la interfaz.
    *   **Login:** Manejo del flujo de autenticación, validación de formularios y redirección.
    *   **Chat:** Establecimiento de conexiones WebSocket con el `ServidorChat`, envío y recepción de mensajes en tiempo real, procesamiento de comandos y actualización del historial de chat.

### 5. Comunicación WebSocket

**Descripción:** WebSocket es un protocolo de comunicación que proporciona canales de comunicación full-duplex sobre una única conexión TCP. Es ideal para aplicaciones que requieren actualizaciones en tiempo real, como los chats.

**Uso en el Proyecto:**
*   **Comunicación en Tiempo Real:** El cliente de chat (JavaScript) y el `ServidorChat` (Java) utilizan WebSockets para el intercambio bidireccional de mensajes de chat y comandos, permitiendo una experiencia de usuario fluida y reactiva.
*   **Protocolo Basado en JSON:** Los mensajes intercambiados viajan en formato JSON, lo que facilita el parseo y la gestión de diferentes tipos de mensajes (chat, comandos, notificaciones).

### 6. Seguridad (SSL/TLS, Autenticación, Auditoría)

**Descripción:** La seguridad es un pilar fundamental del proyecto, abordada en múltiples capas.

**Uso en el Proyecto:**
*   **SSL/TLS:** Todas las comunicaciones del chat se cifran utilizando el protocolo SSL/TLS, garantizando la confidencialidad e integridad de los datos transmitidos entre el cliente y el servidor. Se usa un `keystore.jks` para gestionar los certificados.
*   **Autenticación:** Los usuarios se autentican con credenciales almacenadas en la base de datos (Supabase), utilizando `PasswordEncoder` para un almacenamiento seguro de contraseñas. Se implementa un mecanismo de bloqueo por intentos fallidos.
*   **Roles de Usuario:** Diferenciación de roles (`ADMIN`, `USUARIO`) para controlar el acceso a comandos críticos (ej., `/kick`, `/shutdown`).
*   **Registro de Auditoría (`RegistradorSeguridad`):** Se registran eventos importantes (intentos de login exitosos/fallidos, uso de comandos de administrador, cambios de estado de usuario) en un archivo `security.log` para fines de auditoría y monitoreo.

## Metodología de Desarrollo

El proyecto sigue una metodología de desarrollo modular y por capas, inspirada en principios SOLID y en las mejores prácticas de microservicios:
*   **Separación de Responsabilidades:** Cada componente (controlador, servicio, repositorio, manejador de cliente) tiene una responsabilidad única y bien definida.
*   **Reutilización:** Se maximiza la reutilización de código y lógica, especialmente en la gestión de usuarios.
*   **Facilidad de Mantenimiento:** La estructura clara y el código organizado facilitan la comprensión, modificación y extensión del proyecto.
