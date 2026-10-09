package com.xpedia.backend.infrastructure.repository.jpaRepository.implementation;

import com.xpedia.backend.domain.model.cuestionario.Cuestionario;
import com.xpedia.backend.infrastructure.repository.entity.ActividadEntity;
import com.xpedia.backend.infrastructure.repository.entity.PreguntaEntity;
import com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces.IActividadJpaRepository;
import com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces.IPreguntaJpaRepository;
import com.xpedia.backend.infrastructure.repository.mapper.CuestionarioRepositoryMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CuestionarioRepositoryImplTest {

    private static final UUID RUTA_ID = UUID.randomUUID();
    private static final UUID NODO_ID = UUID.randomUUID();
    private static final UUID PRIMERA_ID = UUID.randomUUID();
    private static final UUID SEGUNDA_ID = UUID.randomUUID();

    @Mock
    private IActividadJpaRepository actividades;

    @Mock
    private IPreguntaJpaRepository preguntas;

    private CuestionarioRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new CuestionarioRepositoryImpl(actividades, preguntas, new CuestionarioRepositoryMapper());
    }

    @Test
    @DisplayName("Agrupa las preguntas de cada cuestionario del nodo con una sola consulta de preguntas")
    void findAprobadosByNodoShouldGroupPreguntasPerCuestionarioWithOneQuery() {
        givenNodoHasCuestionarios(List.of(actividad(PRIMERA_ID), actividad(SEGUNDA_ID)));
        givenPreguntasExist(List.of(pregunta(PRIMERA_ID, 0), pregunta(PRIMERA_ID, 1), pregunta(SEGUNDA_ID, 0)));

        List<Cuestionario> result = repository.findAprobadosByNodo(RUTA_ID, NODO_ID);

        assertThat(result).extracting(Cuestionario::getId).containsExactly(PRIMERA_ID, SEGUNDA_ID);
        assertThat(result.get(0).getPreguntas()).hasSize(2);
        assertThat(result.get(1).getPreguntas()).hasSize(1);
        verify(preguntas).findByActividadIdInOrderByActividadIdAscPosicionAsc(List.of(PRIMERA_ID, SEGUNDA_ID));
    }

    @Test
    @DisplayName("No consulta preguntas cuando el nodo no tiene cuestionarios")
    void findAprobadosByNodoShouldNotQueryPreguntasWhenThereAreNoCuestionarios() {
        givenNodoHasCuestionarios(List.of());

        List<Cuestionario> result = repository.findAprobadosByNodo(RUTA_ID, NODO_ID);

        assertThat(result).isEmpty();
        verifyNoInteractions(preguntas);
    }

    @Test
    @DisplayName("Devuelve un cuestionario sin preguntas cuando la actividad no tiene entidades de pregunta")
    void findAprobadosByNodoShouldReturnCuestionarioWithoutPreguntasWhenNoneAreFound() {
        givenNodoHasCuestionarios(List.of(actividad(PRIMERA_ID)));
        givenPreguntasExist(List.of());

        List<Cuestionario> result = repository.findAprobadosByNodo(RUTA_ID, NODO_ID);

        assertThat(result.getFirst().getPreguntas()).isEmpty();
    }

    @Test
    @DisplayName("Devuelve el cuestionario aprobado con sus preguntas cuando existe")
    void findAprobadoByIdShouldReturnCuestionarioWithPreguntasWhenItExists() {
        when(actividades.findCuestionarioAprobadoById(PRIMERA_ID)).thenReturn(Optional.of(actividad(PRIMERA_ID)));
        givenPreguntasExist(List.of(pregunta(PRIMERA_ID, 0)));

        Optional<Cuestionario> result = repository.findAprobadoById(PRIMERA_ID);

        assertThat(result).isPresent();
        assertThat(result.get().getPreguntas()).hasSize(1);
    }

    @Test
    @DisplayName("Devuelve vacío cuando el cuestionario no existe o no está aprobado")
    void findAprobadoByIdShouldReturnEmptyWhenCuestionarioDoesNotExist() {
        when(actividades.findCuestionarioAprobadoById(PRIMERA_ID)).thenReturn(Optional.empty());

        Optional<Cuestionario> result = repository.findAprobadoById(PRIMERA_ID);

        assertThat(result).isEmpty();
        verifyNoInteractions(preguntas);
    }

    // --- arrange ---
    private void givenNodoHasCuestionarios(List<ActividadEntity> cuestionarios) {
        when(actividades.findCuestionariosAprobados(RUTA_ID, NODO_ID)).thenReturn(cuestionarios);
    }

    private void givenPreguntasExist(List<PreguntaEntity> entidades) {
        when(preguntas.findByActividadIdInOrderByActividadIdAscPosicionAsc(anyCollection())).thenReturn(entidades);
    }

    // --- helpers ---
    private ActividadEntity actividad(UUID id) {
        return ActividadEntity.builder()
                .id(id)
                .rutaId(RUTA_ID)
                .nodoId(NODO_ID)
                .build();
    }

    private PreguntaEntity pregunta(UUID actividadId, int posicion) {
        return PreguntaEntity.builder()
                .id(UUID.randomUUID())
                .actividadId(actividadId)
                .posicion((short) posicion)
                .tipo("CONCEPTO")
                .opciones(List.of("A", "B"))
                .correcta((short) 0)
                .build();
    }
}
