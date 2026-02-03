# Documentación de Pruebas: Despliegue y Uso del Chat Corporativo (Microservicios)

Este documento detalla los pasos para desplegar y utilizar la aplicación de chat corporativo, que ahora incluye una interfaz de usuario frontend desarrollada en React, junto con los microservicios de backend (`servicio-usuarios` y `servicio-chat`).

---

## 🚀 Despliegue y Puesta en Marcha con Docker Compose

La aplicación se despliega utilizando Docker Compose, lo que permite levantar todos los microservicios y el frontend con un solo comando.

### Requisitos Previos

*   **Docker Desktop** (o un entorno Docker equivalente) instalado y en ejecución en tu sistema.

### Pasos para el Despliegue

1.  **Navega a la raíz del proyecto:**
    Abre tu terminal y navega hasta el directorio raíz del proyecto `Chat_PSP` (donde se encuentra el archivo `docker-compose.yml`).

    ```bash
    cd C:\Users\samue\Documents\dam2\Chat_PSP
    ```

2.  **Construye y Arranca los Servicios:**
    Ejecuta el siguiente comando para construir las imágenes Docker (del frontend y el servicio de chat) y levantar todos los contenedores:

    ```bash
    docker-compose up --build
    ```

    **¿Qué hace este comando?**
    *   **`db`**: Arranca un contenedor de PostgreSQL (base de datos).
    *   **`servicio-usuarios`**: Arranca el microservicio de gestión de usuarios y autenticación. Este servicio se descarga de Docker Hub.
    *   **`servicio-chat`**: Construye y arranca el microservicio de chat (WebSockets). Este servicio se construirá localmente utilizando el `Dockerfile` ubicado en `./servicio-chat`.
    *   **`frontend`**: Construye y arranca la aplicación frontend de React. Este servicio se construirá localmente utilizando el `Dockerfile` ubicado en `./frontend`.

    La primera vez que ejecutes este comando, puede tardar varios minutos ya que descargará las imágenes base y construirá las imágenes del frontend y del `servicio-chat`.

### Verificación de Servicios

*   Una vez que el comando `docker-compose up --build` termine, deberías ver logs de todos los servicios indicando que están en ejecución.
*   Puedes verificar el estado de los contenedores con:
    ```bash
    docker ps
    ```

---

## 🌐 Acceso a la Interfaz de Usuario (Frontend)

Una vez que todos los servicios estén en funcionamiento, puedes acceder a la interfaz de usuario del chat en tu navegador web:

*   **URL de Acceso:**
    ```
    http://localhost:3000
    ```

---

## 🚶 Flujo de Uso de la Aplicación

1.  **Página de Inicio (`/`):**
    Al acceder a `http://localhost:3000`, serás recibido por la página de inicio, que te ofrecerá enlaces para `Login` y `Register`.

2.  **Registro de Nuevo Usuario (`/register`):**
    *   Haz clic en el enlace "Register" o navega directamente a `http://localhost:3000/register`.
    *   Completa el formulario con un `Username` y `Password`. Asegúrate de que las contraseñas coincidan.
    *   Haz clic en "Register". Si el registro es exitoso, serás redirigido automáticamente a la página de Login.
    *   **Comunicación:** El frontend envía una petición `POST` a `http://localhost:8080/api/auth/register` (al `servicio-usuarios`).

3.  **Inicio de Sesión (`/login`):**
    *   Haz clic en el enlace "Login" o navega directamente a `http://localhost:3000/login`.
    *   Introduce las credenciales del usuario que acabas de registrar (o uno existente).
    *   Haz clic en "Login". Si las credenciales son correctas, se almacenará un token JWT en tu `localStorage` y serás redirigido a la página de Chat.
    *   **Comunicación:** El frontend envía una petición `POST` a `http://localhost:8080/api/auth/login` (al `servicio-usuarios`).

4.  **Sala de Chat (`/chat`):**
    *   Una vez logueado, accederás a la sala de chat.
    *   El frontend establecerá una conexión WebSocket con el `servicio-chat`.
    *   **Autenticación WebSocket:** El token JWT obtenido en el login se envía automáticamente al `servicio-chat` a través del WebSocket para autenticar la conexión.
    *   Puedes escribir mensajes en el campo de texto y enviarlos. Los mensajes aparecerán en el historial del chat.
    *   **Comunicación:** El frontend utiliza una conexión WebSocket con `ws://localhost:8081/chat` (al `servicio-chat`) para enviar y recibir mensajes en tiempo real.
    *   **Cerrar Sesión:** Hay un botón "Logout" que elimina el token JWT del `localStorage` y te redirige a la página de Login.

---

## 🛠️ Notas Adicionales

*   **Endpoints de Backend:**
    *   `servicio-usuarios`: accesible en el puerto `8080` (dentro de Docker, se comunica como `http://servicio-usuarios:8080`).
    *   `servicio-chat`: accesible en el puerto `8081` (dentro de Docker, se comunica como `ws://servicio-chat:8081`).
*   **Token JWT:** El token JWT se almacena en el `localStorage` del navegador.
*   **Persistencia de Mensajes:** Actualmente, los mensajes del chat no son persistentes. Si el `servicio-chat` se reinicia, el historial se perderá.
*   **Configuración de Orígenes (CORS/WebSocket):** Para facilitar el desarrollo, el `servicio-chat` permite conexiones WebSocket desde cualquier origen (`setAllowedOriginPatterns("*")`). En un entorno de producción, esto debería restringirse a los dominios específicos de tu frontend.
*   **Secretos:** El `jwt.secret` en `servicio-usuarios` y cualquier otra credencial sensible deben ser manejados con mayor seguridad en un entorno de producción (ej. variables de entorno de Docker, secretos de Kubernetes).

---
