package com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces;

import com.xpedia.backend.infrastructure.repository.entity.ActividadEntity;
import com.xpedia.backend.infrastructure.repository.entity.PreguntaEntity;
import com.xpedia.backend.support.PostgresRepositoryTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Sql({"/db/rutas-test.sql", "/db/cuestionarios-test.sql"})
class IActividadCuestionariosJpaRepositoryTest extends PostgresRepositoryTestSupport {

    private static final UUID RUTA_ID = UUID.fromString("00000000-0000-0000-0000-000000000201");
    private static final UUID NODO_ID = UUID.fromString("00000000-0000-0000-0000-000000000501");
    private static final UUID CUESTIONARIO_INICIAL_ID = UUID.fromString("00000000-0000-0000-0000-000000000a01");
    private static final UUID CUESTIONARIO_POSTERIOR_ID = UUID.fromString("00000000-0000-0000-0000-000000000a02");
    private static final UUID CUESTIONARIO_PENDIENTE_ID = UUID.fromString("00000000-0000-0000-0000-000000000a03");
    private static final UUID CUESTIONARIO_SIN_PREGUNTAS_ID = UUID.fromString("00000000-0000-0000-0000-000000000a06");
    private static final UUID PRIMERA_PREGUNTA_ID = UUID.fromString("00000000-0000-0000-0000-000000000b01");
    private static final UUID SEGUNDA_PREGUNTA_ID = UUID.fromString("00000000-0000-0000-0000-000000000b02");
    private static final String TITULO_INICIAL = "Cuestionario inicial";
    private static final String TITULO_POSTERIOR = "Cuestionario posterior";

    @Autowired
    private IActividadJpaRepository actividades;

    @Autowired
    private IPreguntaJpaRepository preguntas;

    @Test
    @DisplayName("Devuelve solo los cuestionarios aprobados con preguntas, ordenados por nivel")
    void findCuestionariosAprobadosShouldReturnOnlyApprovedWithPreguntasOrderedByNivel() {
        List<ActividadEntity> result = actividades.findCuestionariosAprobados(RUTA_ID, NODO_ID);

        assertThat(result).extracting(ActividadEntity::getTitulo)
                .containsExactly(TITULO_INICIAL, TITULO_POSTERIOR);
    }

    @Test
    @DisplayName("Devuelve lista vacía cuando el nodo no existe")
    void findCuestionariosAprobadosShouldReturnEmptyListWhenNodoDoesNotExist() {
        List<ActividadEntity> result = actividades.findCuestionariosAprobados(RUTA_ID, UUID.randomUUID());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Devuelve el cuestionario aprobado por id")
    void findCuestionarioAprobadoByIdShouldReturnApprovedCuestionario() {
        Optional<ActividadEntity> result = actividades.findCuestionarioAprobadoById(CUESTIONARIO_INICIAL_ID);

        assertThat(result).isPresent();
        assertThat(result.get().getRutaId()).isEqualTo(RUTA_ID);
        assertThat(result.get().getNodoId()).isEqualTo(NODO_ID);
    }

    @Test
    @DisplayName("No devuelve por id un cuestionario pendiente de revisión")
    void findCuestionarioAprobadoByIdShouldReturnEmptyWhenCuestionarioIsPending() {
        Optional<ActividadEntity> result = actividades.findCuestionarioAprobadoById(CUESTIONARIO_PENDIENTE_ID);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("No devuelve por id un cuestionario sin preguntas")
    void findCuestionarioAprobadoByIdShouldReturnEmptyWhenCuestionarioHasNoPreguntas() {
        Optional<ActividadEntity> result = actividades.findCuestionarioAprobadoById(CUESTIONARIO_SIN_PREGUNTAS_ID);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Devuelve las preguntas agrupables por actividad, ordenadas por posición")
    void findByActividadIdInShouldReturnPreguntasOrderedByActividadAndPosicion() {
        List<PreguntaEntity> result = preguntas.findByActividadIdInOrderByActividadIdAscPosicionAsc(
                List.of(CUESTIONARIO_INICIAL_ID, CUESTIONARIO_POSTERIOR_ID));

        assertThat(result).extracting(PreguntaEntity::getActividadId)
                .containsExactly(CUESTIONARIO_INICIAL_ID, CUESTIONARIO_INICIAL_ID, CUESTIONARIO_POSTERIOR_ID);
        assertThat(result).extracting(PreguntaEntity::getId)
                .startsWith(PRIMERA_PREGUNTA_ID, SEGUNDA_PREGUNTA_ID);
    }

    @Test
    @DisplayName("Mapea las opciones jsonb, la opción correcta y la explicación")
    void findByActividadIdInShouldMapOpcionesJsonbAndCorrectOption() {
        List<PreguntaEntity> result = preguntas.findByActividadIdInOrderByActividadIdAscPosicionAsc(
                List.of(CUESTIONARIO_INICIAL_ID));

        PreguntaEntity segunda = result.get(1);
        assertThat(segunda.getEnunciado()).isEqualTo("Segunda pregunta ñ");
        assertThat(segunda.getOpciones()).containsExactly("Sí", "No", "Tal vez");
        assertThat(segunda.getCorrecta()).isEqualTo((short) 2);
        assertThat(segunda.getExplicacion()).isEqualTo("Porque tal vez");
        assertThat(result.getFirst().getExplicacion()).isNull();
    }
}
