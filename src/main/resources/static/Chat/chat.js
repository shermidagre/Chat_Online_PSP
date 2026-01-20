document.addEventListener('DOMContentLoaded', () => {
    const mensajesChat = document.getElementById('mensajesChat'); // Renombrado a mensajesChat
    const entradaUsuario = document.getElementById('entradaUsuario'); // Renombrado a entradaUsuario
    const entradaContrasena = document.getElementById('entradaContrasena'); // Renombrado a entradaContrasena
    const botonAcceso = document.getElementById('botonAcceso'); // Renombrado a botonAcceso
    const entradaMensaje = document.getElementById('entradaMensaje'); // Renombrado a entradaMensaje
    const botonEnviar = document.getElementById('botonEnviar'); // Renombrado a botonEnviar
    const divEstado = document.getElementById('estado'); // Renombrado a divEstado (de 'status')

    let ws; // WebSocket
    let sesionIniciada = false; // Renombrado a sesionIniciada
    const direccionServidor = `wss://${window.location.hostname}:8081`; // Renombrado a direccionServidor

    // --- Funciones de Utilidad ---

    function adjuntarMensaje(mensaje, tipo = '') { // Renombrado a adjuntarMensaje
        const elementoMensaje = document.createElement('div'); // Renombrado a elementoMensaje
        elementoMensaje.classList.add('mensaje'); // Renombrado a 'mensaje'
        if (tipo) {
            elementoMensaje.classList.add(tipo);
        }
        elementoMensaje.textContent = mensaje;
        mensajesChat.appendChild(elementoMensaje);
        mensajesChat.scrollTop = mensajesChat.scrollHeight; // Scroll automático al final
    }

    function establecerEstado(texto, estaConectado = false) { // Renombrado a establecerEstado, estaConectado
        divEstado.textContent = texto;
        divEstado.className = estaConectado ? 'estado-conectado' : 'estado-desconectado'; // Renombrado a 'estado-conectado' y 'estado-desconectado'
    }

    function habilitarInterfazChat(habilitar) { // Renombrado a habilitarInterfazChat, habilitar
        entradaMensaje.disabled = !habilitar;
        botonEnviar.disabled = !habilitar;
        if (habilitar) {
            entradaMensaje.focus();
        }
    }

    function habilitarInterfazLogin(habilitar) { // Renombrado a habilitarInterfazLogin, habilitar
        entradaUsuario.disabled = !habilitar;
        entradaContrasena.disabled = !habilitar;
        botonAcceso.disabled = !habilitar;
        if (habilitar) {
            entradaUsuario.focus();
        }
    }

    function enviarMensajeAlServidor(tipo, contenido) { // Renombrado a enviarMensajeAlServidor, tipo, contenido
        if (ws && ws.readyState === WebSocket.OPEN) {
            const mensaje = { // Renombrado a mensaje
                tipo: tipo, // Renombrado a tipo
                remitente: entradaUsuario.value, // Renombrado a remitente
                contenido: contenido, // Renombrado a contenido
                fechaHora: new Date().toISOString() // Renombrado a fechaHora
            };
            ws.send(JSON.stringify(mensaje));
        } else {
            adjuntarMensaje("No estás conectado al servidor.", "sistema"); // Renombrado a "sistema"
        }
    }

    // --- Lógica WebSocket ---

    function conectarWebSocket() { // Renombrado a conectarWebSocket
        if (ws && (ws.readyState === WebSocket.OPEN || ws.readyState === WebSocket.CONNECTING)) {
            ws.close();
        }

        ws = new WebSocket(direccionServidor);
        establecerEstado('Conectando...', false);

        ws.onopen = () => {
            console.log('Conexión WebSocket establecida.');
            establecerEstado('Conectado. Esperando autenticación...', false);
            habilitarInterfazLogin(true);
            habilitarInterfazChat(false); // Deshabilitar chat hasta login
        };

        ws.onmessage = (evento) => { // Renombrado a evento
            console.log('Mensaje del servidor:', evento.data);
            try {
                // El servidor envía mensajes de texto plano para AUTENTICACION, etc.
                // Y JSON para mensajes de chat.
                // Intentamos parsear como JSON primero.
                const mensajeServidor = JSON.parse(evento.data); // Renombrado a mensajeServidor
                // Si es un mensaje de protocolo (del chat), lo mostramos directamente
                adjuntarMensaje(`${mensajeServidor.remitente || 'Sistema'}: ${mensajeServidor.contenido}`); // Renombrado a remitente, contenido

            } catch (e) {
                // No es JSON, es un mensaje de control de texto plano.
                const mensajeTexto = evento.data; // Renombrado a mensajeTexto
                manejarMensajeControlServidor(mensajeTexto); // Renombrado a manejarMensajeControlServidor
            }
        };

        ws.onclose = () => {
            console.log('Conexión WebSocket cerrada.');
            establecerEstado('Desconectado', false);
            sesionIniciada = false; // Renombrado a sesionIniciada
            habilitarInterfazChat(false);
            habilitarInterfazLogin(true); // Permitir re-login
            adjuntarMensaje("Conexión perdida con el servidor.", "sistema"); // Renombrado a "sistema"
        };

        ws.onerror = (error) => {
            console.error('Error WebSocket:', error);
            establecerEstado('Error de conexión', false);
            adjuntarMensaje("Error en la conexión WebSocket. Inténtalo de nuevo.", "sistema"); // Renombrado a "sistema"
            ws.close();
        };
    }

    function manejarMensajeControlServidor(mensaje) { // Renombrado a manejarMensajeControlServidor
        if (mensaje.startsWith("AUTENTICACION_REQUERIDA")) { // Renombrado a "AUTENTICACION_REQUERIDA"
            adjuntarMensaje("Autenticación requerida. Introduce tus credenciales.", "autenticacion-requerida"); // Renombrado a "autenticacion-requerida"
        } else if (mensaje.startsWith("AUTENTICACION_EXITOSA")) { // Renombrado a "AUTENTICACION_EXITOSA"
            adjuntarMensaje("Autenticación exitosa. ¡Bienvenido al chat!", "autenticacion-exitosa"); // Renombrado a "autenticacion-exitosa"
            sesionIniciada = true; // Renombrado a sesionIniciada
            establecerEstado(`Online como ${entradaUsuario.value}`, true);
            habilitarInterfazLogin(false);
            habilitarInterfazChat(true);
        } else if (mensaje.startsWith("AUTENTICACION_FALLIDA")) { // Renombrado a "AUTENTICACION_FALLIDA"
            adjuntarMensaje("Autenticación fallida. Credenciales incorrectas o usuario bloqueado.", "autenticacion-fallida"); // Renombrado a "autenticacion-fallida"
            entradaContrasena.value = ''; // Limpiar contraseña
            habilitarInterfazChat(false);
            habilitarInterfazLogin(true);
        } else if (mensaje.startsWith("AUTENTICACION_BLOQUEADA")) { // Renombrado a "AUTENTICACION_BLOQUEADA"
            adjuntarMensaje("Demasiados intentos fallidos. Tu IP ha sido bloqueada. Conexión cerrada.", "autenticacion-bloqueada"); // Renombrado a "autenticacion-bloqueada"
            sesionIniciada = false; // Renombrado a sesionIniciada
            ws.close(); // El servidor debería cerrar la conexión, pero por si acaso.
            habilitarInterfazChat(false);
            habilitarInterfazLogin(false); // Bloquear UI de login
        } else if (mensaje.startsWith("ERROR_FORMATO")) { // Renombrado a "ERROR_FORMATO"
            adjuntarMensaje(mensaje.replace("ERROR_FORMATO: ", ""), "error-formato"); // Renombrado a "error-formato"
        } else if (mensaje.startsWith("ADIOS")) { // Renombrado a "ADIOS"
            adjuntarMensaje("Desconectado por el servidor. Adiós.", "sistema"); // Renombrado a "sistema"
            ws.close();
        } else {
            // Mensajes generales del sistema o notificaciones de texto plano
            adjuntarMensaje(mensaje, "sistema"); // Renombrado a "sistema"
        }
    }

    // --- Event Listeners ---

    botonAcceso.addEventListener('click', () => { // Renombrado a botonAcceso
        const nombreUsuario = entradaUsuario.value.trim(); // Renombrado a nombreUsuario
        const contrasena = entradaContrasena.value; // Renombrado a contrasena
        if (nombreUsuario && contrasena) {
            // El formato de "AUTENTICACION" se maneja en el ManejadorCliente, aquí solo enviamos el string
            enviarMensajeAlServidor("AUTENTICACION", `${nombreUsuario}:${contrasena}`); // Renombrado a "AUTENTICACION"
        } else {
            adjuntarMensaje("Por favor, introduce usuario y contraseña.", "error-formato"); // Renombrado a "error-formato"
        }
    });

    entradaMensaje.addEventListener('keypress', (e) => { // Renombrado a entradaMensaje
        if (e.key === 'Enter') {
            botonEnviar.click(); // Renombrado a botonEnviar
        }
    });

    botonEnviar.addEventListener('click', () => { // Renombrado a botonEnviar
        const mensaje = entradaMensaje.value.trim(); // Renombrado a mensaje
        if (mensaje) {
            if (mensaje.startsWith('/')) {
                // Es un comando
                enviarMensajeAlServidor("COMANDO", mensaje); // Renombrado a "COMANDO"
            } else {
                // Es un mensaje normal
                enviarMensajeAlServidor("MENSAJE", mensaje); // Renombrado a "MENSAJE"
            }
            entradaMensaje.value = '';
        }
    });

    // Iniciar la conexión al cargar la página
    conectarWebSocket();
});