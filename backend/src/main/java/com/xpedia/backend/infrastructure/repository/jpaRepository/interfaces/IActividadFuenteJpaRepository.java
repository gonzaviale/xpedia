package com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces;

import com.xpedia.backend.infrastructure.repository.entity.ActividadEntity;
import org.springframework.data.repository.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface IActividadFuenteJpaRepository extends Repository<ActividadEntity, UUID> {
    interface FuenteVisible {
        UUID getActividadId();
        UUID getId();
        String getTitulo();
        String getUrl();
        String getLicencia();
        String getUso();
        boolean getPermiteUsoComercial();
        String getUbicacion();
    }

    @Query(value = """
            SELECT af.actividad_id AS actividadId, f.id, f.titulo, f.url, f.licencia, f.uso,
                   f.permite_uso_comercial AS permiteUsoComercial, af.ubicacion
            FROM actividad_fuente af JOIN fuente f ON f.id = af.fuente_id
            WHERE af.actividad_id IN (:ids) AND f.organizacion_id IS NULL
            ORDER BY f.titulo, f.id
            """, nativeQuery = true)
    List<FuenteVisible> findVisiblesDeActividades(@Param("ids") List<UUID> ids);
}
