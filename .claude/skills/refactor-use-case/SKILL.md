---
name: refactor-use-case
description: Refactoriza un use case del backend de Xpedia (com.xpedia.backend.domain.useCase.*) a la estructura SOLID objetivo del proyecto, dejando execute() como una lista de pasos, moviendo reglas/validaciones de dominio al model y la lógica compartida o con I/O a servicios de dominio, y sus tests al estilo Arrange-Act-Assert con helpers privados, nombres camelCase y @DisplayName en español; sin cambiar el comportamiento y manteniendo los tests verdes. Usar cuando se pida "refactorizar este use case", "dejar SOLID", "extraer a private methods/al model/al service", limpiar un execute() con lógica inline, o "refactorizar/limpiar los tests" (extraer when/then y validaciones a métodos privados, camelCase, DisplayName).
---

# Refactor de Use Cases (estilo SOLID de Xpedia)

Lleva un use case de `com.xpedia.backend.domain.useCase.*` a la **forma objetivo**:
`execute()` se lee como los pasos del caso de uso; las reglas de dominio viven en el
model; la lógica compartida o con I/O vive en servicios de dominio. **El comportamiento
no cambia y los tests existentes deben seguir pasando sin modificación de sus assertions.**

Recordá la convención del proyecto: **nombres de dominio en español** (clases, campos,
métodos de servicio, enums, tablas). Ver `docs/modelo-datos.md` para el vocabulario.

## Arquitectura de referencia

Clean Architecture en dos capas (`domain` no depende de `infrastructure`):

- `domain/useCase/<area>/` — use cases planos (sin anotaciones de Spring), con
  `@RequiredArgsConstructor` y un único `execute(<X>Request)`.
- `domain/service/<area>/` — servicios `@Service` con las reglas que tocan repositorio
  (ej. `PuestoService`: normalizar, unicidad, `findOrThrow`).
- `domain/model/<area>/` — modelos de dominio (Lombok `@Builder`/`@Getter`/`@Setter`).
- `domain/dto/<area>/` y `domain/mapper/<area>/` — request/response del use case y su mapper.
- `domain/exception/` — `ResourceNotFoundException`, `DuplicateResourceException`,
  `BusinessRuleException`.
- `infrastructure/config/useCase/<Area>UseCaseConfig` — wiring de mappers y use cases como `@Bean`.

## Forma objetivo de `execute()`

`execute()` solo orquesta. Cada línea es una de estas tres cosas:

1. Una llamada a un **método privado** del use case (`resolverX`, `cargarX`, `validarX`,
   `aplicarX`, `registrarX`).
2. Una llamada a un **método del model** (regla de dominio pura).
3. Una llamada a un **servicio colaborador** (lógica compartida / con I/O) o al **mapper**.

No debe quedar en `execute()`: validaciones inline con `throw`, matemática de dominio,
cadenas `repository.findX().filter(...).isPresent()`, ni armado de varios valores.

Ejemplo de referencia ya hecho (caso simple, todo delegado al servicio) —
`backend/src/main/java/com/xpedia/backend/domain/useCase/puesto/CrearPuestoUseCase.java`:

```java
public CrearPuestoResponse execute(CrearPuestoRequest request) {
    Puesto creado = puestoService.crear(mapper.toModel(request));
    return mapper.toResponse(creado);
}
```

Para un caso con más pasos, la forma esperada es (ilustrativo):

```java
public InscribirseEnRutaResponse execute(InscribirseEnRutaRequest request) {
    Ruta ruta = cargarRutaPublicada(request.rutaId());
    validarQueNoEsteInscripto(request.usuarioId(), ruta);
    Inscripcion inscripcion = Inscripcion.nueva(request.usuarioId(), ruta, LocalDate.now(clock));
    Inscripcion guardada = inscripcionService.registrar(inscripcion);
    return mapper.toResponse(guardada);
}
```

## Dónde va cada cosa (regla de decisión)

- **Función pura del estado de dominio, sin I/O** → al **model**.
  - Si es una clase de model (ej. `Puesto`), agregale un método o predicado:
    ej. `puesto.esGlobal()` (`organizacionId == null`).
  - Si es una utilidad de dominio sin estado (clase `final` con constructor privado y
    estáticos), agregá una función estática.
  - Si deriva **varios** valores, devolvé un **value object del model** (record propio).
- **Toca un repositorio/servicio, es específico de este use case y se usa una sola vez**
  → **método privado** del use case. Si arma varios valores para downstream, devolvé un
  **record privado**.
- **La misma lógica aparece en ≥2 lugares** (compruébalo con grep antes de decidir), o es
  una regla de persistencia del agregado (unicidad, existencia, normalización) →
  **servicio `@Service`** en `com.xpedia.backend.domain.service.<area>`, inyectado en el
  use case. Ejemplo ya creado: `PuestoService` (`crear`, `actualizar`, `eliminar`,
  `obtener`, `listar`, con los privados `findOrThrow`, `assertNombreUnico`, `normalizar`).

Para mantener los servicios agnósticos del tiempo y los tests deterministas: si el use case
necesita la fecha/hora, mantiene su `Clock` y le pasa `Instant.now(clock)` /
`LocalDate.now(clock)` al servicio (no inyectes el `Clock` en el servicio).

## Procedimiento

1. **Leer**: el use case, su request/response/mapper, sus clases de model, el servicio que
   usa y sus tests (`backend/src/test/java/com/xpedia/backend/...`).
2. **Buscar duplicación** antes de extraer, para decidir model vs privado vs servicio.
   Usá Grep sobre `backend/src/main/java/com/xpedia/backend`.
