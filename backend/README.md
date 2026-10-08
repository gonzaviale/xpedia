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

Los tests usan H2 (perfil `h2-test`) sin Flyway. Requieren JDK 21 en `JAVA_HOME`.
