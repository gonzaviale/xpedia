package com.xpedia.backend.infrastructure.repository.jpaRepository.implementation;

import com.xpedia.backend.domain.model.enums.TipoReto;
import com.xpedia.backend.domain.model.reto.Reto;
import com.xpedia.backend.domain.model.rubrica.CriterioRubrica;
import com.xpedia.backend.domain.model.rubrica.Rubrica;
import com.xpedia.backend.domain.repository.rubrica.RubricaRepository;
import com.xpedia.backend.infrastructure.repository.entity.ActividadEntity;
import com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces.IActividadJpaRepository;
import com.xpedia.backend.infrastructure.repository.mapper.RetoRepositoryMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RetoRepositoryImplTest {

    private static final UUID RUTA_ID = UUID.fromString("00000000-0000-0000-0000-000000000201");
    private static final UUID NODO_ID = UUID.fromString("00000000-0000-0000-0000-000000000501");
    private static final UUID HITO_ID = UUID.fromString("00000000-0000-0000-0000-000000000401");
    private static final UUID RETO_ID = UUID.fromString("d3000000-0000-4000-8000-000000000001");
    private static final UUID OTRO_RETO_ID = UUID.fromString("d3000000-0000-4000-8000-000000000002");
    private static final UUID RUBRICA_ID = UUID.fromString("d1000000-0000-4000-8000-000000000001");
    private static final UUID OTRA_RUBRICA_ID = UUID.fromString("d1000000-0000-4000-8000-000000000002");
    private static final UUID CRITERIO_ID = UUID.fromString("d2000000-0000-4000-8000-000000000001");
    private static final OffsetDateTime REVISADO_EN = OffsetDateTime.parse("2026-10-08T10:00:00-03:00");

    @Mock
    private IActividadJpaRepository actividades;

    @Mock
    private RubricaRepository rubricaRepository;

    private RetoRepositoryImpl retoRepositoryImpl;

    @BeforeEach
    void setUp() {
        retoRepositoryImpl = new RetoRepositoryImpl(actividades, rubricaRepository, new RetoRepositoryMapper());
    }

    @Test
    @DisplayName("Devuelve una lista vacía sin consultar rúbricas cuando el nodo no tiene retos")
    void findAprobadosByNodoShouldReturnEmptyWithoutQueryingRubricasWhenNoRetos() {
        givenNoRetosInNodo();

        List<Reto> result = findAprobadosByNodo();

        thenResultIsEmptyAndRubricasNotQueried(result);
    }

    @Test
    @DisplayName("Mapea los campos del reto y le asigna su rúbrica global")
    void findAprobadosByNodoShouldMapRetoWithItsRubrica() {
        givenRetosInNodo(List.of(actividad(RETO_ID, RUBRICA_ID)));
        givenGlobalRubricas(List.of(RUBRICA_ID), Map.of(RUBRICA_ID, rubrica(RUBRICA_ID)));

        List<Reto> result = findAprobadosByNodo();

        thenResultHasMappedReto(result);
    }

    @Test
    @DisplayName("Consulta una sola vez las rúbricas reutilizadas y conserva el orden de los retos")
    void findAprobadosByNodoShouldQueryReusedRubricaOnceAndKeepOrder() {
        givenRetosInNodo(List.of(actividad(RETO_ID, RUBRICA_ID), actividad(OTRO_RETO_ID, RUBRICA_ID)));
        givenGlobalRubricas(List.of(RUBRICA_ID), Map.of(RUBRICA_ID, rubrica(RUBRICA_ID)));

        List<Reto> result = findAprobadosByNodo();

        thenRetosKeepOrderAndRubricaQueriedOnce(result);
    }

    @Test
    @DisplayName("Descarta los retos cuya rúbrica no es global")
    void findAprobadosByNodoShouldDiscardRetosWithoutGlobalRubrica() {
        givenRetosInNodo(List.of(actividad(RETO_ID, RUBRICA_ID), actividad(OTRO_RETO_ID, OTRA_RUBRICA_ID)));
        givenGlobalRubricas(List.of(RUBRICA_ID, OTRA_RUBRICA_ID), Map.of(RUBRICA_ID, rubrica(RUBRICA_ID)));

        List<Reto> result = findAprobadosByNodo();

        assertThat(result).extracting(Reto::getId).containsExactly(RETO_ID);
    }

    @Test
    @DisplayName("Devuelve vacío sin consultar rúbricas cuando el reto no existe")
    void findAprobadoByIdShouldReturnEmptyWithoutQueryingRubricasWhenAbsent() {
        givenRetoAbsent();

        Optional<Reto> result = findAprobadoById();

        thenResultIsEmptyAndRubricasNotQueried(result);
    }

    @Test
    @DisplayName("Devuelve el reto mapeado con su rúbrica global")
    void findAprobadoByIdShouldReturnMappedRetoWithItsRubrica() {
        givenRetoFound(actividad(RETO_ID, RUBRICA_ID));
        givenGlobalRubricas(List.of(RUBRICA_ID), Map.of(RUBRICA_ID, rubrica(RUBRICA_ID)));

        Optional<Reto> result = findAprobadoById();

        assertThat(result).isPresent();
        thenResultHasMappedReto(List.of(result.get()));
    }

    @Test
    @DisplayName("Devuelve vacío cuando la rúbrica del reto ya no es global")
    void findAprobadoByIdShouldReturnEmptyWhenRubricaIsNotGlobal() {
        givenRetoFound(actividad(RETO_ID, RUBRICA_ID));
        givenGlobalRubricas(List.of(RUBRICA_ID), Map.of());

        Optional<Reto> result = findAprobadoById();

        assertThat(result).isEmpty();
    }

    // --- arrange ---
    private void givenNoRetosInNodo() {
        when(actividades.findRetosAprobados(RUTA_ID, NODO_ID)).thenReturn(List.of());
    }

    private void givenRetosInNodo(List<ActividadEntity> retos) {
        when(actividades.findRetosAprobados(RUTA_ID, NODO_ID)).thenReturn(retos);
    }

    private void givenRetoAbsent() {
        when(actividades.findRetoAprobadoById(RUTA_ID, NODO_ID, RETO_ID)).thenReturn(Optional.empty());
    }

    private void givenRetoFound(ActividadEntity reto) {
        when(actividades.findRetoAprobadoById(RUTA_ID, NODO_ID, RETO_ID)).thenReturn(Optional.of(reto));
    }

    private void givenGlobalRubricas(List<UUID> ids, Map<UUID, Rubrica> rubricas) {
        when(rubricaRepository.findGlobalesByIds(ids)).thenReturn(rubricas);
    }

    // --- helpers ---
    private ActividadEntity actividad(UUID id, UUID rubricaId) {
        return ActividadEntity.builder()
                .id(id)
                .rutaId(RUTA_ID)
                .nodoId(NODO_ID)
                .hitoId(HITO_ID)
                .rubricaId(rubricaId)
                .tipo("ENSAYO")
                .titulo("Reto 01")
                .nivel((short) 2)
                .contenido(Map.of("consigna", "Responder", "respuestaEsperada", "SECRETO"))
                .origen("HUMANO")
                .estadoRevision("APROBADA")
                .revisadoEn(REVISADO_EN)
                .build();
    }

    private Rubrica rubrica(UUID id) {
        CriterioRubrica criterio = new CriterioRubrica(
                CRITERIO_ID, (short) 1, "Claridad", null, (short) 3, BigDecimal.ONE, false);
        return new Rubrica(id, "Escritura", null, new BigDecimal("2.00"), List.of(criterio));
    }

    // --- act ---
    private List<Reto> findAprobadosByNodo() {
        return retoRepositoryImpl.findAprobadosByNodo(RUTA_ID, NODO_ID);
    }

    private Optional<Reto> findAprobadoById() {
        return retoRepositoryImpl.findAprobadoById(RUTA_ID, NODO_ID, RETO_ID);
    }

    // --- assert ---
    private void thenResultIsEmptyAndRubricasNotQueried(List<Reto> result) {
        assertThat(result).isEmpty();
        verifyNoInteractions(rubricaRepository);
    }

    private void thenResultIsEmptyAndRubricasNotQueried(Optional<Reto> result) {
        assertThat(result).isEmpty();
        verifyNoInteractions(rubricaRepository);
    }

    private void thenResultHasMappedReto(List<Reto> result) {
        assertThat(result).hasSize(1);
        Reto reto = result.getFirst();
        assertThat(reto.getId()).isEqualTo(RETO_ID);
        assertThat(reto.getRutaId()).isEqualTo(RUTA_ID);
        assertThat(reto.getNodoId()).isEqualTo(NODO_ID);
        assertThat(reto.getHitoId()).isEqualTo(HITO_ID);
        assertThat(reto.getTipo()).isEqualTo(TipoReto.ENSAYO);
        assertThat(reto.getTitulo()).isEqualTo("Reto 01");
        assertThat(reto.getNivel()).isEqualTo((short) 2);
        assertThat(reto.getContenido()).containsOnlyKeys("consigna");
        assertThat(reto.getOrigen()).isEqualTo("HUMANO");
        assertThat(reto.getRevisadoEn()).isEqualTo(REVISADO_EN);
        assertThat(reto.getRubrica().id()).isEqualTo(RUBRICA_ID);
    }

    private void thenRetosKeepOrderAndRubricaQueriedOnce(List<Reto> result) {
        assertThat(result).extracting(Reto::getId).containsExactly(RETO_ID, OTRO_RETO_ID);
        verify(rubricaRepository, times(1)).findGlobalesByIds(List.of(RUBRICA_ID));
    }
}
