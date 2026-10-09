package com.xpedia.backend.infrastructure.presentation.mapper.puesto;

import com.xpedia.backend.domain.dto.puesto.ActualizarPuestoRequest;
import com.xpedia.backend.domain.dto.puesto.ActualizarPuestoResponse;
import com.xpedia.backend.domain.dto.puesto.CrearPuestoRequest;
import com.xpedia.backend.domain.dto.puesto.CrearPuestoResponse;
import com.xpedia.backend.domain.dto.puesto.EliminarPuestoRequest;
import com.xpedia.backend.domain.dto.puesto.ListarPuestosRequest;
import com.xpedia.backend.domain.dto.puesto.ListarPuestosResponse;
import com.xpedia.backend.domain.dto.puesto.ObtenerPuestoRequest;
import com.xpedia.backend.domain.dto.puesto.ObtenerPuestoResponse;
import com.xpedia.backend.domain.dto.puesto.PuestoItem;
import com.xpedia.backend.infrastructure.presentation.dto.puesto.PuestoPageResponse;
import com.xpedia.backend.infrastructure.presentation.dto.puesto.PuestoRequest;
import com.xpedia.backend.infrastructure.presentation.dto.puesto.PuestoResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PuestoPresentationMapperTest {

    private static final UUID PUESTO_ID = UUID.randomUUID();
    private static final UUID ORGANIZACION_ID = UUID.randomUUID();
    private static final String NOMBRE = "Escalamiento N2";
    private static final OffsetDateTime CREADO_EN = OffsetDateTime.parse("2026-01-10T10:00:00Z");
    private static final OffsetDateTime ACTUALIZADO_EN = OffsetDateTime.parse("2026-02-11T11:30:00Z");
    private static final int PAGE = 1;
    private static final int SIZE = 5;
    private static final long TOTAL_ELEMENTS = 10;
    private static final int TOTAL_PAGES = 2;

    private final PuestoPresentationMapper mapper = new PuestoPresentationMapper();

    @Test
    @DisplayName("toCrearRequest copia la organización y el nombre")
    void toCrearRequestShouldMapOrganizacionAndNombre() {
        CrearPuestoRequest request = mapper.toCrearRequest(puestoRequest());

        assertThat(request.organizacionId()).isEqualTo(ORGANIZACION_ID);
        assertThat(request.nombre()).isEqualTo(NOMBRE);
    }

    @Test
    @DisplayName("toActualizarRequest combina el id recibido con el nombre del request")
    void toActualizarRequestShouldMapIdAndNombre() {
        ActualizarPuestoRequest request = mapper.toActualizarRequest(PUESTO_ID, puestoRequest());

        assertThat(request.id()).isEqualTo(PUESTO_ID);
        assertThat(request.nombre()).isEqualTo(NOMBRE);
    }

    @Test
    @DisplayName("toEliminarRequest lleva el id recibido")
    void toEliminarRequestShouldMapId() {
        EliminarPuestoRequest request = mapper.toEliminarRequest(PUESTO_ID);

        assertThat(request.id()).isEqualTo(PUESTO_ID);
    }

    @Test
    @DisplayName("toObtenerRequest lleva el id recibido")
    void toObtenerRequestShouldMapId() {
        ObtenerPuestoRequest request = mapper.toObtenerRequest(PUESTO_ID);

        assertThat(request.id()).isEqualTo(PUESTO_ID);
    }

    @Test
    @DisplayName("toListarRequest lleva la organización, la página y el tamaño")
    void toListarRequestShouldMapOrganizacionPageAndSize() {
        ListarPuestosRequest request = mapper.toListarRequest(ORGANIZACION_ID, PAGE, SIZE);

        assertThat(request.organizacionId()).isEqualTo(ORGANIZACION_ID);
        assertThat(request.page()).isEqualTo(PAGE);
        assertThat(request.size()).isEqualTo(SIZE);
    }

    @Test
    @DisplayName("toResponse copia todos los campos de la respuesta de crear")
    void toResponseShouldMapEveryFieldOfCrearResponse() {
        PuestoResponse response = mapper.toResponse(
                new CrearPuestoResponse(PUESTO_ID, ORGANIZACION_ID, NOMBRE, CREADO_EN, ACTUALIZADO_EN));

        thenResponseHasEveryField(response);
    }

    @Test
    @DisplayName("toResponse copia todos los campos de la respuesta de actualizar")
    void toResponseShouldMapEveryFieldOfActualizarResponse() {
        PuestoResponse response = mapper.toResponse(
                new ActualizarPuestoResponse(PUESTO_ID, ORGANIZACION_ID, NOMBRE, CREADO_EN, ACTUALIZADO_EN));

        thenResponseHasEveryField(response);
    }

    @Test
    @DisplayName("toResponse copia todos los campos de la respuesta de obtener")
    void toResponseShouldMapEveryFieldOfObtenerResponse() {
        PuestoResponse response = mapper.toResponse(
                new ObtenerPuestoResponse(PUESTO_ID, ORGANIZACION_ID, NOMBRE, CREADO_EN, ACTUALIZADO_EN));

        thenResponseHasEveryField(response);
    }

    @Test
    @DisplayName("toPageResponse copia cada ítem del contenido con todos sus campos")
    void toPageResponseShouldMapEveryItemField() {
        PuestoPageResponse response = mapper.toPageResponse(listarResponse());

        assertThat(response.content()).hasSize(1);
        thenResponseHasEveryField(response.content().getFirst());
    }

    @Test
    @DisplayName("toPageResponse conserva los metadatos de paginación")
    void toPageResponseShouldKeepPaginationMetadata() {
        PuestoPageResponse response = mapper.toPageResponse(listarResponse());

        assertThat(response.pageNumber()).isEqualTo(PAGE);
        assertThat(response.pageSize()).isEqualTo(SIZE);
        assertThat(response.totalElements()).isEqualTo(TOTAL_ELEMENTS);
        assertThat(response.totalPages()).isEqualTo(TOTAL_PAGES);
        assertThat(response.first()).isFalse();
        assertThat(response.last()).isTrue();
    }

    // --- helpers ---
    private PuestoRequest puestoRequest() {
        return new PuestoRequest(ORGANIZACION_ID, NOMBRE);
    }

    private ListarPuestosResponse listarResponse() {
        PuestoItem item = new PuestoItem(PUESTO_ID, ORGANIZACION_ID, NOMBRE, CREADO_EN, ACTUALIZADO_EN);
        return new ListarPuestosResponse(List.of(item), PAGE, SIZE, TOTAL_ELEMENTS, TOTAL_PAGES, false, true);
    }

    // --- assert ---
    private void thenResponseHasEveryField(PuestoResponse response) {
        assertThat(response.id()).isEqualTo(PUESTO_ID);
        assertThat(response.organizacionId()).isEqualTo(ORGANIZACION_ID);
        assertThat(response.nombre()).isEqualTo(NOMBRE);
        assertThat(response.creadoEn()).isEqualTo(CREADO_EN);
        assertThat(response.actualizadoEn()).isEqualTo(ACTUALIZADO_EN);
    }
}
