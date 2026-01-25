---
goal: 'Refactorizar el monolito a una arquitectura de dos microservicios: servicio-usuarios y servicio-chat.'
version: '1.0'
date_created: '2026-01-25'
last_updated: '2026-01-25'
owner: 'Gemini AI Architect'
status: 'Planned'
tags: ['refactor', 'architecture']
---

# Introducción

![Estado: Planned](https://img.shields.io/badge/status-Planned-blue)

Este plan detalla los pasos necesarios para refactorizar la aplicación de chat actual desde una única aplicación Spring Boot a una arquitectura de microservicios compuesta por un **`servicio-usuarios`** y un **`servicio-chat`**. El objetivo es lograr un sistema más escalable, mantenible y robusto.

## 1. Requisitos y Restricciones

- **REQ-001**: La nueva arquitectura debe consistir en dos servicios Spring Boot independientes.
- **REQ-002**: La comunicación entre cliente y `servicio-usuarios` será vía API REST.
- **REQ-003**: La comunicación entre cliente y `servicio-chat` será vía WebSockets.
- **REQ-004**: La autenticación se basará en tokens JWT emitidos por `servicio-usuarios`.
- **CON-001**: Ambos servicios deben estar escritos en Java 17+ con Spring Boot.
- **CON-002**: El sistema debe ser orquestable localmente mediante Docker Compose.

## 2. Pasos de Implementación

### Fase 1: Estructura del Proyecto y Preparación

- **META-001**: Crear la nueva estructura de proyectos Maven y segregar el código existente.

| Tarea | Descripción | Completado |
|-------|-------------|------------|
| TSK-001 | Crear una nueva estructura de carpetas: `/servicio-usuarios` y `/servicio-chat`. | ☐ |
| TSK-002 | Inicializar cada carpeta como un proyecto Spring Boot independiente con su propio `pom.xml`. | ☐ |
| TSK-003 | Mover el código relevante (`Usuario`, `RepositorioUsuarios`, lógica de conexión) a `servicio-usuarios`. | ☐ |
| TSK-004 | Mover la lógica de WebSocket (`TextWebSocketHandler`) a `servicio-chat`. | ☐ |
| TSK-005 | Limpiar el proyecto raíz original, dejando solo los archivos de configuración del proyecto (como este plan). | ☐ |

### Fase 2: Implementación de `servicio-usuarios`

- **META-002**: Desarrollar el servicio de identidad y autenticación.

| Tarea | Descripción | Completado |
|-------|-------------|------------|
| TSK-006 | Añadir dependencias de Spring Security y JWT (`io.jsonwebtoken:jjwt-api`, `jjwt-impl`, `jjwt-jackson`). | ☐ |
| TSK-007 | Implementar la lógica de hash de contraseñas con `BCryptPasswordEncoder`. | ☐ |
| TSK-008 | Crear un `AuthController` con los endpoints `POST /api/auth/register` y `POST /api/auth/login`. | ☐ |
| TSK-009 | Implementar la generación de tokens JWT en el endpoint de login. | ☐ |
| TSK-010 | Crear el endpoint interno `GET /api/auth/validate` para la validación de tokens. | ☐ |
| TSK-011 | Configurar la conexión a la base de datos PostgreSQL (Supabase) en `application.properties`. | ☐ |
| TSK-012 | Eliminar cualquier dependencia o código relacionado con WebSockets. | ☐ |

### Fase 3: Implementación de `servicio-chat`

- **META-003**: Desarrollar el servicio de mensajería en tiempo real.

| Tarea | Descripción | Completado |
|-------|-------------|------------|
| TSK-013 | Crear un `ChatWebSocketHandler` que gestione las conexiones WebSocket. | ☐ |
| TSK-014 | Implementar la lógica para validar el token JWT al establecer la conexión. Esto requerirá un cliente HTTP (`WebClient` o `RestTemplate`) para llamar al `servicio-usuarios`. | ☐ |
| TSK-015 | Almacenar las sesiones de WebSocket activas asociadas a los nombres de usuario validados. | ☐ |
| TSK-016 | Implementar la lógica de broadcast de mensajes a todos los clientes conectados. | ☐ |
| TSK-017 | Eliminar cualquier dependencia de Spring Data JPA y acceso directo a la base de datos de usuarios. | ☐ |

### Fase 4: Contenerización y Orquestación

- **META-004**: Preparar el entorno de ejecución con Docker.

| Tarea | Descripción | Completado |
|-------|-------------|------------|
| TSK-018 | Crear un `Dockerfile` para `servicio-usuarios`. | ☐ |
| TSK-019 | Crear un `Dockerfile` para `servicio-chat`. | ☐ |
| TSK-020 | Crear un archivo `docker-compose.yml` en la raíz del proyecto para orquestar los dos servicios y una base de datos PostgreSQL. | ☐ |
| TSK-021 | Configurar las variables de entorno en `docker-compose.yml` para la comunicación entre servicios. | ☐ |

## 3. Alternativas

- **ALT-001**: Usar un único proyecto con módulos de Maven en lugar de proyectos separados. Se descartó para forzar un mayor desacoplamiento, simulando un entorno de microservicios más realista.
- **ALT-002**: Comunicación síncrona vía colas de mensajes (RabbitMQ, Kafka). Se descartó por simplicidad para esta fase inicial, prefiriendo la comunicación directa REST.

## 4. Dependencias

- **DEP-001**: `servicio-chat` depende de que `servicio-usuarios` esté disponible para validar tokens.
- **DEP-002**: Ambos servicios dependen de que la base de datos PostgreSQL esté accesible.

## 5. Archivos Afectados

- `src/main/java/org/example/controller/ChatController.java` (Será eliminado y su lógica dividida)
- `src/main/java/org/example/model/Usuario.java` (Se moverá a `servicio-usuarios`)
- `src/main/java/org/example/repository/RepositorioUsuarios.java` (Se moverá a `servicio-usuarios`)
- `src/main/java/org/example/config/WebSocketConfig.java` (Se moverá y adaptará en `servicio-chat`)
- `pom.xml` (Será reemplazado por los `pom.xml` de cada microservicio)

## 6. Pruebas

- **TEST-001**: Pruebas unitarias para la generación y validación de JWT en `servicio-usuarios`.
- **TEST-002**: Pruebas de integración para los endpoints de `AuthController`.
- **TEST-003**: Pruebas unitarias para el `ChatWebSocketHandler` en `servicio-chat`, usando un cliente mock para la validación de token.
- **TEST-004**: Pruebas de extremo a extremo (E2E) del flujo completo con Docker Compose en ejecución.

## 7. Riesgos y Suposiciones

- **RISK-001**: La latencia en la comunicación HTTP entre `servicio-chat` y `servicio-usuarios` para la validación de tokens podría afectar el tiempo de establecimiento de la conexión WebSocket.
- **ASUM-001**: Se asume que el entorno de desarrollo local tiene Docker y Docker Compose instalados.
- **ASUM-002**: Se asume que las credenciales para la base de datos de Supabase estarán disponibles como variables de entorno.
