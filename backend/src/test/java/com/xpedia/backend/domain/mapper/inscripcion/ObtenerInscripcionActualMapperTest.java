package com.xpedia.backend.domain.mapper.inscripcion;

import com.xpedia.backend.domain.dto.inscripcion.InscripcionItem;
import com.xpedia.backend.domain.model.inscripcion.RecorridoPersonal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.xpedia.backend.support.InscripcionTestData.HITO_ID;
import static com.xpedia.backend.support.InscripcionTestData.INSCRIPCION_ID;
import static com.xpedia.backend.support.InscripcionTestData.LLEGADA;
import static com.xpedia.backend.support.InscripcionTestData.META;
import static com.xpedia.backend.support.InscripcionTestData.NODO_ID;
import static com.xpedia.backend.support.InscripcionTestData.RUTA_ID;
import static com.xpedia.backend.support.InscripcionTestData.SLUG;
import static com.xpedia.backend.support.InscripcionTestData.inscripcion;
import static com.xpedia.backend.support.InscripcionTestData.recorrido;
import static org.assertj.core.api.Assertions.assertThat;

class ObtenerInscripcionActualMapperTest {

    private final ObtenerInscripcionActualMapper mapper = new ObtenerInscripcionActualMapper();

    @Test
    @DisplayName("Devuelve los datos públicos de inscripción y progreso")
    void toResponseShouldMapPublicEnrollmentAndProgress() {
        InscripcionItem response = mapper.toResponse(recorrido());

        thenPublicFields(response);
    }

    @Test
    @DisplayName("Respeta una inscripción anterior sin objetivo ni llegada y sin progresos")
    void toResponseShouldKeepUnknownLegacyFieldsAndEmptyProgress() {
        var inscripcion = inscripcion();
        inscripcion.setObjetivo(null);
        inscripcion.setMetaPersonal(null);
        inscripcion.setFechaLlegadaEstimada(null);

        InscripcionItem response = mapper.toResponse(new RecorridoPersonal(inscripcion, SLUG, List.of()));

        thenOptionalFieldsAreUnknown(response);
    }

    // --- assert ---
    private void thenPublicFields(InscripcionItem response) {
        assertThat(response.id()).isEqualTo(INSCRIPCION_ID);
        assertThat(response.rutaId()).isEqualTo(RUTA_ID);
        assertThat(response.rutaSlug()).isEqualTo(SLUG);
        assertThat(response.objetivo()).isEqualTo(inscripcion().getObjetivo());
        assertThat(response.metaPersonal()).isEqualTo(META);
        assertThat(response.estado()).isEqualTo("ACTIVA");
        assertThat(response.hitoActualId()).isEqualTo(HITO_ID);
        assertThat(response.ritmoMin()).isEqualTo((short) 20);
        assertThat(response.fechaLlegadaEstimada()).isEqualTo(LLEGADA);
        assertThat(response.progreso()).hasSize(1);
        assertThat(response.progreso().getFirst().nodoId()).isEqualTo(NODO_ID);
        assertThat(response.progreso().getFirst().estado()).isEqualTo("EN_CURSO");
        assertThat(response.progreso().getFirst().dominio()).isEqualByComparingTo("0.65");
    }

    private void thenOptionalFieldsAreUnknown(InscripcionItem response) {
        assertThat(response.objetivo()).isNull();
        assertThat(response.metaPersonal()).isNull();
        assertThat(response.fechaLlegadaEstimada()).isNull();
        assertThat(response.progreso()).isEmpty();
    }
}

