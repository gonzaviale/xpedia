# Xpedia · Prototipo

Prototipo usable para mostrar cómo funciona Xpedia: una plataforma de aprendizaje que arma un roadmap personal, lo adapta según cómo te va, filtra las novedades de tu rubro y te manda un informe semanal.

No es un mockup estático: la lógica funciona de verdad sobre una ruta de ejemplo («IA aplicada»). Lo único simulado es la parte que en el producto haría un modelo de IA (ver más abajo).

## Cómo correrlo

Necesita Node 20 o superior.

```bash
cd prototipo
npm install
npm run dev
```

Se abre en <http://localhost:5180>. El progreso se guarda en el navegador; el botón de reiniciar (arriba a la derecha) vuelve a empezar.

Para mostrarlo en una demo:

1. Hacé el onboarding: objetivo, si programás, tiempo por día y un diagnóstico de 5 preguntas. Al final, el camino se arma hito por hito y una pantalla resume qué salteás, dónde arrancás y cuándo llegás.
2. En **Mi ruta**, la vista **Camino** muestra la ruta de arriba hacia abajo: cada hito con su estado, las bifurcaciones cuando dos temas salen del mismo, y las ramas opcionales en un carril lateral con línea punteada. Tocá un tema para ver qué necesita y qué desbloquea. **Grafo completo** muestra todos los prerrequisitos en horizontal.
3. **Practicá** un tema y fallá a propósito: al terminar, la ruta se ajusta y explica por qué.
4. Escribí en **¿Qué te interesa?** «notebooks», «modelos locales», «MCP» o «agentes»: cada uno da una decisión distinta.
5. Tocá **Simular semana** dos veces y abrí el **Informe** y el **Radar**.
6. **Cómo funciona** resume el motor con los números de tu sesión.

## El motor, en 5 piezas

| Pieza | Qué hace | Dónde está |
|---|---|---|
| Ruta como grafo | Cada tema declara sus prerrequisitos y su nivel. Permite adaptar, agendar y bifurcar | `src/data.ts`, `allNodes` y `status` en `src/engine.ts` |
| Evaluación | Preguntas de concepto, aplicación y detalle. Antes de ver la respuesta, la persona dice si lo sabía, lo dudó o lo adivinó | `pickQuestions` y `answer` |
| Perfil de rendimiento | Dominio por tema y patrones: tipo de pregunta que más falla, aciertos adivinados, lentitud, temas oxidados | `insights` |
| Adaptador | Agrega repasos, propone saltear lo que ya domina, reordena y abre ramas, siempre explicando el cambio | `finishSession`, `proposals` y `decideProposal` |
| Radar e informe | Filtra novedades según la ruta (ahora, más adelante o ruido) y arma el informe semanal | `newsView`, `report` y `reportMarkdown` |

**«Me interesa esto»** (`analyzeInterest`): compara los prerrequisitos del tema con el perfil y decide entre:

- **ya está en tu ruta**;
- **ahora** (tenés la base);
- **más adelante** (te falta poco y se agenda en el hito donde vas a tenerla);
- **rama opcional** (es otro camino y no frena la ruta principal);
- **guardado** (está lejos: te avisa cuando llegues).

## Qué es real y qué está simulado

- **Real:** el grafo de prerrequisitos, el dominio por tema, la detección de patrones, las adaptaciones, el filtro de novedades, la decisión de «me interesa esto» y el informe. Todo sale de reglas sobre tus respuestas.
- **Simulado o fijo:**
  - la ruta y las preguntas, que escribió el equipo; en el producto las generaría la IA a partir del objetivo;
  - las novedades, que son ficticias; en el producto saldrían de una búsqueda periódica;
  - la búsqueda de temas, que usa palabras clave sobre un catálogo chico;
  - «Simular semana», que genera práctica con resultados aleatorios (con semilla fija);
  - aceptar «saltear con una prueba», que da la prueba por aprobada.

Los lugares donde entraría un modelo están marcados con la etiqueta **IA** en la interfaz.

## Paleta

| Color | Uso | Hex |
|---|---|---|
| Azul eléctrico | IA: botones principales, acciones de la IA | `#1F5BFF` |
| Cian | IA: degradados, bordes de ramas y temas agendados | `#19C3F0` |
| Naranja neón | XP: progreso, tema en curso | `#FF7A1A` |
| Amarillo neón | XP: temas dominados, racha | `#FFD21F` |
| Gris pizarra | Pedia: texto | `#1C232E` |
| Pizarra medio | Pedia: texto secundario | `#5A6576` |
| Blanco | Pedia: fondo | `#F5F7FA` |

En modo claro, el cian y el amarillo no se leen bien como texto sobre blanco: se usan en rellenos, bordes y degradados, y los textos de acento usan azul y un naranja más oscuro.

## Estructura

```
src/
  data.ts          ruta de ejemplo, temas extra, novedades y diagnóstico
  engine.ts        el motor (funciones puras sobre el estado)
  store.ts         estado persistido en localStorage
  App.tsx          barra superior y navegación
  components/      Onboarding, MapView, Practice, Radar, InterestModal, Report, Motor
  styles.css       estilos y paleta
```
