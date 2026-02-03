# Directrices para Mensajes de Commit

Sigue estas reglas al escribir mensajes de commit para mantener un historial claro, legible y consistente.

## Formato del Mensaje

Cada mensaje de commit debe tener la siguiente estructura:

```
tipo(ámbito opcional): descripción corta en imperativo

cuerpo opcional más detallado

pie opcional
```

### 1. **Asunto (Línea de Título)**

- **`tipo`**: Debe ser uno de los siguientes:
    - `feat`: Una nueva característica (feature).
    - `fix`: Una corrección de un bug.
    - `docs`: Cambios en la documentación.
    - `style`: Cambios que no afectan el significado del código (espacios, formato, etc.).
    - `refactor`: Un cambio en el código que no corrige un bug ni añade una característica.
    - `perf`: Un cambio que mejora el rendimiento.
    - `test`: Añadir tests o corregir tests existentes.
    - `build`: Cambios que afectan al sistema de compilación o a dependencias externas.
    - `ci`: Cambios en nuestros archivos y scripts de configuración de CI.
    - `chore`: Otros cambios que no modifican el código fuente o los tests.
- **`descripción corta`**:
    - No más de 50 caracteres.
    - Usa el modo imperativo (ej: `Añadir` en vez de `Añadido` o `Añadiendo`).
    - No termines con un punto.

### 2. **Cuerpo (Opcional)**

- Separado del asunto por una línea en blanco.
- Explica el **qué** y el **porqué** del cambio, no el cómo.
- Usa párrafos si es necesario.

### 3. **Pie (Opcional)**

- Separado del cuerpo por una línea en blanco.
- Úsalo para hacer referencia a issues de seguimiento (ej: `Closes #123`).

---

## Ejemplo

```
feat(auth): añadir autenticación con JWT

Se integra Spring Security con JSON Web Tokens para proteger los endpoints
de la API. Esto asegura que solo los usuarios autenticados puedan acceder
a los recursos protegidos.

Closes #42
```

## Idioma

- **Escribe todos los mensajes de commit en español.**
