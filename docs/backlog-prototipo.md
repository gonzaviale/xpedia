# Xpedia — Backlog del prototipo (borrador v0)

Este documento ordena las funcionalidades de Xpedia **desde el punto de vista del prototipo**: qué ya funciona en la rama `feat/prototipo` (commit `2a8bd71`), qué está simulado o a medias y qué falta. Hace con el prototipo lo mismo que [backlog-funcionalidades.md](backlog-funcionalidades.md) hizo con la idea principal.

> **Estado: borrador para discutir en equipo.** Las prioridades y las decisiones del final son propuestas, no acuerdos.
>
> **Fuente de las funcionalidades.** La planilla del Product Backlog no se pudo abrir al armar este borrador. Se usó [backlog.md](backlog.md), su copia del 5 de octubre de 2026. La columna «PBI» remite a los números de esa planilla y «Ap.» remite a las propuestas P1 a P6 del apéndice de `backlog.md`. Si la planilla cambió desde esa fecha, hay que actualizar este archivo.

## La idea, pasada en limpio

Xpedia es una app que **acompaña a una persona (o a un equipo) en su aprendizaje hacia una meta concreta**. Tiene cinco partes:

- **Guía.** Una hoja de ruta con hitos que se ajusta según cómo le va a la persona.
- **Aprender y practicar.** Sesiones cortas y una práctica propia de cada rubro, no solo preguntas.
- **Profesor.** Un profesor de IA siempre disponible y, en las empresas, un tutor humano asignado.
- **Noticias.** Avisa lo nuevo del rubro cuando ya le sirve a esa persona, no apenas sale.
- **Prueba.** Cada hito deja evidencia en un portfolio compartible.

Las empresas suman sus propios documentos y arman la ruta junto con Xpedia, con acompañamiento de un tutor.

## Cómo leer las tablas

**Estado hoy** (lo que hay en el prototipo):

- **Funciona:** tiene lógica real. Donde entraría un modelo de IA, hoy hay reglas, y la interfaz lo marca con la etiqueta «IA».
- **Parcial:** existe una parte, o está simulado o fijo.
- **No está.**

**Prioridad** (para el prototipo, con el mismo criterio que el backlog v1):

- **Alta:** sin esto, la demo no muestra la idea.
- **Media:** mejora sensiblemente la demo, pero se entiende sin esto.
- **Baja:** complementaria, o ya cubierta por un mockup.

## Resumen

| | Funciona | Parcial | No está | Total |
|---|---:|---:|---:|---:|
| Prioridad alta | 5 | 3 | 6 | 14 |
| Prioridad media | 4 | 2 | 11 | 17 |
| Prioridad baja | 1 | 0 | 3 | 4 |
| **Total** | **10** | **5** | **20** | **35** |

Lo que ya funciona es el **motor** (ruta, diagnóstico, ajuste, novedades, informe). Lo que falta, casi todo de prioridad alta, es lo que la idea promete como diferencial: **práctica real, prueba, profesor y empresa**.

---

## 1. Guía: la ruta

| Funcionalidad | Descripción | Estado hoy | Prioridad | PBI |
|---|---|---|---|---|
| Objetivo y diagnóstico inicial | La persona cuenta a dónde quiere llegar y responde 5 preguntas. En cada una marca si la sabía, la dudó o la adivinó, y lo adivinado cuenta como no sabido. Al final se resume qué se saltea, dónde arranca y cuándo llega. | Funciona | Alta | 3, 4 |
| Armado de la ruta desde el objetivo | La IA arma la ruta a medida de lo que la persona escribió. Hoy hay una sola ruta escrita a mano («IA aplicada») y el objetivo escrito no la cambia. | Parcial | Alta | 5 |
| Vista de la ruta | Muestra los hitos, el estado de cada tema, las bifurcaciones y las ramas opcionales. Al tocar un tema se ve qué necesita y qué desbloquea. Hay una vista de camino y otra de grafo completo, y sirve también como árbol de habilidades. | Funciona | Alta | 6, 26, Ap. P1 |
| Ajuste automático de la ruta | Después de cada práctica agrega repasos, propone saltear lo que ya domina, reordena y abre ramas. Detecta patrones (adivinar, fallar siempre un tipo de pregunta, ir lento, temas oxidados) y explica cada cambio. | Funciona | Alta | 7, Ap. P4 |
| Ritmo diario y llegada estimada | La persona elige cuántos minutos por día practica y ve en cuánto tiempo llegaría. | Funciona | Media | 8 |
| «Me interesa esto» | La persona escribe un tema o lo toca desde una novedad. Xpedia compara sus prerrequisitos con el perfil y decide si entra ahora, se agenda, queda como rama opcional o se guarda para más adelante. Hoy busca por palabras clave en un catálogo chico. | Funciona | Media | Ap. P2, P3 |
| Informe semanal con ajustes | Resume la semana (sesiones, aciertos, lo que cuesta, novedades) y propone ajustes que la persona acepta o rechaza. Se lee en la app y se puede copiar como texto. | Funciona | Media | Ap. P6 |

