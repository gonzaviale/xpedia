package com.xpedia.backend.domain.model.inscripcion;

import com.xpedia.backend.domain.exception.BusinessRuleException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InscripcionTest {

    private static final LocalDate INICIO = LocalDate.of(2026, 10, 9);

    @Test
    @DisplayName("Redondea hacia arriba los días y contempla cambios de mes")
    void estimarLlegadaShouldRoundUpPracticeDays() {
        LocalDate llegada = estimar(new BigDecimal("7.1"), 20);

        thenEstimarLlegadaShouldRoundUpPracticeDays(llegada);
    }

    @Test
    @DisplayName("Sin duración publicada no inventa una fecha de llegada")
    void estimarLlegadaShouldReturnNullWhenHoursUnknown() {
        LocalDate llegada = estimar(null, 20);

        thenEstimarLlegadaShouldReturnNullWhenHoursUnknown(llegada);
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1"})
    @DisplayName("Rechaza una duración cero o negativa")
    void estimarLlegadaShouldRejectNonPositiveHours(String horas) {
        thenEstimarLlegadaShouldRejectNonPositiveHours(horas);
    }

    @Test
    @DisplayName("A mayor ritmo diario corresponde una llegada anterior")
    void estimarLlegadaShouldShortenEstimateWhenRhythmIncreases() {
        LocalDate lenta = estimar(new BigDecimal("10"), 10);
        LocalDate rapida = estimar(new BigDecimal("10"), 60);

        thenEstimarLlegadaShouldShortenEstimateWhenRhythmIncreases(lenta, rapida);
    }

    @Test
    @DisplayName("Recorta la meta personal sin cambiar su contenido")
    void normalizarMetaShouldTrimText() {
        String meta = Inscripcion.normalizarMeta("  Conseguir trabajo remoto  ");

        thenNormalizarMetaShouldTrimText(meta);
    }

    @Test
    @DisplayName("Conserva la ausencia de meta opcional")
    void normalizarMetaShouldKeepNull() {
        String meta = Inscripcion.normalizarMeta(null);

        thenNormalizarMetaShouldKeepNull(meta);
    }

    // --- act ---
    private LocalDate estimar(BigDecimal horas, int ritmo) {
        return Inscripcion.estimarLlegada(horas, ritmo, INICIO);
    }

    // --- assert ---
    private void thenEstimarLlegadaShouldRoundUpPracticeDays(LocalDate llegada) {
        assertThat(llegada).isEqualTo(LocalDate.of(2026, 10, 31));
    }

    private void thenEstimarLlegadaShouldReturnNullWhenHoursUnknown(LocalDate llegada) {
        assertThat(llegada).isNull();
    }

    private void thenEstimarLlegadaShouldRejectNonPositiveHours(String horas) {
        assertThatThrownBy(() -> estimar(new BigDecimal(horas), 20))
                .isInstanceOf(BusinessRuleException.class);
    }

    private void thenEstimarLlegadaShouldShortenEstimateWhenRhythmIncreases(LocalDate lenta, LocalDate rapida) {
        assertThat(rapida).isBefore(lenta).isEqualTo(INICIO.plusDays(10));
    }

    private void thenNormalizarMetaShouldTrimText(String meta) {
        assertThat(meta).isEqualTo("Conseguir trabajo remoto");
    }

    private void thenNormalizarMetaShouldKeepNull(String meta) {
        assertThat(meta).isNull();
    }
}

