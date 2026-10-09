package com.xpedia.backend.infrastructure.repository.mapper;

import com.xpedia.backend.domain.model.usuario.Usuario;
import com.xpedia.backend.infrastructure.repository.entity.UsuarioEntity;
import org.springframework.stereotype.Component;

@Component
public class UsuarioRepositoryMapper {

    public Usuario toDomain(UsuarioEntity entity) {
        return Usuario.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .email(entity.getEmail())
                .hashContrasenia(entity.getHashContrasenia())
                .tipo(entity.getTipo())
                .estado(entity.getEstado())
                .creadoEn(entity.getCreadoEn())
                .actualizadoEn(entity.getActualizadoEn())
                .build();
    }

    public UsuarioEntity toEntity(Usuario usuario) {
        return UsuarioEntity.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .hashContrasenia(usuario.getHashContrasenia())
                .tipo(usuario.getTipo())
                .estado(usuario.getEstado())
                .creadoEn(usuario.getCreadoEn())
                .actualizadoEn(usuario.getActualizadoEn())
                .build();
    }
}
