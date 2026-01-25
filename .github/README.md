# Microservicio: CatalogoElvan - Centro de Control Operativo

El microservicio `CatalogoElvan` es una aplicación backend central para **Elvan Instalaciones**, que funciona como un "Centro de Control Operativo" para gestionar la logística y el personal de la empresa. Desarrollado con **Java** y **Spring Boot**, proporciona una API robusta para varios módulos clave:

## Propósito y Módulos Clave

Este servicio es el corazón digital para la gestión operativa, abarcando:

*   **Gestión de Usuarios**: Maneja cuentas de usuario, autenticación (incluyendo un flujo de primer inicio de sesión y restablecimiento de contraseñas), y rastrea el estado de actividad de los usuarios en línea mediante un mecanismo de "latidos".
*   **Control de Inventario**: Administra un catálogo de artículos y materiales, incluyendo operaciones CRUD completas y funcionalidad de búsqueda.
*   **Administración de Obras**: Facilita el registro, seguimiento y gestión de proyectos o "Obras" con capacidades CRUD completas.
*   **Trazabilidad de Movimientos**: Registra y rastrea el historial de movimientos de materiales (entradas y salidas), ofreciendo operaciones CRUD y logs de movimientos específicos por usuario.
*   **Gestión de Partes**: Administra partes o componentes individuales relacionados con las obras o los movimientos.
*   **Asistente Inteligente**: Integra el **modelo de IA Google Gemini** a través de una interfaz de chatbot para proporcionar soporte operativo y capacidades conversacionales.

## Pila Tecnológica y Configuración

*   **Lenguaje Principal**: Java
*   **Framework**: Spring Boot
*   **Base de Datos**: PostgreSQL
*   **Gestión de Dependencias**: Maven
*   **Seguridad**: Spring Security (con autenticación HTTP Basic, principalmente para proteger la documentación).
*   **Documentación de API**: OpenAPI (Swagger) para documentación interactiva.
*   **CORS**: Configuración permisiva de CORS para amplio acceso en desarrollo/entornos controlados.
*   **Integración AI**: Google Gemini para el módulo de chatbot.

## Documentación y Convenciones

Las guías de contribución, plantillas y `prompts` específicos para este servicio se encuentran en el directorio `./.github`. Estas directrices especializan y extienden las convenciones generales definidas en la raíz del monorepo. Para una documentación técnica más detallada sobre configuración y despliegue, consulte `Documentacion/README.md`.
