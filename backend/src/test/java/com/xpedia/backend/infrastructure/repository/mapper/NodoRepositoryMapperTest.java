package com.xpedia.backend.infrastructure.repository.mapper;

import com.xpedia.backend.domain.model.enums.TipoNodo;
import com.xpedia.backend.domain.model.nodo.Nodo;
import com.xpedia.backend.infrastructure.repository.entity.NodoEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class NodoRepositoryMapperTest {

    private static final UUID NODO_ID = UUID.randomUUID();
    private static final UUID RUTA_ID = UUID.randomUUID();
    private static final UUID HITO_ID = UUID.randomUUID();
    private static final UUID RAMA_ID = UUID.randomUUID();
    private static final UUID HABILIDAD_ID = UUID.randomUUID();
    private static final UUID PRERREQUISITO_ID = UUID.randomUUID();
    private static final String CODIGO = "A1";
    private static final String TITULO = "Escucha activa";
    private static final String RESUMEN = "Resumen del nodo";
    private static final Short NIVEL = 2;
    private static final Short MINUTOS_ESTIMADOS = 30;
    private static final Short POSICION = 3;
    private static final String[] PALABRAS_CLAVE = {"comunicación", "cliente"};
    private static final OffsetDateTime CREADO_EN = OffsetDateTime.parse("2026-01-01T10:00:00Z");
    private static final OffsetDateTime ACTUALIZADO_EN = OffsetDateTime.parse("2026-01-02T10:00:00Z");

    private NodoRepositoryMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new NodoRepositoryMapper();
    }

    @Test
    @DisplayName("Mapea los campos de la entidad y toma rama, habilidad y prerrequisitos de los parámetros")
    void toDomainShouldMapEntityFieldsAndTakeReferencesFromParameters() {
        Nodo result = mapper.toDomain(nodoEntity(), List.of(PRERREQUISITO_ID), RAMA_ID, HABILIDAD_ID);

        thenNodoHasEntityFieldsAndGivenReferences(result);
    }

    @Test
    @DisplayName("Deja rama y habilidad en null cuando no se pasan referencias visibles")
    void toDomainShouldLeaveRamaAndHabilidadNullWhenNoVisibleReferences() {
        Nodo result = mapper.toDomain(nodoEntity(), List.of(), null, null);

        assertThat(result.getRamaId()).isNull();
        assertThat(result.getHabilidadId()).isNull();
    }

    @Test
    @DisplayName("Conserva palabras clave y prerrequisitos vacíos")
    void toDomainShouldKeepEmptyPalabrasClaveAndPrerrequisitos() {
        NodoEntity entity = nodoEntity();
        entity.setPalabrasClave(new String[0]);

        Nodo result = mapper.toDomain(entity, List.of(), null, null);

        assertThat(result.getPalabrasClave()).isEmpty();
        assertThat(result.getPrerrequisitoIds()).isEmpty();
    }

    // --- helpers ---
    private NodoEntity nodoEntity() {
        return NodoEntity.builder()
                .id(NODO_ID)
                .rutaId(RUTA_ID)
                .hitoId(HITO_ID)
                .codigo(CODIGO)
                .titulo(TITULO)
                .resumen(RESUMEN)
                .tipo(TipoNodo.OPCIONAL)
                .nivel(NIVEL)
                .minutosEstimados(MINUTOS_ESTIMADOS)
                .palabrasClave(PALABRAS_CLAVE)
                .posicion(POSICION)
                .creadoEn(CREADO_EN)
                .actualizadoEn(ACTUALIZADO_EN)
                .build();
    }

    // --- assert ---
    private void thenNodoHasEntityFieldsAndGivenReferences(Nodo nodo) {
        assertThat(nodo.getId()).isEqualTo(NODO_ID);
        assertThat(nodo.getRutaId()).isEqualTo(RUTA_ID);
        assertThat(nodo.getHitoId()).isEqualTo(HITO_ID);
        assertThat(nodo.getRamaId()).isEqualTo(RAMA_ID);
        assertThat(nodo.getHabilidadId()).isEqualTo(HABILIDAD_ID);
        assertThat(nodo.getCodigo()).isEqualTo(CODIGO);
        assertThat(nodo.getTitulo()).isEqualTo(TITULO);
        assertThat(nodo.getResumen()).isEqualTo(RESUMEN);
        assertThat(nodo.getTipo()).isEqualTo(TipoNodo.OPCIONAL);
        assertThat(nodo.getNivel()).isEqualTo(NIVEL);
        assertThat(nodo.getMinutosEstimados()).isEqualTo(MINUTOS_ESTIMADOS);
        assertThat(nodo.getPalabrasClave()).containsExactly(PALABRAS_CLAVE);
        assertThat(nodo.getPosicion()).isEqualTo(POSICION);
        assertThat(nodo.getPrerrequisitoIds()).containsExactly(PRERREQUISITO_ID);
        assertThat(nodo.getCreadoEn()).isEqualTo(CREADO_EN);
        assertThat(nodo.getActualizadoEn()).isEqualTo(ACTUALIZADO_EN);
    }
}
