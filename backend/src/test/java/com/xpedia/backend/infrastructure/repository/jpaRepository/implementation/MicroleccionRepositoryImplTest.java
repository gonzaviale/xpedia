package com.xpedia.backend.infrastructure.repository.jpaRepository.implementation;

import com.xpedia.backend.infrastructure.repository.entity.ActividadEntity;
import com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces.*;
import com.xpedia.backend.infrastructure.repository.mapper.MicroleccionRepositoryMapper;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static com.xpedia.backend.support.ContractData.full;

class MicroleccionRepositoryImplTest {
    private final IActividadJpaRepository actividades = mock(IActividadJpaRepository.class);
    private final IActividadFuenteJpaRepository fuentes = mock(IActividadFuenteJpaRepository.class);
    private final MicroleccionRepositoryImpl repository = new MicroleccionRepositoryImpl(actividades, fuentes, new MicroleccionRepositoryMapper());

    @Test void sinActividadesNoConsultaFuentes() {
        var ruta = UUID.randomUUID(); var nodo = UUID.randomUUID();
        when(actividades.findMicroleccionesAprobadas(ruta, nodo)).thenReturn(List.of());
        assertThat(repository.findAprobadasByNodo(ruta, nodo)).isEmpty(); verifyNoInteractions(fuentes);
    }
    @Test void agrupaFuentesEnUnaConsultaYMantieneOrdenDeActividades() {
        var ruta = UUID.randomUUID(); var nodo = UUID.randomUUID(); var a = full(ActividadEntity.class); var b = full(ActividadEntity.class);
        b.setId(UUID.randomUUID()); var ids = List.of(a.getId(), b.getId());
        when(actividades.findMicroleccionesAprobadas(ruta, nodo)).thenReturn(List.of(a, b));
        var fuente = mock(IActividadFuenteJpaRepository.FuenteVisible.class);
        when(fuente.getActividadId()).thenReturn(a.getId()); when(fuente.getTitulo()).thenReturn("Referencia");
        when(fuentes.findVisiblesDeActividades(ids)).thenReturn(List.of(fuente));
        var result = repository.findAprobadasByNodo(ruta, nodo);
        assertThat(result).extracting("id").containsExactly(a.getId(), b.getId());
        assertThat(result.getFirst().getFuentes()).extracting("titulo").containsExactly("Referencia");
        assertThat(result.get(1).getFuentes()).isEmpty();
        verify(fuentes, times(1)).findVisiblesDeActividades(ids);
    }
}
