# Anexo: Estructura del Proyecto

Este anexo describe la estructura de directorios y la organización del código fuente del proyecto "Chat Corporativo Seguro" en su versión simplificada. La estructura ha sido reducida a su mínima expresión para cumplir con los requisitos básicos de un chat en consola.

```
.
├── Documentacion/
│   ├── Anexos/
│   │   ├── EstructuraProyecto/
│   │   │   └── EstructuraProyecto.md (este archivo)
│   │   └── ProtocoloComunicacion/
│   │       └── ProtocoloComunicacion.md
│   ├── ... (otros documentos)
├── pom.xml
└── src/
    └── main/
        └── java/
            └── org/
                └── example/
                    ├── chat/
                    │   ├── Servidor.java
                    │   └── ManejadorCliente.java
                    └── Main.java
```

## Descripción de Componentes Clave

*   **`src/main/java/org/example/Main.java`**:
    *   **Rol**: Punto de entrada de la aplicación.
    *   **Descripción**: Contiene el método `main` que crea una instancia de `Servidor` y llama a su método `iniciar()` para poner en marcha el chat.

*   **`src/main/java/org/example/chat/Servidor.java`**:
    *   **Rol**: Corazón del servidor de chat.
    *   **Descripción**: Gestiona todas las conexiones entrantes. Abre un `ServerSocket`, espera a los clientes y, por cada uno, crea un hilo (`ManejadorCliente`) para atenderlo. También se encarga de la difusión de mensajes (broadcasting) a todos los clientes conectados.

*   **`src/main/java/org/example/chat/ManejadorCliente.java`**:
    *   **Rol**: Lógica de comunicación para un único cliente.
    *   **Descripción**: Se ejecuta en un hilo separado para cada usuario. Es responsable de leer los mensajes que el cliente envía, procesar los comandos (`/bye`, `/list`, etc.) y enviar las respuestas o los mensajes de otros usuarios a su cliente asociado.

*   **`pom.xml`**:
    *   **Rol**: Gestor de dependencias del proyecto (Maven).
    *   **Descripción**: Ha sido simplificado para eliminar todas las librerías de Spring Boot, JPA, etc. Ahora solo contiene lo esencial para compilar y ejecutar un proyecto Java estándar.

