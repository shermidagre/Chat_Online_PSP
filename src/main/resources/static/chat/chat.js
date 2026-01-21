document.addEventListener('DOMContentLoaded', () => {
    const chatMessages = document.getElementById('chatMessages');
    const messageInput = document.getElementById('messageInput');
    const sendMessageBtn = document.getElementById('sendMessageBtn');
    const chatTitle = document.getElementById('chatTitle');

    const username = localStorage.getItem('chatUsername');

    if (!username) {
        alert('No se ha encontrado el nombre de usuario. Por favor, inicia sesión.');
        window.location.href = '/login/login.html'; // Redirigir si no hay usuario
        return;
    }

    chatTitle.textContent = `Chat PSP - Conectado como: ${username}`;

    // Determina el protocolo WebSocket (ws o wss) basado en el protocolo de la página
    const wsProtocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
    const wsUrl = `${wsProtocol}//${window.location.host}/chat`;

    let ws;

    function connectWebSocket() {
        ws = new WebSocket(wsUrl);

        ws.onopen = (event) => {
            console.log('Conexión WebSocket establecida:', event);
            appendMessage('Sistema', `Conectado al chat como ${username}.`, 'system');
            // Enviar el nombre de usuario al servidor como primer mensaje
            ws.send(username);
        };

        ws.onmessage = (event) => {
            console.log('Mensaje recibido:', event.data);
            const messageData = event.data;
            if (messageData.startsWith('SISTEMA:')) {
                appendMessage('Sistema', messageData.substring(8), 'system');
            } else {
                const parts = messageData.split(': ', 2);
                if (parts.length === 2) {
                    const sender = parts[0];
                    const content = parts[1];
                    appendMessage(sender, content, sender === username ? 'sent' : 'received');
                } else {
                    appendMessage('Sistema', messageData, 'system'); // Mensajes mal formateados
                }
            }
            chatMessages.scrollTop = chatMessages.scrollHeight; // Scroll al último mensaje
        };

        ws.onclose = (event) => {
            console.log('Conexión WebSocket cerrada:', event);
            appendMessage('Sistema', 'Conexión perdida. Intentando reconectar en 3 segundos...', 'system');
            setTimeout(connectWebSocket, 3000); // Intenta reconectar después de 3 segundos
        };

        ws.onerror = (error) => {
            console.error('Error WebSocket:', error);
            appendMessage('Sistema', 'Error en la conexión. Consulta la consola.', 'system');
            ws.close(); // Cierra la conexión para intentar reconectar en onclose
        };
    }

    function appendMessage(sender, content, type) {
        const messageElement = document.createElement('div');
        messageElement.classList.add('message');
        messageElement.classList.add(type); // 'system', 'sent', 'received'

        const senderElement = document.createElement('div');
        senderElement.classList.add('message-sender');
        senderElement.textContent = sender;
        messageElement.appendChild(senderElement);

        const contentElement = document.createElement('div');
        contentElement.classList.add('message-content');
        contentElement.textContent = content;
        messageElement.appendChild(contentElement);

        chatMessages.appendChild(messageElement);
    }

    sendMessageBtn.addEventListener('click', () => {
        const message = messageInput.value.trim();
        if (message && ws && ws.readyState === WebSocket.OPEN) {
            ws.send(message);
            messageInput.value = ''; // Limpiar input
        } else if (ws.readyState !== WebSocket.OPEN) {
            appendMessage('Sistema', 'No conectado al servidor. Intenta reconectar.', 'system');
        }
    });

    messageInput.addEventListener('keypress', (event) => {
        if (event.key === 'Enter') {
            sendMessageBtn.click();
        }
    });

    // Iniciar la conexión WebSocket
    connectWebSocket();
});
