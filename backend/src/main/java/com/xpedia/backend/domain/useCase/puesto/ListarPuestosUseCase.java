package com.xpedia.backend.domain.useCase.puesto;

import com.xpedia.backend.domain.dto.puesto.ListarPuestosRequest;
import com.xpedia.backend.domain.dto.puesto.ListarPuestosResponse;
import com.xpedia.backend.domain.mapper.puesto.ListarPuestosMapper;
import com.xpedia.backend.domain.model.puesto.Puesto;
import com.xpedia.backend.domain.service.puesto.PuestoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;

@RequiredArgsConstructor
public class ListarPuestosUseCase {

    private final PuestoService puestoService;
    private final ListarPuestosMapper mapper;

    public ListarPuestosResponse execute(ListarPuestosRequest request) {
        Page<Puesto> resultado = puestoService.listar(request.organizacionId(), mapper.toPageable(request));
        return mapper.toResponse(resultado);
    }
}
