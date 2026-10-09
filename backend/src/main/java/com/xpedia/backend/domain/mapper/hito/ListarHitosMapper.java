package com.xpedia.backend.domain.mapper.hito;

import com.xpedia.backend.domain.dto.hito.HitoItem;
import com.xpedia.backend.domain.dto.hito.ListarHitosResponse;
import com.xpedia.backend.domain.model.hito.Hito;

import java.util.List;

public class ListarHitosMapper {

    public ListarHitosResponse toResponse(List<Hito> hitos) {
        return new ListarHitosResponse(hitos.stream().map(this::toItem).toList());
    }

    private HitoItem toItem(Hito hito) {
        return new HitoItem(
                hito.getId(),
                hito.getRutaId(),
                hito.getPosicion(),
                hito.getTitulo(),
                hito.getObjetivo(),
                hito.getHorasEstimadas(),
                hito.getEsFinal(),
                hito.getEvidenciaEsperada());
    }
}
