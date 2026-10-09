package com.xpedia.backend.infrastructure.presentation.mapper.inscripcion;

import com.xpedia.backend.domain.dto.inscripcion.CrearInscripcionRequest;
import com.xpedia.backend.domain.dto.inscripcion.ObtenerInscripcionActualRequest;
import com.xpedia.backend.domain.mapper.inscripcion.CrearInscripcionMapper;
import com.xpedia.backend.domain.model.enums.ObjetivoRuta;
import com.xpedia.backend.domain.model.inscripcion.RecorridoPersonal;
import com.xpedia.backend.infrastructure.presentation.dto.inscripcion.CrearInscripcionWebRequest;
import com.xpedia.backend.infrastructure.presentation.dto.inscripcion.InscripcionResponse;
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
import static com.xpedia.backend.support.InscripcionTestData.USUARIO_ID;
import static com.xpedia.backend.support.InscripcionTestData.inscripcion;
import static com.xpedia.backend.support.InscripcionTestData.recorrido;
import static org.assertj.core.api.Assertions.assertThat;

class InscripcionPresentationMapperTest {

    private final InscripcionPresentationMapper mapper = new InscripcionPresentationMapper();

    @Test
    @DisplayName("Convierte la petición web usando la identidad proporcionada por la sesión")
    void toRequestShouldMapBodyAndSessionIdentity() {
        var web = new CrearInscripcionWebRequest(RUTA_ID, ObjetivoRuta.CAMBIAR, META, (short) 20);

        CrearInscripcionRequest request = mapper.toRequest(USUARIO_ID, web);

        thenRequestHasSessionAndBody(request);
    }

    @Test
    @DisplayName("Convierte la identidad de sesión en consulta actual")
    void toActualRequestShouldMapSessionIdentity() {
        ObtenerInscripcionActualRequest request = mapper.toActualRequest(USUARIO_ID);

        thenToActualRequestShouldMapSessionIdentity(request);
    }

    @Test
    @DisplayName("Convierte la respuesta de dominio en contrato web sin información de usuario")
    void toResponseShouldMapEveryPublicField() {
        var item = new CrearInscripcionMapper().toResponse(recorrido());

        InscripcionResponse response = mapper.toResponse(item);

        thenEveryPublicField(response);
    }

    @Test
    @DisplayName("Mantiene una lista de progreso vacía")
    void toResponseShouldKeepEmptyProgress() {
        var item = new CrearInscripcionMapper().toResponse(new RecorridoPersonal(inscripcion(), SLUG, List.of()));

        InscripcionResponse response = mapper.toResponse(item);

        thenToResponseShouldKeepEmptyProgress(response);
    }

    // --- assert ---
    private void thenRequestHasSessionAndBody(CrearInscripcionRequest request) {
        assertThat(request.usuarioId()).isEqualTo(USUARIO_ID);
        assertThat(request.rutaId()).isEqualTo(RUTA_ID);
        assertThat(request.objetivo()).isEqualTo(ObjetivoRuta.CAMBIAR);
        assertThat(request.metaPersonal()).isEqualTo(META);
        assertThat(request.ritmoMin()).isEqualTo((short) 20);
    }

    private void thenEveryPublicField(InscripcionResponse response) {
        assertThat(response.id()).isEqualTo(INSCRIPCION_ID);
        assertThat(response.rutaId()).isEqualTo(RUTA_ID);
        assertThat(response.rutaSlug()).isEqualTo(SLUG);
        assertThat(response.objetivo()).isEqualTo(ObjetivoRuta.CAMBIAR);
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

    private void thenToActualRequestShouldMapSessionIdentity(ObtenerInscripcionActualRequest request) {
        assertThat(request.usuarioId()).isEqualTo(USUARIO_ID);
    }

    private void thenToResponseShouldKeepEmptyProgress(InscripcionResponse response) {
        assertThat(response.progreso()).isEmpty();
    }
}

