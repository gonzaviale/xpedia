package com.xpedia.backend.domain.model.inscripcion;

import java.util.List;

public record RecorridoPersonal(Inscripcion inscripcion, String rutaSlug, List<ProgresoNodo> progreso) {
}

