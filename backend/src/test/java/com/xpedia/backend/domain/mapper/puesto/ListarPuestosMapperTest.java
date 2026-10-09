package com.xpedia.backend.domain.mapper.puesto;

import com.xpedia.backend.domain.dto.puesto.ListarPuestosRequest;
import com.xpedia.backend.domain.dto.puesto.ListarPuestosResponse;
import com.xpedia.backend.domain.dto.puesto.PuestoItem;
import com.xpedia.backend.domain.model.puesto.Puesto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ListarPuestosMapperTest {

    private static final UUID PUESTO_ID = UUID.randomUUID();
    private static final UUID ORGANIZACION_ID = UUID.randomUUID();
    private static final String NOMBRE = "Voz N1";
    private static final OffsetDateTime CREADO_EN = OffsetDateTime.parse("2026-01-10T10:00:00Z");
    private static final OffsetDateTime ACTUALIZADO_EN = OffsetDateTime.parse("2026-02-11T11:30:00Z");
    private static final int PAGE = 1;
    private static final int SIZE = 5;
    private static final long TOTAL_ELEMENTS = 10;

    private final ListarPuestosMapper mapper = new ListarPuestosMapper();

    @Test
    @DisplayName("toPageable usa la página y el tamaño del request ordenando por nombre")
    void toPageableShouldUsePageAndSizeSortedByNombre() {
        Pageable pageable = toPageable();

        thenPageableIsSortedByNombre(pageable);
    }

    @Test
    @DisplayName("toResponse copia cada puesto de la página con todos sus campos")
    void toResponseShouldMapEveryPuestoField() {
        ListarPuestosResponse response = toResponse(pageWithOnePuesto());

        thenContentHasPuestoItem(response);
    }

    @Test
    @DisplayName("toResponse conserva los metadatos de paginación")
    void toResponseShouldKeepPaginationMetadata() {
        ListarPuestosResponse response = toResponse(pageWithOnePuesto());

        thenResponseHasPaginationMetadata(response);
    }

    @Test
    @DisplayName("toResponse devuelve contenido vacío cuando la página está vacía")
    void toResponseShouldReturnEmptyContentWhenPageIsEmpty() {
        ListarPuestosResponse response = toResponse(Page.empty());

        assertThat(response.content()).isEmpty();
    }

    // --- helpers ---
    private Puesto puesto() {
        return Puesto.builder()
                .id(PUESTO_ID)
                .organizacionId(ORGANIZACION_ID)
                .nombre(NOMBRE)
                .creadoEn(CREADO_EN)
                .actualizadoEn(ACTUALIZADO_EN)
                .build();
    }

    private Page<Puesto> pageWithOnePuesto() {
        return new PageImpl<>(List.of(puesto()), PageRequest.of(PAGE, SIZE), TOTAL_ELEMENTS);
    }

    // --- act ---
    private Pageable toPageable() {
        return mapper.toPageable(new ListarPuestosRequest(ORGANIZACION_ID, PAGE, SIZE));
    }

    private ListarPuestosResponse toResponse(Page<Puesto> page) {
        return mapper.toResponse(page);
    }

    // --- assert ---
    private void thenPageableIsSortedByNombre(Pageable pageable) {
        assertThat(pageable.getPageNumber()).isEqualTo(PAGE);
        assertThat(pageable.getPageSize()).isEqualTo(SIZE);
        assertThat(pageable.getSort()).isEqualTo(Sort.by("nombre"));
    }

    private void thenContentHasPuestoItem(ListarPuestosResponse response) {
        assertThat(response.content()).hasSize(1);
        PuestoItem item = response.content().getFirst();
        assertThat(item.id()).isEqualTo(PUESTO_ID);
        assertThat(item.organizacionId()).isEqualTo(ORGANIZACION_ID);
        assertThat(item.nombre()).isEqualTo(NOMBRE);
        assertThat(item.creadoEn()).isEqualTo(CREADO_EN);
        assertThat(item.actualizadoEn()).isEqualTo(ACTUALIZADO_EN);
    }

    private void thenResponseHasPaginationMetadata(ListarPuestosResponse response) {
        assertThat(response.pageNumber()).isEqualTo(PAGE);
        assertThat(response.pageSize()).isEqualTo(SIZE);
        assertThat(response.totalElements()).isEqualTo(TOTAL_ELEMENTS);
        assertThat(response.totalPages()).isEqualTo(2);
        assertThat(response.first()).isFalse();
        assertThat(response.last()).isTrue();
    }
}