## 2. Aprender y practicar

| Funcionalidad | Descripción | Estado hoy | Prioridad | PBI |
|---|---|---|---|---|
| Sesión corta de práctica | Sesión de 3 preguntas (concepto, aplicación y detalle), con explicación de cada respuesta y un resumen final de cómo cambió la ruta. | Funciona | Alta | 10 |
| Microlección antes de practicar | Material breve, a nivel de la persona y con la fuente de cada dato, justo antes de la práctica. Hoy hay un resumen por tema, pero solo se ve en el detalle de la ruta y no cita fuente ni licencia. | Parcial | Alta | 11 |
| Roleplay con IA por chat | Conversación con un personaje simulado (un cliente enojado, un entrevistador) con una escala de enojo que sube o baja según la respuesta, y feedback al final. Es la práctica estrella de atención al cliente. | No está | Alta | 19 |
| Reto con producción propia y rúbrica | La persona produce algo (en la ruta actual, un prompt; en sistemas, el arreglo de un ticket de bug) y se evalúa con una rúbrica. Es el paso de «elegir una opción» a «hacer». | No está | Alta | 21, Ap. P5 |
| Escenarios con decisiones | Situaciones con consecuencias que cambian según lo que se elige. Incluye el modo validación (un solo intento). | No está | Media | 18 |
| Repaso | Se agregan repasos cuando la persona falla un tema o cuando se oxida. Hoy sigue una regla simple y no tiene intervalos espaciados. | Parcial | Media | 12 |
| Ensayo con audio y devolución | Grabar una presentación y recibir feedback de estructura, ritmo y pausas. Es la práctica estrella de oratoria y la más cara de construir. | No está | Baja | 20 |
| Lecturas externas con caso corto | Un libro o una charla entran a la ruta como lectura y, al volver, la app toma un caso de 2 minutos. | No está | Baja | 13 |
| Desafío del mundo real con check-in | Aplicar lo aprendido en el trabajo (por ejemplo, presentar el lunes) y contar cómo salió. | No está | Baja | 23 |

## 3. Prueba y portfolio

| Funcionalidad | Descripción | Estado hoy | Prioridad | PBI |
|---|---|---|---|---|
| Prueba final de hito | Al completar un hito se habilita una prueba en modo validación (un solo intento por paso) cuyo resultado cierra el hito. | No está | Alta | 24 |
| Evidencia de hito y portfolio | Cada hito deja evidencia (lo que se hizo y cómo salió) en un portfolio con link para compartir. Hoy cada hito declara qué evidencia dejaría, pero no se genera ni se guarda nada. | No está | Alta | 25 |
| Historial de práctica con feedback | Lista de prácticas anteriores con su resultado y qué mejorar. Hoy existe un historial de ajustes de la ruta, pero no de prácticas. | No está | Media | 27 |
| Racha y XP | XP por práctica y días seguidos practicando, visibles en la barra superior. Son un refuerzo, no el motivo para quedarse. | Funciona | Baja | 28, 29 |

## 4. Profesor: IA o tutor

| Funcionalidad | Descripción | Estado hoy | Prioridad | PBI |
|---|---|---|---|---|
| Profesor de IA | La persona le consulta dudas sobre su ruta y sus temas, y la respuesta cita la fuente del hito. Está siempre disponible, con un límite diario para controlar el costo. | No está | Alta | 14 |
| Devolución del profesor sobre la práctica | Después de practicar, explica por qué se falló y qué mirar, en lugar de mostrar solo «no era esa». Hoy hay una explicación fija por pregunta. | Parcial | Alta | 14, 19, 21 |
| Tutor humano asignado | Una persona (puesta por la empresa o calificada por la comunidad) acompaña al alumno: ve su avance, deja comentarios y responde dudas. | No está | Alta | 44 |
| Pedir ayuda al tutor | Desde un tema en el que se trabó, la persona le manda la duda al tutor. El profesor de IA puede derivarla cuando no alcanza. | No está | Media | Nuevo |
| Vista del tutor | Panel con sus alumnos, en qué temas están trabados y qué devoluciones tiene pendientes. | No está | Media | Nuevo |

## 5. Noticias

