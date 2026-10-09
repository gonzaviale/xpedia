package com.xpedia.backend.infrastructure.repository.jpaRepository.implementation;

import com.xpedia.backend.domain.model.enums.TipoNodo;
import com.xpedia.backend.domain.model.nodo.Nodo;
import com.xpedia.backend.infrastructure.repository.entity.NodoEntity;
import com.xpedia.backend.infrastructure.repository.entity.NodoPrerrequisitoEntity;
import com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces.INodoJpaRepository;
import com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces.INodoJpaRepository.ReferenciasVisibles;
import com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces.INodoPrerrequisitoJpaRepository;
import com.xpedia.backend.infrastructure.repository.mapper.NodoRepositoryMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NodoRepositoryImplTest {

    private static final UUID RUTA_ID = UUID.randomUUID();
    private static final UUID HITO_ID = UUID.randomUUID();
    private static final UUID PRIMER_NODO_ID = UUID.randomUUID();
    private static final UUID SEGUNDO_NODO_ID = UUID.randomUUID();
    private static final UUID RAMA_ID = UUID.randomUUID();
    private static final UUID HABILIDAD_ID = UUID.randomUUID();
    private static final List<UUID> NODO_IDS = List.of(PRIMER_NODO_ID, SEGUNDO_NODO_ID);
    private static final String PRIMER_CODIGO = "A1";
    private static final String SEGUNDO_CODIGO = "A2";
    private static final OffsetDateTime CREADO_EN = OffsetDateTime.parse("2026-01-01T10:00:00Z");
    private static final OffsetDateTime ACTUALIZADO_EN = OffsetDateTime.parse("2026-01-02T10:00:00Z");

    @Mock
    private INodoJpaRepository jpa;

    @Mock
    private INodoPrerrequisitoJpaRepository prerrequisitos;

    private NodoRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new NodoRepositoryImpl(jpa, prerrequisitos, new NodoRepositoryMapper());
    }

    @Test
    @DisplayName("Devuelve true cuando el nodo es visible en la ruta")
    void existsVisibleByIdAndRutaIdShouldReturnTrueWhenNodoIsVisible() {
        givenJpaVisibility(true);

        boolean result = existsVisible();

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Devuelve false cuando el nodo no es visible en la ruta")
    void existsVisibleByIdAndRutaIdShouldReturnFalseWhenNodoIsNotVisible() {
        givenJpaVisibility(false);

        boolean result = existsVisible();

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Devuelve lista vacía y no consulta prerrequisitos ni referencias cuando no hay nodos")
    void findVisiblesShouldReturnEmptyAndSkipAuxiliaryQueriesWhenThereAreNoNodos() {
        givenJpaReturnsNodos(List.of());

        List<Nodo> result = findVisibles();

        thenResultIsEmptyAndAuxiliaryQueriesWereSkipped(result);
    }

    @Test
    @DisplayName("Mapea los campos del nodo con las referencias visibles")
    void findVisiblesShouldMapNodoFieldsWithVisibleReferences() {
        givenJpaReturnsNodos(List.of(nodoEntity(PRIMER_NODO_ID, PRIMER_CODIGO)));
        givenNoPrerrequisitos(List.of(PRIMER_NODO_ID));
        givenReferencias(List.of(PRIMER_NODO_ID), List.of(referencias(PRIMER_NODO_ID, RAMA_ID, HABILIDAD_ID)));

        List<Nodo> result = findVisibles();

        thenNodoHasMappedFieldsAndReferences(result.getFirst());
    }

    @Test
    @DisplayName("Deja rama y habilidad en null cuando el nodo no tiene referencias visibles")
    void findVisiblesShouldLeaveRamaAndHabilidadNullWhenNodoHasNoVisibleReferences() {
        givenJpaReturnsNodos(List.of(nodoEntity(PRIMER_NODO_ID, PRIMER_CODIGO)));
        givenNoPrerrequisitos(List.of(PRIMER_NODO_ID));
        givenReferencias(List.of(PRIMER_NODO_ID), List.of());

        List<Nodo> result = findVisibles();

        assertThat(result.getFirst().getRamaId()).isNull();
        assertThat(result.getFirst().getHabilidadId()).isNull();
    }

    @Test
    @DisplayName("Asigna a cada nodo solo sus propios prerrequisitos")
    void findVisiblesShouldAssignPrerrequisitosToEachNodo() {
        givenJpaReturnsNodos(List.of(
                nodoEntity(PRIMER_NODO_ID, PRIMER_CODIGO),
                nodoEntity(SEGUNDO_NODO_ID, SEGUNDO_CODIGO)));
        givenPrerrequisitos(NODO_IDS, List.of(prerrequisito(PRIMER_NODO_ID, SEGUNDO_NODO_ID)));
        givenReferencias(NODO_IDS, List.of());

        List<Nodo> result = findVisibles();

        assertThat(result.get(0).getPrerrequisitoIds()).containsExactly(SEGUNDO_NODO_ID);
    }

    @Test
    @DisplayName("Deja prerrequisitos vacíos en el nodo que no tiene ninguno")
    void findVisiblesShouldLeavePrerrequisitosEmptyWhenNodoHasNone() {
        givenJpaReturnsNodos(List.of(
                nodoEntity(PRIMER_NODO_ID, PRIMER_CODIGO),
                nodoEntity(SEGUNDO_NODO_ID, SEGUNDO_CODIGO)));
        givenPrerrequisitos(NODO_IDS, List.of(prerrequisito(PRIMER_NODO_ID, SEGUNDO_NODO_ID)));
        givenReferencias(NODO_IDS, List.of());

        List<Nodo> result = findVisibles();

        assertThat(result.get(1).getPrerrequisitoIds()).isEmpty();
    }

    @Test
    @DisplayName("Conserva el orden de los nodos de la consulta")
    void findVisiblesShouldKeepQueryOrder() {
        givenJpaReturnsNodos(List.of(
                nodoEntity(PRIMER_NODO_ID, PRIMER_CODIGO),
                nodoEntity(SEGUNDO_NODO_ID, SEGUNDO_CODIGO)));
        givenNoPrerrequisitos(NODO_IDS);
        givenReferencias(NODO_IDS, List.of());

        List<Nodo> result = findVisibles();

        assertThat(result).extracting(Nodo::getId).containsExactlyElementsOf(NODO_IDS);
    }

    @Test
    @DisplayName("Consulta prerrequisitos y referencias una sola vez en lote")
    void findVisiblesShouldQueryPrerrequisitosAndReferenciasOnceInBatch() {
        givenJpaReturnsNodos(List.of(
                nodoEntity(PRIMER_NODO_ID, PRIMER_CODIGO),
                nodoEntity(SEGUNDO_NODO_ID, SEGUNDO_CODIGO)));
        givenNoPrerrequisitos(NODO_IDS);
        givenReferencias(NODO_IDS, List.of());

        findVisibles();

        thenAuxiliaryQueriesRanOnce();
    }

    // --- arrange ---
    private void givenJpaVisibility(boolean visible) {
        when(jpa.existsVisibleByIdAndRutaId(PRIMER_NODO_ID, RUTA_ID)).thenReturn(visible);
    }

    private void givenJpaReturnsNodos(List<NodoEntity> nodos) {
        when(jpa.findByRutaId(RUTA_ID, HITO_ID)).thenReturn(nodos);
    }

    private void givenNoPrerrequisitos(List<UUID> ids) {
        givenPrerrequisitos(ids, List.of());
    }

    private void givenPrerrequisitos(List<UUID> ids, List<NodoPrerrequisitoEntity> edges) {
        when(prerrequisitos.findDeNodosEnRuta(ids, RUTA_ID)).thenReturn(edges);
    }

    private void givenReferencias(List<UUID> ids, List<ReferenciasVisibles> referencias) {
        when(jpa.findReferenciasVisibles(RUTA_ID, ids)).thenReturn(referencias);
    }

    // --- helpers ---
    private NodoEntity nodoEntity(UUID id, String codigo) {
        return NodoEntity.builder()
                .id(id)
                .rutaId(RUTA_ID)
                .hitoId(HITO_ID)
                .codigo(codigo)
                .titulo("Título " + codigo)
                .resumen("Resumen " + codigo)
                .tipo(TipoNodo.NUCLEO)
                .nivel((short) 2)
                .minutosEstimados((short) 30)
                .palabrasClave(new String[]{"comunicación", "cliente"})
                .posicion((short) 1)
                .creadoEn(CREADO_EN)
                .actualizadoEn(ACTUALIZADO_EN)
                .build();
    }

    private NodoPrerrequisitoEntity prerrequisito(UUID nodoId, UUID prerrequisitoId) {
        NodoPrerrequisitoEntity edge = new NodoPrerrequisitoEntity();
        edge.setNodoId(nodoId);
        edge.setPrerrequisitoId(prerrequisitoId);
        return edge;
    }

    private ReferenciasVisibles referencias(UUID nodoId, UUID ramaId, UUID habilidadId) {
        ReferenciasVisibles referencias = mock(ReferenciasVisibles.class);
        when(referencias.getNodoId()).thenReturn(nodoId);
        when(referencias.getRamaId()).thenReturn(ramaId);
        when(referencias.getHabilidadId()).thenReturn(habilidadId);
        return referencias;
    }

    // --- act ---
    private boolean existsVisible() {
        return repository.existsVisibleByIdAndRutaId(PRIMER_NODO_ID, RUTA_ID);
    }

    private List<Nodo> findVisibles() {
        return repository.findVisiblesByRutaIdAndHitoId(RUTA_ID, HITO_ID);
    }

    // --- assert ---
    private void thenResultIsEmptyAndAuxiliaryQueriesWereSkipped(List<Nodo> result) {
        assertThat(result).isEmpty();
        verifyNoInteractions(prerrequisitos);
        verify(jpa, never()).findReferenciasVisibles(any(), any());
    }

    private void thenAuxiliaryQueriesRanOnce() {
        verify(prerrequisitos).findDeNodosEnRuta(NODO_IDS, RUTA_ID);
        verify(jpa).findReferenciasVisibles(RUTA_ID, NODO_IDS);
    }

    private void thenNodoHasMappedFieldsAndReferences(Nodo nodo) {
        assertThat(nodo.getId()).isEqualTo(PRIMER_NODO_ID);
        assertThat(nodo.getRutaId()).isEqualTo(RUTA_ID);
        assertThat(nodo.getHitoId()).isEqualTo(HITO_ID);
        assertThat(nodo.getRamaId()).isEqualTo(RAMA_ID);
        assertThat(nodo.getHabilidadId()).isEqualTo(HABILIDAD_ID);
        assertThat(nodo.getCodigo()).isEqualTo(PRIMER_CODIGO);
        assertThat(nodo.getTitulo()).isEqualTo("Título " + PRIMER_CODIGO);
        assertThat(nodo.getResumen()).isEqualTo("Resumen " + PRIMER_CODIGO);
        assertThat(nodo.getTipo()).isEqualTo(TipoNodo.NUCLEO);
        assertThat(nodo.getNivel()).isEqualTo((short) 2);
        assertThat(nodo.getMinutosEstimados()).isEqualTo((short) 30);
        assertThat(nodo.getPalabrasClave()).containsExactly("comunicación", "cliente");
        assertThat(nodo.getPosicion()).isEqualTo((short) 1);
        assertThat(nodo.getPrerrequisitoIds()).isEmpty();
        assertThat(nodo.getCreadoEn()).isEqualTo(CREADO_EN);
        assertThat(nodo.getActualizadoEn()).isEqualTo(ACTUALIZADO_EN);
    }
}
