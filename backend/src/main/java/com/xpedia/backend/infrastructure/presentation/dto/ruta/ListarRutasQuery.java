package com.xpedia.backend.infrastructure.presentation.dto.ruta;

import com.xpedia.backend.domain.model.enums.TipoRuta;
import com.xpedia.backend.domain.model.enums.ObjetivoRuta;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

public record ListarRutasQuery(
        TipoRuta tipo,
        ObjetivoRuta objetivo,
        @Min(value = 0, message = "La página debe ser mayor o igual a 0")
        @Schema(defaultValue = "0", minimum = "0") Integer page,
        @Min(value = 1, message = "El tamaño debe ser mayor o igual a 1")
        @Max(value = 100, message = "El tamaño no puede superar 100")
        @Schema(defaultValue = "20", minimum = "1", maximum = "100") Integer size
) {
}
