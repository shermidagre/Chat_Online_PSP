
---


```markdown
# 📡 Chat Corporativo Seguro

> **Asignatura**: Programación de Servicios y Procesos (PSP)  
> **Curso**: DAM – Desarrollo de Aplicaciones Multiplataforma  
> **Autor**: [Samuel Hermida]  
> **Fecha de entrega**: 5 de febrero de 2026  

Este proyecto implementa un **sistema de chat corporativo en red** con soporte para múltiples usuarios, autenticación, roles, cifrado SSL/TLS, interfaz gráfica y más. Cumple con todos los niveles obligatorios y varios opcionales descritos en el enunciado de la práctica.

---

## 📌 Índice

- [Características](#-características)
- [Arquitectura](#-arquitectura)
- [Protocolo de Comunicación](#-protocolo-de-comunicación)
- [Requisitos](#-requisitos)
- [Instalación y Uso](#-instalación-y-uso)
- [Niveles Implementados](#-niveles-implementados)
- [Pruebas](#-pruebas)
- [Capturas](#-capturas)
- [Licencia](#-licencia)

---

## ✨ Características

- ✅ **Servidor multicliente** con pool de hilos (máx. 10 conexiones).
- ✅ **Broadcasting** en tiempo real con formato `nick: mensaje`.
- ✅ **Comandos integrados**: `/bye`, `/list`, `/ping`.
- ✅ **Autenticación** con archivo de hashes SHA-256.
- ✅ **Roles**: `USER` y `ADMIN` (con comandos `/kick` y `/shutdown`).
- ✅ **Cifrado SSL/TLS** mediante `SSLSocket` y `SSLServerSocket`.
- ✅ **Interfaz gráfica** en JavaFX para el cliente.
- ✅ **Integración con API REST** (ej: clima actual).
- ✅ **Registro de auditoría** en `security.log`.
- ✅ **Robustez**: manejo de excepciones sin cierres inesperados.

---

## 🏗️ Arquitectura

El sistema sigue una arquitectura modular clara:

- **Servidor**:
  - `ChatServer.java`: Gestiona el socket y el pool de hilos (`ExecutorService`).
  - `ClientHandler.java`: Lógica por cliente (autenticación, comandos, broadcast).
  - `UserManager.java`: Gestión de usuarios, roles y bloqueos.
  - `MessageBroadcaster.java`: Envío seguro a todos los clientes conectados.
  - `SecurityLogger.java`: Registro de eventos de seguridad.

- **Cliente**:
  - `ChatClientCLI.java`: Versión de consola (obligatoria).
  - `ChatClientGUI.java`: Interfaz gráfica en JavaFX (nivel opcional).
  - `NetworkService.java`: Abstracción de la lógica de red.

- **Utilidades**:
  - `ProtocolParser.java`: Parseo manual de tramas JSON/texto.
  - `HashUtils.java`: Generación y verificación de hashes SHA-256.
  - `WeatherService.java`: Integración con API REST (OpenWeather).

Todas las clases están separadas por responsabilidad, evitando "código espagueti".

---

## 📡 Protocolo de Comunicación

Se utiliza un protocolo basado en **JSON sobre texto plano**, con las siguientes estructuras:

### Mensaje genérico:
```json
{
  "type": "MESSAGE|COMMAND|AUTH|NOTIFICATION",
  "sender": "nickname",
  "content": "contenido o comando",
  "timestamp": "2026-01-20T15:30:00"
}
```

### *Estructura del proyecto en formaato diagrama de Mermaid :*

```mermaid
---
config:
  theme: forest
  layout: elk
  look: neo
---
flowchart LR
    A["Práctica 02: Chat Corporativo"] --> B["Nivel 1: Mínimo Obligatorio (5 pts)"] & C["Nivel 2: Documentación (1 pt)"] & D["Nivel 3: Identidad y Control (2 pts)"] & E["Nivel 4: SSL/TLS (1.5 pts)"] & F["Nivel 5: Interfaz Gráfica (1 pt)"] & G["Nivel 6: Integración API REST (1 pt)"] & H["Nivel 7: Auditoría/Logs (0.5 pt)"] & I["Nivel 8: ¡Impresionadme! (? pts)"] & Z["Normas Obligatorias"]
    B --> B1["Concurrencia: Pool de hilos (ExecutorService) ≤10 conexiones"] & B2["Sincronización: Colecciones thread-safe (java.util.concurrent)"] & B3["Separación de responsabilidades: red, hilos, negocio"] & B4@{ label: "Inicio: Pide puerto → 'Ningún cliente conectado'" } & B5["> Nuevo cliente (nick). X usuarios."] & B6@{ label: "Broadcasting: 'nick: mensaje' → reenvío a todos" } & B7@{ label: "Cierre: Envía 'El servidor se desconectó'" } & B8["Configuración: IP, puerto, nickname"] & B9@{ label: "Interfaz: 'Conectado a la sala', notifica entrada" } & B10["Comandos: /bye, /list, /ping"] & B11["Control de excepciones: sin stacktrace crudo"]
