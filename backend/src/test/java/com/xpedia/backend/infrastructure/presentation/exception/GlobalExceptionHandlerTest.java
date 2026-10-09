package com.xpedia.backend.infrastructure.presentation.exception;

import com.xpedia.backend.domain.exception.*;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private final MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/rutas");

    @Test void erroresDeDominioTienenEstadoMensajeRutaYTraza() {
        var absent = handler.handleNotFound(new ResourceNotFoundException("ruta", "id", "x"), request);
        var duplicate = handler.handleDuplicate(new DuplicateResourceException("ruta", "slug", "x"), request);
        var business = handler.handleBusinessRule(new BusinessRuleException("Regla incumplida"), request);
        assertThat(absent.getStatusCode().value()).isEqualTo(404);
        assertThat(duplicate.getStatusCode().value()).isEqualTo(409);
        assertThat(business.getStatusCode().value()).isEqualTo(400);
        assertThat(absent.getBody().getPath()).isEqualTo("/api/rutas");
        assertThat(absent.getBody().getTimestamp()).isNotNull();
        assertThat(UUID.fromString(absent.getBody().getTraceId())).isNotNull();
        assertThat(business.getBody().getMessage()).isEqualTo("Regla incumplida");
    }
    @Test void validacionDeCampoYDeObjetoNoProduceErrorInterno() {
        var binding = new BeanPropertyBindingResult(new Object(), "query");
        binding.addError(new FieldError("query", "size", "Debe ser positivo"));
        binding.addError(new ObjectError("query", "Combinación inválida"));
        var result = handler.handleValidationExceptions(new MethodArgumentNotValidException(null, binding), request);
        assertThat(result.getStatusCode().value()).isEqualTo(400);
        assertThat(result.getBody().getErrors()).containsEntry("size", "Debe ser positivo").containsEntry("query", "Combinación inválida");
    }
    @Test void tipoInvalidoIdentificaParametroSinExponerCausa() {
        var error = new MethodArgumentTypeMismatchException("texto", UUID.class, "rutaId", null, new IllegalArgumentException("secreto"));
        var result = handler.handleTypeMismatch(error, request);
        assertThat(result.getStatusCode().value()).isEqualTo(400);
        assertThat(result.getBody().getErrors()).containsKey("rutaId");
        assertThat(result.getBody().getMessage()).doesNotContain("secreto");
    }
    @Test void erroresTecnicosNoExponenDetallesDeBaseNiSecretos() {
        var integrity = handler.handleDataIntegrity(new DataIntegrityViolationException("password-secreto"), request);
        var generic = handler.handleGeneric(new IllegalStateException("password-secreto"), request);
        assertThat(integrity.getStatusCode().value()).isEqualTo(409);
        assertThat(generic.getStatusCode().value()).isEqualTo(500);
        assertThat(integrity.getBody().getMessage()).doesNotContain("password-secreto");
        assertThat(generic.getBody().getMessage()).doesNotContain("password-secreto");
    }
}
