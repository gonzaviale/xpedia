# Catálogo de rutas
Alcance aprobado en la conversación: listado y detalle de rutas; solo backend.
Se mantiene la arquitectura documentada en backend/README.md y el esquema V1.
## Contrato
- GET /api/rutas: filtros opcionales tipo y objetivo, page desde 0 (por defecto 0), size entre 1 y 100 (por defecto 20).
- Orden estable por titulo e id. Una página sin resultados devuelve 200 y content vacío.
- GET /api/rutas/{id}: detalle por UUID.
- Solo contenido global (organizacion_id NULL) y PUBLICADA en ambas consultas; cualquier otro estado o ruta de empresa devuelve 404.
- Filtros usan los valores exactos en mayúsculas del esquema. Conversión o validación inválida devuelve 400 con ErrorResponse.
- El listado incluye identidad, objetivo, duración y validación. El detalle añade perfil inicial, fechas y estado; no expone identificadores de revisores o empresas.
## Estructura
Modelos y enums de dominio, puerto de repositorio, servicio de lectura, un caso de uso por operación y mappers planos registrados como beans.
Infraestructura contiene entity JPA, consulta de persistencia, implementación del puerto y DTOs/mappers/controller web.
## Validación y entrega
Pruebas HTTP de binding, paginación, errores y serialización. Integración con PostgreSQL 17/pgvector y Flyway en un contenedor de pruebas descartable.
No se cargan datos de prueba en la base de desarrollo ni se modifica V1.
Se posponen hitos/nodos, diagnóstico, inscripción, escritura administrativa y autenticación.
## Plan
1. Mapear ruta y sus enums al esquema existente.
2. Implementar repositorio y servicio con la restricción de visibilidad.
3. Implementar casos de uso, DTOs y endpoints.
4. Verificar integración, errores y OpenAPI; documentar ejemplos para Swagger.
