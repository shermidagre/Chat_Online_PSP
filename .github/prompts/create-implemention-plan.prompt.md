---
agent: 'agent'
description: 'Crea un nuevo archivo de plan de implementación para nuevas características, refactorizaciones, actualizaciones, diseño, arquitectura o infraestructura.'
---
# Crear Plan de Implementación

## Directiva Principal

Tu objetivo es crear un nuevo archivo de plan de implementación para un propósito específico. Tu salida debe ser legible por máquina, determinista y estructurada para su ejecución autónoma por otros sistemas de IA o por humanos.
Usa el idioma **español** para redactar el plan.

## Requisitos Fundamentales

- Genera planes de implementación que sean completamente ejecutables por agentes de IA o humanos.
- Utiliza un lenguaje determinista sin ambigüedad.
- Estructura todo el contenido para el análisis y la ejecución automatizados.
- Asegura una autocontención completa sin dependencias externas para su comprensión.

## Especificaciones del Archivo de Salida

- Guarda los archivos de plan de implementación en el directorio `/plan/`.
- Usa la convención de nomenclatura: `[proposito]-[componente]-[version].md`.
- Prefijos de propósito: `upgrade|refactor|feature|data|infrastructure|process|architecture|design`.
- Ejemplo: `feature-auth-module-1.md`.
- El archivo debe ser un Markdown válido con una estructura de front matter adecuada.

## Estructura de Plantilla Obligatoria

Todos los planes de implementación deben adherirse estrictamente a la siguiente plantilla.

```md
---
goal: '[Título conciso que describe el objetivo del plan]'
version: '[Opcional: ej., 1.0, Fecha]'
date_created: '[YYYY-MM-DD]'
last_updated: '[Opcional: YYYY-MM-DD]'
owner: '[Opcional: Equipo/Individuo responsable]'
status: 'Planned' # Planned | In progress | Completed | Deprecated | On Hold
tags: ['feature', 'upgrade', 'chore', 'architecture', 'migration', 'bug']
---

# Introducción

![Estado: Planned](https://img.shields.io/badge/status-Planned-blue)

[Una breve y concisa introducción al plan y al objetivo que se pretende alcanzar.]

## 1. Requisitos y Restricciones

[Enumera explícitamente todos los requisitos y restricciones que afectan al plan. Usa viñetas o tablas para mayor claridad.]

- **REQ-001**: Requisito 1.
- **CON-001**: Restricción 1.

## 2. Pasos de Implementación

### Fase de Implementación 1: [Nombre de la Fase]

- **META-001**: [Describe el objetivo de esta fase, ej., "Implementar la capa de servicio para X".]

| Tarea | Descripción | Completado |
|-------|-------------|------------|
| TSK-001 | Descripción de la tarea 1. | ☐ |
| TSK-002 | Descripción de la tarea 2. | ☐ |

### Fase de Implementación 2: [Nombre de la Fase]

- **META-002**: [Describe el objetivo de esta fase.]

| Tarea | Descripción | Completado |
|-------|-------------|------------|
| TSK-003 | Descripción de la tarea 3. | ☐ |
| TSK-004 | Descripción de la tarea 4. | ☐ |

## 3. Alternativas

[Una lista de enfoques alternativos que se consideraron y por qué no se eligieron.]

- **ALT-001**: Enfoque alternativo 1.

## 4. Dependencias

[Enumera las dependencias que deben abordarse, como bibliotecas, frameworks u otros componentes.]

- **DEP-001**: Dependencia 1 (ej. otra tarea, una API externa).

## 5. Archivos Afectados

[Enumera los archivos que se verán afectados por la tarea.]

- `src/main/java/com/elvan/service/MiServicio.java`
- `src/main/java/com/elvan/controller/MiControlador.java`

## 6. Pruebas

[Enumera las pruebas que deben implementarse para verificar la tarea.]

- **TEST-001**: Prueba unitaria para `MiServicio`.
- **TEST-002**: Prueba de integración para el endpoint en `MiControlador`.

## 7. Riesgos y Suposiciones

[Enumera los riesgos o suposiciones relacionados con la implementación del plan.]

- **RISK-001**: Riesgo 1 (ej. la API externa puede no estar disponible).
- **ASUM-001**: Suposición 1 (ej. el esquema de la base de datos es estable).

```
