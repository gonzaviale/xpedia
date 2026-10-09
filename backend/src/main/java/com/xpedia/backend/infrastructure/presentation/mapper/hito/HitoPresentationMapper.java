package com.xpedia.backend.infrastructure.presentation.mapper.hito;

import com.xpedia.backend.domain.dto.hito.HitoItem;
import com.xpedia.backend.domain.dto.hito.ListarHitosRequest;
import com.xpedia.backend.domain.dto.hito.ListarHitosResponse;
import com.xpedia.backend.infrastructure.presentation.dto.hito.HitoResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class HitoPresentationMapper {

    public ListarHitosRequest toRequest(UUID rutaId) {
        return new ListarHitosRequest(rutaId);
    }

    public List<HitoResponse> toResponse(ListarHitosResponse response) {
        return response.content().stream().map(this::toResponse).toList();
    }

    private HitoResponse toResponse(HitoItem hito) {
        return new HitoResponse(
                hito.id(),
                hito.rutaId(),
                hito.posicion(),
                hito.titulo(),
                hito.objetivo(),
                hito.horasEstimadas(),
                hito.esFinal(),
                hito.evidenciaEsperada());
    }
}
