package com.xpedia.backend.infrastructure.presentation.mapper.puesto;

import com.xpedia.backend.domain.dto.puesto.*;
import com.xpedia.backend.infrastructure.presentation.dto.puesto.PuestoPageResponse;
import com.xpedia.backend.infrastructure.presentation.dto.puesto.PuestoRequest;
import com.xpedia.backend.infrastructure.presentation.dto.puesto.PuestoResponse;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PuestoPresentationMapper {

    public CrearPuestoRequest toCrearRequest(PuestoRequest request) {
        return new CrearPuestoRequest(request.organizacionId(), request.nombre());
    }

    public ActualizarPuestoRequest toActualizarRequest(UUID id, PuestoRequest request) {
        return new ActualizarPuestoRequest(id, request.nombre());
    }

    public EliminarPuestoRequest toEliminarRequest(UUID id) {
        return new EliminarPuestoRequest(id);
    }

    public ObtenerPuestoRequest toObtenerRequest(UUID id) {
        return new ObtenerPuestoRequest(id);
    }

    public ListarPuestosRequest toListarRequest(UUID organizacionId, int page, int size) {
        return new ListarPuestosRequest(organizacionId, page, size);
    }

    public PuestoResponse toResponse(CrearPuestoResponse response) {
        return new PuestoResponse(response.id(), response.organizacionId(), response.nombre(),
                response.creadoEn(), response.actualizadoEn());
    }

    public PuestoResponse toResponse(ActualizarPuestoResponse response) {
        return new PuestoResponse(response.id(), response.organizacionId(), response.nombre(),
                response.creadoEn(), response.actualizadoEn());
    }

    public PuestoResponse toResponse(ObtenerPuestoResponse response) {
        return new PuestoResponse(response.id(), response.organizacionId(), response.nombre(),
                response.creadoEn(), response.actualizadoEn());
    }

    public PuestoPageResponse toPageResponse(ListarPuestosResponse response) {
        return new PuestoPageResponse(
                response.content().stream().map(this::toResponse).toList(),
                response.pageNumber(),
                response.pageSize(),
                response.totalElements(),
                response.totalPages(),
                response.first(),
                response.last());
    }

    private PuestoResponse toResponse(PuestoItem item) {
        return new PuestoResponse(item.id(), item.organizacionId(), item.nombre(),
                item.creadoEn(), item.actualizadoEn());
    }
}
