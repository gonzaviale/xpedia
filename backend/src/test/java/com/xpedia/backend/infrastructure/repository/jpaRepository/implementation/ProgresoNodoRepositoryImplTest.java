package com.xpedia.backend.infrastructure.repository.jpaRepository.implementation;

import com.xpedia.backend.domain.model.inscripcion.ProgresoNodo;
import com.xpedia.backend.infrastructure.repository.entity.ProgresoNodoEntity;
import com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces.IProgresoNodoJpaRepository;
import com.xpedia.backend.infrastructure.repository.mapper.ProgresoNodoRepositoryMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.xpedia.backend.support.InscripcionTestData.INSCRIPCION_ID;
import static com.xpedia.backend.support.InscripcionTestData.NODO_ID;
import static com.xpedia.backend.support.InscripcionTestData.progreso;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProgresoNodoRepositoryImplTest {

    @Mock
    private IProgresoNodoJpaRepository jpaRepository;

    private ProgresoNodoRepositoryImpl repository;

    private final ProgresoNodoRepositoryMapper mapper = new ProgresoNodoRepositoryMapper();

    @BeforeEach
    void setUp() {
        repository = new ProgresoNodoRepositoryImpl(jpaRepository, mapper);
    }

    @Test
    @DisplayName("Guarda los progresos en un único lote conservando sus valores")
    void saveAllShouldMapAndFlushBatch() {
        repository.saveAll(List.of(progreso()));

        thenProgressBatchWasSaved();
    }

    @Test
    @DisplayName("Devuelve los progresos en el orden recibido del repositorio")
    void findByInscripcionIdShouldKeepOrderedResults() {
        givenFindByInscripcionIdShouldKeepOrderedResults();

        List<ProgresoNodo> result = repository.findByInscripcionId(INSCRIPCION_ID);

        thenFindByInscripcionIdShouldKeepOrderedResults(result);
    }

    @Test
    @DisplayName("Devuelve lista vacía para una inscripción sin progresos")
    void findByInscripcionIdShouldKeepEmptyResult() {
        List<ProgresoNodo> result = repository.findByInscripcionId(INSCRIPCION_ID);

        thenFindByInscripcionIdShouldKeepEmptyResult(result);
    }

    // --- arrange ---
    private void givenFindByInscripcionIdShouldKeepOrderedResults() {
        when(jpaRepository.findByInscripcionIdOrdenado(INSCRIPCION_ID))
                .thenReturn(List.of(mapper.toEntity(progreso())));
    }

    // --- assert ---
    @SuppressWarnings("unchecked")
    private void thenProgressBatchWasSaved() {
        ArgumentCaptor<List<ProgresoNodoEntity>> captor = ArgumentCaptor.forClass(List.class);
        verify(jpaRepository).saveAllAndFlush(captor.capture());
        List<ProgresoNodoEntity> entities = captor.getValue();
        assertThat(entities).hasSize(1);
        assertThat(entities.getFirst().getInscripcionId()).isEqualTo(INSCRIPCION_ID);
        assertThat(entities.getFirst().getNodoId()).isEqualTo(NODO_ID);
        assertThat(entities.getFirst().getDominio()).isEqualByComparingTo("0.65");
    }

    private void thenFindByInscripcionIdShouldKeepOrderedResults(List<ProgresoNodo> result) {
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getNodoId()).isEqualTo(NODO_ID);
        assertThat(result.getFirst().getDominio()).isEqualByComparingTo("0.65");
    }

    private void thenFindByInscripcionIdShouldKeepEmptyResult(List<ProgresoNodo> result) {
        assertThat(result).isEmpty();
    }
}

