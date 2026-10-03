# Ruta piloto 01 · Sistemas: desarrollo web desde cero

> Tipo: técnica · Documento de diseño para revisar en equipo · 3 de octubre de 2026
> Contexto: [propuesta-roadmaps.md](../propuesta-roadmaps.md) · Fuentes y licencias verificadas el 3/10/2026

---

## 1. Resumen

**Para quién.** Personas sin experiencia en programación que quieren postularse a puestos de **frontend junior o trainee**. Perfil típico: trabaja de otra cosa, aprende desde el celular en ratos libres y tiene acceso a una compu algunas horas por semana.

**Punto de partida.** Usa una compu y un navegador. No sabe qué es HTML ni abrió nunca una terminal.

**Meta.** Ser postulable (no "contratado/a": la ruta no garantiza trabajo) con evidencia concreta: un sitio personal publicado, 3 proyectos en GitHub (uno en React) con rúbrica aprobada y una entrevista técnica simulada aprobada.

**Alcance.** Es el núcleo del [MDN Curriculum](https://developer.mozilla.org/en-US/curriculum/) (estándares web, HTML semántico, CSS, JavaScript, accesibilidad, control de versiones) más dos de sus extensiones: frameworks y herramientas.

| Entra | Por qué |
|---|---|
| Cómo funciona la web, HTML semántico, CSS (flexbox, grid, responsive) y accesibilidad | Es la base de cualquier puesto frontend. La accesibilidad, además, distingue a un portfolio junior |
| JavaScript: lenguaje, DOM, `fetch`, asincronía | Es lo más pedido y lo más difícil, así que se lleva el 40 % del tiempo |
| Terminal mínima, Git, GitHub, DevTools, npm, Vite, deploy | Sin esto no se trabaja en equipo y la evidencia no queda en línea |
| React | Es el framework frontend más usado: 44,7 % en la [Stack Overflow Developer Survey 2025](https://survey.stackoverflow.co/2025/technology), contra 18,2 % de Angular y 17,6 % de Vue. Su documentación es CC BY 4.0 y está en español |
| Programar con asistentes de IA, con criterio | En 2026 es parte del trabajo: enseñamos a verificar el código, no a copiarlo |

**Queda afuera:** TypeScript, testing, Next.js, Tailwind y backend. Cada uno suma 30-60 h. Con JavaScript y React sólidos un junior ya es postulable, y sumar todo aumenta el abandono. Quedan como nodos opcionales y como base de una futura ruta "junior → semi-senior".

**Duración:** unas **300 h de práctica efectiva** (a calibrar en el piloto).

| Dedicación | Horas/semana | Sitio publicado (fin del hito 3, 52 h) | Ruta completa |
|---|---|---|---|
| 10-20 min/día | 1,2-2,3 | 5-10 meses | 2,5-5 años |
| 15 min/día + 2 h el fin de semana | ~3,75 | ~3,5 meses | ~18 meses |
| 1 h/día | 7 | ~7 semanas | ~10 meses |
| 2 h/día | 14 | ~4 semanas | ~5 meses |

> **Conclusión honesta:** con 10-20 min por día la ruta sirve para **explorar**: en 5-10 semanas terminás el hito 1 y sabés si te gusta. Para cambiar de trabajo no alcanza. Proponemos mostrar un **ritmo recomendado de 1 h/día** y usar la sesión de 10 minutos como piso para los días en que no da para más.

---

## 2. Diagnóstico inicial (3-5 minutos)

El diagnóstico **ubica** a la persona en el árbol; no le pone nota. Es adaptativo: si fallás dos preguntas seguidas de un bloque, ese bloque termina.

**Paso 1 · Meta y contexto (30 s)**
- ¿Para qué querés aprender? *Primer trabajo en sistemas · El sitio de mi emprendimiento · Entender a mi equipo técnico*
- ¿Cuánto tiempo por día tenés? *10 min · 20 min · 1 h · 2 h o más*
- ¿Tenés compu para practicar? *Sí · Solo celular*

**Paso 2 · Preguntas y mini-ejercicios (3-4 min).** Después de cada respuesta aparece el chip *"¿Lo sabías o lo adivinaste?"*. Las adivinadas cuentan como no sabidas.

| # | Bloque | Pregunta o mini-ejercicio | Respuesta esperada |
|---|---|---|---|
| 1 | Web | "Escribís una dirección y aparece una página. ¿Dónde estaba?" a) En tu compu b) En otra compu (un servidor) que te la mandó c) Adentro de Google | b |
| 2 | HTML | `<a href="contacto.html">Escribinos</a>`: "¿Qué pasa al tocar «Escribinos»?" | Abre contacto.html |
| 3 | HTML | Editor de una línea: "Hacé que esto sea el título principal: Panadería Don Luis" | `<h1>Panadería Don Luis</h1>` (lo valida un parser) |
| 4 | CSS | `.oferta { color: red; }` y tres párrafos, uno con `class="oferta"`: "Tocá el que se ve rojo" | El párrafo con la clase |
| 5 | JS | `let precio = 100; precio = precio * 2; console.log(precio);` | `200` |
| 6 | JS | `console.log("5" + 3);` | `"53"` |
| 7 | Git | "Te dicen «hacé un commit». ¿Qué te piden?" a) Guardar una versión de los cambios con una descripción b) Subir el sitio c) Borrar lo que no sirve | a |

