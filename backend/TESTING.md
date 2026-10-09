# Pruebas del backend

Desde `backend`, con Docker funcionando (las pruebas de repositorio y de integración usan PostgreSQL 17 con pgvector en un contenedor temporal):

```powershell
.\mvnw.cmd clean verify
```

`verify` ejecuta todas las pruebas, empaqueta la aplicación y genera el reporte de cobertura en `target/site/jacoco/index.html`. `test` ejecuta las pruebas sin generar el reporte. Para correr solo algunas clases:

```powershell
.\mvnw.cmd test "-Dtest=CrearPuestoUseCaseTest,PuestoServiceTest"
```

## Estructura

Los tests replican el paquete de la clase que prueban, una clase `*Test` por clase de producción:

| Capa | Prueba | Cómo se prueba |
|---|---|---|
| Casos de uso | `*UseCaseTest` | Mockito: se mockea el servicio, el mapper es real |
| Servicios de dominio | `*ServiceTest` | Mockito: se mockea el repositorio de dominio |
| Modelos de dominio | `*Test` | Sin mocks: reglas puras |
| Mappers de dominio, de presentación y de repositorio | `*MapperTest` | Sin mocks: un test por método y escenario |
| Controladores | `*ControllerTest` | `MockMvc` standalone con `GlobalExceptionHandler` |
| Adaptadores de repositorio | `*RepositoryImplTest` | Mockito: se mockea el repositorio JPA |
| Interfaces JPA | `I*JpaRepositoryTest` | PostgreSQL real (Testcontainers) con `support/PostgresRepositoryTestSupport` y los `.sql` de `src/test/resources/db` |
| Integración HTTP | `*PostgresIntegrationTest` | Aplicación completa sobre PostgreSQL real |
| Configuración y arranque | `CorsFilterTest`, `SwaggerConfigTest`, `BackendApplicationTests`, `ArchitectureTest` | Contexto con H2 aislado y reglas de arquitectura |

## Estilo de los tests

Es el de `huly-tpi` y el de la skill `.claude/skills/refactor-use-case`:

- Imports explícitos (sin `*`) y sin nombres totalmente calificados en el código.
- Nombre del test en camelCase `metodoShouldHacerXWhenY` y `@DisplayName` en español.
- Un comportamiento por test. Cada test se lee como tres pasos —`givenX()`, la acción y `thenX()`— y la mecánica va en métodos privados al final de la clase, bajo `// --- arrange ---`, `// --- act ---` y `// --- assert ---` (`// --- helpers ---` para los builders de datos).
- Datos de prueba con `Modelo.builder()` o el constructor del record, y verificación explícita de cada campo mapeado.
- Plantilla: `domain/useCase/puesto/CrearPuestoUseCaseTest`.

Los contenedores de pruebas son temporales e independientes del `xpedia` local. Los fixtures `rutas-test.sql`, `microlecciones-test.sql` y `retos-test.sql` eliminan y reconstruyen datos exclusivamente en esos contenedores; nunca se usan para cargar el entorno de desarrollo.

## Al agregar funcionalidad

Incorporar pruebas por clase con comportamiento (caso de uso, servicio, mapper, adaptador), consultas de repositorio contra PostgreSQL y casos HTTP positivos y negativos; ejecutar `clean verify` antes del commit y describir los resultados reales en el mensaje.

JaCoCo exige **100% de líneas y ramas por clase de producción**, mediante
`check-modulos` en la fase verify. Un informe agregado al 100% no reemplaza este
control; no se excluyen las clases nuevas para alcanzar el límite.

## Cuentas y sesiones

Las clases de usuario y acceso cuentan con pruebas de modelo, servicios, casos de
uso, mappers, adaptadores, controladores, DTOs con validación y configuración.
`IUsuarioJpaRepositoryTest` verifica consultas y restricciones reales.
`AuthPostgresIntegrationTest` prueba registro/login/actual/logout, CSRF por sesión,
rotación, cookies, persistencia, expiración, suspensión posterior, CORS, OpenAPI,
permisos de puestos y registros simultáneos. `UsuariosMigrationPostgresIntegrationTest`
crea bases independientes dentro del contenedor temporal para verificar V1 → V2
con usuarios existentes y colisiones de email sin perder datos.

`usuarios-test.sql` elimina usuarios y sesiones solo en los contenedores de pruebas.
Nunca ejecutarlo en la base de desarrollo. H2 inicializa únicamente las tablas de
sesión de su contexto aislado; en PostgreSQL Flyway es el único dueño del esquema.