C --> C1["Protocolo: estructura tramas texto/JSON"] & C2["Arquitectura: diseño interno explicado"] & C3["Pruebas: capturas de denegación/bloqueo"]
D --> D1["Autenticación: login + hash SHA-256"] & D2["Roles: USER / ADMIN"] & D3["Comandos ADMIN: /kick, /shutdown"] & D4["Bloqueo tras 3 fallos de login"]
E --> E1["Migrar a SSLSocket / SSLServerSocket"]
F --> F1["Cliente con GUI (Swing/JavaFX)"] & F2["Hilo UI ≠ hilo de red"]
G --> G1["Integrar funcionalidad vía API REST<br>(ej: clima)"]
H --> H1["Archivo security.log: IP, fecha, nick"] & H2["Eventos: login OK/KO, uso comandos ADMIN"]
Z --> Z1["Prohibido ObjectOutputStream/InputStream"] & Z2["Protocolo propio o JSON parseado manual/librería"] & Z3["Servidor nunca debe caerse"] & Z4["Defensa oral: explicar/modificar código en vivo"]

B4@{ shape: rect}
B6@{ shape: rect}
B7@{ shape: rect}
B9@{ shape: rect}

````

### Ejemplos:
- Autenticación:
  ```json
  {"type":"AUTH","sender":"alice","content":"sha256_hash"}
  ```
- Mensaje de chat:
  ```json
  {"type":"MESSAGE","sender":"bob","content":"Hola a todos!"}
  ```
- Comando:
  ```json
  {"type":"COMMAND","sender":"admin","content":"/kick bob"}
  ```

> **Nota**: No se usa `ObjectOutputStream`. Todo se serializa/deserializa como texto.

---

## ⚙️ Requisitos

- JDK 17 o superior
- Maven (para dependencias como GSON o JavaFX)
- Certificado SSL autofirmado (`keystore.jks`) incluido en `/resources`
- Archivo `users.json` con credenciales predefinidas (ver ejemplo abajo)

Ejemplo de `users.json`:
```json
{
  "alice": {"password": "2bd8e...", "role": "USER"},
  "admin": {"password": "8c697...", "role": "ADMIN"}
}
```

---

## 🚀 Instalación y Uso

### 1. Clonar el repositorio
```bash
git clone https://github.com/tu-usuario/chat-corporativo-p02.git
cd chat-corporativo-p02
```

### 2. Compilar
```bash
mvn clean compile
```

---

## 📊 Niveles Implementados

| Nivel | Descripción | Puntos |
|------|-------------|--------|
| ✅ 1 | Mínimo obligatorio (concurrencia, comandos, robustez) | 5.0 |
| ✅ 2 | Documentación (protocolo, arquitectura, pruebas) | 1.0 |
| ✅ 3 | Identidad y control de acceso (login, roles, bloqueo) | 2.0 |
| ✅ 4 | SSL/TLS | 1.5 |
| ✅ 5 | Interfaz gráfica (JavaFX) | 1.0 |
| ✅ 6 | Integración API REST (clima) | 1.0 |
| ✅ 7 | Auditoría (`security.log`) | 0.5 |
| 💡 8 | ¡Impresionadme! (diseño limpio, extensible, tests unitarios) | +? |

**Total estimado**: **12.0+ puntos**

---

## 🧪 Pruebas

- ✅ Servidor resiste desconexiones bruscas.
- ✅ Cliente no se rompe si el servidor cae.
- ✅ 3 intentos fallidos → bloqueo temporal.
- ✅ Solo ADMIN puede usar `/kick` o `/shutdown`.
- ✅ Comunicación cifrada (verificado con Wireshark).
- ✅ API REST devuelve clima correctamente.
- ✅ Logs registran IP, nick, fecha y eventos.

---

## 🖼️ Capturas

![Cliente CLI](https://ibb.co/TBjfxwW7)  
*Cliente en modo consola*

![Documentación](https://ibb.co/jvcmtbKR)  
*Ejemplo de arquitectura y protocolo*



---

## 📜 Licencia

Este proyecto es de uso académico exclusivo. Todos los derechos reservados © 2026 [Samuel Hermida].
```

---
