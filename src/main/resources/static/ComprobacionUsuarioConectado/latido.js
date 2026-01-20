
// ==========================================
// LATIDO: Avisar al servidor que estoy trabajando
// ==========================================
(function () {
    // Al estar tras Nginx, usamos ruta relativa
    const usuarioLogueado = localStorage.getItem('username');

    if (usuarioLogueado) {
        const enviarSenal = () => {
            fetch(`${API_ROOT}/usuarios/latido/${usuarioLogueado}`, { method: 'POST' })
                .catch(e => console.error("Error latido", e));
        };

        // 1. Enviar señal nada más cargar la página
        enviarSenal();

        // 2. Repetir cada 60 segundos
        setInterval(enviarSenal, 60000);

        console.log("Sistema de presencia activado para: " + usuarioLogueado);
    }
})();
