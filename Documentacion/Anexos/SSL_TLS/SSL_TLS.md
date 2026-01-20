# Anexo: Configuración SSL/TLS

Este anexo detalla la configuración y el uso de los protocolos SSL/TLS (Secure Sockets Layer / Transport Layer Security) para asegurar las comunicaciones en el "Chat Corporativo Seguro".

## 1. Propósito

La implementación de SSL/TLS tiene como objetivo principal:
*   **Confidencialidad:** Cifrar los datos transmitidos entre el cliente y el servidor, impidiendo que terceros puedan interceptar y leer la información sensible (mensajes de chat, credenciales).
*   **Integridad:** Garantizar que los datos no han sido alterados durante la transmisión.
*   **Autenticación del Servidor:** Permitir que el cliente verifique la identidad del servidor, asegurando que se está comunicando con el servidor legítimo.

## 2. Implementación en el Servidor de Chat (Java)

El `ServidorChat` (implementado en Java con Spring Boot) utiliza `SSLServerSocket` y `SSLSocket` para establecer conexiones seguras.

### Archivo `keystore.jks`

*   **Ubicación:** `src/main/resources/keystore.jks`
*   **Función:** Este archivo es un almacén de claves (KeyStore) que contiene el certificado digital del servidor (en este caso, un certificado autofirmado) y su clave privada asociada. El certificado es fundamental para la identidad del servidor y para la criptografía de clave pública utilizada en el establecimiento de la conexión SSL/TLS.
*   **Generación:** Se genera típicamente usando la herramienta `keytool` de Java. Un ejemplo de comando para su generación es:
    ```bash
    keytool -genkeypair -alias chatserver -keyalg RSA -keysize 2048 -storetype JKS -keystore src/main/resources/keystore.jks -storepass password -keypass password -dname "CN=localhost, OU=IT, O=Example, L=Anytown, ST=CA, C=US" -validity 365
    ```
    *   **Nota de Seguridad:** Para entornos de producción, se recomienda encarecidamente obtener un certificado emitido por una Autoridad de Certificación (CA) de confianza, en lugar de uno autofirmado.

### Configuración en `ServidorChat.java`

Las propiedades del almacén de claves se establecen mediante variables de sistema de Java antes de iniciar el `SSLServerSocket`:

```java
// Configuración SSL/TLS
System.setProperty("javax.net.ssl.keyStore", "src/main/resources/keystore.jks");
System.setProperty("javax.net.ssl.keyStorePassword", "password");
SSLServerSocketFactory ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
socketServidor = (SSLServerSocket) ssf.createServerSocket(PUERTO);
```

*   `javax.net.ssl.keyStore`: Ruta al archivo `keystore.jks`.
*   `javax.net.ssl.keyStorePassword`: Contraseña para acceder al almacén de claves.

## 3. Conexión desde el Cliente Web (JavaScript)

El cliente web, implementado en JavaScript (`chat.js`), se conecta al servidor de chat utilizando el protocolo WebSocket seguro (`wss://`).

### URL de Conexión

La URL del servidor de chat se construye utilizando `wss://` para indicar una conexión segura:

```javascript
const direccionServidor = `wss://${window.location.hostname}:8081`;
ws = new WebSocket(direccionServidor);
```

*   **`wss://`**: Es el esquema de URL para WebSockets sobre SSL/TLS. Indica que la conexión debe ser cifrada.
*   **Puerto:** El servidor de chat escucha en el puerto `8081`.

### Manejo de Certificados

Dado que se utiliza un certificado autofirmado, es posible que los navegadores web muestren advertencias de seguridad al intentar conectarse al servidor. Para fines de desarrollo local o prueba, el navegador puede solicitar al usuario que acepte el certificado como de confianza. En un entorno de producción, un certificado de una CA reconocida evitaría estas advertencias.

## 4. Auditoría de Seguridad

Además del cifrado, el proyecto incorpora un `RegistradorSeguridad` que registra eventos relacionados con la seguridad, como intentos de login (exitosos o fallidos), comandos de administrador y cambios de estado de usuario, en un archivo `security.log`. Esto proporciona una traza de auditoría para monitorear y analizar actividades relevantes.
