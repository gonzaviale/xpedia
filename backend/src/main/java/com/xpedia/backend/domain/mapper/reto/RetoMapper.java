package com.xpedia.backend.domain.mapper.reto;

import com.xpedia.backend.domain.dto.reto.*;
import com.xpedia.backend.domain.model.reto.Reto;
import java.util.List;

public class RetoMapper {
    public ListarRetosResponse toListResponse(List<Reto> retos) {
        return new ListarRetosResponse(retos.stream().map(this::toItem).toList());
    }
    public ObtenerRetoResponse toObtenerResponse(Reto reto) {
        return new ObtenerRetoResponse(toItem(reto));
    }
    private RetoItem toItem(Reto reto) {
        return new RetoItem(reto.getId(), reto.getRutaId(), reto.getNodoId(), reto.getHitoId(), reto.getTipo(),
                reto.getTitulo(), reto.getNivel(), reto.getContenido(), reto.getOrigen(), reto.getRevisadoEn(), reto.getRubrica());
    }
}
