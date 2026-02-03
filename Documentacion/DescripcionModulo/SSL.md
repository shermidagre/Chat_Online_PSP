# Chat Corporativo Seguro (PSP Práctica 02)

Este proyecto implementa un sistema de chat cliente-servidor robusto, seguro y persistente, desarrollado en Java. Cumple con los requisitos de la asignatura Programación de Servicios y Procesos, incluyendo cifrado SSL, autenticación segura y una interfaz gráfica responsiva.

## 🚀 Características Implementadas

### Nivel Base
- [x] **Arquitectura Cliente-Servidor:** Comunicación mediante Sockets TCP.
- [x] **Protocolo de Texto:** Formato propio (`COMANDO|ARGUMENTOS`) sin serialización de objetos.
- [x] **Persistencia:** Base de datos PostgreSQL alojada en Supabase.
- [x] **Robustez:** Gestión de desconexiones inesperadas y errores de entrada.

### Niveles Avanzados
- [x] **Nivel 4 (Seguridad SSL/TLS):** Comunicación encriptada mediante `SSLSocket` y `SSLServerSocket` usando certificados autofirmados (JKS).
- [x] **Nivel 5 (Interfaz Gráfica):** Cliente desarrollado en **JavaFX** con gestión de hilos (`Platform.runLater`) para no bloquear la UI.
- [x] **Login Seguro:** Autenticación contra base de datos con contraseñas cifradas (BCrypt).
- [x] **Auditoría (Log):** Registro de eventos de seguridad (Login, Kicks) en `security.log` y consola.
- [x] **Comandos Admin:** Funcionalidad `/kick` y `/list` implementadas.

---

## 🛠️ Requisitos Previos

* Java JDK 17 o superior.
* Maven.
* Conexión a Internet (para conectar a la BD Supabase).
* Archivo `chat_keystore.jks` en la raíz (generado con keytool).

---

## ⚙️ Configuración e Instalación

1.  **Clonar el repositorio:**
    ```bash
    git clone <url-del-repositorio>
    ```

2.  **Generar Certificado SSL (Si no existe):**
    Ejecutar en la raíz del proyecto:
    ```bash
    keytool -genkey -alias chatserver -keyalg RSA -keystore chat_keystore.jks -storepass 123456 -validity 365
    ```

3.  **Compilar:**
    ```bash
    mvn clean install
    ```

---

## ▶️ Ejecución

### 1. Iniciar el Servidor
El servidor iniciará Spring Boot y abrirá el puerto SSL 9000.

```bash
mvn spring-boot:run

```env

PS C:\Users\samue\Documents\Dam2\Chatpsp\Chat_Online_PSP> & "C:\Users\samue\.jdks\liberica-full-17.0.18\bin\keytool.exe" -genkey -alias chatserver -keyalg RSA -keystore chat_keystore.jks -storepass 123456 -validity 365                                                                              
¿Cuáles son su nombre y su apellido?
  [Unknown]:  samu
¿Cuál es el nombre de su unidad de organización?
  [Unknown]:  yokse
¿Cuál es el nombre de su organización?
  [Unknown]:  eldiablo
¿Cuál es el nombre de su ciudad o localidad?
  [Unknown]:   lo se
¿Cuál es el nombre de su estado o provincia?
  [Unknown]:  no lo se
¿Cuál es el código de país de dos letras de la unidad?
  [Unknown]:  es
¿Es correcto CN=samu, OU=yokse, O=eldiablo, L=" lo se", ST=no lo se, C=es?
  [no]:  si 

Generando par de claves RSA de 2.048 bits para certificado autofirmado (SHA256withRSA) con una validez de 365 días
        para: CN=samu, OU=yokse, O=eldiablo, L=" lo se", ST=no lo se, C=es
```