package com.xpedia.backend.infrastructure.presentation.dto.inscripcion;

import com.xpedia.backend.domain.model.enums.ObjetivoRuta;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import tools.jackson.databind.annotation.JsonDeserialize;

import java.util.UUID;

public record CrearInscripcionWebRequest(
        @NotNull UUID rutaId,
        @NotNull ObjetivoRuta objetivo,
        @Size(max = 2000) String metaPersonal,
        @NotNull @Min(1) @Max(1440)
        @JsonDeserialize(using = RitmoMinDeserializer.class) Short ritmoMin) {
}
