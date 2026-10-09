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

JaCoCo exige **100% de líneas y ramas por clase** en servicios, casos de uso, mappers y adaptadores implementados para estos módulos. El CRUD de ejemplo `puesto` mantiene sus pruebas existentes y queda fuera de ese umbral hasta su revisión. El umbral no afirma 100% de todo el proyecto ni demuestra por sí solo ausencia de errores.

Los contenedores de pruebas son temporales e independientes de `xpedia` local. `rutas-test.sql` es un fixture destructivo exclusivo de ellos; nunca se usa para cargar el entorno de desarrollo.

Para agregar funcionalidad: incorporar pruebas unitarias por clase con comportamiento, consultas de repositorio en PostgreSQL y casos HTTP positivos/negativos; ejecutar `clean verify` antes del commit y describir los resultados reales en su mensaje.