**Cómo ajusta el roadmap**

| Resultado | Qué hace el roadmap |
|---|---|
| Falla el bloque Web/HTML (1-3) | Arranca en el hito 1, nodo "Cómo funciona la web" |
| Aprueba HTML y falla CSS | Ofrece la **prueba de salto** del hito 1: solo el reto final (10-15 min). Si la aprueba, el hito queda completo con su evidencia |
| Aprueba HTML y CSS, falla JS | Pruebas de salto de los hitos 1 y 2. Recomienda arrancar por el hito 3 |
| Acierta JS con "lo sabía" | Ofrece la prueba de salto del hito 5 (un reto de código de 10 min) |

- **El tiempo disponible** recalcula las fechas y el formato de la sesión: con 10 min hay repaso y un reto chico; con 1 h, lección, reto y avance del proyecto.
- **"Solo celular"** prioriza las mecánicas que funcionan en el teléfono (predecir la salida, ordenar líneas, debugging con decisiones). Avisa que desde el hito 3 hace falta una compu.
- **Recalibración:** si en la primera semana la persona aprueba más del 85 % de los retos al primer intento, el roadmap le propone saltar nodos.

---

## 3. Árbol de habilidades

Cuatro ramas, con los mismos estados de nodo que en v14 (bloqueado, siguiente, desbloqueado). Un nodo se **oxida** si fallás dos veces su repaso espaciado. Entre paréntesis va el hito donde se trabaja.

```
A · Estructura y estilo
    A1 Cómo funciona la web (H1) → A2 HTML semántico (H1) → A3 Selectores, cascada y box model (H2)
    → A4 Flexbox (H2) → A5 Grid y responsive (H4) → A6 Accesibilidad (H4)

B · Lógica
    B1 Variables, tipos y operadores (H5) → B2 Condicionales y bucles (H5) → B3 Funciones (H5)
    → B4 Arrays y objetos (H5) → B5 DOM y eventos (H6) → B6 Formularios (H6) → B7 Asincronía y fetch (H6)

C · Herramientas y oficio
    C1 Editor y DevTools (H1-H2) → C2 Terminal mínima (H3) → C3 Git y GitHub (H3) → C4 Deploy (H3)
    → C5 Debugging metódico (H5-H6) → C6 npm y Vite (H7) → C7 Programar con IA con criterio (H5-H8)
    → C8 Comunicación técnica: tickets, README, code review (H6-H8)

D · Aplicaciones
    D1 Pensar en componentes (H7) → D2 Props y estado (H7) → D3 Efectos y datos remotos (H7)
    → D4 Proyecto integrador (H8) → D5 Entrevista técnica (H8)
    Opcionales (punteados): TypeScript básico · Testing con Vitest · Tailwind
```

---

## 4. Hitos

**Columna "Uso"**
- **Adaptable:** la IA puede resumir, traducir y generar material derivado, con atribución (CC BY, CC BY-SA, MIT, CC0). Con CC BY-SA, lo derivado se publica con la misma licencia. Los ejercicios y tests que escribimos desde cero son nuestros.
- **Solo enlace:** licencias NC, ND o con todos los derechos reservados. Entran como "lectura externa" (sección 7).
- MDN: la fuente de verdad es la versión en inglés. La traducción al español es comunitaria y puede estar desactualizada, así que enlazamos las dos.
- **Ojo:** el *software* de freeCodeCamp es BSD-3, pero según su README el **currículo** es *copyright © freeCodeCamp.org*. Va como solo enlace.

### Hito 1 · Tu primera página (12 h)
- **Objetivo:** entender cómo llega una web a tu pantalla y escribir una página con HTML semántico.
- **Qué aprende:** cliente y servidor, URL; títulos, listas, links, imágenes con `alt`; `header`, `nav`, `main` y `footer`; ver el HTML con DevTools.
- **Material:**

