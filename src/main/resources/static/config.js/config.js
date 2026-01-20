/**
 * config.js
 * Detecta automáticamente si estamos en desarrollo (Live Server) o en Producción (Docker/Nginx)
 */

// Si el puerto es 5500, estás usando Live Server de VS Code -> Usa la ruta completa.
// Si no, estás en Nginx (Docker) -> Usa la ruta relativa.
const IS_DEVELOPMENT = window.location.port === "5500";

// Exportamos la variable global para usarla en todos los archivos
const BASE_URL = IS_DEVELOPMENT ? 'http://localhost:8081/api' : '/api';
const API_ROOT = BASE_URL; // Alias por compatibilidad

console.log(`🌍 Entorno detectado: ${IS_DEVELOPMENT ? 'DESARROLLO (Live Server)' : 'PRODUCCIÓN (Docker/Nginx)'}`);
console.log(`🔗 API apuntando a: ${BASE_URL}`);