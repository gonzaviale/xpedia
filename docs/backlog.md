# Xpedia · Product Backlog

Xpedia es una plataforma de aprendizaje adaptativa. A cada persona le arma, gratis, un roadmap personal para mejorar una habilidad o cambiar de rubro, y adentro de la app aprende, practica y demuestra lo que sabe. Las empresas pagan por una versión en la que cargan sus propias rutas (matriz de competencias, procesos y documentos) y obtienen evidencia de competencia por persona, rol y punto clave. El enfoque completo está en [propuesta-roadmaps.md](propuesta-roadmaps.md) y el pitch, en [pitch.md](pitch.md).

> **Origen de este archivo.** Es una copia de la hoja «Product Backlog Template» de la [planilla del Product Backlog](https://docs.google.com/spreadsheets/d/1O8O-hu_70DwhT4tmFNyuvwZWZzVfqEsu/edit?gid=1084473338), hecha el 5 de octubre de 2026.
>
> **La planilla es la fuente de verdad.** Si la planilla cambia, hay que regenerar este archivo. Lo único que no sale de la planilla es el apéndice del final, marcado como tal.
>
> La planilla tiene otras dos hojas, «Sprint Backlog Template» y «Ejemplo Capacidad», que son ejemplos de la plantilla y todavía no tienen datos de Xpedia.

## Contenido

- [Cómo leer este backlog](#cómo-leer-este-backlog)
- [Resumen por épica](#resumen-por-épica)
- [Índice de PBIs](#índice-de-pbis)
- [Detalle por épica](#detalle-por-épica)
- [Nota de la planilla](#nota-de-la-planilla)
- [Apéndice: propuestas que no están en la planilla](#apéndice-propuestas-que-no-están-en-la-planilla)

## Cómo leer este backlog

Cada PBI (Product Backlog Item) es una funcionalidad del producto. Estos son sus campos y la columna de la planilla de la que sale cada uno (en los nombres de columna, « / » separa las líneas del encabezado de la celda):

| Campo | Columna en la planilla | Qué significa |
|---|---|---|
| # | `#PBI / (Historia)` | Número del PBI. |
| Épica | `Sección / Epica / (Walking Skeleton)` | Área del producto a la que pertenece el PBI. |
| Nombre | `Nombre` | Título corto del PBI. |
| Descripción | `Descripión` | Historia de usuario: «Como [quién] quiero [qué]», casi siempre con el «para qué». |
| Prioridad | `Prioridad` | 1, 2 o 3 (ver abajo). |
| Estimación | `Estimación (Hs) / Puntos de Historia` | Esfuerzo estimado en horas. Según la nota de la planilla, es una primera propuesta para revisar en equipo. |
| Complejidad | `Complejidad` | Baja, Media o Alta. |
| Observaciones | `Observaciones` | Contexto: qué PBI anterior retoma o reemplaza, decisiones abiertas, mockups relacionados y límites técnicos. |
| Cómo probarlo | `Como Probarlo?` | La prueba que confirma que el PBI está hecho. |
| Sprint | `Sprint` | Vacía en todos los PBIs, porque todavía no hay planificación de sprints. Por eso no aparece en el detalle. |

**Prioridad**, según la nota de la planilla:

- **1** = núcleo
- **2** = mejora sensible
- **3** = posterior

**Referencias que aparecen en las observaciones:**

- «PBI n», cuando la frase no dice «del backlog anterior»: un PBI de este mismo backlog. Por ejemplo, el PBI 14 cita el PBI 49 y el PBI 37 cita los PBI 34 y 4.
- «PBI n del backlog anterior»: la numeración del backlog con enfoque de empleados y misiones, previo al cambio a roadmaps.
- «backlog de funcionalidades (v1)»: el documento [backlog-funcionalidades.md](backlog-funcionalidades.md).
- «Mockup v15» y «Mockup v16»: las carpetas `mockups/v15` y `mockups/v16` del repositorio.
- «Decisión abierta»: un tema que el equipo todavía no resolvió (aparece en los PBI 4, 17, 19, 44 y 49).

## Resumen por épica

| Épica | PBIs | Números | Horas totales | Horas de prioridad 1 |
|---|---:|---|---:|---:|
| Base de plataforma | 2 | 1 y 2 | 32 | 32 |
| Roadmap personal | 7 | 3 a 9 | 152 | 128 |
| Aprender | 5 | 10 a 14 | 116 | 76 |
| Contenido | 3 | 15 a 17 | 72 | 52 |
| Probar | 7 | 18 a 24 | 196 | 120 |
| Resultados y progreso | 5 | 25 a 29 | 80 | 24 |
| Novedades | 3 | 30 a 32 | 56 | 0 |
| Empresa | 13 | 33 a 45 | 296 | 236 |
| Admin Xpedia | 6 | 46 a 51 | 124 | 40 |
| **Total** | **51** | | **1.124** | **708** |

Por prioridad:

- Prioridad 1 (núcleo): 29 PBIs, 708 h.
- Prioridad 2 (mejora sensible): 17 PBIs, 340 h.
- Prioridad 3 (posterior): 5 PBIs, 76 h.

## Índice de PBIs

| # | Nombre | Épica | Prioridad | Horas |
|---:|---|---|:---:|---:|
| 1 | [Cuentas y tipos de usuario](#pbi-1--cuentas-y-tipos-de-usuario) | Base de plataforma | 1 | 16 |
| 2 | [Vista diferenciada por versión del producto](#pbi-2--vista-diferenciada-por-versión-del-producto) | Base de plataforma | 1 | 16 |
| 3 | [Elección de objetivo y ruta](#pbi-3--elección-de-objetivo-y-ruta) | Roadmap personal | 1 | 16 |
| 4 | [Diagnóstico inicial](#pbi-4--diagnóstico-inicial) | Roadmap personal | 1 | 28 |
| 5 | [Generación del roadmap personalizado](#pbi-5--generación-del-roadmap-personalizado) | Roadmap personal | 1 | 32 |
| 6 | [Vista del roadmap](#pbi-6--vista-del-roadmap) | Roadmap personal | 1 | 24 |
| 7 | [Ajuste del roadmap según rendimiento](#pbi-7--ajuste-del-roadmap-según-rendimiento) | Roadmap personal | 1 | 28 |
| 8 | [Ritmo diario y llegada estimada](#pbi-8--ritmo-diario-y-llegada-estimada) | Roadmap personal | 2 | 12 |
| 9 | [Sumar otra ruta](#pbi-9--sumar-otra-ruta) | Roadmap personal | 3 | 12 |
| 10 | [Sesión diaria de 10 minutos](#pbi-10--sesión-diaria-de-10-minutos) | Aprender | 1 | 28 |
| 11 | [Microlecciones con fuentes citadas](#pbi-11--microlecciones-con-fuentes-citadas) | Aprender | 1 | 28 |
| 12 | [Repaso espaciado](#pbi-12--repaso-espaciado) | Aprender | 1 | 20 |
| 13 | [Lecturas externas con check de comprensión](#pbi-13--lecturas-externas-con-check-de-comprensión) | Aprender | 2 | 16 |
| 14 | [Coach de IA](#pbi-14--coach-de-ia) | Aprender | 2 | 24 |
| 15 | [Biblioteca de fuentes y licencias](#pbi-15--biblioteca-de-fuentes-y-licencias) | Contenido | 1 | 20 |
| 16 | [Generación de material con IA y caché](#pbi-16--generación-de-material-con-ia-y-caché) | Contenido | 1 | 32 |
| 17 | [Revisión humana del material generado](#pbi-17--revisión-humana-del-material-generado) | Contenido | 2 | 20 |
| 18 | [Escenarios con decisiones](#pbi-18--escenarios-con-decisiones) | Probar | 1 | 32 |
| 19 | [Roleplay con IA por chat](#pbi-19--roleplay-con-ia-por-chat) | Probar | 1 | 32 |
| 20 | [Ensayo con audio y feedback de IA](#pbi-20--ensayo-con-audio-y-feedback-de-ia) | Probar | 2 | 36 |
| 21 | [Reto con proyecto real y rúbrica](#pbi-21--reto-con-proyecto-real-y-rúbrica) | Probar | 1 | 32 |
| 22 | [Conexión con GitHub](#pbi-22--conexión-con-github) | Probar | 2 | 24 |
| 23 | [Desafío en el mundo real con check-in](#pbi-23--desafío-en-el-mundo-real-con-check-in) | Probar | 2 | 16 |
| 24 | [Prueba final de hito](#pbi-24--prueba-final-de-hito) | Probar | 1 | 24 |
| 25 | [Evidencia de hito y portfolio](#pbi-25--evidencia-de-hito-y-portfolio) | Resultados y progreso | 1 | 24 |
| 26 | [Árbol de habilidades](#pbi-26--árbol-de-habilidades) | Resultados y progreso | 2 | 20 |
| 27 | [Historial de práctica con feedback](#pbi-27--historial-de-práctica-con-feedback) | Resultados y progreso | 2 | 12 |
| 28 | [Racha y recordatorios](#pbi-28--racha-y-recordatorios) | Resultados y progreso | 2 | 12 |
| 29 | [XP y niveles](#pbi-29--xp-y-niveles) | Resultados y progreso | 3 | 12 |
| 30 | [Novedades filtradas por ruta](#pbi-30--novedades-filtradas-por-ruta) | Novedades | 2 | 28 |
| 31 | [Retención de novedades hasta el hito](#pbi-31--retención-de-novedades-hasta-el-hito) | Novedades | 2 | 16 |
| 32 | [Sumar una novedad a la ruta o descartarla](#pbi-32--sumar-una-novedad-a-la-ruta-o-descartarla) | Novedades | 2 | 12 |
| 33 | [Alta de empresa y equipo](#pbi-33--alta-de-empresa-y-equipo) | Empresa | 1 | 20 |
| 34 | [Carga de la matriz de competencias](#pbi-34--carga-de-la-matriz-de-competencias) | Empresa | 1 | 20 |
| 35 | [Carga de documentos y puntos clave con trazabilidad](#pbi-35--carga-de-documentos-y-puntos-clave-con-trazabilidad) | Empresa | 1 | 32 |
| 36 | [Armado de la ruta en conjunto](#pbi-36--armado-de-la-ruta-en-conjunto) | Empresa | 1 | 28 |
| 37 | [Roadmap por persona según brecha de rol](#pbi-37--roadmap-por-persona-según-brecha-de-rol) | Empresa | 1 | 28 |
| 38 | [Práctica generada desde documentos de la empresa](#pbi-38--práctica-generada-desde-documentos-de-la-empresa) | Empresa | 1 | 32 |
| 39 | [Asignación de rutas al equipo](#pbi-39--asignación-de-rutas-al-equipo) | Empresa | 1 | 16 |
| 40 | [Panel del equipo](#pbi-40--panel-del-equipo) | Empresa | 1 | 24 |
| 41 | [Evidencia de competencia por persona y punto clave](#pbi-41--evidencia-de-competencia-por-persona-y-punto-clave) | Empresa | 1 | 24 |
| 42 | [Puntos clave más fallados](#pbi-42--puntos-clave-más-fallados) | Empresa | 2 | 24 |
| 43 | [Privacidad y datos agregados](#pbi-43--privacidad-y-datos-agregados) | Empresa | 1 | 12 |
| 44 | [Tutor de acompañamiento](#pbi-44--tutor-de-acompañamiento) | Empresa | 3 | 20 |
| 45 | [Avisos automáticos](#pbi-45--avisos-automáticos) | Empresa | 3 | 16 |
| 46 | [Dashboard de plataforma](#pbi-46--dashboard-de-plataforma) | Admin Xpedia | 2 | 20 |
| 47 | [Gestión de empresas](#pbi-47--gestión-de-empresas) | Admin Xpedia | 1 | 24 |
| 48 | [Planes y suscripciones](#pbi-48--planes-y-suscripciones) | Admin Xpedia | 2 | 24 |
| 49 | [Límites de práctica con IA](#pbi-49--límites-de-práctica-con-ia) | Admin Xpedia | 1 | 16 |
| 50 | [Gestión de rutas y contenido](#pbi-50--gestión-de-rutas-y-contenido) | Admin Xpedia | 2 | 24 |
| 51 | [Gestión de usuarios internos](#pbi-51--gestión-de-usuarios-internos) | Admin Xpedia | 3 | 16 |

## Detalle por épica

Los textos de cada PBI están copiados tal cual de la planilla, en su mismo orden.

## Épica: Base de plataforma

PBI 1 y 2 · 2 PBIs · 32 h, de las cuales 32 h son de prioridad 1.

### PBI 1 · Cuentas y tipos de usuario

- **Épica:** Base de plataforma
- **Prioridad:** 1
- **Estimación:** 16 h
- **Complejidad:** Media
- **Descripción:** Como plataforma quiero identificar si quien entra es una persona (plan individual), una empresa o un administrador de Xpedia para mostrarle su vista y sus funcionalidades.
- **Observaciones:** Retoma el PBI 1 del backlog anterior. El tipo «empleado» pasa a ser «persona» (plan individual gratis) o integrante de una empresa.
- **Cómo probarlo:** Ingresar con un usuario de cada tipo y verificar que accede a su vista.

### PBI 2 · Vista diferenciada por versión del producto

- **Épica:** Base de plataforma
- **Prioridad:** 1
- **Estimación:** 16 h
- **Complejidad:** Media
- **Descripción:** Como usuario quiero ver una interfaz acorde a mi versión (individual o empresa) para trabajar solo con lo que me corresponde.
- **Observaciones:** Retoma el PBI 2 del backlog anterior. Las dos versiones comparten las rutas y la práctica; cambia quién arma la ruta.
- **Cómo probarlo:** Validar navegación y contenido visible con persona, empresa y administrador.

## Épica: Roadmap personal

PBI 3 a 9 · 7 PBIs · 152 h, de las cuales 128 h son de prioridad 1.

### PBI 3 · Elección de objetivo y ruta

- **Épica:** Roadmap personal
- **Prioridad:** 1
- **Estimación:** 16 h
- **Complejidad:** Media
- **Descripción:** Como persona quiero elegir un objetivo (mejorar una habilidad o cambiar de rubro) y una de las rutas disponibles para empezar mi roadmap.
- **Observaciones:** Rutas piloto: desarrollo web desde cero, atención al cliente remota y oratoria. Mockup v15, onboarding.
- **Cómo probarlo:** Elegir un objetivo y verificar que se asigna la ruta con su meta y su duración estimada.

### PBI 4 · Diagnóstico inicial

- **Épica:** Roadmap personal
- **Prioridad:** 1
- **Estimación:** 28 h
- **Complejidad:** Alta
- **Descripción:** Como persona quiero responder un diagnóstico corto para que Xpedia sepa desde dónde parto y no me haga repetir lo que ya sé.
- **Observaciones:** Incluye habilidades transferibles en el cambio de rubro. Decisión abierta: si «cambiar de rubro» es un tipo de roadmap aparte o una variante.
- **Cómo probarlo:** Hacer el diagnóstico con distintos niveles de respuesta y verificar que cambia el hito de partida.

### PBI 5 · Generación del roadmap personalizado

- **Épica:** Roadmap personal
- **Prioridad:** 1
- **Estimación:** 32 h
- **Complejidad:** Alta
- **Descripción:** Como persona quiero recibir un roadmap con hitos ordenados hacia mi meta, y saber qué voy a poder demostrar al terminar cada hito.
- **Observaciones:** Cada hito declara su evidencia final. Reemplaza el mapa de misiones (PBI 24 del backlog anterior).
- **Cómo probarlo:** Completar el diagnóstico y verificar que se genera una lista de hitos con duración y evidencia.

### PBI 6 · Vista del roadmap

- **Épica:** Roadmap personal
- **Prioridad:** 1
- **Estimación:** 24 h
- **Complejidad:** Media
- **Descripción:** Como persona quiero ver mi roadmap con los hitos hechos, el actual y los que siguen, con los nodos de cada hito, para saber dónde estoy.
- **Observaciones:** Retoma la vista de aventura (PBI 24) y el dashboard del empleado (PBI 16) del backlog anterior, ahora por hitos.
- **Cómo probarlo:** Avanzar un hito y comprobar que cambia el estado de los hitos y de los nodos.

### PBI 7 · Ajuste del roadmap según rendimiento

- **Épica:** Roadmap personal
- **Prioridad:** 1
- **Estimación:** 28 h
- **Complejidad:** Alta
- **Descripción:** Como persona quiero que mi roadmap se ajuste a cómo me va para repasar lo que me cuesta y no perder tiempo en lo que ya domino.
- **Observaciones:** «Roadmap vivo». Reemplaza el PBI 14 del backlog anterior (habilidades a reforzar) en la versión individual.
- **Cómo probarlo:** Fallar varias veces un nodo y verificar que se agrega un repaso o se reordena el hito.

### PBI 8 · Ritmo diario y llegada estimada

- **Épica:** Roadmap personal
- **Prioridad:** 2
- **Estimación:** 12 h
- **Complejidad:** Baja
- **Descripción:** Como persona quiero elegir cuánto tiempo practico por día y ver cuándo llegaría a mi meta.
- **Observaciones:** Con 10-20 min por día las rutas técnicas no cierran (sistemas: 2,5 a 5 años). Mostrar el ritmo recomendado y presentar el modo liviano como exploración. Reemplaza el objetivo semanal (PBI 6 y 18 del backlog anterior).
- **Cómo probarlo:** Cambiar el ritmo y verificar que cambian la fecha estimada y el aviso de ritmo.

### PBI 9 · Sumar otra ruta

- **Épica:** Roadmap personal
- **Prioridad:** 3
- **Estimación:** 12 h
- **Complejidad:** Baja
- **Descripción:** Como persona quiero sumar una segunda ruta sin perder el progreso de la primera.
- **Observaciones:** Fuera del primer alcance.
- **Cómo probarlo:** Sumar una ruta y verificar que la primera conserva sus hitos y su avance.

## Épica: Aprender

PBI 10 a 14 · 5 PBIs · 116 h, de las cuales 76 h son de prioridad 1.

### PBI 10 · Sesión diaria de 10 minutos

- **Épica:** Aprender
- **Prioridad:** 1
- **Estimación:** 28 h
- **Complejidad:** Alta
- **Descripción:** Como persona quiero una sesión corta con repaso, microlección, práctica y cierre para avanzar todos los días sin tener que decidir qué hacer.
- **Observaciones:** Retoma el listado de misiones (PBI 17) y la ejecución de misión (PBI 19) del backlog anterior.
- **Cómo probarlo:** Completar una sesión y verificar que se registran el repaso, la práctica y el cierre.

### PBI 11 · Microlecciones con fuentes citadas

- **Épica:** Aprender
- **Prioridad:** 1
- **Estimación:** 28 h
- **Complejidad:** Alta
- **Descripción:** Como persona quiero material breve adaptado a mi nivel, con la fuente de cada dato, para confiar en lo que aprendo.
- **Observaciones:** La IA adapta y no inventa. Cada hito cita sus fuentes.
- **Cómo probarlo:** Abrir una microlección y verificar que muestra la fuente y la licencia.

### PBI 12 · Repaso espaciado

- **Épica:** Aprender
- **Prioridad:** 1
- **Estimación:** 20 h
- **Complejidad:** Media
- **Descripción:** Como persona quiero repasar lo que fallé en el momento en que conviene para no olvidarlo.
- **Observaciones:** Retoma los repasos del backlog de funcionalidades (v1).
- **Cómo probarlo:** Fallar un nodo y verificar que reaparece en una sesión posterior.

### PBI 13 · Lecturas externas con check de comprensión

- **Épica:** Aprender
- **Prioridad:** 2
- **Estimación:** 16 h
- **Complejidad:** Media
- **Descripción:** Como persona quiero que un libro o una charla externa entren al roadmap como lectura, y que al volver la app me tome un caso corto.
- **Observaciones:** Para lo que no se puede traer adentro por licencia. El caso de 2 minutos es lo que suma al nodo.
- **Cómo probarlo:** Marcar una lectura como leída y verificar que se presenta el caso y que el resultado suma al nodo.

### PBI 14 · Coach de IA

- **Épica:** Aprender
- **Prioridad:** 2
- **Estimación:** 24 h
- **Complejidad:** Media
- **Descripción:** Como persona quiero consultar a un coach de IA sobre mi ruta y mis dudas para no trabarme.
- **Observaciones:** Responde con base en las fuentes del hito. Costo acotado por el límite diario (PBI 49).
- **Cómo probarlo:** Hacer una consulta y verificar que la respuesta cita la fuente.

## Épica: Contenido

PBI 15 a 17 · 3 PBIs · 72 h, de las cuales 52 h son de prioridad 1.

### PBI 15 · Biblioteca de fuentes y licencias

- **Épica:** Contenido
- **Prioridad:** 1
- **Estimación:** 20 h
- **Complejidad:** Media
- **Descripción:** Como Xpedia quiero registrar qué fuentes y licencias usa cada hito para usar solo lo que se puede adaptar comercialmente.
- **Observaciones:** Hay muy poco contenido abierto de uso comercial. Se puede adaptar MDN (CC BY-SA), documentación de React, argentina.gob.ar, O\*NET y ESCO.
- **Cómo probarlo:** Cargar una fuente con su licencia y verificar que queda asociada al hito.

### PBI 16 · Generación de material con IA y caché

- **Épica:** Contenido
- **Prioridad:** 1
- **Estimación:** 32 h
- **Complejidad:** Alta
- **Descripción:** Como Xpedia quiero generar el material una vez por habilidad y nivel y reutilizarlo para controlar el costo.
- **Observaciones:** Xpedia produciría entre el 70 y el 80 % del material.
- **Cómo probarlo:** Pedir el mismo hito para dos personas del mismo nivel y verificar que se reutiliza el material.

### PBI 17 · Revisión humana del material generado

- **Épica:** Contenido
- **Prioridad:** 2
- **Estimación:** 20 h
- **Complejidad:** Media
- **Descripción:** Como Xpedia quiero que un especialista revise el material generado antes de publicarlo para evitar errores.
- **Observaciones:** Decisión abierta: quién revisa (docentes y especialistas por ruta). Retoma la revisión y aprobación de misiones (PBI 10 y 11 del backlog anterior).
- **Cómo probarlo:** Generar material y verificar que no se publica hasta ser aprobado.

## Épica: Probar

PBI 18 a 24 · 7 PBIs · 196 h, de las cuales 120 h son de prioridad 1.

### PBI 18 · Escenarios con decisiones

- **Épica:** Probar
- **Prioridad:** 1
- **Estimación:** 32 h
- **Complejidad:** Alta
- **Descripción:** Como persona quiero practicar situaciones con decisiones y consecuencias para entrenar criterio.
- **Observaciones:** Retoma los motores Diálogo y Crisis (PBI 26 a 28 y 33 a 35 del backlog anterior) como base de la capa «Probar». Incluye modo validación.
- **Cómo probarlo:** Tomar decisiones distintas en la misma escena y verificar que cambian las consecuencias.

### PBI 19 · Roleplay con IA por chat

- **Épica:** Probar
- **Prioridad:** 1
- **Estimación:** 32 h
- **Complejidad:** Alta
- **Descripción:** Como persona quiero conversar con un personaje simulado (cliente enojado, entrevistador) para practicar con feedback.
- **Observaciones:** Mecánica estrella de atención al cliente. Escala de enojo que sube o baja según la respuesta. Decisión abierta: roleplay por voz en el plan gratis.
- **Cómo probarlo:** Responder bien y mal y verificar que cambia el enojo y el feedback final.

### PBI 20 · Ensayo con audio y feedback de IA

- **Épica:** Probar
- **Prioridad:** 2
- **Estimación:** 36 h
- **Complejidad:** Alta
- **Descripción:** Como persona quiero grabar una presentación y recibir feedback de estructura, ritmo y pausas.
- **Observaciones:** Mecánica estrella de oratoria. Transcribir y medir ritmo es viable. Detectar muletillas no es confiable todavía y el lenguaje corporal por video queda fuera.
- **Cómo probarlo:** Grabar un audio y verificar que se muestra la transcripción y el feedback.

### PBI 21 · Reto con proyecto real y rúbrica

- **Épica:** Probar
- **Prioridad:** 1
- **Estimación:** 32 h
- **Complejidad:** Alta
- **Descripción:** Como persona quiero resolver un reto con un proyecto real y que se evalúe con una rúbrica.
- **Observaciones:** Mecánica estrella de sistemas: «ticket de bug» (un carrito que concatena en vez de sumar), con casos de prueba y explicación a la clienta.
- **Cómo probarlo:** Resolver el ticket y verificar que los casos de prueba y la rúbrica dan el resultado esperado.

### PBI 22 · Conexión con GitHub

- **Épica:** Probar
- **Prioridad:** 2
- **Estimación:** 24 h
- **Complejidad:** Alta
- **Descripción:** Como persona quiero conectar mi cuenta de GitHub para que Xpedia verifique el trabajo que hago fuera de la app.
- **Observaciones:** En sistemas hay que usar VS Code y GitHub. Se verifica con commits y sitio publicado.
- **Cómo probarlo:** Conectar GitHub, publicar un commit y verificar que el nodo lo reconoce.

### PBI 23 · Desafío en el mundo real con check-in

- **Épica:** Probar
- **Prioridad:** 2
- **Estimación:** 16 h
- **Complejidad:** Media
- **Descripción:** Como persona quiero un desafío para aplicar lo aprendido en mi vida laboral y contar cómo me fue.
- **Observaciones:** Ejemplo: presentar en la reunión del lunes.
- **Cómo probarlo:** Aceptar un desafío, hacer el check-in y verificar que suma evidencia.

### PBI 24 · Prueba final de hito

- **Épica:** Probar
- **Prioridad:** 1
- **Estimación:** 24 h
- **Complejidad:** Alta
- **Descripción:** Como persona quiero una prueba final en modo validación para demostrar que cumplí el hito.
- **Observaciones:** Un solo intento por paso. Retoma el desafío final de rol del backlog de funcionalidades (v1).
- **Cómo probarlo:** Completar el hito y verificar que se habilita la prueba y que su resultado cierra el hito.

## Épica: Resultados y progreso

PBI 25 a 29 · 5 PBIs · 80 h, de las cuales 24 h son de prioridad 1.

### PBI 25 · Evidencia de hito y portfolio

- **Épica:** Resultados y progreso
- **Prioridad:** 1
- **Estimación:** 24 h
- **Complejidad:** Media
- **Descripción:** Como persona quiero guardar la evidencia de cada hito en un portfolio que pueda compartir.
- **Observaciones:** Útil para el currículum. Reemplaza el historial de misiones (PBI 23 del backlog anterior).
- **Cómo probarlo:** Cerrar un hito y verificar que la evidencia aparece en el portfolio con un link para compartir.

### PBI 26 · Árbol de habilidades

- **Épica:** Resultados y progreso
- **Prioridad:** 2
- **Estimación:** 20 h
- **Complejidad:** Media
- **Descripción:** Como persona quiero ver mis habilidades como un árbol para entender cuánto avancé en cada rama.
- **Observaciones:** Retoma el PBI 22 del backlog anterior. Es la forma natural de mostrar un roadmap.
- **Cómo probarlo:** Completar nodos de una rama y verificar su avance.

### PBI 27 · Historial de práctica con feedback

- **Épica:** Resultados y progreso
- **Prioridad:** 2
- **Estimación:** 12 h
- **Complejidad:** Baja
- **Descripción:** Como persona quiero revisar mis prácticas anteriores y su feedback.
- **Observaciones:** Retoma los PBI 23 y 37 del backlog anterior (historial y feedback de misión).
- **Cómo probarlo:** Completar una práctica y verificar que aparece en el historial con su feedback.

### PBI 28 · Racha y recordatorios

- **Épica:** Resultados y progreso
- **Prioridad:** 2
- **Estimación:** 12 h
- **Complejidad:** Baja
- **Descripción:** Como persona quiero una racha y recordatorios de la IA para sostener la práctica diaria.
- **Observaciones:** Refuerzo, no el motivo principal para quedarse.
- **Cómo probarlo:** Practicar dos días seguidos y verificar la racha y el recordatorio.

### PBI 29 · XP y niveles

- **Épica:** Resultados y progreso
- **Prioridad:** 3
- **Estimación:** 12 h
- **Complejidad:** Baja
- **Descripción:** Como persona quiero acumular XP y subir de nivel como refuerzo de mi avance.
- **Observaciones:** Retoma los PBI 20, 21 y 38 del backlog anterior. Baja prioridad frente al progreso hacia una meta real.
- **Cómo probarlo:** Acumular XP y verificar el cambio de nivel.

## Épica: Novedades

PBI 30 a 32 · 3 PBIs · 56 h, de las cuales 0 h son de prioridad 1.

### PBI 30 · Novedades filtradas por ruta

- **Épica:** Novedades
- **Prioridad:** 2
- **Estimación:** 28 h
- **Complejidad:** Alta
- **Descripción:** Como persona quiero enterarme de lo que cambia en mi rubro y que se me muestre solo lo que importa para mi ruta.
- **Observaciones:** Mockup v16. En la app real saldría de una búsqueda periódica filtrada por el roadmap de cada persona.
- **Cómo probarlo:** Cargar una novedad relacionada con la ruta y verificar que aparece solo a quien le corresponde.

### PBI 31 · Retención de novedades hasta el hito

- **Épica:** Novedades
- **Prioridad:** 2
- **Estimación:** 16 h
- **Complejidad:** Media
- **Descripción:** Como persona quiero que una novedad que todavía no me sirve se guarde y aparezca cuando llegue al hito correspondiente.
- **Observaciones:** Cada novedad retenida explica por qué se guarda.
- **Cómo probarlo:** Cargar una novedad de un hito posterior y verificar que se muestra como «más adelante» con su motivo.

### PBI 32 · Sumar una novedad a la ruta o descartarla

- **Épica:** Novedades
- **Prioridad:** 2
- **Estimación:** 12 h
- **Complejidad:** Baja
- **Descripción:** Como persona quiero sumar una novedad como nodo de mi roadmap o descartarla.
- **Observaciones:** Mockup v16. Las dos acciones se pueden deshacer.
- **Cómo probarlo:** Sumar una novedad y verificar que aparece como nodo en el hito. Descartar otra y verificar que se marca como descartada.

## Épica: Empresa

PBI 33 a 45 · 13 PBIs · 296 h, de las cuales 236 h son de prioridad 1.

### PBI 33 · Alta de empresa y equipo

- **Épica:** Empresa
- **Prioridad:** 1
- **Estimación:** 20 h
- **Complejidad:** Media
- **Descripción:** Como empresa quiero dar de alta a mi equipo, con rol y área, para que cada persona tenga su ruta.
- **Observaciones:** Retoma los PBI 4 y 5 del backlog anterior.
- **Cómo probarlo:** Crear una persona y comprobar que aparece en el listado con su rol.

### PBI 34 · Carga de la matriz de competencias

- **Épica:** Empresa
- **Prioridad:** 1
- **Estimación:** 20 h
- **Complejidad:** Media
- **Descripción:** Como empresa quiero cargar mi matriz de competencias para definir el nivel esperado por rol.
- **Observaciones:** Reemplaza la configuración de roles e importancias del enfoque anterior.
- **Cómo probarlo:** Cargar una matriz y verificar que quedan asociadas las competencias a cada rol.

### PBI 35 · Carga de documentos y puntos clave con trazabilidad

- **Épica:** Empresa
- **Prioridad:** 1
- **Estimación:** 32 h
- **Complejidad:** Alta
- **Descripción:** Como empresa quiero cargar mis documentos y ver los puntos clave que identificó Xpedia, con la página de origen.
- **Observaciones:** Es el diferencial de la versión paga. Retoma los PBI 7 y 8 del backlog anterior.
- **Cómo probarlo:** Cargar un documento y verificar que cada punto clave indica página y fragmento de origen.

### PBI 36 · Armado de la ruta en conjunto

- **Épica:** Empresa
- **Prioridad:** 1
- **Estimación:** 28 h
- **Complejidad:** Alta
- **Descripción:** Como empresa quiero armar la ruta junto con Xpedia: Xpedia propone los hitos, yo marco los que ajustar y confirmo.
- **Observaciones:** Mockup v16. A diferencia del plan individual, la empresa no recibe una ruta ya armada.
- **Cómo probarlo:** Marcar un hito para ajustar, confirmar la ruta y verificar que habilita el resto del flujo.

### PBI 37 · Roadmap por persona según brecha de rol

- **Épica:** Empresa
- **Prioridad:** 1
- **Estimación:** 28 h
- **Complejidad:** Alta
- **Descripción:** Como empresa quiero que el roadmap de cada persona salga de la diferencia entre el nivel esperado del rol y el nivel medido.
- **Observaciones:** Combina la matriz (PBI 34) con el diagnóstico (PBI 4).
- **Cómo probarlo:** Medir a dos personas con distinto nivel en el mismo rol y verificar que sus roadmaps difieren.

### PBI 38 · Práctica generada desde documentos de la empresa

- **Épica:** Empresa
- **Prioridad:** 1
- **Estimación:** 32 h
- **Complejidad:** Alta
- **Descripción:** Como empresa quiero que se genere práctica (escenarios, casos) a partir de mis documentos, y revisarla antes de publicarla.
- **Observaciones:** Retoma los PBI 9, 10 y 11 del backlog anterior.
- **Cómo probarlo:** Cargar un documento, generar práctica, aprobarla y verificar que queda disponible.

### PBI 39 · Asignación de rutas al equipo

- **Épica:** Empresa
- **Prioridad:** 1
- **Estimación:** 16 h
- **Complejidad:** Baja
- **Descripción:** Como empresa quiero asignar rutas a personas o roles para definir quién hace qué.
- **Observaciones:** Retoma el PBI 12 del backlog anterior.
- **Cómo probarlo:** Asignar una ruta a un rol y verificar que la reciben sus integrantes.

### PBI 40 · Panel del equipo

- **Épica:** Empresa
- **Prioridad:** 1
- **Estimación:** 24 h
- **Complejidad:** Media
- **Descripción:** Como empresa quiero ver el estado de los hitos de mi equipo y el resultado de la prueba final de cada persona.
- **Observaciones:** Retoma los PBI 3 y 5 del backlog anterior. No incluye los resultados de práctica individuales.
- **Cómo probarlo:** Avanzar hitos con varias personas y verificar que el panel se actualiza.

### PBI 41 · Evidencia de competencia por persona y punto clave

- **Épica:** Empresa
- **Prioridad:** 1
- **Estimación:** 24 h
- **Complejidad:** Media
- **Descripción:** Como empresa quiero tener evidencia de competencia por persona, rol y punto clave para mostrarla ante una auditoría.
- **Observaciones:** Es lo que compra la empresa: resultado y evidencia, no gamificación.
- **Cómo probarlo:** Completar una prueba y verificar que queda registrada contra el punto clave.

### PBI 42 · Puntos clave más fallados

- **Épica:** Empresa
- **Prioridad:** 2
- **Estimación:** 24 h
- **Complejidad:** Alta
- **Descripción:** Como empresa quiero ver qué puntos clave falla más mi equipo para detectar dónde falla el proceso.
- **Observaciones:** Retoma el PBI 13 del backlog anterior.
- **Cómo probarlo:** Fallar un punto clave con varias personas y verificar que sube en el ranking.

### PBI 43 · Privacidad y datos agregados

- **Épica:** Empresa
- **Prioridad:** 1
- **Estimación:** 12 h
- **Complejidad:** Media
- **Descripción:** Como persona de una empresa quiero que mi empresa no escuche ni lea mis prácticas, y que vea solo niveles agregados de grupos de 5 o más.
- **Observaciones:** De cada persona se ve el estado de los hitos y el resultado de la prueba final. Sin reconocimiento de emociones.
- **Cómo probarlo:** Entrar como empresa y verificar que no se pueden abrir las prácticas ni ver grupos de menos de 5.

### PBI 44 · Tutor de acompañamiento

- **Épica:** Empresa
- **Prioridad:** 3
- **Estimación:** 20 h
- **Complejidad:** Media
- **Descripción:** Como empresa quiero elegir entre un tutor puesto por la empresa o uno calificado por la comunidad.
- **Observaciones:** Decisión abierta en el equipo. Mockup v16 deja probar las dos opciones.
- **Cómo probarlo:** Elegir cada tipo de tutor y verificar que queda asignado al equipo.

### PBI 45 · Avisos automáticos

- **Épica:** Empresa
- **Prioridad:** 3
- **Estimación:** 16 h
- **Complejidad:** Media
- **Descripción:** Como empresa quiero que se avise a quien va atrasado y recibir un resumen semanal.
- **Observaciones:** Retoma el objetivo semanal (PBI 6 del backlog anterior) adaptado a hitos.
- **Cómo probarlo:** Dejar a una persona atrasada y verificar que recibe el aviso.

## Épica: Admin Xpedia

PBI 46 a 51 · 6 PBIs · 124 h, de las cuales 40 h son de prioridad 1.

### PBI 46 · Dashboard de plataforma

- **Épica:** Admin Xpedia
- **Prioridad:** 2
- **Estimación:** 20 h
- **Complejidad:** Media
- **Descripción:** Como administrador quiero ver el estado general de la plataforma, incluidas las personas y empresas activas.
- **Observaciones:** Retoma el PBI 39 del backlog anterior. Suma el plan individual.
- **Cómo probarlo:** Ingresar como administrador y verificar los indicadores.

### PBI 47 · Gestión de empresas

- **Épica:** Admin Xpedia
- **Prioridad:** 1
- **Estimación:** 24 h
- **Complejidad:** Alta
- **Descripción:** Como administrador quiero dar de alta, consultar y suspender empresas.
- **Observaciones:** Unifica los PBI 40, 41 y 42 del backlog anterior.
- **Cómo probarlo:** Crear una empresa, abrir su detalle y suspenderla.

### PBI 48 · Planes y suscripciones

- **Épica:** Admin Xpedia
- **Prioridad:** 2
- **Estimación:** 24 h
- **Complejidad:** Media
- **Descripción:** Como administrador quiero gestionar los planes y las suscripciones de las empresas, con cobro por usuario.
- **Observaciones:** Unifica los PBI 43 y 44 del backlog anterior. El plan individual es gratis.
- **Cómo probarlo:** Crear un plan, asignarlo a una empresa y verificar su consumo.

### PBI 49 · Límites de práctica con IA

- **Épica:** Admin Xpedia
- **Prioridad:** 1
- **Estimación:** 16 h
- **Complejidad:** Media
- **Descripción:** Como administrador quiero definir cuánta práctica con IA incluye el plan gratis por día para controlar el costo.
- **Observaciones:** Decisión abierta: cuánta práctica incluye y si el roleplay por voz entra.
- **Cómo probarlo:** Superar el límite diario y verificar que se bloquea la práctica con IA.

### PBI 50 · Gestión de rutas y contenido

- **Épica:** Admin Xpedia
- **Prioridad:** 2
- **Estimación:** 24 h
- **Complejidad:** Media
- **Descripción:** Como administrador quiero crear y editar rutas, hitos y fuentes para mantener el catálogo.
- **Observaciones:** Nuevo. Las rutas dependen del país (hoy, Argentina).
- **Cómo probarlo:** Editar un hito y verificar que se actualiza en las rutas que lo usan.

### PBI 51 · Gestión de usuarios internos

- **Épica:** Admin Xpedia
- **Prioridad:** 3
- **Estimación:** 16 h
- **Complejidad:** Media
- **Descripción:** Como administrador quiero gestionar las cuentas internas de Xpedia.
- **Observaciones:** Retoma el PBI 45 del backlog anterior.
- **Cómo probarlo:** Crear un usuario interno y verificar su estado.

## Nota de la planilla

Texto de la fila «Nota» al pie de la hoja, copiado tal cual:

> Backlog reformulado a partir del enfoque de roadmaps (docs/propuesta-roadmaps2.md). La columna Sprint se deja vacía porque en esta etapa se está definiendo el Product Backlog, sin planificación de sprints. Las estimaciones son una primera propuesta para revisar en equipo. Prioridad: 1 = núcleo, 2 = mejora sensible, 3 = posterior. Del backlog anterior se descartan el objetivo semanal por empresa (PBI 6) y los motores Auditor y Protocolo como funcionalidad propia (PBI 29 a 32). Las referencias «PBI n del backlog anterior» apuntan a la numeración del backlog con enfoque de empleados y misiones.

## Apéndice: propuestas que no están en la planilla

> **Propuestas que no están en la planilla (surgieron en una conversación del 5/10/2026, pendientes de revisar en equipo).** No forman parte del Product Backlog oficial. Están redactadas como PBIs para facilitar la discusión, pero no tienen número definitivo (se identifican de P1 a P6), ni prioridad, ni estimación. La épica es solo una sugerencia.

### P1 · La ruta como grafo con prerrequisitos

- **Épica sugerida:** Roadmap personal
- **Prioridad:** sin definir
- **Estimación:** sin estimar
- **Descripción:** Como Xpedia quiero que cada hito y cada nodo de una ruta declare sus prerrequisitos y su nivel, para poder adaptar y bifurcar el roadmap de cada persona.
- **Observaciones:** Es la base técnica para adaptar y bifurcar el roadmap. Se relaciona con la generación del roadmap (PBI 5), el ajuste según rendimiento (PBI 7) y las propuestas P2 y P3.
- **Cómo probarlo (borrador):** Cargar una ruta y verificar que cada hito y cada nodo tienen declarados sus prerrequisitos y su nivel.

### P2 · «Me interesa esto»

- **Épica sugerida:** Roadmap personal
- **Prioridad:** sin definir
- **Estimación:** sin estimar
- **Descripción:** Como persona quiero marcar un tema que me interesa, desde una novedad o escribiéndolo, para que la IA lo ubique en mi roadmap según lo que ya sé.
- **Observaciones:** La IA compara los prerrequisitos del tema con el perfil de la persona:
  - si los tiene, el tema entra ahora como nodo;
  - si le falta poco, se agenda en el hito donde ya los va a tener;
  - si es otro camino, queda como rama opcional (P3);
  - si está muy lejos, se guarda y se avisa más adelante.

  Siempre explica por qué. Depende de P1. Se relaciona con las novedades (PBI 30 a 32).
- **Cómo probarlo (borrador):** Marcar temas con distinta distancia al perfil y verificar que cada uno cae en el caso que corresponde (nodo ahora, agendado en un hito, rama opcional o guardado para más adelante), con su explicación.

### P3 · Ramas opcionales del roadmap

- **Épica sugerida:** Roadmap personal
- **Prioridad:** sin definir
- **Estimación:** sin estimar
- **Descripción:** Como persona quiero sumar ramas opcionales (bifurcaciones) a mi roadmap para explorar otros caminos sin frenar mi ruta principal.
- **Observaciones:** Las ramas no frenan la ruta principal. Depende de P1, y P2 puede generar ramas. Se relaciona con «Sumar otra ruta» (PBI 9).
- **Cómo probarlo (borrador):** Sumar una rama opcional y verificar que la ruta principal conserva su orden y su avance.

### P4 · Detección de ineficiencias

- **Épica sugerida:** Roadmap personal
- **Prioridad:** sin definir
- **Estimación:** sin estimar
- **Descripción:** Como persona quiero que Xpedia detecte los patrones que me hacen aprender peor, para que mi roadmap y mi práctica se ajusten.
- **Observaciones:** Patrones a detectar: fallar siempre el mismo tipo de punto clave, adivinar, tardar mucho o abandonar cierto tipo de práctica. Hoy el PBI 7 lo cubre de forma muy general.
- **Cómo probarlo (borrador):** Reproducir cada patrón (por ejemplo, fallar varias veces el mismo tipo de punto clave) y verificar que queda detectado.

### P5 · Generación de evaluaciones con IA y su calibración

- **Épica sugerida:** Probar
- **Prioridad:** sin definir
- **Estimación:** sin estimar
- **Descripción:** Como Xpedia quiero generar evaluaciones con IA, con variantes por nodo y rúbricas, y calibrarlas para que sus resultados sean confiables.
- **Observaciones:** Un agente evaluador distinto del que enseña, calibrado contra especialistas, con auditoría por muestra. Se relaciona con el reto con rúbrica (PBI 21), la prueba final de hito (PBI 24) y la revisión humana del material (PBI 17).
- **Cómo probarlo (borrador):** Generar variantes de evaluación para un nodo, hacer que especialistas corrijan una muestra y comparar su resultado con el del agente evaluador.

### P6 · Informe periódico personal

- **Épica sugerida:** Resultados y progreso
- **Prioridad:** sin definir
- **Estimación:** sin estimar
- **Descripción:** Como persona quiero recibir un informe periódico con lo que avancé, lo que me cuesta, las novedades que me importan y un ajuste propuesto, para decidir si acepto o rechazo ese ajuste.
- **Observaciones:** La persona acepta o rechaza el ajuste propuesto. Se relaciona con el ajuste del roadmap según rendimiento (PBI 7) y las novedades filtradas por ruta (PBI 30).
- **Cómo probarlo (borrador):** Generar un informe y verificar que tiene las cuatro partes. Aceptar el ajuste y verificar que cambia el roadmap; rechazarlo y verificar que no cambia.

### Observación de tamaño

El backlog suma 1.124 h y solo la prioridad 1 son 708 h. Si la capacidad real del equipo fuera la del ejemplo de la plantilla (hoja «Ejemplo Capacidad»: 95 h por sprint, 285 h en 3 sprints), la prioridad 1 sola sería unas 2,5 veces la capacidad (708 h contra 285 h), así que hace falta recortar un MVP.
