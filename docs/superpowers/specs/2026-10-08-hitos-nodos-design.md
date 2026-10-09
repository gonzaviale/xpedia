# Estructura de una ruta
Continuación del catálogo público aprobada en la conversación. Se mantiene el esquema V1 y la arquitectura del README.
## Contrato
- GET /api/rutas/{rutaId}/hitos: lista de hitos de una ruta global publicada, ordenados por posicion e id.
- GET /api/rutas/{rutaId}/nodos: lista de nodos de la ruta, ordenados por posicion del hito (NULL al final), posicion del nodo e id.
- hitoId opcional en nodos: debe existir y pertenecer a la ruta solicitada; devuelve 404 en otro caso.
- Ambos devuelven arrays; una ruta visible sin contenido devuelve 200 con [].
- Rutas privadas, no publicadas o inexistentes devuelven 404. UUIDs inválidos devuelven 400.
- Los hitos incluyen objetivo, duración, etapa final y evidencia esperada.
- Los nodos incluyen su hito, rama y habilidad, código, tipo, nivel, minutos, palabras clave y prerrequisitoIds.
- Los nodos TEMA sin hito se incluyen en el listado general y se excluyen al filtrar por hito.
- Los prerrequisitos deben corresponder a nodos válidos de la misma ruta. Las referencias cruzadas entre rutas se excluyen, pues V1 no impone esa pertenencia.
- No se calcula progreso, dominio, desbloqueo o aprobación personal; dependen de inscripciones.
## Arquitectura
Features hito y nodo con modelos, puertos, servicios, casos de uso y mappers de dominio.
Entities JPA corresponden a hito, nodo y nodo_prerrequisito. Este último usa clave compuesta.
Se consulta la ruta visible antes de sus contenidos. Los prerrequisitos se cargan en lote, sin una consulta por nodo.
Se ignoran nodos cuyo hito pertenece a otra ruta y referencias de rama/habilidad privadas o ajenas.
## Plan y verificación
1. Mapear las tablas y enums existentes.
2. Implementar lecturas de hitos y nodos, carga en lote y controles de pertenencia.
3. Registrar casos de uso y exponer las dos consultas.
4. Agregar fixtures y pruebas HTTP en el PostgreSQL descartable existente: orden, filtros, arrays, temas sin hito, datos nulos, privacidad y UUIDs inválidos.
5. Probar la suite, el arranque dev contra la base local y OpenAPI. Mantener el servidor del usuario y sus datos intactos.
