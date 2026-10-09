---
name: escribir-tests
description: Escribe o reescribe los tests del backend de Xpedia al estilo del proyecto (el de huly-tpi) - Arrange-Act-Assert con helpers privados givenX/thenX, métodos camelCase xShouldYWhenZ, @DisplayName en español, imports explícitos, builders en vez de reflexión, un comportamiento por test - con plantillas por capa (use case, servicio, mapper de dominio, mapper de presentación, controller MockMvc, repo impl, mapper de repositorio, JPA contra PostgreSQL con Testcontainers, integración HTTP, excepciones, config). Usar cuando se pida "escribir/agregar/arreglar tests", "testear este use case/servicio/controller/mapper", "cubrir X con tests" o al terminar una feature nueva. Para refactorizar el use case en sí usar refactor-use-case; para crear una feature usar crear-use-case.
---

# Escribir tests del backend (estilo Xpedia / huly)

Un test se lee como una frase: **given → acción → then**. La mecánica (stubs, ejecución, asserts, verifies)
va en **métodos privados al final de la clase**. Plantilla viva: `CrearPuestoUseCaseTest`; y el módulo
`hito` completo (`HitoServiceTest`, `ListarHitosUseCaseTest`, `ListarHitosMapperTest`,
`HitoPresentationMapperTest`, `HitoRepositoryImplTest`, `HitoRepositoryMapperTest`, `IHitoJpaRepositoryTest`,
`ContenidoRutaControllerTest`). Mirá el que corresponda antes de escribir.

Los tests replican el paquete de la clase probada, bajo `backend/src/test/java/com/xpedia/backend/`.

## Reglas (todas obligatorias)

1. **Nombre** `metodoShouldHacerXWhenY` en camelCase, **sin guiones bajos** y sin frases en español.
2. **`@DisplayName` en español** describiendo el comportamiento ("Lanza ResourceNotFoundException cuando el hito no pertenece a la ruta").
3. **Un comportamiento por test.** No pruebes dos métodos ni dos endpoints en el mismo test.
4. **Cuerpo de 3 bloques**, separados por línea en blanco, de pocas líneas. Los helpers se agrupan al final bajo
   `// --- arrange ---`, `// --- act ---`, `// --- assert ---` (y `// --- helpers ---` para builders de datos).
5. **Imports explícitos**: ningún `*`, ni en estáticos (`import static ...Mockito.*` está prohibido). Sin nombres
   totalmente calificados dentro del código. Sin imports ni variables sin usar.
6. **Una sentencia por línea.** Prohibido `@Test void x() { ...; ...; }` en una línea.
7. **Datos con builders** (`Hito.builder()...`) o con el constructor del record. **Prohibido** generar datos o comparar
   por reflexión (`ContractData.full`, `usingRecursiveComparison` para esquivar campos): cada campo mapeado se verifica
   con un `assertThat` explícito. Valores en `private static final` con nombre (`RUTA_ID`, `TITULO`).
8. **Mockito estricto**: cada `givenX()` debe usarse en el camino de ese test, o salta `UnnecessaryStubbingException`.
   (En un test donde el nombre ya está tomado no stubbees el `save`.)
9. Comentarios solo donde no es obvio.
10. Mantené la cobertura de ramas: cada `if`, `orElseThrow` y `null` del código probado tiene su test.

## Qué probar en cada capa

| Clase probada | Test | Cómo | Casos mínimos |
|---|---|---|---|
| `*UseCase` | `*UseCaseTest` | `@ExtendWith(MockitoExtension.class)`, `@Mock` del servicio, mapper **real**, use case en `@BeforeEach` | respuesta mapeada · delega con los argumentos correctos · propaga la excepción sin convertirla |
| `*Service` | `*ServiceTest` | `@Mock` de repositorios/otros servicios + `@InjectMocks` | camino feliz · cada rama de error · **orden** de validaciones (`InOrder`) · que no consulta si la validación falla (`verifyNoInteractions`) |
| Model con reglas | `*Test` | sin mocks | cada regla, casos borde, inmutabilidad si devuelve copias |
| `*Mapper` (dominio) | `*MapperTest` | sin mocks, mapper en `@BeforeEach` | **todos** los campos · orden conservado · nulos · lista/página vacía |
| `*PresentationMapper` | `*PresentationMapperTest` | sin mocks | un test por método (`toRequest`, `toResponse`) · defaults · lista vacía |
| `*Controller` | `*ControllerTest` | `mock()` de los use cases + `MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(new GlobalExceptionHandler())` | 200 con JSON · el request llega bien al use case (`ArgumentCaptor`) · 200 `[]` · 404 por `ResourceNotFoundException` · 400 por UUID/parámetro inválido (`@ParameterizedTest`, `verifyNoInteractions`) · 400 por validación del body |
| `*RepositoryImpl` | `*RepositoryImplTest` | `@Mock` del JPA repo; mapper real | mapeo completo · orden · vacío · `Optional` presente/ausente · consultas en lote (una sola llamada) |
| `*RepositoryMapper` | `*RepositoryMapperTest` | sin mocks | `toDomain` con todos los campos · entidad vacía (nulos) · enums |
| `I*JpaRepository` | `I*JpaRepositoryTest` | `extends PostgresRepositoryTestSupport` | la consulta real: filtros, orden, exclusión de privadas/no publicadas/ajenas |
| HTTP completo | `*PostgresIntegrationTest` | aplicación real sobre PostgreSQL | contrato JSON, 404/400, OpenAPI, cantidad de consultas |
| Excepciones, config, arranque | `DomainExceptionTest`, `CorsFilterTest`, `SwaggerConfigTest`, `BackendApplicationTests`, `ArchitectureTest` | ver los existentes | mensaje/causa/herencia · headers · contexto carga |

