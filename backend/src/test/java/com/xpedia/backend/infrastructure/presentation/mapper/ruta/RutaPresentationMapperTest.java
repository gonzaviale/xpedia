package com.xpedia.backend.infrastructure.presentation.mapper.ruta;

import com.xpedia.backend.domain.dto.ruta.ListarRutasRequest;
import com.xpedia.backend.domain.dto.ruta.ListarRutasResponse;
import com.xpedia.backend.domain.dto.ruta.ObtenerRutaRequest;
import com.xpedia.backend.domain.dto.ruta.ObtenerRutaResponse;
import com.xpedia.backend.domain.dto.ruta.RutaItem;
import com.xpedia.backend.domain.model.enums.EstadoRuta;
import com.xpedia.backend.domain.model.enums.ObjetivoRuta;
import com.xpedia.backend.domain.model.enums.TipoRuta;
import com.xpedia.backend.domain.model.enums.ValidacionRuta;
import com.xpedia.backend.infrastructure.presentation.dto.ruta.ListarRutasQuery;
import com.xpedia.backend.infrastructure.presentation.dto.ruta.RutaPageResponse;
import com.xpedia.backend.infrastructure.presentation.dto.ruta.RutaResponse;
import com.xpedia.backend.infrastructure.presentation.dto.ruta.RutaResumenResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RutaPresentationMapperTest {

    private static final UUID RUTA_ID = UUID.randomUUID();
    private static final String SLUG = "atencion-al-cliente";
    private static final Integer VERSION = 3;
    private static final String TITULO = "Atención al cliente";
    private static final String META = "Atender consultas por chat";
    private static final String PERFIL_INICIAL = "Personas con experiencia";
    private static final String PAIS = "AR";
    private static final BigDecimal HORAS_ESTIMADAS = new BigDecimal("10.5");
    private static final Short RITMO_RECOMENDADO_MIN = 30;
    private static final OffsetDateTime REVISADA_EN = OffsetDateTime.parse("2026-03-01T10:15:30Z");
    private static final OffsetDateTime CREADO_EN = OffsetDateTime.parse("2026-02-01T08:00:00Z");
    private static final OffsetDateTime ACTUALIZADO_EN = OffsetDateTime.parse("2026-03-02T09:30:00Z");
    private static final int PAGE = 2;
    private static final int SIZE = 3;
    private static final long TOTAL_ELEMENTS = 9;
    private static final int TOTAL_PAGES = 3;

    private final RutaPresentationMapper mapper = new RutaPresentationMapper();

    @Test
    @DisplayName("Aplica página 0 y tamaño 20 cuando la query no trae page ni size")
    void toListarRequestShouldApplyDefaultsWhenPageAndSizeAreNull() {
        ListarRutasRequest request = toListarRequest(new ListarRutasQuery(null, null, null, null));

        assertThat(request.page()).isZero();
        assertThat(request.size()).isEqualTo(20);
    }

    @Test
    @DisplayName("Conserva filtros, página y tamaño explícitos de la query")
    void toListarRequestShouldKeepExplicitValues() {
        ListarRutasRequest request = toListarRequest(
                new ListarRutasQuery(TipoRuta.CAMBIO_RUBRO, ObjetivoRuta.CAMBIAR, PAGE, SIZE));

        assertThat(request.tipo()).isEqualTo(TipoRuta.CAMBIO_RUBRO);
        assertThat(request.objetivo()).isEqualTo(ObjetivoRuta.CAMBIAR);
        assertThat(request.page()).isEqualTo(PAGE);
        assertThat(request.size()).isEqualTo(SIZE);
    }

    @Test
    @DisplayName("Aplica el default solo al campo nulo cuando el otro es explícito")
    void toListarRequestShouldDefaultOnlyNullFieldWhenOtherIsExplicit() {
        ListarRutasRequest request = toListarRequest(new ListarRutasQuery(null, null, PAGE, null));

        assertThat(request.page()).isEqualTo(PAGE);
        assertThat(request.size()).isEqualTo(20);
    }

    @Test
    @DisplayName("Arma el request de obtener con el id recibido")
    void toObtenerRequestShouldWrapId() {
        ObtenerRutaRequest request = mapper.toObtenerRequest(RUTA_ID);

        assertThat(request.id()).isEqualTo(RUTA_ID);
    }

    @Test
    @DisplayName("Mapea cada campo del detalle de ruta a la respuesta de presentación")
    void toResponseShouldMapEveryField() {
        RutaResponse response = mapper.toResponse(detalle());

        thenResponseHasEveryField(response);
    }

    @Test
    @DisplayName("Mapea cada campo del item a un resumen de la página")
    void toPageResponseShouldMapEveryItemField() {
        RutaPageResponse response = mapper.toPageResponse(pagina(List.of(item())));

        thenResumenHasEveryField(response.content().getFirst());
    }

    @Test
    @DisplayName("Conserva los metadatos de paginación en la página de presentación")
    void toPageResponseShouldKeepPageMetadata() {
        RutaPageResponse response = mapper.toPageResponse(pagina(List.of(item())));

        thenPageHasMetadata(response);
    }

    @Test
    @DisplayName("Devuelve contenido vacío cuando la página no tiene items")
    void toPageResponseShouldReturnEmptyContentWhenPageHasNoItems() {
        RutaPageResponse response = mapper.toPageResponse(pagina(List.of()));

        assertThat(response.content()).isEmpty();
    }

    // --- act ---
    private ListarRutasRequest toListarRequest(ListarRutasQuery query) {
        return mapper.toListarRequest(query);
    }

    // --- assert ---
    private void thenResponseHasEveryField(RutaResponse response) {
        assertThat(response.id()).isEqualTo(RUTA_ID);
        assertThat(response.slug()).isEqualTo(SLUG);
        assertThat(response.version()).isEqualTo(VERSION);
        assertThat(response.titulo()).isEqualTo(TITULO);
        assertThat(response.tipo()).isEqualTo(TipoRuta.TECNICA);
        assertThat(response.objetivo()).isEqualTo(ObjetivoRuta.MEJORAR);
        assertThat(response.meta()).isEqualTo(META);
        assertThat(response.perfilInicial()).isEqualTo(PERFIL_INICIAL);
        assertThat(response.pais()).isEqualTo(PAIS);
        assertThat(response.horasEstimadas()).isEqualTo(HORAS_ESTIMADAS);
        assertThat(response.ritmoRecomendadoMin()).isEqualTo(RITMO_RECOMENDADO_MIN);
        assertThat(response.estado()).isEqualTo(EstadoRuta.PUBLICADA);
        assertThat(response.validacion()).isEqualTo(ValidacionRuta.REVISADA);
        assertThat(response.revisadaEn()).isEqualTo(REVISADA_EN);
        assertThat(response.creadoEn()).isEqualTo(CREADO_EN);
        assertThat(response.actualizadoEn()).isEqualTo(ACTUALIZADO_EN);
    }

    private void thenResumenHasEveryField(RutaResumenResponse resumen) {
        assertThat(resumen.id()).isEqualTo(RUTA_ID);
        assertThat(resumen.slug()).isEqualTo(SLUG);
        assertThat(resumen.version()).isEqualTo(VERSION);
        assertThat(resumen.titulo()).isEqualTo(TITULO);
        assertThat(resumen.tipo()).isEqualTo(TipoRuta.TECNICA);
        assertThat(resumen.objetivo()).isEqualTo(ObjetivoRuta.MEJORAR);
        assertThat(resumen.meta()).isEqualTo(META);
        assertThat(resumen.pais()).isEqualTo(PAIS);
        assertThat(resumen.horasEstimadas()).isEqualTo(HORAS_ESTIMADAS);
        assertThat(resumen.ritmoRecomendadoMin()).isEqualTo(RITMO_RECOMENDADO_MIN);
        assertThat(resumen.validacion()).isEqualTo(ValidacionRuta.REVISADA);
    }

    private void thenPageHasMetadata(RutaPageResponse response) {
        assertThat(response.pageNumber()).isEqualTo(PAGE);
        assertThat(response.pageSize()).isEqualTo(SIZE);
        assertThat(response.totalElements()).isEqualTo(TOTAL_ELEMENTS);
        assertThat(response.totalPages()).isEqualTo(TOTAL_PAGES);
        assertThat(response.first()).isFalse();
        assertThat(response.last()).isTrue();
    }

    // --- helpers ---
    private ObtenerRutaResponse detalle() {
        return new ObtenerRutaResponse(
                RUTA_ID,
                SLUG,
                VERSION,
                TITULO,
                TipoRuta.TECNICA,
                ObjetivoRuta.MEJORAR,
                META,
                PERFIL_INICIAL,
                PAIS,
                HORAS_ESTIMADAS,
                RITMO_RECOMENDADO_MIN,
                EstadoRuta.PUBLICADA,
                ValidacionRuta.REVISADA,
                REVISADA_EN,
                CREADO_EN,
                ACTUALIZADO_EN);
    }

    private RutaItem item() {
        return new RutaItem(
                RUTA_ID,
                SLUG,
                VERSION,
                TITULO,
                TipoRuta.TECNICA,
                ObjetivoRuta.MEJORAR,
                META,
                PAIS,
                HORAS_ESTIMADAS,
                RITMO_RECOMENDADO_MIN,
                ValidacionRuta.REVISADA);
    }

    private ListarRutasResponse pagina(List<RutaItem> items) {
        return new ListarRutasResponse(items, PAGE, SIZE, TOTAL_ELEMENTS, TOTAL_PAGES, false, true);
    }
}
