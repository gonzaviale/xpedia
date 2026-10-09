package com.xpedia.backend.infrastructure.presentation.dto.inscripcion;

import com.xpedia.backend.domain.model.enums.ObjetivoRuta;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static com.xpedia.backend.support.InscripcionTestData.RUTA_ID;
import static org.assertj.core.api.Assertions.assertThat;

class CrearInscripcionWebRequestTest {

    @ParameterizedTest
    @ValueSource(shorts = {0, -1, 1441})
    @NullSource
    @DisplayName("Rechaza ritmo ausente o fuera de los minutos de un día")
    void validationShouldRejectInvalidRhythm(Short ritmo) {
        var request = new CrearInscripcionWebRequest(RUTA_ID, ObjetivoRuta.CAMBIAR, null, ritmo);

        thenValidationShouldRejectInvalidRhythm(request);
    }

    @ParameterizedTest
    @ValueSource(shorts = {1, 10, 20, 60, 120, 1440})
    @DisplayName("Acepta ritmos positivos dentro del día y una meta opcional")
    void validationShouldAcceptValidRhythm(short ritmo) {
        var request = new CrearInscripcionWebRequest(RUTA_ID, ObjetivoRuta.CAMBIAR, null, ritmo);

        thenValidationShouldAcceptValidRhythm(request);
    }

    @Test
    @DisplayName("Exige ruta y objetivo")
    void validationShouldRequireRouteAndObjective() {
        var request = new CrearInscripcionWebRequest(null, null, null, (short) 20);

        thenValidationShouldRequireRouteAndObjective(request);
    }

    @Test
    @DisplayName("Limita la meta personal a 2000 caracteres")
    void validationShouldLimitPersonalGoal() {
        var request = new CrearInscripcionWebRequest(RUTA_ID, ObjetivoRuta.CAMBIAR, "a".repeat(2001), (short) 20);

        thenValidationShouldLimitPersonalGoal(request);
    }

    // --- act ---
    private Validator validator() {
        return Validation.buildDefaultValidatorFactory().getValidator();
    }

    // --- assert ---
    private void thenValidationShouldRejectInvalidRhythm(CrearInscripcionWebRequest request) {
        assertThat(validator().validate(request)).extracting(v -> v.getPropertyPath().toString())
                .contains("ritmoMin");
    }

    private void thenValidationShouldAcceptValidRhythm(CrearInscripcionWebRequest request) {
        assertThat(validator().validate(request)).isEmpty();
    }

    private void thenValidationShouldRequireRouteAndObjective(CrearInscripcionWebRequest request) {
        assertThat(validator().validate(request)).extracting(v -> v.getPropertyPath().toString())
                .containsExactlyInAnyOrder("rutaId", "objetivo");
    }

    private void thenValidationShouldLimitPersonalGoal(CrearInscripcionWebRequest request) {
        assertThat(validator().validate(request)).extracting(v -> v.getPropertyPath().toString())
                .containsExactly("metaPersonal");
    }
}

