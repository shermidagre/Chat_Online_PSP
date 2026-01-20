// ==========================================
// CONFIGURACIÓN: Usamos ruta relativa para que Nginx lo maneje
// ==========================================


const loginSubmit = document.getElementById("loginSubmit");
const loginError = document.getElementById("loginError");
const usernameInput = document.getElementById("login-username");
const passwordContainer = document.getElementById("password-container");
const passwordInput = document.getElementById("login-password");
const passwordLabel = document.getElementById("password-label");
const recentUsersContainer = document.getElementById('recent-users-container');

// REFERENCIAS NUEVAS PARA EL BUSCADOR
const buscadorInput = document.getElementById('buscador-usuarios');
const listaUsuariosDbContainer = document.getElementById('lista-usuarios-db');

// Estado global
let pasoActual = 1; 
let usuarioDetectado = null;
let todosLosUsuariosDB = []; 

// ==========================================
// 1. CARGA INICIAL
// ==========================================
document.addEventListener('DOMContentLoaded', () => {
    renderizarUsuariosRecientes();
    cargarTodosLosUsuarios(); 
    cargarUsuariosOnline(); // Cargar la lista verde al entrar
    
    usernameInput.focus();
    if(passwordInput) passwordInput.value = "";
});

window.addEventListener('pageshow', (event) => {
    if (passwordInput) passwordInput.value = "";
});

// ==========================================
// 2. LÓGICA DE LOGIN (CLIC EN BOTÓN)
// ==========================================
loginSubmit.addEventListener("click", async () => {
    loginError.textContent = "";

    // --- PASO 1: VERIFICAR USUARIO ---
    if (pasoActual === 1) {
        const username = usernameInput.value.trim();
        if (!username) {
            loginError.textContent = "Por favor, introduce un usuario.";
            return;
        }

        try {
            const response = await fetch(`${API_ROOT}/usuarios/check/${username}`);

            if (response.status === 404) {
                loginError.textContent = "El usuario no existe.";
                return;
            }

            usuarioDetectado = await response.json();

            // Usuario encontrado -> Pasamos al Paso 2
            pasoActual = 2;
            usernameInput.disabled = true; 
            passwordContainer.style.display = "block"; 

            // Personalizamos mensaje
            if (usuarioDetectado.tienePassword) {
                passwordLabel.textContent = "Contraseña";
                passwordLabel.style.color = "#4a5568";
                passwordInput.placeholder = "Introduce tu contraseña";
                loginSubmit.textContent = "Entrar";
            } else {
                passwordLabel.textContent = "Crea una contraseña nueva:";
                passwordLabel.style.color = "#007bff"; 
                passwordInput.placeholder = "Escribe tu nueva clave";
                loginSubmit.textContent = "Guardar y Entrar";
            }
            passwordInput.focus();

        } catch (error) {
            console.error(error);
            loginError.textContent = "Error de conexión con el servidor.";
        }

    // --- PASO 2: ENVIAR LOGIN ---
    } else if (pasoActual === 2) {
        const password = passwordInput.value.trim();
        if (!password) {
            loginError.textContent = "La contraseña no puede estar vacía.";
            return;
        }

        try {
            const loginDTO = {
                idUsuario: usuarioDetectado.idUsuario,
                password: password
            };

            const response = await fetch(`${API_ROOT}/usuarios/login`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(loginDTO)
            });

            if (response.ok) {
                redirigirUsuario(usuarioDetectado);
            } else {
                loginError.textContent = "Contraseña incorrecta. Inténtalo de nuevo.";
            }

        } catch (error) {
            console.error(error);
            loginError.textContent = "Error al intentar iniciar sesión.";
        }
    }
});

// ==========================================
// 3. BUSCADOR DE USUARIOS (DERECHA)
// ==========================================

async function cargarTodosLosUsuarios() {
    try {
        const response = await fetch(`${API_ROOT}/usuarios`); 
        if (!response.ok) throw new Error('Error al cargar lista de usuarios');
        
        const data = await response.json();
        todosLosUsuariosDB = data; 
        renderizarListaUsuarios(todosLosUsuariosDB); 

    } catch (error) {
        console.error("Error cargando usuarios:", error);
        listaUsuariosDbContainer.innerHTML = '<div style="font-size:12px; color:#e53e3e;">Error cargando lista</div>';
    }
}

buscadorInput.addEventListener('input', (e) => {
    const texto = e.target.value.toLowerCase();
    const usuariosFiltrados = todosLosUsuariosDB.filter(u => 
        u.nombre.toLowerCase().includes(texto)
    );
    renderizarListaUsuarios(usuariosFiltrados);
});

function renderizarListaUsuarios(lista) {
    listaUsuariosDbContainer.innerHTML = '';

    if (lista.length === 0) {
        listaUsuariosDbContainer.innerHTML = '<div style="padding:10px; font-size:12px; color:#a0aec0;">No hay coincidencias</div>';
        return;
    }

    lista.forEach(usuario => {
        const iniciales = usuario.nombre.split(' ').map(n => n[0]).slice(0, 2).join('').toUpperCase();
        const div = document.createElement('div');
        div.className = 'db-user-chip';
        div.onclick = () => seleccionarUsuarioGenerico(usuario.nombre); 
        
        div.innerHTML = `
            <div class="user-avatar" style="background: linear-gradient(135deg, #10b981, #3b82f6);">${iniciales}</div>
            <div class="user-name-text">${usuario.nombre}</div>
        `;
        listaUsuariosDbContainer.appendChild(div);
    });
}

