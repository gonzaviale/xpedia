# Datos piloto y consulta de microlecciones

Diseño aceptado en la conversación: carga manual de desarrollo, ejemplos de Swagger y consulta de microlecciones con fuentes. Solo backend; rama `backlog-Brian`.

## Datos piloto

- SQL independiente de Flyway en `backend/src/main/resources/db/dev/ruta-piloto.sql`.
- Una ruta global publicada exclusivamente de demostración, basada en los primeros tres hitos del borrador de atención al cliente. No representa la ruta completa ni contenido validado por el equipo.
- Tres hitos, dos ramas, seis nodos y cinco relaciones de prerrequisitos, con UUID fijos. No hay usuarios, inscripciones ni progreso.
- Transacción única; conflictos por ID no sobrescriben registros. Repetir la carga no duplica contenido ni cambia fechas o ediciones locales. Un conflicto con otra clave única aborta la transacción.
- La carga se ejecuta explícitamente en la base local. No se ejecuta al arrancar la aplicación.

## Microlecciones

- `GET /api/rutas/{rutaId}/nodos/{nodoId}/microlecciones`: lista de actividades `MICROLECCION` aprobadas de ese nodo, con contenido JSON y fuentes globales (título, URL, licencia, uso, permiso comercial y ubicación).
- Ruta global publicada obligatoria; nodo perteneciente a esa ruta y hito compatible. Ruta/nodo oculto o inexistente: 404. UUID inválido: 400. Nodo válido sin material visible: 200 con array vacío.
- Actividades con hito ajeno, o con fuentes privadas, se excluyen por completo. No se devuelven actividades pendientes/rechazadas, otros tipos, IDs de revisores ni respuestas de evaluaciones.
- Orden estable por nivel, fecha de creación e ID; fuentes por título e ID. Fuentes en lote, sin una consulta por actividad.
- Se conserva el JSON almacenado; no se genera contenido ni se personaliza por usuario en esta etapa.
- Una microlección ficticia marcada como DEMO y una fuente interna de prueba permiten verificar el endpoint. Su aprobación solo habilita la demostración técnica, no declara revisión editorial real.

## Verificación y commits

- Pruebas unitarias por clase con comportamiento en los módulos rutas/hitos/nodos y microlecciones: servicios, casos de uso, mappers y adaptadores.
- Consultas JPA y SQL probadas en PostgreSQL 17 con pgvector, Flyway V1 y validación de entidades. Pruebas HTTP, validación, OpenAPI, referencias cruzadas y cantidad de consultas.
- Probar doble carga, conservación de registros ajenos y editados, rollback ante error y recorrido con IDs piloto.
- Reporte JaCoCo y matriz de cobertura que distingue pruebas unitarias, integración y clases declarativas. No afirmar cobertura completa sin medirla.
- Commits separados para datos/documentación, cobertura de módulos existentes y microlecciones, con descripción de alcance y resultados reales.