## Plantillas

Servicio:

```java
@ExtendWith(MockitoExtension.class)
class HitoServiceTest {

    private static final UUID RUTA_ID = UUID.randomUUID();

    @Mock
    private HitoRepository hitoRepository;

    @Mock
    private RutaService rutaService;

    @InjectMocks
    private HitoService hitoService;

    @Test
    @DisplayName("No consulta hitos cuando la ruta no es visible")
    void listarShouldNotQueryHitosWhenRutaIsNotVisible() {
        givenRutaIsNotVisible();

        assertThatThrownBy(this::listar).isInstanceOf(ResourceNotFoundException.class);

        thenRepositoryWasNotQueried();
    }

    // --- arrange ---
    private void givenRutaIsNotVisible() {
        doThrow(new ResourceNotFoundException("ruta", "id", RUTA_ID)).when(rutaService).validarVisible(RUTA_ID);
    }

    // --- act ---
    private List<Hito> listar() {
        return hitoService.listar(RUTA_ID);
    }

    // --- assert ---
    private void thenRepositoryWasNotQueried() {
        verifyNoInteractions(hitoRepository);
    }
}
```

Use case (mapper real, colaborador mockeado):

```java
@BeforeEach
void setUp() {
    listarHitosUseCase = new ListarHitosUseCase(hitoService, new ListarHitosMapper());
}

@Test
@DisplayName("Lista los hitos de la ruta pedida y los devuelve mapeados")
void executeShouldReturnMappedHitosOfRuta() {
    givenServiceListsHitos(List.of(hito()));

    ListarHitosResponse response = listar();

    thenResponseHasMappedHito(response);
}
```

Mapper (sin mocks; todos los campos, nulos, vacío):

```java
@Test
@DisplayName("Conserva los nulos de un hito sin datos")
void toResponseShouldKeepNullsWhenHitoHasNoData() {
    ListarHitosResponse response = mapear(List.of(new Hito()));

    thenItemHasOnlyNulls(response.content().getFirst());
}
```

Controller (MockMvc standalone; el 400 no debe llegar al use case):

```java
@ParameterizedTest
@ValueSource(strings = {"id-invalido", "123", "null"})
@DisplayName("GET hitos devuelve 400 cuando el id de la ruta no es un UUID")
void listarHitosShouldReturn400WhenRutaIdIsNotUuid(String rutaId) throws Exception {
    ResultActions result = performGet("/api/rutas/" + rutaId + "/hitos");

    result.andExpect(status().isBadRequest());
    verifyNoInteractions(listarHitosUseCase);
}
```

Repo impl (JPA mockeado, mapper real):

```java
@BeforeEach
void setUp() {
    repository = new HitoRepositoryImpl(jpa, new HitoRepositoryMapper());
}
```

JPA contra PostgreSQL (requiere Docker; los datos salen de `src/test/resources/db/*.sql`, que `PostgresRepositoryTestSupport` carga con `@Sql`):

```java
class IHitoJpaRepositoryTest extends PostgresRepositoryTestSupport {

    private static final UUID RUTA_ID = UUID.fromString("00000000-0000-0000-0000-000000000201");

    @Autowired
    private IHitoJpaRepository repository;

    @Test
    @DisplayName("Devuelve false cuando el hito pertenece a otra ruta")
    void existsByIdAndRutaIdShouldReturnFalseWhenHitoBelongsToOtherRuta() {
        boolean result = repository.existsByIdAndRutaId(HITO_AJENO_ID, RUTA_ID);

        assertThat(result).isFalse();
    }
}
```

## Detalles que suelen romper

- JSON numérico: Jackson devuelve `Integer`/`Double`. Si el modelo usa `Short` o `BigDecimal`, comparalo con
  `.value(POSICION.intValue())` / `.value(HORAS.doubleValue())`; si no, falla con "expected 1 but was 1".
- Los UUID de los `I*JpaRepositoryTest` son los de los `.sql` de fixtures: usalos como constantes con nombre, no uses números sueltos.
- Los `*-test.sql` borran y reconstruyen datos **solo** en el contenedor de pruebas. Nunca los ejecutes en la base local.
- Si una prueba cuenta consultas (Hibernate `Statistics`), dejá la medición en un helper y deshabilitá las estadísticas en `@AfterEach`.
- Un `@Test` sin `@DisplayName`, un nombre con `_` o un `import ...*;` son errores de estilo aunque el test pase.

## Ejecutar

Desde `backend/`, con JDK 21 y Docker corriendo (`JAVA_HOME` debe apuntar a la 21):

```powershell
.\mvnw.cmd test "-Dtest=HitoServiceTest,ListarHitosUseCaseTest"
.\mvnw.cmd clean verify
```

Los resúmenes quedan en `target/surefire-reports/*.txt` y el reporte de cobertura en `target/site/jacoco/index.html`
(la cobertura se reporta pero no se exige).

## Controles antes de cerrar

```bash
grep -rEn "^import .*\*;" backend/src/test        # sin wildcards
grep -rEn "void [a-z]+_[a-zA-Z_]+\(" backend/src/test   # sin guiones bajos
grep -rl "ContractData" backend/src               # no debe existir
```

Y confirmá que cada `@Test` tiene su `@DisplayName` (misma cantidad de `@Test`/`@ParameterizedTest` que de `@DisplayName` por archivo).
