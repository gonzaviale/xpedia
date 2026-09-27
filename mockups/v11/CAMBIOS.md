# v11 — cambios respecto de v9

Base: v9 (la v10 se trabaja en paralelo). Todo sigue siendo un único `index.html` + `support.js`.

## Sensación de app real
- Navegación con carga simulada (≈0,2–0,5 s): barra superior animada + esqueleto de la pantalla. El menú marca el destino al instante.
- Cambio de pestañas dentro de un tópico con carga corta solo en el área de la pestaña.
- Botones de acción con estado «ocupado» (spinner + texto): crear empleado, guardar, aprobar/rechazar misión, aprobar todas, crear empresa, guardar plan, agregar usuario, suspender/reactivar empresa, guardar cuenta.
- La generación de misiones muestra pasos («Leyendo la documentación…», «Armando escenarios…», etc.) con barra de progreso.

## Tópicos y misiones
- **Se simplificó: sin importancia manual ni dificultad manual.** Antes había tres conceptos parecidos (importancia del tópico por rol, nivel de habilidad esperado por rol, dificultad de la misión) que se pisaban entre sí y confundían. Ahora quedan solo dos:
  - **Qué necesita cada rol y en qué nivel**: se define una sola vez en **Roles y objetivos** (sin cambios ahí).
  - **Qué tan lejos está cada persona de eso**: lo calcula Xpedia solo y con eso arma todo lo demás.
  - En un tópico ya no se elige Baja/Media/Alta por rol ni se ve el % de reparto: el rol simplemente está o no está asignado (toggle). Xpedia reparte las misiones entre los tópicos de un rol priorizando, para cada persona, el tópico donde tiene más brecha respecto al nivel de su rol.
  - Al generar, la **dificultad ya no se elige a mano**: se calcula sola por motor según qué tan lejos está el equipo del nivel esperado (más brecha → misiones más guiadas; equipo al nivel o por encima → más exigentes). Queda un botón «Fijar manualmente» para el caso puntual de querer forzarla.
- **Generador**: modo *Automático* o *Elegir por motor* con contador por motor (se pueden pedir varias del mismo motor, máx. 20 por tanda). Panel de resumen con el botón principal, documentos de origen, roles destino y cupo mensual del plan.
- **Botón «Generar misiones» en la cabecera del tópico** (visible desde cualquier pestaña) + pasos guiados (Subir documentación → Asignar roles → Publicar misiones) + botones «Siguiente» + tooltips.
- «Orden respecto a otros tópicos» pasó a la pestaña **Roles y calendario** (era una propiedad del tópico, no de la tanda).

## Feedback del profe
- **Mismo idioma**: Delivery → Reparto, Checklist → Lista de control, Email → Correo electrónico, planes Inicial/Crecimiento/Escala, Superadmin → Administrador general, «Admin Xpedia» → «Equipo Xpedia», Black Friday → Temporada alta, etc.
- **Habilidades por rol**: nueva pantalla **Roles y objetivos** (matriz rol × habilidad con nivel esperado: No aplica / Básico / Intermedio / Avanzado). El perfil del empleado, las métricas (con filtro por rol) y los refuerzos se miden contra el nivel de su rol; lo que no aplica no cuenta.
- **Constancia**: bloque «Para mantener al equipo al día» en el Panel general (repaso pendiente, material actualizado, brecha por rol, campaña de temporada), **Repaso periódico** por tópico (cada 3/6/12 meses), KPI de **Retención** en Métricas y cupo mensual de generaciones.

## Configuración → Roles y objetivos
- Se quitó la pantalla Configuración (solo tenía el objetivo semanal global).
- El objetivo semanal ahora es **por rol** (fila «Ritmo semanal» en Roles y objetivos; se puede personalizar por persona).
- Nuevo bloque **Avisos automáticos**: recordatorio a quien va atrasado, resumen semanal por correo, aviso de repaso.

## Vista Empleado
- **Inicio centrado en qué hacer hoy**: arriba, la próxima misión recomendada (la que más ayuda a llegar al nivel que pide el rol) con el motivo y «Jugar ahora»; al lado, el objetivo semanal. Abajo, 3 misiones más con enlace a Aventura y un resumen de habilidades. Las estadísticas (XP total, misiones, precisión) pasaron a Historial.
- **Habilidades simples y por rol**: se reemplazó el árbol de 16 nodos. El empleado ve solo las habilidades de su rol, en la misma escala (0-100) y con los mismos niveles que ve la empresa (Básico 30 · Intermedio 55 · Avanzado 80). Cada una muestra su nivel, el que pide el rol, una frase con lo que falta y un botón para practicar.
- **Resultado de misión**: muestra el avance de la habilidad en esa misma escala y festeja cuando se sube de nivel o se alcanza el que pide el rol.
- **Arreglos**: el jefe final cuenta solo las misiones del capítulo actual; el «−20» de tensión aparece junto al medidor (antes quedaba cortado); textos del resultado sin espacios de más o de menos; nombres del mapa más anchos; los nodos de misión completada pasaron de morado a verde en el mapa (se confundían con el nodo pendiente del motor Diálogo, que también es morado).

- **Capítulos**: cada mes se abre un capítulo con las misiones que la empresa publica para el rol. El mapa muestra solo el capítulo actual; el anterior queda resumido arriba («Capítulo 1 · completado»). El jefe final cuenta las misiones del capítulo actual.
- **Datos coherentes**: esta semana Martín jugó 2 misiones del capítulo actual → objetivo semanal 2 de 5 y capítulo 2 de 5. Las del capítulo 1 son de semanas anteriores.
- **Mi progreso** (reemplaza Habilidades + Historial; el menú pasa de 5 a 4): estadísticas (XP, misiones, precisión, racha), pestaña Habilidades y pestaña Historial. Cada misión del historial se abre para releer el feedback y repetirla.
- **Repaso y racha**: las misiones de repaso llevan etiqueta «Repaso» y se recomiendan primero. Racha de semanas cumpliendo el objetivo semanal (no castiga los días libres), visible en Inicio y en Mi progreso.

## Motores
- **Auditor**: los errores encontrados se marcan en rojo (antes amarillo); los que se pasaron, con borde punteado.
- **Crisis**: +15 s por problema (90 s en total, antes 60 s). Si no queda ningún problema abierto, el siguiente llega enseguida (medio segundo después) y entra deslizándose desde abajo. Las misiones de Crisis ahora duran ≈ 2 min.
- **Auditor**: al encontrar el último error (o quedarse sin vidas o sin tiempo) hay una pausa de 5 s con el registro marcado antes de pasar al resultado.
- **Auditor · ronda 2**: después de la última tarjeta también hay una pausa de 5 s para ver si acertaste, con el total de la ronda, antes de pasar al resultado.

## Racha
- Al cumplir el objetivo semanal, el resultado de la misión lo festeja («¡Cumpliste el objetivo semanal! Racha de 5 semanas»).
- Insignias de constancia en Mi progreso: 4, 8, 12 y 26 semanas seguidas. Se aclara que la racha vuelve a cero si no se cumple una semana, con 1 semana de gracia por trimestre (vacaciones, licencias).
