package com.xpedia.backend.domain.mapper.hito;

import com.xpedia.backend.domain.dto.hito.HitoItem;
import com.xpedia.backend.domain.dto.hito.ListarHitosResponse;
import com.xpedia.backend.domain.model.hito.Hito;
import java.util.List;

public class ListarHitosMapper {
    public ListarHitosResponse toResponse(List<Hito> items) {
        return new ListarHitosResponse(items.stream().map(this::toItem).toList());
    }

    private HitoItem toItem(Hito item) {
        return new HitoItem(
                item.getId(),
                item.getRutaId(),
                item.getPosicion(),
                item.getTitulo(),
                item.getObjetivo(),
                item.getHorasEstimadas(),
                item.getEsFinal(),
                item.getEvidenciaEsperada());
    }
}
