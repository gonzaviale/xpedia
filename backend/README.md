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

### Piloto local para Swagger

Desde `backend`, con PostgreSQL levantado:

```powershell
.\scripts\cargar-ruta-piloto.ps1
```

El script apunta al servicio PostgreSQL del compose local (`xpedia`, `xpedia_user`). Si configuraste otro usuario/base, adaptá esos dos argumentos explícitos antes de ejecutarlo. No utiliza la conexión configurada para otros entornos.

La carga es manual y transaccional, está fuera de Flyway y puede repetirse sin duplicar ni sobrescribir registros. Un conflicto con una clave única distinta del ID produce un error y revierte la carga. Se publica una ruta **DEMO**, con tres hitos, dos ramas y seis nodos: es un subconjunto del borrador de atención al cliente, no material editorial validado ni la ruta completa. No crea usuarios ni progreso.

En Swagger, abrí cada endpoint, elegí **Try it out**, completá los parámetros y presioná **Execute**. No llevan cuerpo JSON.

| Consulta | Parámetros | Resultado esperado |
|---|---|---|
| `GET /api/rutas/{id}` | `id = b1000000-0000-4000-8000-000000000001` | 200, detalle DEMO |
| `GET /api/rutas/{rutaId}/hitos` | `rutaId = b1000000-0000-4000-8000-000000000001` | 200, tres hitos |
| `GET /api/rutas/{rutaId}/nodos` | mismo `rutaId`, `hitoId` vacío | 200, seis nodos |
| `GET /api/rutas/{rutaId}/nodos` | mismo `rutaId`, `hitoId = b2000000-0000-4000-8000-000000000002` | 200, DEMO-03 y DEMO-04 |
| Hitos o nodos de ruta inexistente | `rutaId = 00000000-0000-0000-0000-000000000000` | 404 |
| UUID inválido | `rutaId = texto` | 400 |

Los UUID que usa `rutas-test.sql` pertenecen a una base temporal; para Swagger local usá los del piloto. **No ejecutes `rutas-test.sql` en tu base local:** ese archivo elimina y reconstruye fixtures para las pruebas.

### Microlecciones con fuentes

`GET /api/rutas/{rutaId}/nodos/{nodoId}/microlecciones` devuelve un array de actividades `MICROLECCION` aprobadas. Cada una incluye título, nivel, contenido JSON, origen, fecha de revisión y fuentes (título, URL, licencia, uso, permiso comercial y ubicación).

La ruta debe ser global y publicada, y el nodo debe pertenecer a esa ruta con un hito compatible. La consulta excluye actividades pendientes/rechazadas, de otros tipos, con hito diferente al del nodo, sin fuentes o con alguna fuente privada. Los permisos y licencias se muestran como están registrados; este endpoint no verifica licencias ni genera o personaliza material.

Una ruta/nodo oculto o inexistente devuelve `404`, un UUID inválido devuelve `400`, y un nodo válido sin material visible devuelve `200` con `[]`. El orden es por nivel, creación e ID; las fuentes se ordenan por título e ID y se consultan en lote.

Para probar la microlección DEMO, repetí `.\scripts\cargar-ruta-piloto.ps1` si cargaste el piloto antes de agregar este módulo. En Swagger usá:

```text
rutaId: b1000000-0000-4000-8000-000000000001
nodoId: b4000000-0000-4000-8000-000000000003
```

El resultado esperado es `200` con una microlección marcada como ficticia y una fuente interna de prueba. La marca `APROBADA` habilita la demostración técnica; no se declara revisión humana ni autorización comercial. Otro nodo del piloto sin material devuelve `[]`.

### Retos y rúbricas

- `GET /api/rutas/{rutaId}/nodos/{nodoId}/retos`: array de consignas aprobadas, de tipo `ENSAYO`, `RETO_PROYECTO` o `DESAFIO_REAL`.
- `GET /api/rutas/{rutaId}/nodos/{nodoId}/retos/{retoId}`: detalle de una consigna visible, con el mismo formato que los elementos del listado.

Cada reto incluye título, tipo, nivel, origen, fecha de revisión y `contenido`. Este último solo expone los campos de texto `consigna`, `contexto` y `formatoEntrega`; se omiten soluciones, respuestas esperadas y configuraciones internas. La consigna debe contener texto. Los dos campos opcionales se omiten si tienen otro tipo de dato.

La rúbrica contiene nombre, descripción, `puntajeAprobacion`, `puntajeMaximo` y criterios ordenados por posición e ID. Cada criterio informa su máximo, peso y condición `eliminatorio`. El máximo total se calcula con decimales como la suma de `puntajeMax * peso`. La condición eliminatoria se entrega como metadato para la futura corrección; estos endpoints todavía no reciben ni califican respuestas.

Solo aparecen rúbricas globales con criterios, pesos positivos y un umbral de aprobación entre cero y el máximo ponderado. Se excluyen rúbricas privadas, vacías o inconsistentes, actividades con fuentes privadas y actividades cuyo hito no coincide con el nodo. La ruta debe ser global publicada y el nodo debe pertenecer a ella. Ruta, nodo o reto oculto/inexistente devuelve `404`; UUID inválido, `400`; nodo válido sin retos, `200` con `[]`.

El orden de los retos es por nivel, creación e ID. Rúbricas y criterios se cargan en lote: el recorrido con resultados requiere cinco consultas incluyendo las validaciones de ruta y nodo, sin aumentar por cada reto.

Repetí `.\scripts\cargar-ruta-piloto.ps1` para agregar el reto DEMO y su rúbrica. En Swagger, sección **Retos**, usá **Try it out** y estos parámetros, sin cuerpo JSON:

```text
rutaId: b1000000-0000-4000-8000-000000000001
nodoId: b4000000-0000-4000-8000-000000000003
retoId: b5000000-0000-4000-8000-000000000002 (solo en el detalle)
```

El listado devuelve un reto de escritura y el detalle devuelve ese mismo reto. La rúbrica DEMO tiene cuatro criterios, máximo 12 y aprobación 8; el criterio de política es eliminatorio. Son datos ficticios para comprobar la API. La carga conserva las ediciones existentes y no crea intentos ni evaluaciones.

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

Para ejecutar pruebas, generar el informe JaCoCo y exigir cobertura por clase: `.\mvnw.cmd clean verify`. La matriz de pruebas y los límites exactos están en [TESTING.md](TESTING.md).
