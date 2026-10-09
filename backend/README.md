# Xpedia · Backend

Spring Boot 4 · Java 21 · PostgreSQL 17 (pgvector) · Flyway.

## Arquitectura

Clean Architecture con dos capas. `domain` no depende de `infrastructure`.

```
com.xpedia.backend
├── domain
│   ├── dto/<feature>          records de entrada/salida de los use cases
│   ├── exception              excepciones de negocio (ResourceNotFound, DuplicateResource, ...)
│   ├── mapper/<feature>       mappers de use case (clases planas, se registran en *UseCaseConfig)
│   ├── model/<feature>        modelos de dominio
│   ├── port                   puertos de salida (IA, storage, ...)
│   ├── repository/<feature>   puertos de persistencia
│   ├── service/<feature>      reglas de negocio (@Service)
│   └── useCase/<feature>      un caso de uso por clase, con execute()
└── infrastructure
    ├── adapter                implementaciones de domain.port
    ├── config                 Swagger, CORS y config/useCase (wiring de use cases como @Bean)
    ├── presentation           controllers, DTOs web con validaciones, mappers web, exception handler
    └── repository             entities JPA, interfaces Spring Data y *RepositoryImpl
```

El flujo de un request es:

1. `Controller` recibe el DTO web.
2. `PresentationMapper` lo convierte en un request de dominio.
3. `UseCase` llama al `Service`.
4. `Service` usa el `Repository` (puerto).
5. `RepositoryImpl` traduce a la `Entity` JPA.

El ABM de ejemplo es **puesto** (`/api/puestos`).

## Catálogo público de rutas

- `GET /api/rutas`: lista paginada de rutas globales con estado `PUBLICADA`.
- `GET /api/rutas/{id}`: detalle de una ruta del catálogo por UUID.
- Las rutas de empresa (`organizacion_id` con valor), en borrador, en armado o archivadas no aparecen; su detalle devuelve `404`, igual que un ID inexistente.

El listado admite `tipo` (`TECNICA`, `CAMBIO_RUBRO`, `HABILIDAD_BLANDA`) y `objetivo` (`ARRANCAR`, `CAMBIAR`, `MEJORAR`). Los filtros son opcionales y se combinan. `page` comienza en 0 y `size` admite de 1 a 100; los valores por defecto son 0 y 20. El orden es por título e ID, para mantener estable la paginación. Los filtros, UUID o valores de paginación inválidos devuelven `400` con el formato común `ErrorResponse`.

Ejemplos en Swagger o en un cliente HTTP:

```text
GET http://localhost:8080/api/rutas
GET http://localhost:8080/api/rutas?tipo=CAMBIO_RUBRO&objetivo=CAMBIAR&page=0&size=20
GET http://localhost:8080/api/rutas/{id}
```

El listado devuelve `content`, `pageNumber`, `pageSize`, `totalElements`, `totalPages`, `first` y `last`. Cada ruta incluye su meta, duración y sello de validación. El detalle añade perfil inicial, estado y fechas; no expone IDs de empresas ni de revisores. Una base sin rutas devuelve una página vacía con `200`. Este módulo no carga contenido piloto ni incluye todavía inscripciones.

### Hitos, nodos y prerrequisitos

- `GET /api/rutas/{rutaId}/hitos`: array de hitos, ordenado por `posicion` e ID. Incluye objetivo, horas estimadas, `esFinal` y `evidenciaEsperada`.
- `GET /api/rutas/{rutaId}/nodos`: array de nodos con `hitoId`, `ramaId`, `habilidadId`, código, tipo, nivel, minutos estimados, palabras clave y `prerrequisitoIds`.
- `GET /api/rutas/{rutaId}/nodos?hitoId={hitoId}`: nodos de un hito específico. El hito debe pertenecer a la ruta; de lo contrario devuelve `404`.

Los nodos se ordenan por posición del hito, posición del nodo e ID. Los temas `TEMA` sin hito aparecen al final del listado general y no aparecen al filtrar por hito. Los prerrequisitos del mismo roadmap se conservan aunque pertenezcan a un hito anterior al consultado.

Todas estas consultas exigen una ruta global publicada. Una ruta inexistente, privada o no publicada devuelve `404`; un UUID inválido devuelve `400`. Una ruta válida sin contenido devuelve `200` con `[]`. No se exponen nodos cuyo hito pertenezca a otra ruta, prerrequisitos de otra ruta ni IDs de ramas ajenas o habilidades privadas. Los prerrequisitos y las referencias se cargan en lote para evitar una consulta por nodo.

Estas respuestas representan el contenido de la ruta. El estado de avance, dominio y desbloqueo de cada persona requiere una inscripción y queda para el módulo de progreso.

## Migraciones

En `src/main/resources/db/migration`, con el formato `V{n}__{descripcion}.sql` (dos guiones bajos). Las aplica Flyway al arrancar y `ddl-auto=none`. **Nunca se edita una migración ya aplicada**: para cambiar algo se crea una nueva.

## Levantar en local

```bash
docker compose up -d postgres
./mvnw spring-boot:run
```

- `docker compose up -d postgres` crea la base `xpedia` con pgvector. Flyway no crea bases: crea las tablas al arrancar la app. Si la base quedó a medias por un intento anterior, se borra el volumen con `docker compose down -v` y se vuelve a levantar.
- El perfil por defecto es `dev`. Usa `localhost:5434/xpedia` con `xpedia_user` / `xpedia_pass`. Se puede sobrescribir con variables de entorno o con `application-xpedia-secrets.properties` (ver el `.example`).
- Swagger: http://localhost:8080/swagger-ui.html

## Tests

```bash
./mvnw test
```

El test de contexto usa H2 (perfil `h2-test`) sin Flyway. La integración del catálogo usa Testcontainers con PostgreSQL 17 y pgvector: aplica V1, valida las entities JPA y prueba consultas HTTP y OpenAPI. **Docker debe estar funcionando** para ejecutar la suite completa. El contenedor y los datos de prueba son descartables; no se conecta a la base local `xpedia`.

Para ejecutar solo las pruebas del catálogo en PowerShell, desde la carpeta `backend`:

```powershell
.\mvnw.cmd '-Dtest=RutaControllerTest,ContenidoRutaControllerTest,RutaPostgresIntegrationTest' test
```

La versión objetivo de Java es 21. Usar un JDK compatible en `JAVA_HOME`.
