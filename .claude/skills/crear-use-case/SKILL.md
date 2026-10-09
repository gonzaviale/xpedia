---
name: crear-use-case
description: Crea un use case nuevo (endpoint) del backend de Xpedia de punta a punta siguiendo la estructura de capas del proyecto - model, repositorio (puerto), servicio, DTOs, mapper de dominio, use case, wiring en *UseCaseConfig, entity, JPA, RepositoryImpl, mapper de repositorio, DTO web, mapper de presentación y controller - más sus tests. Usar cuando se pida "agregar un endpoint", "crear el use case de X", "nuevo módulo/feature del backend", "exponer GET/POST/PUT/DELETE de X", "agregar una consulta" o "sumar una entidad". Para limpiar o reordenar un use case que ya existe usar refactor-use-case; para los tests usar escribir-tests.
---

# Crear un use case nuevo (backend Xpedia)

Lleva una funcionalidad nueva a la estructura de capas del proyecto, sin saltearse ninguna.
Los nombres de dominio van **en español** (clases, campos, métodos de servicio, enums, tablas).
Vocabulario y tablas: `docs/modelo-datos.md`. Endpoints existentes: `backend/README.md`.

## Antes de escribir código

1. **Leé el módulo de referencia más parecido** a lo que vas a hacer:
   - Listado simple de solo lectura → `hito` (`ListarHitosUseCase`, `HitoService`, `ContenidoRutaController`).
   - Listado paginado con filtros + detalle → `ruta` (`ListarRutasUseCase`, `ObtenerRutaUseCase`, `RutaController`, `ListarRutasQuery`).
   - Detalle con datos relacionados cargados en lote → `reto` (`ObtenerRetoUseCase`, `RetoRepositoryImpl`, rúbrica).
   - CRUD completo → `puesto` (los cinco use cases, `PuestoService`, `PuestoController`).
2. **Verificá que la tabla exista** en `backend/src/main/resources/db/migration`. Si hay tablas o columnas nuevas, van en una **migración Flyway nueva** (`V2__...sql`); no edites `V1__esquema_inicial.sql` sin acordarlo con el equipo.
3. **Definí la regla de visibilidad** (ver más abajo) antes de escribir la consulta: es lo que más se olvida.

## Estructura: qué archivo va en cada capa

Todo bajo `backend/src/main/java/com/xpedia/backend/`. `domain` **no** depende de `infrastructure`
(`ArchitectureTest` lo verifica). Ejemplo con la feature `hito` / `Listar`:

| Orden | Archivo | Qué contiene |
|---:|---|---|
| 1 | `domain/model/hito/Hito.java` | Modelo de dominio: `@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor`. Reglas puras sin I/O van como métodos acá. |
| 2 | `domain/repository/hito/HitoRepository.java` | Interfaz (puerto) de persistencia. Solo tipos de dominio. |
| 3 | `domain/service/hito/HitoService.java` | `@Service @RequiredArgsConstructor @Transactional(readOnly = true)`. Reglas que tocan repositorio: existencia, unicidad, visibilidad, pertenencia. Lanza `ResourceNotFoundException` / `DuplicateResourceException` / `BusinessRuleException`. |
| 4 | `domain/dto/hito/` | `ListarHitosRequest`, `ListarHitosResponse`, `HitoItem` (records). **Nunca modelos de dominio dentro de un DTO.** |
| 5 | `domain/mapper/hito/ListarHitosMapper.java` | Clase plana (sin anotaciones), `toResponse(...)`. Un mapper **por use case**. |
| 6 | `domain/useCase/hito/ListarHitosUseCase.java` | Sin anotaciones de Spring. `@RequiredArgsConstructor`, un único `execute(Request)`. |
| 7 | `infrastructure/config/useCase/<Area>UseCaseConfig.java` | `@Bean` de cada mapper y cada use case. El orden de los argumentos del constructor es el orden de los campos. |
| 8 | `infrastructure/repository/entity/HitoEntity.java` | Entidad JPA (`@Entity @Table`, `@Enumerated(EnumType.STRING)` + `columnDefinition = "text"` para enums). |
| 9 | `infrastructure/repository/jpaRepository/interfaces/IHitoJpaRepository.java` | Spring Data. Consultas derivadas o `@Query` con javadoc de la regla. |
| 10 | `infrastructure/repository/mapper/HitoRepositoryMapper.java` | `@Component`, `toDomain(entity)` con `Hito.builder()...` un campo por línea. |
| 11 | `infrastructure/repository/jpaRepository/implementation/HitoRepositoryImpl.java` | `@Component`, implementa el puerto, usa el JPA repo y el mapper. |
| 12 | `infrastructure/presentation/dto/hito/HitoResponse.java` | Record del JSON HTTP. Validaciones (`@NotBlank`, etc.) en los request web. |
| 13 | `infrastructure/presentation/mapper/hito/HitoPresentationMapper.java` | `@Component`. Convierte parámetros HTTP ↔ DTO de dominio. **No importa `domain.model.*`.** |
| 14 | `infrastructure/presentation/controller/...Controller.java` | `@RestController`, `@Tag`, `@Operation`. Sin lógica. |

Flujo de un request: `Controller → PresentationMapper → UseCase → Service → Repository (puerto) → RepositoryImpl → JPA`.

## Plantillas (copiá la forma, cambiá los nombres)