| Fuente | Licencia | Uso |
|---|---|---|
| [MDN · Getting started](https://developer.mozilla.org/en-US/docs/Learn_web_development/Getting_started) y [Structuring content with HTML](https://developer.mozilla.org/en-US/docs/Learn_web_development/Core/Structuring_content) | CC BY-SA 2.5 (texto), CC0 (código) | Adaptable |
| [web.dev · Learn HTML](https://web.dev/learn/html) | CC BY 4.0 (texto), Apache 2.0 (código) | Adaptable |

  *La IA genera:* micro-lecciones de 2 minutos con ejemplos locales (la página de una panadería, un CV) y variantes de ejercicios. Cada tarjeta lleva su chip de fuente.
- **Práctica:** editor con vista previa en la app. Retos: *Ordená la página* (convertir un montón de `<div>` en etiquetas semánticas), *Predecí cómo se ve* y *Encontrá el link roto*.
- **Evaluación (4/4):** HTML válido (validador automático) · un solo `h1` y títulos sin saltos de nivel · `alt` que describe la imagen (la IA rechaza textos como "imagen1") · links que funcionan.
- **Evidencia:** "Mi primera página" (presentación personal o CV), publicada en el perfil de Xpedia con link para compartir.

### Hito 2 · Darle estilo (25 h)
- **Objetivo:** que la página del hito 1 se vea bien.
- **Qué aprende:** selectores, cascada y especificidad, color y tipografía, box model, flexbox; ver en DevTools qué regla gana.
- **Material:**

| Fuente | Licencia | Uso |
|---|---|---|
| [MDN · CSS styling basics](https://developer.mozilla.org/en-US/docs/Learn_web_development/Core/Styling_basics) y [Flexbox](https://developer.mozilla.org/en-US/docs/Learn_web_development/Core/CSS_layout/Flexbox) | CC BY-SA 2.5 | Adaptable |
| [web.dev · Learn CSS](https://web.dev/learn/css) | CC BY 4.0 | Adaptable |
| [Flexbox Froggy](https://flexboxfroggy.com/) (en español) | Código MIT; imágenes "Creative Commons", versión **a verificar** | El código se puede alojar en la app; las imágenes, recién cuando se verifique su licencia |

  *La IA genera:* retos "replicá esta captura" con variaciones, y explicaciones de por qué una regla no se aplica.
- **Práctica:** *Replicá el diseño* (comparación visual con tolerancia), *¿Qué regla gana?* con el inspector y el juego de flexbox.
- **Evaluación:** coincidencia visual de al menos 90 % · usa flexbox para alinear, no posiciones absolutas · contraste del texto de al menos 4,5:1.
- **Evidencia:** la página del hito 1 con estilo, en vista "antes y después".

### Hito 3 · Publicar y versionar (15 h)
- **Objetivo:** tener la página en internet y su historial en GitHub.
- **Qué aprende:** terminal mínima (`cd`, `ls`, `mkdir`); `git init`, `add`, `commit` y `push`; buenos mensajes de commit; GitHub Pages.
- **Material:**

| Fuente | Licencia | Uso |
|---|---|---|
| [MDN · Command line crash course](https://developer.mozilla.org/en-US/docs/Learn_web_development/Getting_started/Environment_setup/Command_line) y [Version control](https://developer.mozilla.org/en-US/docs/Learn_web_development/Core/Version_control) | CC BY-SA 2.5 | Adaptable |
| [GitHub Docs · Quickstart for GitHub Pages](https://docs.github.com/en/pages/quickstart) | CC BY 4.0 (texto), MIT (código) | Adaptable |
| [GitHub Skills · Introduction to GitHub](https://github.com/skills/introduction-to-github) | MIT | Se hace dentro de GitHub |
| [Learn Git Branching](https://learngitbranching.js.org/) (con traducción al español) | MIT | Se puede alojar en la app |
| [Conventional Commits (es)](https://www.conventionalcommits.org/es/v1.0.0/) | CC BY 3.0 | Adaptable |
| [Pro Git en español](https://git-scm.com/book/es/v2) | CC BY-NC-SA 3.0 | Solo enlace |

  *La IA genera:* escenarios de terminal simulada ("borraste un archivo: recuperalo") y ejemplos de commits buenos y malos.
- **Práctica:**
  1. Git simulado en la app, sin riesgo.
  2. *Misión real:* el mismo flujo en tu compu, con un checklist.
  3. A la vuelta pegás el link del repo y Xpedia lo verifica con la API pública de GitHub.
- **Evaluación:** repo público · al menos 3 commits con mensajes descriptivos (la IA puntúa cada uno de 0 a 2) · sitio en línea en `usuario.github.io` · explica en 2 líneas qué hace `git push`.
- **Evidencia:** la URL pública y el repo con su historial. Es el primer logro para compartir.

### Hito 4 · Para cualquier pantalla y cualquier persona (25 h)
- **Objetivo:** que el sitio funcione en el celular y sea accesible.
- **Qué aprende:** media queries, grid, unidades relativas; foco y teclado, contraste, `label` en formularios; Lighthouse.
- **Material:**

| Fuente | Licencia | Uso |
|---|---|---|
| [MDN · CSS layout](https://developer.mozilla.org/en-US/docs/Learn_web_development/Core/CSS_layout) y [Accessibility](https://developer.mozilla.org/en-US/docs/Learn_web_development/Core/Accessibility) | CC BY-SA 2.5 | Adaptable |
| [web.dev · Learn Responsive Design](https://web.dev/learn/design) y [Learn Accessibility](https://web.dev/learn/accessibility) | CC BY 4.0 | Adaptable |
| [Chrome for Developers · Lighthouse](https://developer.chrome.com/docs/lighthouse/overview) | CC BY 4.0 | Adaptable |
| [WCAG 2.2](https://www.w3.org/TR/WCAG22/) | W3C Document License: se puede copiar con atribución, no modificar | Solo cita y enlace |

  *La IA genera:* páginas rotas a propósito para auditar, y cada criterio explicado en lenguaje simple.
- **Práctica:** *Auditoría*: una página con 6 problemas escondidos para encontrar y arreglar: un botón hecho con `div`, una imagen sin `alt`, poco contraste, un input sin `label`, un salto de `h1` a `h4` y foco invisible. La vista previa tiene un selector de 320, 768 y 1280 px.
- **Evaluación:** Lighthouse ≥ 90 en Accesibilidad · sin scroll horizontal a 320 px · se navega todo con teclado · encuentra al menos 5 de los 6 problemas.
- **Evidencia:** el sitio actualizado y el informe de Lighthouse de antes y después.

### Hito 5 · Pensar en código (55 h)
- **Objetivo:** resolver problemas chicos con JavaScript y leer código ajeno.
- **Qué aprende:** variables, tipos, operadores, condicionales, bucles, funciones, arrays y objetos, `map` y `filter`, la consola.
- **Material:**

| Fuente | Licencia | Uso |
|---|---|---|
| [MDN · Dynamic scripting with JavaScript](https://developer.mozilla.org/en-US/docs/Learn_web_development/Core/Scripting), [JavaScript Guide](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide) y [What went wrong?](https://developer.mozilla.org/en-US/docs/Learn_web_development/Core/Scripting/What_went_wrong) | CC BY-SA 2.5 | Adaptable |
| [Exercism · JavaScript](https://exercism.org/tracks/javascript) (159 ejercicios) | MIT (repositorio del track) | Adaptable: se traducen, con atribución |
| [Chrome DevTools · Console](https://developer.chrome.com/docs/devtools/console) | CC BY 4.0 | Adaptable |

  *La IA genera:* variantes de cada ejercicio (para que la solución no circule), pistas en 3 niveles y explicaciones de errores frecuentes.
- **Práctica:** retos con tests · *Predecí la salida* · *Ordená las líneas* (problemas de Parsons, ideales en el celular) · *Explicá esta función* (la IA evalúa la explicación) · primeros *Tickets de bug*.
- **Evaluación:** si un reto pasa lo deciden **los tests, no la IA** · legibilidad: nombres claros y funciones cortas (rúbrica de 0 a 2) · explica su solución en 3 líneas.
- **Evidencia:** el mini-proyecto "La vaquita" (divide los gastos de un grupo y dice quién le debe a quién) y un repo con 15 retos resueltos.

### Hito 6 · Páginas que responden (55 h)
- **Objetivo:** conectar JavaScript con la página y con datos reales.
- **Qué aprende:** DOM, eventos, formularios y validación, `fetch`, `async`/`await`, JSON, estados de carga y de error, `localStorage`.
- **Material:**

| Fuente | Licencia | Uso |
|---|---|---|
| [MDN · Making network requests](https://developer.mozilla.org/en-US/docs/Learn_web_development/Core/Scripting/Network_requests), [Asynchronous JavaScript](https://developer.mozilla.org/en-US/docs/Learn_web_development/Extensions/Async_JS) y [Web forms](https://developer.mozilla.org/en-US/docs/Learn_web_development/Extensions/Forms) | CC BY-SA 2.5 | Adaptable |
| [API Georef Argentina](https://datosgobar.github.io/georef-ar-api/) (`https://apis.datos.gob.ar/georef/api/provincias`) | Datos CC BY 4.0, código MIT | API real para practicar |

  *La IA genera:* tickets con código roto y variantes de formularios para validar.
- **Práctica:** **Ticket de bug** (la mecánica estrella, sección 6) · *Construí la función*, con tests que simulan clics · *Sin conexión*: qué ve el usuario si falla el `fetch`.
- **Evaluación:** pasa los tests de comportamiento · contempla carga, error y lista vacía · formulario accesible (cada error asociado a su campo) · commits claros.
- **Evidencia:** un "Buscador de localidades" publicado: elegís una provincia y ves sus municipios.

### Hito 7 · Componentes con React (60 h)
- **Objetivo:** construir una interfaz con componentes y estado.
- **Qué aprende:** npm, Vite, JSX, componentes, props, `useState`, listas con `key`, efectos y datos remotos.
- **Material:**

| Fuente | Licencia | Uso |
|---|---|---|
| [React · Aprende (es)](https://es.react.dev/learn) | CC BY 4.0 | Adaptable: es la mejor fuente de la ruta |
| [Vite · Getting Started](https://vite.dev/guide/) | MIT (licencia del repositorio, que incluye la documentación) | Adaptable |
| [MDN · Getting started with React](https://developer.mozilla.org/en-US/docs/Learn_web_development/Core/Frameworks_libraries/React_getting_started) y [Understanding client-side tools](https://developer.mozilla.org/en-US/docs/Learn_web_development/Extensions/Client-side_tools) | CC BY-SA 2.5 | Adaptable |

  *La IA genera:* lecciones adaptadas de react.dev y retos de componentes.
- **Práctica:**
  - *Partí la pantalla:* sobre una captura, la persona dibuja cajas para separar componentes y dice qué es prop y qué es estado. Funciona en el celular.
  - *Revisá el código de la IA:* la app muestra una solución "sugerida por un asistente" con un error sutil (una `key` inestable o estado duplicado), y la persona la aprueba o pide cambios. Entrena el nodo C7.
  - Migrar el buscador del hito 6 a React.
- **Evaluación:** cada componente tiene una sola responsabilidad · el estado es mínimo y no guarda lo que se puede calcular · las listas usan una `key` estable · `useEffect` solo para sincronizar con algo externo · el build no tiene errores.
- **Evidencia:** el buscador en React, publicado y con README.

### Hito 8 · Proyecto final y salida laboral (50 h)
- **Objetivo:** demostrar todo junto, como en un trabajo real.
- **Qué aprende:** leer un brief, planificar con issues, trabajar con ramas y pull requests, recibir code review, escribir un README.
- **Material:** el módulo *Soft skills* del [MDN Curriculum](https://developer.mozilla.org/en-US/curriculum/) (contenido de MDN, CC BY-SA 2.5) y GitHub Docs sobre issues y pull requests (CC BY 4.0). *La IA genera:* el brief, el code review sobre el pull request real y el entrevistador del roleplay.
- **Práctica:**
  - un proyecto con brief a elegir (por ejemplo, "El comedor del barrio necesita publicar el menú semanal y sumar voluntarios"), con dos rondas de code review de la IA;
  - **entrevista técnica simulada** (roleplay por chat o voz, 20 minutos): contar el proyecto, resolver un debugging en vivo y responder preguntas de conducta.
- **Evaluación:** 5 criterios, de 0 a 3 cada uno: funcionalidad · calidad del código · accesibilidad y performance (Lighthouse ≥ 90) · Git y README · comunicación en la entrevista. Aprueba con 10/15 o más y ningún criterio en 0.
- **Evidencia:** el proyecto final, el informe de la rúbrica y una **credencial de la ruta** con links a toda la evidencia, lista para el CV o LinkedIn.

**Total:** 12 + 25 + 15 + 25 + 55 + 55 + 60 + 50 = **297 h**.

---

## 5. Una sesión de 10 minutos, paso a paso

**Hito 2, día 9: la barra de navegación.** Caro tiene 29 años y es moza; eligió 20 minutos por día. Hoy tiene 10, en el celular, viajando en el colectivo. El editor ofrece autocompletado táctil (chips con propiedades y valores), así que no tiene que tipear todo.

| Minuto | Paso | Qué ve y qué hace |
|---|---|---|
| 0:00-1:30 | **Repaso espaciado** | Dos tarjetas de lo que falló antes. "¿Qué agrega espacio adentro del borde: `margin` o `padding`?" Acierta. "¿Por qué este título no se pone azul?" (`#titulo` le gana a `.destacado` por especificidad). Falla, y la tarjeta vuelve en 2 días |
| 1:30-3:30 | **Micro-lección** | "Flexbox en 3 propiedades": `display: flex` pone a los hijos en fila, `justify-content` los reparte a lo largo de la fila y `align-items` los alinea en vertical. Demo con 3 cajas y botones para probar `flex-start`, `center` y `space-between`. Chip de fuente: *MDN · Flexbox · "Horizontal and vertical alignment" · CC BY-SA 2.5 · traducido y adaptado* |
| 3:30-7:30 | **Reto sobre su propia página** | "Tu nombre está arriba y los links debajo. Ponelos en la misma fila: el nombre a la izquierda y los links a la derecha." Tests en vivo: ① el `nav` usa flex ✓ ② nombre y links en la misma fila ✓ ③ separados a los extremos ✗ ④ centrados en vertical ✗ |
| 7:30-9:00 | **Feedback** | Caro puso `justify-content: space-between` en el `ul`. Feedback de catálogo (es un error frecuente y no gasta IA): *"Casi. `justify-content` reparte a los **hijos directos** del contenedor flex. Tu `nav` tiene dos hijos, el nombre y la lista, así que la propiedad va en el `nav`."* La mueve, suma `align-items: center` (lo vio en la demo) y pasa los 4 tests |
| 9:00-10:00 | **Cierre** | El nodo Flexbox queda en 2/3. *Guardá esta versión:* escribe en una frase qué cambió ("Nombre y links en una fila"), como preparación para los commits del hito 3. Mañana sigue con `flex-wrap`, para que la barra no se rompa en pantallas chicas |

---

## 6. Mecánica estrella: "Ticket de bug"

**Qué entrena.** El trabajo real de un junior: recibir un reporte, reproducir el error, formular una hipótesis, verificarla, arreglar, documentar y explicarle el problema a alguien no técnico. Junta el escenario con decisiones de v14, un reto de código con tests y la comunicación.

**Ficha.** Hito 6 (hay una versión simple en el hito 5). Dura 8-12 minutos. Todo funciona en el celular salvo el paso 4, que necesita editor.

**Puntos clave trazados a la fuente**
- **PC1:** lo que leés de un atributo `data-*` con `dataset` es texto (string). Fuente: [MDN · HTMLElement: dataset property](https://developer.mozilla.org/en-US/docs/Web/API/HTMLElement/dataset).
- **PC2:** si uno de los lados de `+` es texto, JavaScript concatena en vez de sumar. Fuente: [MDN · Addition (+)](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Operators/Addition).
- **PC3:** `Number(valor)` convierte un texto en número. Fuente: [MDN · Number() constructor](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Number/Number).
- **PC4:** antes de tocar el código, reproducí el error y verificá la hipótesis con la consola. Fuente: [MDN · What went wrong? Troubleshooting JavaScript](https://developer.mozilla.org/en-US/docs/Learn_web_development/Core/Scripting/What_went_wrong).

### Pantalla 1 · El ticket

> **#142 · El carrito suma mal** · Prioridad: alta · Reporta: Marta, dueña de "La Yerbera"
> "Agrego el mate y el total dice $01500. Agrego la bombilla y dice $015002300. ¡Tendría que decir $3800! Los clientes se asustan."

```html
<ul>
  <li><button class="agregar" data-precio="1500">Mate de calabaza</button></li>
  <li><button class="agregar" data-precio="2300">Bombilla de alpaca</button></li>
</ul>
<p>Total: $<span id="total">0</span></p>
```

```js
let total = 0;
const botones = document.querySelectorAll(".agregar");

botones.forEach((boton) => {
  boton.addEventListener("click", () => {
    total = total + boton.dataset.precio;
    document.querySelector("#total").textContent = total;
  });
});
```

### Paso 1 · ¿Qué hacés primero?

| Opción | Feedback | Efecto |
|---|---|---|
| A. Reescribo la función del carrito desde cero | "Todavía no sabés qué está mal: podrías volver a escribir el mismo error. Primero, reproducilo." | Pierde confianza y vuelve a elegir |
| **B. Reproduzco el error: hago los mismos clics y miro el total** ✓ | "Bien. Reproducir confirma el problema y te deja un caso para probar el arreglo." | +10 XP |
| C. Le pido la solución a la IA | "En un trabajo real a veces se hace. Pero si no entendés la causa, no vas a saber si la respuesta es correcta. Te damos una pista." | Pista: *"Fijate de qué tipo es cada cosa que sumás."* Sin XP en este paso |
| D. Le digo a Marta que es un problema de su navegador | "Sin evidencia, no. Además, el total tiene un patrón: parece que los números se están pegando." | Pierde confianza |

### Paso 2 · Después de un clic se ve `01500`. ¿Cuál es la hipótesis más probable?

| Opción | Feedback |
|---|---|
| A. El precio está mal cargado en el HTML | "Los precios del HTML son 1500 y 2300: están bien. Lo raro es que aparecen *pegados*." |
| **B. Se están uniendo textos en lugar de sumar números** ✓ | "Exacto: `01500` es un 0 seguido de "1500". Eso es concatenar (PC2)." |
| C. El evento se dispara dos veces | "En ese caso verías el precio repetido con un solo clic (`015001500`)." |
| D. `textContent` no puede mostrar números | "Sí puede: `textContent` muestra lo que le des. El problema está antes." |

### Paso 3 · ¿Cómo confirmás la hipótesis?

| Opción | Feedback |
|---|---|
| **A. `console.log(typeof boton.dataset.precio)`** ✓ La consola muestra `"string"` | "Confirmado: el precio llega como texto (PC1), y `0 + "1500"` da `"01500"` (PC2)." |
| B. Cambio los precios del HTML y pruebo de nuevo | "Vas a ver el mismo patrón con otros números. No confirma la causa." |
| C. Borro el caché del navegador | "El caché no cambia la forma en que se suman los valores." |
| D. `console.log(total)` | "Ayuda: verías `01500`, pero no sabrías de qué tipo es el precio. Probá con `typeof`." (crédito parcial, +5 XP) |

### Paso 4 · Arreglalo

**Consigna:** "Cambiá el JavaScript para que el total sume bien. No toques el HTML." El HTML se restaura antes de correr los tests.

| # | Caso | Esperado |
|---|---|---|
| 1 | Al cargar la página | `#total` muestra `0` |
| 2 | Clic en Mate | `1500` |
| 3 | Clic en Mate y en Bombilla | `3800` |
| 4 | Clic en Mate, Mate y Bombilla | `5300` |
| 5 (oculto) | El HTML de prueba trae un tercer botón con `data-precio="850"`; clic en él y en Mate | `2350` (verifica que no haya precios escritos a mano) |
| 6 (oculto) | `typeof total` después de dos clics | `"number"` |

**Soluciones aceptadas:** `Number(boton.dataset.precio)`, `parseInt(boton.dataset.precio, 10)`, `parseFloat(...)` o `+boton.dataset.precio`, siempre que la conversión se haga antes de sumar.

**Feedback por patrón.** Es un catálogo precomputado; la IA solo se usa cuando la respuesta no coincide con ningún patrón.

| Lo que hizo | Resultado | Feedback |
|---|---|---|
| `total = Number(total + boton.dataset.precio)` | Falla el test 3 (da 15002300) | "La conversión llega tarde: primero se pegan los textos y después convertís el resultado. Convertí el precio **antes** de sumarlo." |
| `if (boton.textContent === "Mate de calabaza") total += 1500; ...` | Falla el test 5 | "Funciona para estos dos productos, pero la tienda va a vender más. Usá el precio que ya trae cada botón." |
| `total += +boton.dataset.precio` | Pasa todos | "Funciona. El `+` de adelante convierte a número, pero cuesta leerlo. En un equipo, `Number(...)` deja más clara la intención." |
| `parseInt(boton.dataset.precio)` sin base | Pasa todos | "Funciona. Pero si un día hay precios con centavos (`"1250.50"`), `parseInt` los corta. Pensá si no te conviene `Number`." |

### Paso 5 · Mensaje de commit

"Escribí el mensaje del commit que arregla esto."
- **Rúbrica (de 0 a 2 por criterio, la evalúa la IA):** dice qué cambió · dice dónde o por qué · la primera línea tiene 72 caracteres o menos · bonus si usa el prefijo `fix:` de Conventional Commits.
- **Ejemplos:** ✓ `fix: convertir el precio a número antes de sumarlo al total` · ✗ `arreglos`

### Paso 6 · Respuesta a Marta

"Contale a Marta qué pasó, en 2 o 3 líneas y sin palabras técnicas."
- **Rúbrica:** sin jerga (no aparecen "string" ni "concatenar") · dice que está resuelto y cómo se comprobó · tono amable.
- **Respuesta modelo:** *"Hola, Marta. Ya está resuelto: el sistema leía los precios como texto y los pegaba en lugar de sumarlos. Probé con varios productos y el total da bien (mate + bombilla = $3800)."*
- **Feedback ante una respuesta floja:** *"Se entiende que lo arreglaste, pero «era un string concatenado» es jerga para Marta. Probá con «leía los precios como texto»."*

### Resultado
- **Puntaje:** decisiones de los pasos 1-3 (30), tests (40), commit (15) y respuesta a Marta (15).
- **Repaso:** cada punto clave fallado vuelve como tarjeta. Si fallaste el paso 2, en 2 días te aparece un "predecí la salida" sobre concatenación.
- **Modo validación** (prueba de hito): una sola chance por paso y sin pistas, como el juego del virus de v14.
- **Variantes:** mismos puntos clave, otro contexto. Por ejemplo, un contador de "me gusta" que muestra `0111`, o una edad que viene de un `<input>` y al sumarle 1 da `"301"`. Cada variante cita su propia fuente.
- **Pantallas para el mockup:**
  1. Ticket y código de solo lectura.
  2. a 4. Tarjetas de decisión con feedback en línea.
  5. Editor y panel de tests.
  6. Commit y respuesta a Marta.
  7. Resultado con los puntos clave y sus fuentes.

---

## 7. Contenido externo

| Recurso | Licencia o tipo | Cómo entra a la ruta |
|---|---|---|
| [Eloquent JavaScript](https://eloquentjavascript.net/), 4.ª ed. ([traducción al español](https://eloquent-javascript-es.vercel.app/)) | Libro online, CC BY-NC 3.0 (el código, MIT) | Lectura del hito 5, capítulos 1 a 3 |
| [El Tutorial de JavaScript Moderno](https://es.javascript.info/) | CC BY-NC según su [LICENSE.md](https://github.com/javascript-tutorial/en.javascript.info); el autor ofrece permisos para uso comercial | Consulta por nodo. Hay una posible alianza |
| [You Don't Know JS Yet](https://github.com/getify/You-Dont-Know-JS) | CC BY-NC-ND 4.0 | Solo enlace, para profundizar |
| [The Odin Project](https://github.com/TheOdinProject/curriculum) | CC BY-NC-SA 4.0 | Proyectos extra. Xpedia revisa el repo que resulta |
| [CS50x](https://cs50.harvard.edu/x/) | CC BY-NC-SA 4.0 | Opcional: fundamentos de computación |
| [freeCodeCamp](https://www.freecodecamp.org/) | Software BSD-3; currículo © freeCodeCamp.org | Credencial complementaria: importás el certificado y Xpedia lo valida con un reto |
| [Frontend Mentor](https://www.frontendmentor.io/) | Propietario, con plan gratuito | Desafíos de diseño opcionales en el hito 8 |
| [roadmap.sh](https://roadmap.sh/) | Solo uso personal; prohíbe reutilizar su contenido | Referencia de mercado, no contenido |
| Cursos pagos (Scrimba, socio del MDN Curriculum; Udemy; Frontend Masters) y videos | Propietario | Lectura opcional. Los videos se insertan con el reproductor oficial cuando el autor lo permite |

**Cómo funciona una "lectura externa" sin sacarte de la app**
1. **Tarjeta de lectura:** qué leer (capítulo y secciones), por qué, cuánto tiempo lleva y qué te vamos a preguntar a la vuelta.
2. **Lectura adentro:** se abre en el navegador interno, con un botón fijo "Ya lo leí".
3. **Check de vuelta (2 minutos):** un caso corto o un "predecí la salida". Se arma con **nuestras fuentes abiertas, no con el texto del libro**, para no crear obras derivadas de material NC.
4. **Resultado:** si aprobás, el nodo avanza; si no, la app te ofrece su micro-lección del tema.
5. **Proyectos externos** (Odin, Frontend Mentor): pegás el link del repo, Xpedia corre la rúbrica y te hace 2 preguntas sobre tu código para validar que es tuyo.

> Salir a las herramientas reales (editor, terminal, GitHub) es parte de la meta. Lo que no queremos es que la persona tenga que ir a **aprender** a otro lado.

---

## 8. Versión empresa

**Ejemplo:** una software factory de 120 personas que incorpora 15 trainees por año. Trabaja con React y TypeScript, Tailwind y Vitest, y en GitHub con trunk-based y pull requests obligatorios.

| Qué adapta | Cómo | Ejemplo |
|---|---|---|
| Nodos | Agrega, reemplaza o saca | Suma TypeScript, Testing Library y su design system. Cambia Georef por su API de staging |
| Fuentes | Sube sus documentos; la IA los usa como fuente de verdad, con trazabilidad a la página | "Guía de PR v3, pág. 2: todo pull request que cambia la interfaz lleva capturas" |
| Estilo | Su guía propia o una base pública como la [Airbnb JavaScript Style Guide](https://github.com/airbnb/javascript) (MIT) | Los retos se corrigen con su configuración de ESLint |
| Tickets | La mecánica "Ticket de bug" usa bugs reales y anonimizados de su Jira | El onboarding pasa a ser "resolvé 5 tickets como los que vas a recibir" |
| Niveles | Carga su matriz de competencias | Ver la tabla de abajo |

**Nivel esperado por rol (de 0 a 4).** El roadmap de cada persona es la **brecha** entre el nivel esperado de su rol objetivo y el nivel medido, igual que en v14.

| Competencia | Trainee | Junior | Semi-senior |
|---|---|---|---|
| HTML, CSS y accesibilidad | 2 | 3 | 3 |
| JavaScript y TypeScript | 1 | 2 | 3 |
| React | 1 | 2 | 3 |
| Testing | 0 | 1 | 2 |
| Git y code review | 1 | 2 | 3 |
| Comunicación técnica | 1 | 2 | 3 |

**Qué ve la empresa en las métricas**
- **Brecha por persona y equipo:** un mapa de calor de competencias por persona.
- **Tiempos clave:** cuánto tarda cada persona en estar lista para su primer ticket real y, si conectan GitHub, en tener su primer pull request aprobado.
- **Puntos clave más fallados.** Si el 62 % falla "estados de carga y error", la pregunta es si su componente base no lo resuelve o si su guía no lo explica. Son fallas del proceso, no solo de las personas.
- **Evidencia por persona:** rúbricas de proyectos y tickets, exportables para evaluaciones de desempeño o promociones.
- **Comparación entre cohortes de ingreso.**

---

## 9. Riesgos específicos de esta ruta

| Riesgo | Mitigación |
|---|---|
| **~300 h chocan con la promesa de "10 min por día"** | Mostrar el ritmo recomendado desde el onboarding. Hitos chicos al principio, con un logro visible en el hito 3. Un modo liviano con una meta honesta: explorar |
| **Escribir código en el celular es incómodo** | En el celular: predecir, ordenar líneas, decisiones y edición con chips. Los proyectos van en compu, con un botón "seguir en la compu" |
| **No se pueden evitar las herramientas externas** (VS Code, terminal, GitHub) | Simularlas en la app durante los hitos 1-3. Después, misiones reales con checklist y verificación automática por la API de GitHub |
| **La IA se equivoca o queda desactualizada** (APIs viejas de React, herramientas discontinuadas) | Si un reto pasa lo deciden tests determinísticos. Fuentes ancladas a versiones, sandbox con versiones fijas, revisión humana del contenido cacheado, botón "reportar error" y revisión trimestral de la ruta |
| **Copiar con un chatbot y que la evidencia pierda valor** | Variantes por usuario, "explicá esta línea" y defensa del proyecto en la entrevista simulada. Usar IA se permite y se enseña (nodo C7): se evalúa si entendiste |
| **Mercado junior competitivo** | Hablar de "postulable", no de "contratado". Un portfolio que se diferencie por accesibilidad, comunicación y trabajo con Git |
| **CC BY-SA obliga a liberar lo que adaptamos de MDN; las licencias NC dejan afuera buenos recursos** | Priorizar CC BY (web.dev, react.dev, GitHub Docs) para adaptar. Registro de atribución por pieza. Las fuentes NC, solo como enlace |
| **Correr código del usuario: costo y seguridad** | Ejecutar en el navegador, en un iframe aislado. En GitHub, permisos mínimos de solo lectura |

---

## 10. Métricas de validación del piloto

**Diseño:** 40 personas durante 8 semanas, en dos grupos según el ritmo elegido (hasta 20 min/día, y 45 min/día o más). Alcanza con los hitos 1 a 4 completos en la app y la primera parte del hito 5.

| Métrica | Cómo se mide | Meta |
|---|---|---|
| Activación | Completa el diagnóstico y la primera sesión el día 1 | ≥ 70 % |
| Retorno | Vuelve al menos 3 días durante la semana 2 | ≥ 40 % |
| **Métrica principal** (de la propuesta) | Completa el hito 2 y vuelve la semana siguiente | Grupo intensivo: ≥ 35 % en 6 semanas. Grupo liviano: ≥ 30 % completa el hito 1 (el hito 2 no entra en 8 semanas) |
| Evidencia real | Publica su sitio (fin del hito 3) | ≥ 25 % del grupo intensivo |
| Dificultad calibrada | Retos aprobados al primer intento | Entre 50 % y 75 % |
| Tiempo calibrado | Desvío entre horas reales y estimadas por hito | < 30 % |
| Calidad del contenido | Auditoría manual de 50 feedbacks de IA por semana, más la encuesta "¿te sirvió?" | 0 errores conceptuales graves; ≥ 80 % de "me sirvió" |
| Diagnóstico confiable | Quienes saltan un hito aprueban la prueba del siguiente | ≥ 80 % |
| Lectura externa | Vuelve y aprueba el check en 48 h o menos | ≥ 60 % |
| Costo | Costo de IA por usuario activo por semana; porcentaje de feedback que sale del catálogo | Por debajo del tope del plan gratuito; ≥ 60 % desde el catálogo |
| Validación externa | 3 líderes técnicos o reclutadores IT revisan portfolios a ciegas: "¿esto muestra progreso real?" | Al menos 2 de 3 dicen que sí en la mayoría de los casos |

Además, 8 entrevistas cualitativas con foco en el salto a la terminal y GitHub y en el uso desde el celular.

**Antes de lanzar el piloto, el equipo tiene que decidir:**
1. Qué ritmo comunicamos para esta ruta: 1 h/día recomendado, o una meta distinta para el modo liviano.
2. Desde qué hito se pasa a herramientas reales y si pedimos conectar GitHub.
3. Si aceptamos publicar como CC BY-SA lo que adaptemos de MDN, y si buscamos acuerdos con las fuentes NC (javascript.info, The Odin Project).
