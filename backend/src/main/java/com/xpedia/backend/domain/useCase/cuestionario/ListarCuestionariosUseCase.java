package com.xpedia.backend.domain.useCase.cuestionario;

import com.xpedia.backend.domain.dto.cuestionario.ListarCuestionariosRequest;
import com.xpedia.backend.domain.dto.cuestionario.ListarCuestionariosResponse;
import com.xpedia.backend.domain.mapper.cuestionario.ListarCuestionariosMapper;
import com.xpedia.backend.domain.model.cuestionario.Cuestionario;
import com.xpedia.backend.domain.service.cuestionario.CuestionarioService;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ListarCuestionariosUseCase {

    private final CuestionarioService cuestionarioService;
    private final ListarCuestionariosMapper mapper;

    public ListarCuestionariosResponse execute(ListarCuestionariosRequest request) {
        List<Cuestionario> cuestionarios = cuestionarioService.listar(request.rutaId(), request.nodoId());
        return mapper.toResponse(cuestionarios);
    }
}