Use case: solo orquesta, el resultado en una variable local.

```java
@RequiredArgsConstructor
public class ListarHitosUseCase {

    private final HitoService hitoService;
    private final ListarHitosMapper mapper;

    public ListarHitosResponse execute(ListarHitosRequest request) {
        List<Hito> hitos = hitoService.listar(request.rutaId());
        return mapper.toResponse(hitos);
    }
}
```

Servicio: valida primero, consulta después.

```java
public List<Hito> listar(UUID rutaId) {
    rutaService.validarVisible(rutaId);
    return hitoRepository.findByRutaId(rutaId);
}
```

Wiring (se reutiliza el nombre del tipo como nombre del parámetro):

```java
@Bean
public ListarHitosUseCase listarHitosUseCase(HitoService hitoService, ListarHitosMapper listarHitosMapper) {
    return new ListarHitosUseCase(hitoService, listarHitosMapper);
}
```

Controller: delega todo, devuelve `ResponseEntity`.

```java
@GetMapping("/hitos")
@Operation(summary = "Listar hitos de una ruta global publicada")
public ResponseEntity<List<HitoResponse>> listarHitos(@PathVariable UUID rutaId) {
    ListarHitosResponse response = listarHitosUseCase.execute(hitoPresentationMapper.toRequest(rutaId));
    return ResponseEntity.ok(hitoPresentationMapper.toResponse(response));
}
```

## Reglas del proyecto que hay que respetar

- **Visibilidad.** El catálogo público solo expone contenido **global** (`organizacion_id IS NULL`), con `ruta.estado = 'PUBLICADA'` y `actividad.estado_revision = 'APROBADA'`. Lo que no cumple se responde `404`, igual que un ID inexistente (no se filtra que existe). Para anidados (`/rutas/{rutaId}/nodos/{nodoId}/...`) usá `RutaService.validarVisible(rutaId)` y `NodoService.validarVisible(rutaId, nodoId)`: **no repitas** esas validaciones inline ni uses `obtener(...)` solo para que lance.
- **Errores.** `ResourceNotFoundException` → 404, `DuplicateResourceException` → 409, `BusinessRuleException` → 400; violación de restricción de la base → 409; body inválido (`@Valid`) o UUID/parámetro inválido en el path → 400; cualquier otra → 500 (todo lo traduce `GlobalExceptionHandler`). No armes respuestas de error a mano en el controller.
- **Sin N+1.** Si una lista trae datos relacionados (prerrequisitos, fuentes, criterios), cargalos **en lote** con un `IN (...)` y agrupá en memoria (ver `NodoRepositoryImpl.agruparPrerrequisitos`).
- **Reglas de negocio en el dominio**, no en SQL ni en mappers. Si una consulta nativa necesita reglas de visibilidad, documentalas con javadoc (ver `IActividadJpaRepository`).
- **Tiempo.** Los servicios no inyectan `Clock`: el use case pasa `Instant.now(clock)` / `LocalDate.now(clock)`.
- **No** anotar use cases con `@Service`/`@Component` (se registran en el `*UseCaseConfig`), **no** crear interfaces para use cases con una sola implementación, **no** devolver entidades JPA ni modelos de dominio por HTTP.
- **Un controller y un `*UseCaseConfig` por área** (`<Area>Controller`, `<Area>UseCaseConfig`).

## Estilo de código

Igual que el módulo `puesto`: una sentencia por línea, una anotación por línea, línea en blanco después de `{` de la clase y entre miembros, imports explícitos (solo se toleran `lombok.*`, `jakarta.persistence.*` y `web.bind.annotation.*`), ordenados proyecto/libs, línea en blanco, `java.*`; líneas ≤ 120 columnas; argumentos y builders uno por línea; campos con el nombre del tipo (`hitoService`, `listarHitosMapper`, `listarHitosUseCase`), nunca `service`/`mapper`/`listar`.

## Tests (obligatorios)

Cada clase nueva con comportamiento lleva su test: usá el skill **escribir-tests** (estilo AAA, `@DisplayName`, camelCase). Para una feature de lectura típica son: use case, servicio, mapper de dominio, controller, mapper de presentación, repo impl, mapper de repositorio y el `I*JpaRepositoryTest` contra PostgreSQL.

## Verificación

Desde `backend/`, con JDK 21 (`java -version`) y Docker corriendo:

```powershell
.\mvnw.cmd test "-Dtest=Listar*Test,HitoServiceTest"
.\mvnw.cmd clean verify
```

Después abrí Swagger (`/swagger-ui.html`, solo activo con el perfil `dev`) y probá el endpoint; para datos de prueba locales está `scripts/cargar-ruta-piloto.ps1`. Nunca cargues los `*-test.sql` en la base local.

## Checklist

- [ ] Regla de visibilidad definida y cubierta con test (ruta oculta/ajena → 404).
- [ ] DTOs de dominio y web sin modelos de dominio; presentación no importa `domain.model`.
- [ ] `execute()` es una lista de pasos; nada de `throw` ni consultas inline.
- [ ] Bean agregado al `*UseCaseConfig` con el orden correcto de argumentos.
- [ ] `@Tag`/`@Operation` en el controller y entrada nueva en `backend/README.md` si es un endpoint público.
- [ ] Migración Flyway si cambió el esquema.
- [ ] Tests por capa y `clean verify` en verde.
