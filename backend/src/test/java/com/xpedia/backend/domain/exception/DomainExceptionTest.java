package com.xpedia.backend.domain.exception;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class DomainExceptionTest {
    @Test void conservaMensajeSinCausa() {
        var exception = new DomainException("Regla incumplida") {};
        assertThat(exception).hasMessage("Regla incumplida").hasNoCause();
    }
    @Test void conservaCausaParaDiagnosticoSinPerderMensajeDeNegocio() {
        var cause = new IllegalStateException("Detalle técnico");
        var exception = new DomainException("Operación no disponible", cause) {};
        assertThat(exception).hasMessage("Operación no disponible").hasCause(cause);
    }
}
