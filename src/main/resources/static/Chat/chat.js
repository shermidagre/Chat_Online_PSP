document.addEventListener('DOMContentLoaded', () => {
    const chatMessages = document.getElementById('chatMessages');
    const usernameInput = document.getElementById('usernameInput');
    const passwordInput = document.getElementById('passwordInput');
    const loginButton = document.getElementById('loginButton');
    const messageInput = document.getElementById('messageInput');
    const sendButton = document.getElementById('sendButton');
    const statusDiv = document.getElementById('status');

    let ws; // WebSocket
    let isLoggedIn = false;
    const serverAddress = `wss://${window.location.hostname}:8081`; // Usamos wss para SSL/TLS

    // --- Funciones de Utilidad ---

    function appendMessage(message, type = '') {
        const messageElement = document.createElement('div');
        messageElement.classList.add('message');
        if (type) {
            messageElement.classList.add(type);
        }
        messageElement.textContent = message;
        chatMessages.appendChild(messageElement);
        chatMessages.scrollTop = chatMessages.scrollHeight; // Scroll automático al final
    }

    function setStatus(text, isOnline = false) {
        statusDiv.textContent = text;
        statusDiv.className = isOnline ? 'status-online' : 'status-offline';
    }

    function enableChatUI(enable) {
        messageInput.disabled = !enable;
        sendButton.disabled = !enable;
        if (enable) {
            messageInput.focus();
        }
    }

    function enableLoginUI(enable) {
        usernameInput.disabled = !enable;
        passwordInput.disabled = !enable;
        loginButton.disabled = !enable;
        if (enable) {
            usernameInput.focus();
        }
    }

    function sendMessageToServer(type, content) {
        if (ws && ws.readyState === WebSocket.OPEN) {
            const message = {
                type: type,
                sender: usernameInput.value, // El servidor validará esto
                content: content,
                timestamp: new Date().toISOString()
            };
            ws.send(JSON.stringify(message));
        } else {
            appendMessage("No estás conectado al servidor.", "system");
        }
    }

    // --- Lógica WebSocket ---

    function connectWebSocket() {
        if (ws && (ws.readyState === WebSocket.OPEN || ws.readyState === WebSocket.CONNECTING)) {
            ws.close();
        }

        ws = new WebSocket(serverAddress);
        setStatus('Conectando...', false);

        ws.onopen = () => {
            console.log('Conexión WebSocket establecida.');
            setStatus('Conectado. Esperando autenticación...', false);
            enableLoginUI(true);
            enableChatUI(false); // Deshabilitar chat hasta login
        };

        ws.onmessage = (event) => {
            console.log('Mensaje del servidor:', event.data);
            try {
                // El servidor envía mensajes de texto plano para AUTH, etc.
                // Y JSON para mensajes de chat.
                // Intentamos parsear como JSON primero.
                const serverMessage = JSON.parse(event.data);
                // Si es un mensaje de protocolo (del chat), lo mostramos directamente
                appendMessage(`${serverMessage.sender || 'Sistema'}: ${serverMessage.content}`);

            } catch (e) {
                // No es JSON, es un mensaje de control de texto plano.
                const textMessage = event.data;
                handleServerControlMessage(textMessage);
            }
        };

        ws.onclose = () => {
            console.log('Conexión WebSocket cerrada.');
            setStatus('Desconectado', false);
            isLoggedIn = false;
            enableChatUI(false);
            enableLoginUI(true); // Permitir re-login
            appendMessage("Conexión perdida con el servidor.", "system");
        };

        ws.onerror = (error) => {
            console.error('Error WebSocket:', error);
            setStatus('Error de conexión', false);
            appendMessage("Error en la conexión WebSocket. Inténtalo de nuevo.", "system");
            ws.close();
        };
    }

    function handleServerControlMessage(message) {
        if (message.startsWith("AUTH_REQUIRED")) {
            appendMessage("Autenticación requerida. Introduce tus credenciales.", "auth-required");
        } else if (message.startsWith("AUTH_SUCCESS")) {
            appendMessage("Autenticación exitosa. ¡Bienvenido al chat!", "auth-success");
            isLoggedIn = true;
            setStatus(`Online como ${usernameInput.value}`, true);
            enableLoginUI(false);
            enableChatUI(true);
        } else if (message.startsWith("AUTH_FAILED")) {
            appendMessage("Autenticación fallida. Credenciales incorrectas o usuario bloqueado.", "auth-failed");
            passwordInput.value = ''; // Limpiar contraseña
            enableChatUI(false);
            enableLoginUI(true);
        } else if (message.startsWith("AUTH_BLOCKED")) {
            appendMessage("Demasiados intentos fallidos. Tu IP ha sido bloqueada. Conexión cerrada.", "auth-blocked");
            isLoggedIn = false;
            ws.close(); // El servidor debería cerrar la conexión, pero por si acaso.
            enableChatUI(false);
            enableLoginUI(false); // Bloquear UI de login
        } else if (message.startsWith("FORMAT_ERROR")) {
            appendMessage(message.replace("FORMAT_ERROR: ", ""), "format-error");
        } else if (message.startsWith("BYE")) {
            appendMessage("Desconectado por el servidor. Adiós.", "system");
            ws.close();
        } else {
            // Mensajes generales del sistema o notificaciones de texto plano
            appendMessage(message, "system");
        }
    }

    // --- Event Listeners ---

    loginButton.addEventListener('click', () => {
        const username = usernameInput.value.trim();
        const password = passwordInput.value; // No trim en la contraseña
        if (username && password) {
            // El formato de "AUTH" se maneja en el ClientHandler, aquí solo enviamos el string
            sendMessageToServer("AUTH", `${username}:${password}`);
        } else {
            appendMessage("Por favor, introduce usuario y contraseña.", "format-error");
        }
    });

    messageInput.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            sendButton.click();
        }
    });

    sendButton.addEventListener('click', () => {
        const message = messageInput.value.trim();
        if (message) {
            if (message.startsWith('/')) {
                // Es un comando
                sendMessageToServer("COMMAND", message);
            } else {
                // Es un mensaje normal
                sendMessageToServer("MESSAGE", message);
            }
            messageInput.value = '';
        }
    });

    // Iniciar la conexión al cargar la página
    connectWebSocket();
});
