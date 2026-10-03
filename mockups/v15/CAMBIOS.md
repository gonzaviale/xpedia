# v15: cambios respecto de v14

Base: el nuevo enfoque de [docs/propuesta-roadmaps.md](../../docs/propuesta-roadmaps.md) y las tres rutas piloto de [docs/rutas/](../../docs/rutas/). Es un mockup nuevo, escrito desde cero en un solo `index.html` con HTML y JavaScript, sin `support.js`. Mantiene el sistema de diseño Nocturne y los iconos de Phosphor de las versiones anteriores.

## El cambio de fondo
Hasta la v14, Xpedia era capacitación para empresas. Desde la v15, **el centro es la persona**: entra gratis, elige un objetivo y Xpedia le arma una hoja de ruta para mejorar o cambiar de rubro. La versión para empresas queda como vista previa (selector **Persona · gratis / Empresa · pago** arriba).

## Onboarding (persona)
1. **¿Qué querés lograr?** Mejorar en mi trabajo, cambiar de rubro o aprender algo desde cero.
2. **Elegí tu ruta.** Las 3 rutas piloto, con la sugerida marcada. Hay una tarjeta «Otro objetivo» que explica que la IA va a armar rutas nuevas más adelante.
3. **¿Cuánto tiempo por día?** Cada ritmo muestra cuánto tardarías de verdad. Si el ritmo no alcanza, aparece un aviso honesto: en sistemas, con 10-20 minutos la ruta sirve solo para explorar.
4. **Diagnóstico de 4 minutos**, distinto en cada ruta:
   - **Sistemas:** preguntas cortas con «¿Lo sabías o lo adivinaste?». Las adivinadas cuentan como no sabidas.
   - **Atención al cliente:** el «escenario espejo» (la misma situación en el salón y por chat) y una respuesta escrita. El resultado es un **mapa de habilidades transferibles**: lo que hacés hoy, cómo se llama en atención remota y en qué nivel estás.
   - **Oratoria:** contexto, nervios del 0 al 10 y una grabación de 60 s (simulada). El resultado es una fortaleza y 4 niveles con palabras, sin puntaje.

## Mi roadmap
- Meta, ritmo editable, tiempo estimado, progreso y racha.
- **Hoja de ruta vertical:** hitos hechos con su evidencia, el hito actual con sus nodos y los bloqueados con lo que dejan al terminarlos.
- **Sesión de hoy** (10 min) y **desafío en el mundo real** con check-in («Lo hice / No hubo oportunidad / No me animé»). En sistemas, ese lugar lo ocupa «Conectar GitHub», porque salir a las herramientas reales es parte de la meta.
- **Coach IA:** recomendaciones reactivas según cómo venís (pruebas de salto, nodos que se oxidan, lecturas externas).
- **Lecturas y externos:** lo que no se puede traer adentro entra como lectura. Al volver, la app te toma un caso de 2 minutos, y eso es lo que suma al nodo. Cada ítem muestra su licencia.
- Un aviso aclara que la demo muestra el roadmap avanzado (semana 4 o mes 5), para que se vea la práctica.

## Sesión de hoy
Repaso espaciado → microlección con su fuente y licencia → práctica (la mecánica estrella) → cierre con avance del nodo, desafío real y qué toca mañana.

## Las tres mecánicas estrella
- **Ticket de bug (sistemas).**
  - Reporte de una clienta, 3 decisiones con feedback, editor con 6 tests (2 ocultos) que **corren de verdad en el navegador**, commit y respuesta a la clienta sin jerga.
  - Cada paso está trazado a un punto clave de MDN.
  - El feedback del código sale de un catálogo de errores frecuentes (por ejemplo, la conversión que llega tarde o los precios escritos a mano).
- **Roleplay con cliente enojado (atención al cliente).**
  - Marcelo, con ficha, sistema de pedidos y política a la vista.
  - Termómetro de enojo y pistas en modo práctica; una sola chance en modo validación.
  - Resultado con curva de enojo, rúbrica de 5 criterios (política bloqueante), «lo mejor» y «una cosa para mejorar».
  - En el mockup se elige entre respuestas de ejemplo; en la app real se escribe libre.
- **Ensayo con feedback (oratoria).**
  - Consigna, preparación, grabación simulada y procesamiento.
  - Feedback en 3 capas: una fortaleza y un solo foco; detalle por criterio con marca de tiempo; transcripción con muletillas e idea principal marcadas.
  - Reintento con foco y comparación entre intentos y entre días.
  - Aclara que el conteo de «eh» es aproximado y que el audio es privado.

## Progreso y portfolio
- **Árbol de habilidades** con estados nuevos: «probado en la vida real» (cuando se completa el desafío afuera) y «se está oxidando» (cuando se falla el repaso dos veces).
- **Portfolio compartible** con la evidencia de cada hito y lo que se guarda desde las mecánicas.
- Botón para subir **evidencia de terceros** (certificados de otros sitios).

## Empresa (vista previa)
- Se carga una matriz de competencias por rol y documentos de ejemplo.
- «Lo que entendió Xpedia» muestra puntos clave con su página, como en v13 y v14.
- Mapa de brechas por persona, puntos clave más fallados («¿la política es confusa?») y reglas de privacidad.

## Qué está simulado
Todo lo que en la app real haría la IA: el cliente del roleplay, la evaluación de textos (en el mockup son reglas simples), la transcripción y el feedback del ensayo, y las respuestas del coach. Lo único que funciona de verdad son los tests del ticket de bug.