function seleccionarUsuarioGenerico(nombre) {
    usernameInput.value = nombre;
    passwordInput.value = ""; 
    loginSubmit.click();
}

// ==========================================
// 4. FUNCIONES AUXILIARES Y RECIENTES
// ==========================================

function redirigirUsuario(usuario) {
    guardarEnRecientes(usuario.nombre);
    localStorage.setItem('username', usuario.nombre);
    localStorage.setItem('usuarioId', usuario.idUsuario);
    localStorage.setItem('userRole', usuario.rol);

    if (usuario.rol === "TECNICO") {
        window.location.href = '../SeleccionarObra/SeleccionarObra.html';
    } else if (usuario.rol === "ADMINISTRADOR") {
        window.location.href = '../SeleccionAdministracion/SeleccionAdministrador.html';
    } else {
        loginError.textContent = "Rol desconocido: " + usuario.rol;
    }
}

function renderizarUsuariosRecientes() {
    const recientes = JSON.parse(localStorage.getItem('recentUsers')) || [];
    if (recientes.length === 0) {
        recentUsersContainer.innerHTML = ''; 
        return; 
    }

    let html = '<div class="recent-title">Recientes</div>';
    recientes.forEach(nombre => {
        const iniciales = nombre.split(' ').map(n => n[0]).slice(0, 2).join('').toUpperCase();
        html += `
        <div class="recent-user-chip" onclick="seleccionarUsuarioGenerico('${nombre}')">
            <div class="user-avatar">${iniciales}</div>
            <div class="user-name-text">${nombre}</div>
            <span class="delete-recent-btn" onclick="eliminarDeRecientes(event, '${nombre}')" title="Eliminar">×</span>
        </div>
        `;
    });
    recentUsersContainer.innerHTML = html;
}

window.eliminarDeRecientes = function(event, nombreUsuario) {
    event.stopPropagation();
    let recientes = JSON.parse(localStorage.getItem('recentUsers')) || [];
    recientes = recientes.filter(u => u !== nombreUsuario);
    localStorage.setItem('recentUsers', JSON.stringify(recientes));
    renderizarUsuariosRecientes();
};

function guardarEnRecientes(nombreUsuario) {
    let recientes = JSON.parse(localStorage.getItem('recentUsers')) || [];
    recientes = recientes.filter(u => u !== nombreUsuario); 
    recientes.unshift(nombreUsuario); 
    if (recientes.length > 5) recientes.pop(); 
    localStorage.setItem('recentUsers', JSON.stringify(recientes));
}

document.addEventListener('keypress', function (e) {
    if (e.key === 'Enter') loginSubmit.click();
});

// LÓGICA DEL OJO (VER/OCULTAR)
const togglePasswordBtn = document.getElementById('togglePassword');
const eyeOpen = document.getElementById('eye-open');
const eyeClosed = document.getElementById('eye-closed');

if(togglePasswordBtn){
    togglePasswordBtn.addEventListener('click', function () {
        const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
        passwordInput.setAttribute('type', type);
        if (type === 'text') {
            eyeOpen.style.display = 'none';
            eyeClosed.style.display = 'block';
        } else {
            eyeOpen.style.display = 'block';
            eyeClosed.style.display = 'none';
        }
    });
}

// ==========================================
// 5. SISTEMA DE USUARIOS ONLINE (SOLO VISOR)
// ==========================================
const contenedorOnline = document.getElementById('contenedor-usuarios-online');

function cargarUsuariosOnline() {
    if(!contenedorOnline) return;

    fetch(`${API_ROOT}/usuarios/online`)
        .then(response => response.json())
        .then(listaNombres => {
            renderizarOnline(listaNombres);
        })
        .catch(err => console.error("Error cargando online:", err));
}

function renderizarOnline(lista) {
    contenedorOnline.innerHTML = '';
    
    if (lista.length === 0) {
        contenedorOnline.innerHTML = '<span style="font-size:12px; color:#aaa;">Nadie conectado ahora</span>';
        return;
    }

    lista.forEach(nombre => {
        const div = document.createElement('div');
        // Usamos tus estilos inline para no obligarte a tocar el CSS
        div.style.cssText = `
            background-color: #d1fae5; 
            color: #065f46; 
            padding: 4px 8px; 
            border-radius: 12px; 
            font-size: 11px; 
            border: 1px solid #10b981;
            font-weight: bold;
            display: flex;
            align-items: center;
            gap: 5px;
            margin-bottom: 5px;
        `;
        div.innerHTML = `<span style="width:8px; height:8px; background: #10b981; border-radius:50%; display:inline-block;"></span> ${nombre}`;
        contenedorOnline.appendChild(div);
    });
}

// Recargar lista cada 10 segundos
setInterval(cargarUsuariosOnline, 10000);

