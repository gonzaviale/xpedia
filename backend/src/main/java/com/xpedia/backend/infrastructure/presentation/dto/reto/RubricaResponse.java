package com.xpedia.backend.infrastructure.presentation.dto.reto;
import java.math.BigDecimal;
import java.util.*;
public record RubricaResponse(UUID id, String nombre, String descripcion, BigDecimal puntajeAprobacion,
                              BigDecimal puntajeMaximo, List<CriterioRubricaResponse> criterios) {}
