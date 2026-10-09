package com.xpedia.backend.domain.mapper.ruta;

import com.xpedia.backend.domain.dto.ruta.ListarRutasRequest;
import com.xpedia.backend.domain.dto.ruta.ListarRutasResponse;
import com.xpedia.backend.domain.dto.ruta.RutaItem;
import com.xpedia.backend.domain.model.enums.ObjetivoRuta;
import com.xpedia.backend.domain.model.enums.TipoRuta;
import com.xpedia.backend.domain.model.enums.ValidacionRuta;
import com.xpedia.backend.domain.model.ruta.Ruta;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ListarRutasMapperTest {

    private static final UUID RUTA_ID = UUID.randomUUID();
    private static final String SLUG = "atencion-al-cliente";
    private static final Integer VERSION = 3;
    private static final String TITULO = "Atención al cliente";
    private static final String META = "Atender consultas por chat";
    private static final String PAIS = "AR";
    private static final BigDecimal HORAS_ESTIMADAS = new BigDecimal("10.5");
    private static final Short RITMO_RECOMENDADO_MIN = 30;
    private static final int PAGE = 2;
    private static final int SIZE = 3;
    private static final long TOTAL_ELEMENTS = 9;

    private final ListarRutasMapper mapper = new ListarRutasMapper();

    @Test
    @DisplayName("Mapea cada campo público de la ruta al item de la respuesta")
    void toResponseShouldMapEveryPublicFieldWhenPageHasRuta() {
        Page<Ruta> page = pageWithRuta();

        ListarRutasResponse response = toResponse(page);

        thenItemHasEveryPublicField(response.content().getFirst());
    }

    @Test
    @DisplayName("Conserva los metadatos de paginación de la página")
    void toResponseShouldKeepPageMetadataWhenPageHasRuta() {
        Page<Ruta> page = pageWithRuta();

        ListarRutasResponse response = toResponse(page);

        thenResponseHasPageMetadata(response);
    }

    @Test
    @DisplayName("Devuelve contenido vacío cuando la página está vacía")
    void toResponseShouldReturnEmptyContentWhenPageIsEmpty() {
        ListarRutasResponse response = toResponse(Page.empty());

        assertThat(response.content()).isEmpty();
    }

    @Test
    @DisplayName("Arma el Pageable con la página y tamaño pedidos")
    void toPageableShouldUseRequestedPageAndSize() {
        Pageable pageable = toPageable();

        assertThat(pageable.getPageNumber()).isEqualTo(PAGE);
        assertThat(pageable.getPageSize()).isEqualTo(SIZE);
    }

    @Test
    @DisplayName("Ordena el Pageable por título e id para un orden estable")
    void toPageableShouldSortByTituloAndId() {
        Pageable pageable = toPageable();

        assertThat(pageable.getSort()).isEqualTo(Sort.by("titulo", "id"));
    }

    // --- act ---
    private ListarRutasResponse toResponse(Page<Ruta> page) {
        return mapper.toResponse(page);
    }

    private Pageable toPageable() {
        return mapper.toPageable(new ListarRutasRequest(null, null, PAGE, SIZE));
    }

    // --- assert ---
    private void thenItemHasEveryPublicField(RutaItem item) {
        assertThat(item.id()).isEqualTo(RUTA_ID);
        assertThat(item.slug()).isEqualTo(SLUG);
        assertThat(item.version()).isEqualTo(VERSION);
        assertThat(item.titulo()).isEqualTo(TITULO);
        assertThat(item.tipo()).isEqualTo(TipoRuta.CAMBIO_RUBRO);
        assertThat(item.objetivo()).isEqualTo(ObjetivoRuta.CAMBIAR);
        assertThat(item.meta()).isEqualTo(META);
        assertThat(item.pais()).isEqualTo(PAIS);
        assertThat(item.horasEstimadas()).isEqualTo(HORAS_ESTIMADAS);
        assertThat(item.ritmoRecomendadoMin()).isEqualTo(RITMO_RECOMENDADO_MIN);
        assertThat(item.validacion()).isEqualTo(ValidacionRuta.REVISADA);
    }

    private void thenResponseHasPageMetadata(ListarRutasResponse response) {
        assertThat(response.pageNumber()).isEqualTo(PAGE);
        assertThat(response.pageSize()).isEqualTo(SIZE);
        assertThat(response.totalElements()).isEqualTo(TOTAL_ELEMENTS);
        assertThat(response.totalPages()).isEqualTo(3);
        assertThat(response.first()).isFalse();
        assertThat(response.last()).isTrue();
    }

    // --- helpers ---
    private Page<Ruta> pageWithRuta() {
        return new PageImpl<>(List.of(ruta()), PageRequest.of(PAGE, SIZE), TOTAL_ELEMENTS);
    }

    private Ruta ruta() {
        return Ruta.builder()
                .id(RUTA_ID)
                .slug(SLUG)
                .version(VERSION)
                .titulo(TITULO)
                .tipo(TipoRuta.CAMBIO_RUBRO)
                .objetivo(ObjetivoRuta.CAMBIAR)
                .meta(META)
                .pais(PAIS)
                .horasEstimadas(HORAS_ESTIMADAS)
                .ritmoRecomendadoMin(RITMO_RECOMENDADO_MIN)
                .validacion(ValidacionRuta.REVISADA)
                .build();
    }
}
