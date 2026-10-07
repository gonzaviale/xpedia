# Xpedia: backlog del prototipo (resumen)

> Borrador para discutir. Un **PBI** (Product Backlog Item) es cada funcionalidad de la lista de trabajo, con su número en la planilla. Los números de PBI y las horas salen de [backlog.md](backlog.md), la copia del 5/10/2026 de la planilla. Las horas son una primera propuesta.

## La idea

Una app que **te guía para cambiar de rumbo**. Te orienta, te arma una ruta, la recorrés practicando con un profesor de IA y dejás pruebas de lo que sabés. Las rutas tienen un **sello de validación** y una comunidad que las afina. Te avisa lo nuevo del rubro cuando ya te sirve.

## 1. Ya funciona en el prototipo

Se mantiene. Falta pasarlo a un backend real. Hoy usa reglas en lugar de IA y guarda el progreso solo en el navegador.

| Funcionalidad | Qué hace |
|---|---|
| Diagnóstico y ruta personal | 5 preguntas (lo sabía, lo dudé, lo adiviné), una ruta por hitos y la fecha estimada de llegada. Hay una sola ruta, cargada a mano. |
| Ruta que se ajusta | Después de cada práctica agrega repasos, saltea lo que ya sabés, abre ramas y explica el cambio. |
| Sesión corta de práctica | 3 preguntas de opción múltiple con explicación. |
| «Me interesa esto» | Decide si un tema entra ahora, se agenda, va como rama o se guarda. |
| Informe semanal | Avance, lo que cuesta y ajustes para aceptar o rechazar. |
| Radar de novedades | Filtra por la ruta: ahora, más adelante o ruido. Las novedades son ficticias. |
| Racha y XP | Refuerzo de constancia. |

## 2. Primero: el esqueleto con la ruta de oro

Un hito completo, de punta a punta. Son unas **200 h**, algo más de 2 sprints con la capacidad del ejemplo de la plantilla (95 h por sprint).

| Pieza | Qué hace | Tipo | PBI | Horas |
|---|---|---|---|---|
| Ruta de oro escrita a mano | Diagnóstico y un hito con microlección, reto, prueba final y evidencia. Propuesta: atención al cliente remota. | Contenido | Nuevo | sin estimar |
| Ruta en la base de datos | Guardar la ruta y servirla a la app. | Código | 5 | 32 |
| Microlección con fuente | Material breve con la fuente citada. | Código | 11 | 28 |
| Práctica real con devolución de IA | Reto con rúbrica: la persona produce algo y la IA lo evalúa. | Código (IA) | 21 | 32 |
| Profesor de IA | Responde dudas con la fuente del hito y explica los errores. | Código (IA) | 14 | 24 |
| Prueba final de hito | Un solo intento por paso; su resultado cierra el hito. | Código | 24 | 24 |
| Evidencia y portfolio | Cada hito deja evidencia en un portfolio compartible. | Código | 25 | 24 |
| Sello de validación | Estados: borrador de IA, revisada por una persona. | Código | 17 | 20 |
| Cuentas | Guardar el progreso en el servidor. | Código | 1 | 16 |

## 3. Después, en este orden

1. **Orientación:** «de dónde vengo y a qué puedo pasar», con las habilidades transferibles (nuevo, extiende el PBI 4).
2. **Rutas 2 y 3:** la IA las arma como borrador y un docente las revisa (PBI 16 y 17).
3. **Roleplay con IA** para atención al cliente (PBI 19).
4. **Comunidad mínima:** reseñas de rutas (nuevo) y tutor de pares, es decir un compañero avanzado que acompaña (PBI 44).
5. **Noticias reales y newsletter por mail** (PBI 30 y Ap. P6).
6. **Repaso espaciado** de verdad (PBI 12).

## 4. Fuera por ahora

- **Todo lo de empresa:** documentos, matriz de competencias, panel del equipo y puntos clave más fallados.
- **Admin Xpedia.**
- **Conexión con GitHub.**
- **Sumar otra ruta** a una persona.
- **Ensayo con audio.**

## Para decidir

1. ¿Cuántas personas somos y cuántas horas por sprint? Con eso se ordena por sprint.
2. ¿Quién firma una ruta como «revisada»? Propuesta: un docente de la facultad.
3. ¿La ruta de oro es atención al cliente? El prototipo hoy tiene «IA aplicada».