3. **Cambiar en orden de dependencia**: model → servicios → use case → `*UseCaseConfig` → tests.
4. **Wiring**: si agregaste/quitaste dependencias del use case, actualizá el `@Bean` en
   `com.xpedia.backend.infrastructure.config.useCase.<Area>UseCaseConfig` (ej.
   `PuestoUseCaseConfig`). El orden de los argumentos del constructor debe coincidir con el
   orden de los campos (por `@RequiredArgsConstructor`). Los `@Service` se inyectan solos
   como parámetros del `@Bean`; los mappers se declaran como `@Bean` en el mismo config.
5. **Tests**:
   - Los tests del use case no deben cambiar sus assertions; solo adaptá la construcción
     (mocks). Si una dependencia pasó a servicio, mockeá el servicio
     (`when(puestoService.crear(any(Puesto.class))).thenReturn(...)`) en lugar del repo.
   - Agregá tests unitarios para **toda** regla movida al model/servicio (mismo estilo:
     JUnit5 + AssertJ + Mockito, un caso por comportamiento), como `PuestoServiceTest`.
6. **Compilar y correr** los tests afectados (ver abajo). Verificar exit 0 y los conteos
   en `backend/target/surefire-reports/*.txt`.

## Anti-patrones (NO hacer)

- Un método por cada write o por cada línea: eso es fragmentar, no es SOLID.
- Crear interfaces para los use cases cuando hay una sola implementación (YAGNI).
- Mover I/O (repositorios) dentro del model.
- Anotar los use cases con `@Service`/`@Component`: se registran en el `*UseCaseConfig`.
- Cambiar el comportamiento o relajar assertions para que "entre" la extracción.
- Inyectar `Clock` en los servicios de dominio (pasá el instante por parámetro).
- Nombres de dominio en inglés (`Position`, `create`): el dominio va en español.

## Refactor de los tests del use case (estilo Arrange-Act-Assert)

Cada `@Test` queda en tres bloques (arrange / act / assert) de pocas líneas; toda la
mecánica (stubs, ejecución, asserts y verifies) se extrae a **métodos privados** agrupados
al final de la clase, bajo comentarios `// --- arrange ---`, `// --- act ---`, `// --- assert ---`.

- **Arrange** → métodos `givenX(...)` que encapsulan los `when(...).thenReturn(...)` y
  builders de fixtures. Uno por colaborador/escenario: `givenServiceCreatesPuesto()`,
  `givenNombreIsFree()`, `givenRepositoryReturnsSaved()`, `savedPuesto()`, `nuevoPuesto(nombre)`.
- **Act** → un método que ejecuta el use case con el verbo del caso: `crear()` /
  `actualizar()` → `useCase.execute(new XRequest(...))`. Permite además
  `assertThatThrownBy(this::crear)`.
- **Assert** → métodos `thenX(...)` que agrupan los `assertThat` y `verify`:
  `thenServiceReceivedNewPuesto()` (con `ArgumentCaptor`), `thenResponseHasCreatedPuesto(response)`.

Convenciones obligatorias:

- Nombres de los `@Test` en **camelCase** sin guiones bajos:
  `executeShouldReturnCreatedPuesto` (NO `execute_shouldReturn_createdPuesto`).
- `@DisplayName` en **español** describiendo el comportamiento, ej.
  `@DisplayName("Devuelve el puesto creado con el id generado")`
  (import `org.junit.jupiter.api.DisplayName`).
- Dejar comentarios solo en los casos no obvios.
- Cuidar el **strict stubbing** de Mockito: cada `givenX()` que dejes en un test tiene que
  usarlo el camino de `execute` de ese test, o salta `UnnecessaryStubbingException`
  (ej.: en un test donde el nombre ya está tomado no stubbees el `save`, porque corta antes).

Resultado típico:

```java
@Test
@DisplayName("Devuelve el puesto creado con el id generado")
void executeShouldReturnCreatedPuesto() {
    givenServiceCreatesPuesto();

    CrearPuestoResponse response = crear();

    thenResponseHasCreatedPuesto(response);
}
```

Plantilla ya hecha: `CrearPuestoUseCaseTest`, `PuestoServiceTest`.

## Build & test

Desde `backend/` (requiere JDK 21 en `JAVA_HOME`; los tests usan H2 con el perfil `h2-test`, sin Flyway):

```bash
./mvnw test -Dtest=CrearPuestoUseCaseTest,PuestoServiceTest
```

En Windows/PowerShell: `.\mvnw.cmd test "-Dtest=CrearPuestoUseCaseTest,PuestoServiceTest"`.
Surefire matchea `-Dtest=NombreSimple` en todos los paquetes. Los resúmenes quedan en
`backend/target/surefire-reports/*.txt` (`Tests run: N, Failures: 0, Errors: 0`).
Antes de cerrar, correr la suite completa con `./mvnw test`.

## Ejemplo completo de referencia

El módulo de puestos ya está en esta forma — úsalo como plantilla:

- Use cases: `domain/useCase/puesto/CrearPuestoUseCase.java`, `ActualizarPuestoUseCase.java`,
  `EliminarPuestoUseCase.java`, `ObtenerPuestoUseCase.java`, `ListarPuestosUseCase.java`
- Model: `domain/model/puesto/Puesto.java`
- Servicio: `domain/service/puesto/PuestoService.java`
- DTOs y mappers: `domain/dto/puesto/*`, `domain/mapper/puesto/*`
- Wiring: `infrastructure/config/useCase/PuestoUseCaseConfig.java`
- Tests: `CrearPuestoUseCaseTest`, `PuestoServiceTest`
