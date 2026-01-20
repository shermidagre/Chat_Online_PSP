// Usamos API_ROOT definido en tu config.js
const API_CHAT_URL = (typeof API_ROOT !== 'undefined' ? API_ROOT : '') + '/chatbot/chat';

// Referencias de UI
const elements = {
    window: document.getElementById('chatbot-window'),
    toggle: document.getElementById('chatbot-toggle-button'),
    messages: document.getElementById('chatbot-messages'),
    input: document.getElementById('chatbot-input'),
    send: document.getElementById('chatbot-send-button'),
    mic: document.getElementById('mic-button'),
    voiceToggle: document.getElementById('voice-toggle')
};

// --- Diagnóstico Inicial ---
const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
const recognition = SpeechRecognition ? new SpeechRecognition() : null;
const canSpeak = 'speechSynthesis' in window;

if (!recognition) elements.mic.style.display = 'none';

// --- Funciones de Voz ---
function speak(text) {
    if (!canSpeak || !elements.voiceToggle.checked) return;
    window.speechSynthesis.cancel();
    const utterance = new SpeechSynthesisUtterance(text.replace(/\*\*/g, ''));
    utterance.lang = 'es-ES';
    window.speechSynthesis.speak(utterance);
}

if (recognition) {
    recognition.lang = 'es-ES';
    recognition.onstart = () => elements.mic.classList.add('listening');
    recognition.onend = () => elements.mic.classList.remove('listening');
    recognition.onresult = (e) => {
        elements.input.value = e.results[0][0].transcript;
        handleSend();
    };
    elements.mic.onclick = () => recognition.start();
}

// --- Lógica del Chat ---
function append(text, type) {
    const div = document.createElement('div');
    div.className = `message ${type}`;
    div.innerHTML = type === 'bot-message' ? text.replace(/\*\*(.*?)\*\*/g, '<b>$1</b>') : text;
    elements.messages.appendChild(div);
    elements.messages.scrollTop = elements.messages.scrollHeight;
    if (type === 'bot-message') speak(text);
}

async function handleSend() {
    const msg = elements.input.value.trim();
    if (!msg) return;
    append(msg, 'user-message');
    elements.input.value = '';

    try {
        const res = await fetch(API_CHAT_URL, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({ mensaje: msg })
        });
        const data = await res.json();
        append(data.respuesta, 'bot-message');
    } catch (e) {
        append("Error de conexión.", 'bot-message');
    }
}

// Eventos
elements.send.onclick = handleSend;
elements.input.onkeypress = (e) => e.key === 'Enter' && handleSend();
elements.toggle.onclick = () => {
    const show = elements.window.style.display === 'none' || elements.window.style.display === '';
    elements.window.style.display = show ? 'flex' : 'none';
    elements.toggle.textContent = show ? '✖️' : '💬';
};