| Funcionalidad | Descripción | Estado hoy | Prioridad | PBI |
|---|---|---|---|---|
| Radar de novedades filtradas por ruta | Cada novedad se clasifica en «para vos ahora», «más adelante» o ruido, con el motivo. Las que todavía no sirven se guardan hasta que la persona llegue. Las novedades de la demo son ficticias. | Funciona | Alta | 30, 31 |
| Sumar o descartar una novedad | Desde una novedad la persona puede tocar «Me interesa» para que entre a la ruta o descartarla. Hoy no se puede deshacer. | Funciona | Media | 32 |
| Newsletter por correo | El informe semanal y las novedades que importan llegan por mail, incluso en las semanas en que la persona no estudió. Hoy el informe se ve y se copia en la app, pero no se envía. | Parcial | Media | Ap. P6 |
| Novedades reales | Búsqueda periódica de novedades del rubro, filtradas por la ruta de cada persona. | No está | Media | 30 |

## 6. Empresa

El flujo de empresa ya está recorrible como **mockup v16** (matriz, documentos, armado conjunto de la ruta, equipo, tutor, puntos clave más fallados y privacidad). En el prototipo no hay nada de esto con lógica real.

| Funcionalidad | Descripción | Estado hoy | Prioridad | PBI |
|---|---|---|---|---|
| Documentos y puntos clave con trazabilidad | La empresa carga manuales y protocolos, y ve los puntos clave que identificó Xpedia con la página de origen. | No está | Media | 35 |
| Armado de la ruta en conjunto | A partir de la matriz de competencias y los documentos, Xpedia propone los hitos y la empresa marca los que ajustar y confirma. | No está | Media | 34, 36 |
| Panel del equipo y evidencia por persona | Estado de los hitos y resultado de la prueba final de cada persona, con evidencia por rol y por punto clave. | No está | Media | 40, 41 |
| Asignación de rutas y de tutor al equipo | La empresa asigna rutas a personas o roles y elige el tutor de su equipo. | No está | Media | 39, 44 |
| Puntos clave más fallados | Muestra qué puntos clave falla más el equipo, para detectar dónde falla el proceso. | No está | Media | 42 |
| Privacidad y datos agregados | La empresa no ve ni escucha las prácticas y solo ve niveles agregados de grupos de 5 o más. | No está | Media | 43 |

---

## Lo que queda fuera del prototipo

Son funcionalidades de la planilla que no tiene sentido construir en un prototipo. Se mantienen en la planilla para después.

| PBI | Por qué queda afuera |
|---|---|
| 1, 2 · Cuentas y vistas por versión | El prototipo guarda el progreso en el navegador y no tiene inicio de sesión. |
| 46 a 51 · Admin Xpedia | Es gestión interna de la plataforma. El backlog v1 ya la dejaba fuera de lo que se presenta. |
| 15 a 17 · Biblioteca de fuentes, caché y revisión humana del material | Es trastienda. En el prototipo solo se ve el resultado (la fuente citada en la microlección). |
| 22 · Conexión con GitHub | Solo sirve a la ruta de sistemas y depende de un tercero. El reto con rúbrica cubre la misma idea. |
| 9 · Sumar otra ruta | Ya es de prioridad 3 en la planilla. |
| 45 · Avisos automáticos | Los cubre el newsletter y los recordatorios. |

## Cambios de prioridad que propongo en la planilla

La idea pasada en limpio pone al profesor y a las noticias como partes centrales, pero en la planilla están más abajo.

| PBI | Hoy | Propuesta | Motivo |
|---|---|---|---|
| 44 · Tutor de acompañamiento | 3 | 1 | Es una de las cinco partes de la idea. La planilla lo deja como decisión abierta. |
| 14 · Coach de IA | 2 | 1 | Es el profesor de IA. Sin él, la persona practica sola. |
| 30 · Novedades filtradas por ruta | 2 | 1 | Es lo que hace volver a la persona en las semanas que no estudia. |

## Decisiones para discutir

1. **¿Cuáles de la planilla no encajan con la app?** Mis sospechosas están en «Lo que queda fuera del prototipo», pero decime cuáles son las tuyas.
2. **El profesor.** Propongo un solo lugar, «Tu profesor», con la IA siempre disponible y la derivación a un tutor humano cuando existe (empresa). La alternativa es separarlos en dos funcionalidades sin relación.
3. **Qué práctica real mostramos primero.** Propongo el reto con rúbrica sobre la ruta actual (la persona escribe un prompt y se evalúa), porque no obliga a cambiar la ruta. El roleplay quedaría para una segunda ruta de atención al cliente.
4. **La empresa.** Propongo dejarla en el mockup v16 y concentrar el prototipo en la experiencia de la persona, el profesor y las noticias.
