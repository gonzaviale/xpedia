# Xpedia — Backlog de funcionalidades (v1)

Este documento lista las funcionalidades propuestas para Xpedia, separadas por las dos vistas que vamos a mostrar: la del **empleado** (quien se capacita) y la de la **empresa** (quien administra la capacitación de su equipo). Queda fuera de este backlog la vista de administración de Xpedia como producto (gestión interna de la plataforma), que no forma parte del alcance que presentamos.

Es una primera versión: sirve como base para ordenar qué ya está construido en los mockups y qué falta definir, y para discutir prioridades de cara al próximo sprint.

**Criterio de prioridad:**
- **Alta** — funcionalidad núcleo: sin ella, la propuesta no resuelve el problema que plantea.
- **Media** — mejora sensiblemente la experiencia o la gestión, pero la app funciona sin ella.
- **Baja** — complementaria o de conveniencia; no afecta el valor central.

---

## Vista Empleado

| Funcionalidad | Descripción | Prioridad |
|---|---|---|
| Inicio con misión recomendada | Al entrar, el empleado ve la próxima misión sugerida (priorizando repasos vencidos sobre contenido nuevo), su objetivo semanal y accesos directos a otras misiones pendientes. | Alta |
| Mapa de misiones (Aventura) | Muestra todas las misiones publicadas para el rol del empleado y permite elegir cuál jugar, en lugar de depender solo de la recomendación del inicio. | Alta |
| Motor de juego "Diálogo" | Simulación de una conversación con opciones de respuesta y un medidor de tensión que sube o baja según la decisión tomada. Entrena trato con clientes y compañeros. | Alta |
| Motor de juego "Auditor" | Presenta un documento o registro con errores ocultos que hay que encontrar contra reloj, con un manual de consulta opcional. Entrena atención al detalle y cumplimiento de normas. | Alta |
| Motor de juego "Protocolo" | Pide reproducir los pasos de un procedimiento en el orden correcto, contra reloj y evitando pasos trampa. Entrena procedimientos operativos. | Alta |
| Motor de juego "Crisis" | Presenta varios problemas simultáneos y obliga a elegir qué atender primero y cómo resolverlo. Entrena priorización bajo presión. | Alta |
| Desafío final de rol | Al completar todas las misiones publicadas para su rol, se habilita un desafío integrador que combina escenarios de los distintos motores en modo validación (una sola oportunidad por paso, sin reintentos). | Media |
| Árbol de habilidades | Dentro de "Mi progreso", muestra el avance en cuatro ramas de habilidades (comunicación, criterio y normas, procedimientos, decisiones bajo presión), qué nodos ya se desbloquearon y cuánto falta para el siguiente. | Alta |
| Historial de misiones con feedback | Lista las misiones ya jugadas con su resultado; al abrir cada una muestra qué se hizo bien y qué se puede mejorar, y permite repetirla si sigue vigente. | Alta |
| Racha e insignias de constancia | Mide semanas consecutivas cumpliendo el objetivo semanal y otorga insignias por sostenerla, con una semana de gracia por trimestre (vacaciones, licencias). | Media |
| Mi cuenta | Permite editar datos personales y ver los datos laborales (rol, área) que gestiona la empresa. | Baja |

## Vista Empresa

| Funcionalidad | Descripción | Prioridad |
|---|---|---|
| Panel general del equipo | Resume el estado de la capacitación: empleados activos, cumplimiento del objetivo semanal, misiones completadas y progreso general, con alertas accionables (repasos pendientes, brechas de habilidad por rol, material desactualizado). | Alta |
| Gestión de empleados | Alta de empleados, asignación de rol y área, y vista del nivel, XP y objetivo semanal de cada uno. | Alta |
| Carga de material y comprensión automática | Permite subir manuales, protocolos o documentos, y muestra qué puntos clave identificó la app en ese material como base para generar misiones. | Alta |
| Roles y objetivos por tópico | Define a qué roles aplica un tópico de capacitación, qué importancia tiene para cada rol y qué proporción de su carga de misiones debería salir de ahí. | Alta |
| Generación y revisión de misiones | A partir de los puntos clave detectados en el material, propone misiones por motor de juego; permite revisarlas y probarlas en modo prueba antes de publicarlas a los empleados. | Alta |
| Calendario de publicación y repaso | Define el orden en que se publican los tópicos y cada cuánto se repite un repaso periódico, para que el conocimiento no se pierda con el tiempo. | Media |
| Avisos automáticos | Envía recordatorios a quien va atrasado con su objetivo semanal, un resumen semanal por correo y avisos cuando vence un repaso. | Media |
| Métricas de puntos clave más fallados | Muestra qué puntos clave fallan más los empleados, por tópico y por rol, para detectar dónde reforzar el material o la capacitación presencial. | Alta |

---

*Pendiente: revisar esta lista en equipo, ajustar prioridades si hace falta y confirmar qué funcionalidades quedan para este sprint vs. backlog futuro.*
