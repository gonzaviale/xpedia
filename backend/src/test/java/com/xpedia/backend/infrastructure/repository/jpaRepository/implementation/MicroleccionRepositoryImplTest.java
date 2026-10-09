package com.xpedia.backend.infrastructure.repository.jpaRepository.implementation;

import com.xpedia.backend.domain.model.microleccion.FuenteMicroleccion;
import com.xpedia.backend.domain.model.microleccion.Microleccion;
import com.xpedia.backend.infrastructure.repository.entity.ActividadEntity;
import com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces.IActividadFuenteJpaRepository;
import com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces.IActividadFuenteJpaRepository.FuenteVisible;
import com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces.IActividadJpaRepository;
import com.xpedia.backend.infrastructure.repository.mapper.MicroleccionRepositoryMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MicroleccionRepositoryImplTest {

    private static final UUID RUTA_ID = UUID.randomUUID();
    private static final UUID NODO_ID = UUID.randomUUID();
    private static final UUID PRIMERA_ID = UUID.randomUUID();
    private static final UUID SEGUNDA_ID = UUID.randomUUID();
    private static final UUID FUENTE_ID = UUID.randomUUID();
    private static final String FUENTE_TITULO = "Referencia";

    @Mock
    private IActividadJpaRepository actividades;

    @Mock
    private IActividadFuenteJpaRepository fuentes;

    private MicroleccionRepositoryImpl microleccionRepository;

    @BeforeEach
    void setUp() {
        microleccionRepository = new MicroleccionRepositoryImpl(actividades, fuentes, new MicroleccionRepositoryMapper());
    }

    @Test
    @DisplayName("Devuelve lista vacía cuando no hay actividades aprobadas")
    void findAprobadasByNodoShouldReturnEmptyListWhenNoActividades() {
        givenNoActividades();

        List<Microleccion> result = findAprobadas();

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("No consulta las fuentes cuando no hay actividades aprobadas")
    void findAprobadasByNodoShouldNotQueryFuentesWhenNoActividades() {
        givenNoActividades();

        findAprobadas();

        verifyNoInteractions(fuentes);
    }

    @Test
    @DisplayName("Mantiene el orden de las actividades devueltas por la consulta")
    void findAprobadasByNodoShouldKeepActividadesOrder() {
        givenTwoActividadesWithFuenteOnFirst();

        List<Microleccion> result = findAprobadas();

        assertThat(result).extracting(Microleccion::getId).containsExactly(PRIMERA_ID, SEGUNDA_ID);
    }

    @Test
    @DisplayName("Asigna cada fuente a su actividad y deja sin fuentes a las demás")
    void findAprobadasByNodoShouldAssignFuentesToTheirActividad() {
        givenTwoActividadesWithFuenteOnFirst();

        List<Microleccion> result = findAprobadas();

        assertThat(result.get(0).getFuentes()).extracting(FuenteMicroleccion::titulo).containsExactly(FUENTE_TITULO);
        assertThat(result.get(0).getFuentes()).extracting(FuenteMicroleccion::id).containsExactly(FUENTE_ID);
        assertThat(result.get(1).getFuentes()).isEmpty();
    }

    @Test
    @DisplayName("Consulta las fuentes de todas las actividades en una sola consulta")
    void findAprobadasByNodoShouldQueryFuentesOnceForAllActividades() {
        givenTwoActividadesWithFuenteOnFirst();

        findAprobadas();

        verify(fuentes, times(1)).findVisiblesDeActividades(List.of(PRIMERA_ID, SEGUNDA_ID));
    }

    // --- arrange ---
    private void givenNoActividades() {
        when(actividades.findMicroleccionesAprobadas(RUTA_ID, NODO_ID)).thenReturn(List.of());
    }

    private void givenTwoActividadesWithFuenteOnFirst() {
        FuenteVisible fuenteDeLaPrimera = fuenteVisible(PRIMERA_ID);
        when(actividades.findMicroleccionesAprobadas(RUTA_ID, NODO_ID))
                .thenReturn(List.of(actividad(PRIMERA_ID), actividad(SEGUNDA_ID)));
        when(fuentes.findVisiblesDeActividades(List.of(PRIMERA_ID, SEGUNDA_ID)))
                .thenReturn(List.of(fuenteDeLaPrimera));
    }

    // --- act ---
    private List<Microleccion> findAprobadas() {
        return microleccionRepository.findAprobadasByNodo(RUTA_ID, NODO_ID);
    }

    // --- helpers ---
    private ActividadEntity actividad(UUID id) {
        return ActividadEntity.builder()
                .id(id)
                .rutaId(RUTA_ID)
                .nodoId(NODO_ID)
                .build();
    }

    private FuenteVisible fuenteVisible(UUID actividadId) {
        FuenteVisible fuente = mock(FuenteVisible.class);
        when(fuente.getActividadId()).thenReturn(actividadId);
        when(fuente.getId()).thenReturn(FUENTE_ID);
        when(fuente.getTitulo()).thenReturn(FUENTE_TITULO);
        return fuente;
    }
}
