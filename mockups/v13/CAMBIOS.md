# v13 — cambios respecto de v11

Base: v11. Todo sigue en un único `index.html` + `support.js`.

El problema que ataca esta versión: **la misión que aprobaba la empresa no era la que jugaba el empleado.** En v11 el contenido existía uno por formato y rubro, así que todas las misiones de Diálogo de un rubro jugaban la misma charla. Además, la empresa no veía qué había entendido Xpedia del documento, no podía probar ni corregir una misión, y el resultado no decía de qué regla salía cada respuesta.

## Cada misión tiene su propio contenido
- Guiones nuevos para las misiones que antes jugaban otra cosa: «La reserva que no aparece», «Heladera bajo la lupa» y el repaso de alérgenos, más sus equivalentes en salud y logística.
- Cada decisión del juego está atada a una regla del material, el **punto clave**, con su documento y su página.
- Arreglos de contenido:
  - El Auditor ya no tiene pistas en el texto («se deja así», «(no se preguntó)»).
  - Salud tiene una sola respuesta correcta en la ficha de ingreso.
  - En el protocolo de logística, la hoja de seguridad va antes que el equipo de protección.
  - El Diálogo ya no promete tiempos sin chequear con cocina.
  - La recepcionista de salud ya no da información clínica.

## Tópico en 3 pasos: Material → Lo que entendió Xpedia → Misiones
- **Material:** al subir un archivo se ve «Leyendo pág. X de N → Entendido · N puntos clave».
- **Lo que entendió Xpedia** (nuevo):
  - los puntos clave de cada documento, con tipo, página, marca de crítico y en cuántas misiones están;
  - se puede desmarcar lo que no se quiere entrenar;
  - el formato sale del tipo de punto clave: norma → Auditor, procedimiento → Protocolo, trato → Diálogo, prioridad → Crisis.
- **Misiones:**
  - aviso de las que esperan revisión;
  - «Te proponemos N misiones», una por cada punto clave sin misión, con su fuente y un solo botón;
  - el generador anterior quedó plegado en «Opciones avanzadas».
- Una sola forma de navegar: se sacaron los pasos duplicados, las pestañas y el botón «Generar» repetido. Roles, orden y repaso pasaron a «Ajustes».
- En el listado de tópicos:
  - se sacó «Tópicos por rol»;
  - cada tarjeta dice su próximo paso («1 misión para revisar», «2 puntos clave sin misión», «Al día»).

## Revisar misión (pantalla completa, reemplaza el panel lateral)
- **Estructura** de la misión: momentos, líneas, pasos o problemas. Cada parte se marca si tiene un problema o si está atada a un punto clave.
- **Editor:**
  - textos;
  - la mejor respuesta;
  - el porqué de cada opción;
  - el orden de los pasos;
  - la gravedad.
- **Calidad automática:**
  - «la mejor respuesta es siempre la más larga»;
  - «dos respuestas igual de buenas»;
  - «el texto delata el error»;
  - «todavía no la probaste».
- **Pedile un cambio a Xpedia:** el pedido se hace sobre una parte puntual. Xpedia devuelve una propuesta con lo que cambia; al aplicarla se crea una versión nueva, que se puede recordar para las próximas misiones del tópico.
- **Rechazar con motivo.** El motivo también queda como aprendizaje del tópico.
- **Probar como empleado:** se juega el motor real, con el cartel «Modo prueba», sin sumar XP ni quedar registrado.
- **Publicar congela una versión.** Si se edita una misión publicada, los empleados siguen jugando la anterior hasta volver a publicar.
- Historial con versiones y con quién aprobó y cuándo. «Publicar las revisadas» solo publica las que se abrieron.

## Juegos y resultado
- Diálogo: las respuestas se mezclan en cada partida (antes la correcta era siempre la 1).
- Auditor: las reglas no están a la vista; consultarlas cuesta 10 s.
- **Tus decisiones** en el resultado: cada elección, su porqué, la regla y la página del material.
- Repetir una misión solo suma si mejorás: no infla el objetivo semanal ni la habilidad.
- Protocolo: tablero donde te movés hasta cada punto y elegís el paso mientras te persigue «el caos». El caos y el reloj se frenan mientras decidís.
- **Jefe del protocolo (validación):**
  - se desbloquea al completar el entrenamiento;
  - trae otro escenario, hasta 5 opciones y una sola chance por paso;
  - pide confianza («seguro» o «con dudas») y mide el tiempo de decisión;
  - el resultado se ve paso por paso, y la empresa lo ve en la tarjeta **Validaciones** del perfil del empleado.
- Crisis: igual que en v11 (problemas simultáneos, priorización, control), ahora con el registro de cada decisión.

## Métricas
- **Puntos clave que más se fallan:** la regla concreta, el % que falla a la primera, los errores con seguridad y la marca de crítico.
- «Crear repaso» con un clic: copia la misión real que practica esa regla.

## Qué es simulado en el mockup
- Las misiones creadas desde la propuesta usan **contenido de muestra**, y la pantalla lo avisa. En el producto real, Xpedia las escribe a partir del punto clave.
- Las propuestas de «Pedile un cambio a Xpedia» siguen reglas simples: incorrectas más tentadoras, citar la fuente, sacar pistas del texto.
- Las páginas citadas son inventadas, pero siempre las mismas para cada regla.
- En Métricas, los datos del resto del equipo son de ejemplo; los de Martín salen de lo que juega.
