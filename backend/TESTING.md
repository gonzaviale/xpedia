# Pruebas del backend

Desde `backend`, con Docker funcionando:

```powershell
.\mvnw.cmd clean verify
```

`verify` ejecuta todas las pruebas, empaqueta la aplicación, genera `target/site/jacoco/index.html` y verifica cobertura por clase. `test` ejecuta las pruebas pero no genera ni exige el reporte de `verify`.

## Alcance comprobado

| Capa | Pruebas | Qué verifican |
|---|---|---|
| Servicios ruta/hito/nodo | Una clase `*ServiceTest` por servicio | resultados, errores, filtros, pertenencia y bloqueo de consultas ante rutas ocultas |
| Casos de uso | Una clase `*UseCaseTest` por caso | entrada, salida y propagación de errores |
| Mappers de dominio, web y repositorio | Una clase `*MapperTest` por mapper | todos los campos, nulos, orden, paginación y saneamiento de referencias |
| Adaptadores de repositorio | Una clase `*RepositoryImplTest` por adaptador | consultas invocadas, mapeo, ausencia de resultados y agrupación en lote |
| Interfaces JPA | Una clase `I*JpaRepositoryTest` por repositorio | SQL/HQL real en PostgreSQL 17 con pgvector, filtros y relaciones cruzadas |
| Controladores | Pruebas MVC y `RutaPostgresIntegrationTest` | JSON HTTP, validación, estados, OpenAPI y número de consultas |
| Configuración, entidades, modelos y DTOs | Integración/contexto y contratos de mappers | wiring, Flyway V1, validación del esquema y serialización; no se duplican getters generados por Lombok con pruebas sin comportamiento |
| Datos piloto | Integración PostgreSQL | doble carga, fechas, ediciones, registros ajenos, rollback y UUID de Swagger |
| Microlecciones | Una clase de pruebas por servicio, caso de uso, mapper, adaptador, interfaz JPA y controlador | JSONB anidado, fuentes con licencia, material no aprobado, fuentes privadas, referencias cruzadas, nodo sin hito, array vacío, UUID inválidos, OpenAPI y cuatro consultas con 22 actividades |
| Errores comunes | `GlobalExceptionHandlerTest` | errores de campo/objeto, estados HTTP, trazas y mensajes sin detalles internos |
| Arquitectura y excepciones | `ArchitectureTest`, `DomainExceptionTest` | ninguna clase de dominio referencia infraestructura; conservación del mensaje y de la causa de una excepción |
| Arranque | `BackendApplicationTests` con `UseMainMethod.ALWAYS` | ejecuta el `main` real y carga el contexto con H2 aislado; el arranque con PostgreSQL se verifica además en integración y en el recorrido local |

JaCoCo exige **100% de líneas y ramas por clase en todo el código de producción analizado**, sin excluir módulos del backend. JaCoCo filtra automáticamente código generado, por ejemplo Lombok; las interfaces sin instrucciones no tienen líneas ejecutables que medir. La regla alcanza también `puesto`, configuración, controladores y arranque. El registro de casos de uso se verifica con el contexto Spring y los recorridos HTTP. La cobertura no demuestra por sí sola ausencia de errores ni reemplaza las pruebas de comportamiento.

Verificación de esta etapa (8/10/2026): **194 pruebas**, sin fallos, errores ni pruebas omitidas. JaCoCo: **532/532 líneas y 32/32 ramas** cubiertas, con control por clase aprobado. El CRUD `puesto` incluye además pruebas PostgreSQL para filtros, restricciones únicas, FK y callbacks de fechas.

Los contenedores de pruebas son temporales e independientes de `xpedia` local. `rutas-test.sql` es un fixture destructivo exclusivo de ellos; nunca se usa para cargar el entorno de desarrollo.

Para agregar funcionalidad: incorporar pruebas unitarias por clase con comportamiento, consultas de repositorio en PostgreSQL y casos HTTP positivos/negativos; ejecutar `clean verify` antes del commit y describir los resultados reales en su mensaje.
