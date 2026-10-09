package com.xpedia.backend.infrastructure.repository.mapper;

import com.xpedia.backend.domain.model.microleccion.FuenteMicroleccion;
import com.xpedia.backend.domain.model.microleccion.Microleccion;
import com.xpedia.backend.infrastructure.repository.entity.ActividadEntity;
import com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces.IActividadFuenteJpaRepository.FuenteVisible;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MicroleccionRepositoryMapperTest {

    private static final UUID ACTIVIDAD_ID = UUID.randomUUID();
    private static final UUID RUTA_ID = UUID.randomUUID();
    private static final UUID NODO_ID = UUID.randomUUID();
    private static final UUID FUENTE_ID = UUID.randomUUID();
    private static final String TITULO = "Lección inicial";
    private static final Short NIVEL = 2;
    private static final Map<String, Object> CONTENIDO = Map.of("texto", "Comunicación ñ");
    private static final String ORIGEN = "HUMANO";
    private static final OffsetDateTime REVISADO_EN = OffsetDateTime.parse("2026-10-01T10:00:00Z");
    private static final String FUENTE_TITULO = "Fuente global";
    private static final String FUENTE_URL = "https://example.org/material";
    private static final String FUENTE_LICENCIA = "Licencia de prueba";
    private static final String FUENTE_USO = "ADAPTABLE";
    private static final String FUENTE_UBICACION = "Sección 1";
    private static final String FUENTE_SOLO_ENLACE_USO = "SOLO_ENLACE";
    private static final String FUENTE_SOLO_ENLACE_LICENCIA = "Solo referencia";

    private MicroleccionRepositoryMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new MicroleccionRepositoryMapper();
    }

    @Test
    @DisplayName("Mapea cada campo de la entidad a la microlección")
    void toDomainShouldMapEveryEntityField() {
        ActividadEntity entity = actividad();

        Microleccion result = toDomain(entity, List.of());

        thenMicroleccionHasEntityFields(result);
    }

    @Test
    @DisplayName("Asigna a la microlección solo las fuentes recibidas")
    void toDomainShouldAssignReceivedFuentes() {
        FuenteMicroleccion fuente = new FuenteMicroleccion(FUENTE_ID, FUENTE_TITULO, FUENTE_URL,
                FUENTE_LICENCIA, FUENTE_USO, true, FUENTE_UBICACION);

        Microleccion result = toDomain(actividad(), List.of(fuente));

        assertThat(result.getFuentes()).containsExactly(fuente);
    }

    @Test
    @DisplayName("Deja la microlección sin fuentes cuando no recibe ninguna")
    void toDomainShouldLeaveFuentesEmptyWhenNoneReceived() {
        Microleccion result = toDomain(actividad(), List.of());

        assertThat(result.getFuentes()).isEmpty();
    }

    @Test
    @DisplayName("Mapea cada campo de la fuente visible a la fuente de dominio")
    void toFuenteShouldMapEveryFuenteVisibleField() {
        FuenteVisible input = fuenteVisibleCompleta();

        FuenteMicroleccion result = toFuente(input);

        thenFuenteHasAllFields(result);
    }

    @Test
    @DisplayName("Conserva los campos nulos y los permisos de una fuente solo enlace")
    void toFuenteShouldKeepNullsAndPermissionsForLinkOnlyFuente() {
        FuenteVisible input = fuenteSoloEnlace();

        FuenteMicroleccion result = toFuente(input);

        thenFuenteIsLinkOnly(result);
    }

    // --- act ---
    private Microleccion toDomain(ActividadEntity entity, List<FuenteMicroleccion> fuentes) {
        return mapper.toDomain(entity, fuentes);
    }

    private FuenteMicroleccion toFuente(FuenteVisible input) {
        return mapper.toFuente(input);
    }

    // --- assert ---
    private void thenMicroleccionHasEntityFields(Microleccion result) {
        assertThat(result.getId()).isEqualTo(ACTIVIDAD_ID);
        assertThat(result.getRutaId()).isEqualTo(RUTA_ID);
        assertThat(result.getNodoId()).isEqualTo(NODO_ID);
        assertThat(result.getTitulo()).isEqualTo(TITULO);
        assertThat(result.getNivel()).isEqualTo(NIVEL);
        assertThat(result.getContenido()).isEqualTo(CONTENIDO);
        assertThat(result.getOrigen()).isEqualTo(ORIGEN);
        assertThat(result.getRevisadoEn()).isEqualTo(REVISADO_EN);
    }

    private void thenFuenteHasAllFields(FuenteMicroleccion result) {
        assertThat(result.id()).isEqualTo(FUENTE_ID);
        assertThat(result.titulo()).isEqualTo(FUENTE_TITULO);
        assertThat(result.url()).isEqualTo(FUENTE_URL);
        assertThat(result.licencia()).isEqualTo(FUENTE_LICENCIA);
        assertThat(result.uso()).isEqualTo(FUENTE_USO);
        assertThat(result.permiteUsoComercial()).isTrue();
        assertThat(result.ubicacion()).isEqualTo(FUENTE_UBICACION);
    }

    private void thenFuenteIsLinkOnly(FuenteMicroleccion result) {
        assertThat(result.id()).isEqualTo(FUENTE_ID);
        assertThat(result.titulo()).isEqualTo(FUENTE_TITULO);
        assertThat(result.licencia()).isEqualTo(FUENTE_SOLO_ENLACE_LICENCIA);
        assertThat(result.uso()).isEqualTo(FUENTE_SOLO_ENLACE_USO);
        assertThat(result.permiteUsoComercial()).isFalse();
        assertThat(result.url()).isNull();
        assertThat(result.ubicacion()).isNull();
    }

    // --- helpers ---
    private ActividadEntity actividad() {
        return ActividadEntity.builder()
                .id(ACTIVIDAD_ID)
                .rutaId(RUTA_ID)
                .nodoId(NODO_ID)
                .titulo(TITULO)
                .nivel(NIVEL)
                .contenido(CONTENIDO)
                .origen(ORIGEN)
                .revisadoEn(REVISADO_EN)
                .build();
    }

    private FuenteVisible fuenteVisibleCompleta() {
        FuenteVisible fuente = mock(FuenteVisible.class);
        when(fuente.getId()).thenReturn(FUENTE_ID);
        when(fuente.getTitulo()).thenReturn(FUENTE_TITULO);
        when(fuente.getUrl()).thenReturn(FUENTE_URL);
        when(fuente.getLicencia()).thenReturn(FUENTE_LICENCIA);
        when(fuente.getUso()).thenReturn(FUENTE_USO);
        when(fuente.getPermiteUsoComercial()).thenReturn(true);
        when(fuente.getUbicacion()).thenReturn(FUENTE_UBICACION);
        return fuente;
    }

    private FuenteVisible fuenteSoloEnlace() {
        FuenteVisible fuente = mock(FuenteVisible.class);
        when(fuente.getId()).thenReturn(FUENTE_ID);
        when(fuente.getTitulo()).thenReturn(FUENTE_TITULO);
        when(fuente.getLicencia()).thenReturn(FUENTE_SOLO_ENLACE_LICENCIA);
        when(fuente.getUso()).thenReturn(FUENTE_SOLO_ENLACE_USO);
        when(fuente.getPermiteUsoComercial()).thenReturn(false);
        return fuente;
    }
}
