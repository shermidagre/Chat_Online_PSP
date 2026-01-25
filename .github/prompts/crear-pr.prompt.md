---
agent: 'agent'
description: 'Crea una Pull Request en GitHub a partir de una especificación, utilizando la plantilla del proyecto.'
---
# Crear Pull Request en GitHub desde Especificación

**Objetivo:** Automatizar la creación de una Pull Request (PR) en GitHub, utilizando la plantilla `pr-template.md` del repositorio.

## Contexto del Proyecto

> [!IMPORTANT]
> -   **Instrucción:** Como agente, debes determinar el nombre y la descripción del microservicio afectado analizando los archivos y el contexto del proyecto. El sistema está compuesto por varios microservicios (ej: Servicio de Usuarios, Servicio de Chat). Identifica cuál es el relevante para los cambios actuales.

## Proceso Detallado

1.  **Analizar Plantilla:**
    Lee el archivo de plantilla de PR ubicado en `./.github/pr-template.md` para entender la estructura y los campos requeridos.

2.  **Verificar PR Existente:**
    Antes de crear una nueva PR, comprueba si ya existe una para la rama actual. Si es así, salta al paso 4 para actualizarla.

3.  **Crear Borrador de PR:**
    Crea una nueva Pull Request en modo **borrador** (draft) apuntando a la rama de destino (ej: `main` o `develop`).

4.  **Obtener Cambios:**
    Analiza los cambios (el "diff") incluidos en la PR para obtener el contexto necesario para rellenar la descripción.

5.  **Actualizar Título y Cuerpo:**
    Actualiza la PR con la información obtenida de los cambios y la estructura de la plantilla `pr-template.md`. El título debe ser claro y descriptivo.

6.  **Marcar como "Lista para Revisión":**
    Cambia el estado de la PR de "borrador" a "lista para revisión" (ready for review).

7.  **Asignar Responsable:**
    Asigna la PR al usuario que está ejecutando este proceso.

8.  **Responder con la URL:**
    Finaliza el proceso proporcionando al usuario la URL de la Pull Request creada o actualizada.

## Requisitos

-   Debe crearse una única Pull Request por rama de funcionalidad.
-   El título y la descripción deben ser claros y seguir la plantilla del proyecto.
-   Se debe verificar la existencia de PRs previas para evitar duplicados.
