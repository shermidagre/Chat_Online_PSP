# 📋 Auditoría Re-Enfocada: Arquitectura Híbrida (Socket + REST)

## 1. Nueva Arquitectura del Sistema
El proyecto se dividirá conceptualmente en dos capas dentro del mismo backend:

1.  **Capa "Core" (Obligatoria - Niveles 1-5):**
    * **Servidor TCP:** Un `ServerSocket` que escucha en un puerto distinto (ej. 9000).
    * **Gestión de Hilos:** Un hilo por cliente conectado.
    * **Protocolo:** Texto plano (ej. `MSG|Usuario|Texto`) sobre TCP.
    * **Cliente:** Aplicación de escritorio JavaFX.
2.  **Capa "Extra/Soporte" (Tu código actual - Niveles 6-8):**
    * **Persistencia:** Guardar historial de chat y usuarios en BD (ya implementado).
    * **API REST:** Endpoints para consultas externas (ej. obtener historial desde un navegador o app móvil externa).

---

## 2. Análisis del Código Actual (Backend Spring Boot)

### ✅ Lo que se conserva y REUTILIZA
Tu código actual es muy valioso como "backend de persistencia" para el servidor de sockets.

* **`MensajeChat`, `Usuario` (Modelos):** Perfectos. Se usarán para guardar en BD lo que llegue por los sockets.
* **`RepositorioMensajeChat`, `RepositorioUsuario`:** Esenciales. El servidor de sockets los llamará para guardar/validar datos.
* **`ServicioChat`:** Aquí está la clave.
    * El **SocketServer** inyectará este servicio.
    * Cuando llegue un mensaje por TCP, el SocketServer llamará a `servicioChat.sendMessage(...)`.
* **`ConfiguracionSeguridad`:** Útil si quieres proteger la API REST, aunque el Socket TCP necesitará su propia seguridad (SSL Nivel 4).

### ⚠️ Lo que debe ADAPTARSE
* **`ControladorChat`:** Pasa a ser secundario.
    * Ya no es el mecanismo principal de chat en tiempo real.
    * Se queda como cumplimiento del **Nivel 6 (API REST)**.
    * *Ejemplo de uso:* Un admin entra vía web (Swagger) a `/api/chat/usuarios` para ver quién está registrado, mientras los usuarios chatean por la app JavaFX (TCP).

---

## 3. Lo que FALTA (Lista de Tareas Inmediatas)

Para cumplir con el enunciado principal usando tu base actual, necesitamos implementar:

### A. El Motor del Chat (Paquete `com.example.chat.socket`)
Necesitamos crear clases que no existen actualmente:
1.  **`SocketServer`**:
    * Debe arrancar al iniciar Spring (usando `CommandLineRunner`).
    * Abre el puerto 9000.
    * Bucle infinito aceptando clientes.
2.  **`ClientHandler` (Hilo)**:
    * Gestiona la conexión individual de un cliente.
    * Lee el `InputStream` y escribe en el `OutputStream`.
    * Implementa el protocolo de texto (Switch/Case para comandos: `LOGIN`, `MSG`, `LOGOUT`).
3.  **Integración Spring-Socket**:
    * El `SocketServer` debe tener acceso a `ServicioChat` (Autowired) para que, cuando alguien hable por el socket, se guarde en la base de datos H2/Postgres que ya tienes configurada.

### B. El Cliente JavaFX (Nuevo Proyecto o Módulo)
Actualmente tienes `resources/static` (Web), que eliminaremos. Necesitamos una estructura JavaFX:
1.  **`ChatClientApp.java`**: Clase principal `Application` de JavaFX.
2.  **Vistas (`.fxml`)**: Login y Sala de Chat.
3.  **Controladores de Vista**: Lógica de botones y campos de texto.
4.  **Cliente de Red**: Clase que maneja el `Socket` cliente y envía los strings al servidor.

---

## 4. Auditoría de Errores (Manteniendo el código actual)
Aunque el código actual pase a ser el "Extra", debe funcionar bien. Reitero las correcciones necesarias del análisis anterior:

1.  **Seguridad en `ControladorChat`**:
    * El método `enviarMensaje` confía en el `username` del JSON. Esto es inseguro. Aunque sea un "extra", debería validar que el usuario existe o usar un token básico si es posible.
2.  **Eficiencia (N+1)**:
    * En `RepositorioMensajeChat`, la query `findTop20...` debe optimizarse para traer al usuario (`sender`) en la misma consulta y no saturar la BD.

---

## 5. Hoja de Ruta Sugerida

Dado que ya tienes la base de datos y los modelos, el siguiente paso lógico es **construir el Servidor de Sockets sobre Spring Boot**.

**Paso 1:** Crear el paquete `socket` y la clase `ServidorSocket`.
**Paso 2:** Hacer que Spring arranque este servidor en un hilo aparte al iniciar.
**Paso 3:** Implementar el manejo de clientes (`ClientHandler`) y el protocolo de texto plano requerido en el PDF.
**Paso 4:** Crear el cliente JavaFX básico para probar la conexión.