document.getElementById('loginForm').addEventListener('submit', async function(event) {
    event.preventDefault(); // Evita el envío tradicional del formulario

    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    const errorMessage = document.getElementById('errorMessage');

    errorMessage.textContent = ''; // Limpiar mensajes de error previos

    try {
        const response = await fetch('/api/conexion', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ nombre: username, password: password })
        });

        const data = await response.json();

        if (data.estado === 'ok') {
            // Guardar el nombre de usuario para usarlo en la página de chat
            localStorage.setItem('chatUsername', username);
            window.location.href = '/chat/index.html'; // Redirigir a la página de chat
        } else {
            errorMessage.textContent = data.mensaje || 'Error de autenticación.';
        }
    } catch (error) {
        console.error('Error durante la conexión:', error);
        errorMessage.textContent = 'No se pudo conectar con el servidor. Inténtalo de nuevo más tarde.';
    }
});
