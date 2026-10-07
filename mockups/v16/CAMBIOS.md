# v16: cambios respecto de v15

Extiende la v15 (no se reescribió desde cero: la v15 ya implementaba el enfoque de roadmaps a fondo). Mismo `index.html` único, mismo sistema de diseño Nocturne. Dos agregados, uno por cada versión del producto: **Novedades** para la persona individual y **armado conjunto de la ruta + tutor** para la empresa.

## Novedades (persona)
Resuelve una aclaración del modelo de negocio: el plan individual no solo arma un roadmap y lo deja fijo, también vigila lo que pasa en el rubro del usuario y decide cuándo mostrárselo.

- Nueva pestaña en el menú de la persona, con contador de no leídas.
- **Para vos ahora:** novedades relacionadas con el hito actual o uno ya hecho. Cada una se puede **sumar a la ruta** (entra como un nodo nuevo, marcado «Novedad», dentro del hito al que corresponde en «Mi roadmap») o **descartar**. Las dos acciones se pueden deshacer.
- **Más adelante:** novedades que tienen que ver con la ruta pero corresponden a un hito todavía no alcanzado. Cada una explica por qué Xpedia la retiene en vez de mostrarla ya (por ejemplo, React 19 se guarda para el hito «React» porque antes falta «Asincronía y fetch»).
- Implementado para las tres rutas piloto (sistemas, atención al cliente, oratoria), con 4 novedades de ejemplo cada una.
- En el mockup las novedades son fijas; en la app real saldrían de una búsqueda periódica filtrada por el roadmap de cada persona.

## Arma la ruta en conjunto + tutor de acompañamiento (empresa)
Resuelve la otra aclaración: a diferencia del plan individual, la empresa no recibe un roadmap ya armado — lo arma junto con Xpedia, y puede sumar acompañamiento humano.

- Nuevo paso **3 · Arma la ruta en conjunto**, entre «Lo que entendió Xpedia» y «Tu equipo»: a partir de la matriz de competencias y los documentos, Xpedia propone los hitos de la ruta. Cada uno se puede marcar para ajustar antes de confirmar.
- Botón **Confirmar ruta**, que deja el paso marcado como «Confirmada» y habilita el resto del flujo (antes, «Tu equipo» y lo que sigue se habilitaba directo al cargar los documentos).
- Nuevo paso **5 · Tutor de acompañamiento**: elegir entre tutor puesto por la empresa o tutor calificado por la comunidad. Es una decisión todavía abierta en el equipo (ver `docs/propuesta-roadmaps.md`), por eso el mockup deja probar las dos y aclara que no está definida.
- El resto del flujo de empresa (matriz, puntos clave, equipo, puntos clave más fallados, privacidad) sigue igual que en v15.

## Qué está simulado
Igual que en v15, más: las novedades son datos fijos en el código (no vienen de una búsqueda real) y la asignación de tutor es una confirmación visual sin lógica de matching real.
