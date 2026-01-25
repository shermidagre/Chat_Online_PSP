---
description: 'Mejores prácticas y convenciones para scripts de shell (bash, sh, zsh, etc.)'
applyTo: '**/*.sh'
---

# Directrices para Scripts de Shell

Instrucciones para escribir scripts de shell limpios, seguros y mantenibles para bash, sh, zsh y otros shells.

## Principios Generales

- Genera código que sea limpio, simple y conciso.
- Asegúrate de que los scripts sean fáciles de leer y entender.
- Añade comentarios donde sean útiles para comprender cómo funciona el script.
- Genera salidas `echo` concisas y simples para proporcionar el estado de la ejecución.
- Evita salidas `echo` innecesarias y el exceso de registros (logging).
- Utiliza `shellcheck` para análisis estático cuando esté disponible.
- Asume que los scripts son para automatización y pruebas en lugar de sistemas de producción, a menos que se especifique lo contrario.
- Prefiere expansiones seguras: usa comillas dobles en las referencias a variables (`"$var"`), utiliza `${var}` para mayor claridad y evita `eval`.
- Utiliza características modernas de Bash (`[[ ]]`, `local`, arrays) cuando los requisitos de portabilidad lo permitan; recurre a construcciones POSIX solo cuando sea necesario.
- Elige analizadores (parsers) fiables para datos estructurados en lugar de procesar texto de forma improvisada.

## Manejo de Errores y Seguridad

- Activa siempre `set -euo pipefail` para fallar rápidamente ante errores, detectar variables no definidas y mostrar fallos en pipelines.
- Valida todos los parámetros requeridos antes de la ejecución.
- Proporciona mensajes de error claros con contexto.
- Usa `trap` para limpiar recursos temporales o manejar salidas inesperadas cuando el script termina.
- Declara valores inmutables con `readonly` (o `declare -r`) para prevenir reasignaciones accidentales.
- Utiliza `mktemp` para crear archivos o directorios temporales de forma segura y asegúrate de que se eliminen en tu manejador de limpieza.

## Estructura del Script

- Comienza con un shebang claro: `#!/bin/bash` a menos que se especifique lo contrario.
- Incluye un comentario de cabecera que explique el propósito del script.
- Define los valores predeterminados para todas las variables al principio.
- Utiliza funciones para bloques de código reutilizables.
- Crea funciones reutilizables en lugar de repetir bloques de código similares.
- Mantén el flujo de ejecución principal limpio y legible.

## Trabajar con JSON y YAML

- Prefiere analizadores dedicados (`jq` para JSON, `yq` para YAML) en lugar de procesar texto con `grep`, `awk` o división de cadenas en el shell.
- Cuando `jq`/`yq` no estén disponibles o no sean apropiados, elige el siguiente analizador más fiable disponible en tu entorno y sé explícito sobre cómo debe usarse de forma segura.
- Valida que los campos requeridos existan y maneja rutas de datos faltantes/inválidas explícitamente.
- Usa comillas en los filtros de `jq`/`yq` para prevenir la expansión del shell y prefiere `--raw-output` cuando necesites cadenas de texto plano.
- Trata los errores del analizador como fatales: combínalos con `set -euo pipefail` o comprueba el éxito del comando antes de usar los resultados.
- Documenta las dependencias de analizadores al principio del script y falla rápidamente con un mensaje útil si `jq`/`yq` son necesarios pero no están instalados.

```bash
#!/bin/bash

# ============================================================================
# Descripción del Script Aquí
# ============================================================================

set -euo pipefail

# Función de limpieza que se ejecuta al salir del script
cleanup() {
    # Elimina recursos temporales u otros pasos de limpieza necesarios
    if [[ -n "${TEMP_DIR:-}" && -d "$TEMP_DIR" ]]; then
        rm -rf "$TEMP_DIR"
    fi
}

trap cleanup EXIT

# Valores predeterminados
RESOURCE_GROUP=""
REQUIRED_PARAM=""
OPTIONAL_PARAM="default-value"
readonly SCRIPT_NAME="$(basename "$0")"

TEMP_DIR=""

# Funciones
usage() {
    echo "Uso: $SCRIPT_NAME [OPCIONES]"
    echo "Opciones:"
    echo "  -g, --resource-group   Grupo de recursos (requerido)"
    echo "  -h, --help             Muestra esta ayuda"
    exit 0
}

validate_requirements() {
    if [[ -z "$RESOURCE_GROUP" ]]; then
        echo "Error: El grupo de recursos es requerido" >&2
        exit 1
    fi
}

main() {
    validate_requirements

    TEMP_DIR="$(mktemp -d)"
    if [[ ! -d "$TEMP_DIR" ]]; then
        echo "Error: no se pudo crear el directorio temporal" >&2
        exit 1
    fi
    
    echo "============================================================================"
    echo "Inicio de la Ejecución del Script"
    echo "============================================================================"
    
    # Lógica principal aquí
    
    echo "============================================================================"
    echo "Ejecución del Script Completada"
    echo "============================================================================"
}

# Analizar argumentos
while [[ $# -gt 0 ]]; do
    case $1 in
        -g|--resource-group)
            RESOURCE_GROUP="$2"
            shift 2
            ;;
        -h|--help)
            usage
            ;;
        *)
            echo "Opción desconocida: $1" >&2
            exit 1
            ;;
    esac
done

# Ejecutar función principal
main "$@"

```
