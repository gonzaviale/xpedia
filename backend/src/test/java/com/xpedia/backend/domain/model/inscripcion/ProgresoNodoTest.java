package com.xpedia.backend.domain.model.inscripcion;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static com.xpedia.backend.support.InscripcionTestData.FECHA;
import static com.xpedia.backend.support.InscripcionTestData.INSCRIPCION_ID;
import static com.xpedia.backend.support.InscripcionTestData.NODO_ID;
import static org.assertj.core.api.Assertions.assertThat;

class ProgresoNodoTest {

    @ParameterizedTest
    @CsvSource({"false,DISPONIBLE", "true,BLOQUEADO"})
    @DisplayName("Inicializa disponibilidad por prerrequisitos sin atribuir conocimientos previos")
    void inicialShouldSetAvailabilityWithoutInventingMastery(boolean prerrequisitos, String estado) {
        ProgresoNodo progreso = ProgresoNodo.inicial(INSCRIPCION_ID, NODO_ID, prerrequisitos, FECHA);

        thenInitialProgress(progreso, estado);
    }

    // --- assert ---
    private void thenInitialProgress(ProgresoNodo progreso, String estado) {
        assertThat(progreso.getInscripcionId()).isEqualTo(INSCRIPCION_ID);
        assertThat(progreso.getNodoId()).isEqualTo(NODO_ID);
        assertThat(progreso.getEstado()).isEqualTo(estado);
        assertThat(progreso.getDominio()).isZero();
        assertThat(progreso.getNivel()).isZero();
        assertThat(progreso.getCantidadFallos()).isZero();
        assertThat(progreso.getCreadoEn()).isEqualTo(FECHA);
        assertThat(progreso.getActualizadoEn()).isEqualTo(FECHA);
    }
}

