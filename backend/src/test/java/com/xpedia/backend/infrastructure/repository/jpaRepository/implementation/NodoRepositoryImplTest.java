package com.xpedia.backend.infrastructure.repository.jpaRepository.implementation;

import com.xpedia.backend.infrastructure.repository.entity.*;
import com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces.*;
import com.xpedia.backend.infrastructure.repository.mapper.NodoRepositoryMapper;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static com.xpedia.backend.support.ContractData.full;

class NodoRepositoryImplTest {
    private final INodoJpaRepository jpa = mock(INodoJpaRepository.class);
    private final INodoPrerrequisitoJpaRepository edges = mock(INodoPrerrequisitoJpaRepository.class);
    private final NodoRepositoryImpl repository = new NodoRepositoryImpl(jpa, edges, new NodoRepositoryMapper());

    @Test void resultadoVacioEvitaConsultasAuxiliares() {
        var ruta = UUID.randomUUID(); when(jpa.findByRutaId(ruta, null)).thenReturn(List.of());
        assertThat(repository.findByRutaId(ruta, null)).isEmpty();
        verifyNoInteractions(edges); verify(jpa, never()).findReferenciasVisibles(any(), any());
    }
    @Test void agrupaPrerrequisitosYReferenciasConDosConsultasEnLote() {
        var ruta = UUID.randomUUID(); var first = full(NodoEntity.class); var second = full(NodoEntity.class);
        second.setId(UUID.randomUUID()); var ids = List.of(first.getId(), second.getId());
        when(jpa.findByRutaId(ruta, null)).thenReturn(List.of(first, second));
        var edge = new NodoPrerrequisitoEntity(); edge.setNodoId(first.getId()); edge.setPrerrequisitoId(second.getId());
        when(edges.findDeNodosEnRuta(ids, ruta)).thenReturn(List.of(edge));
        var ref = mock(INodoJpaRepository.ReferenciasVisibles.class);
        when(ref.getNodoId()).thenReturn(first.getId()); when(ref.getRamaId()).thenReturn(first.getRamaId());
        when(ref.getHabilidadId()).thenReturn(first.getHabilidadId());
        when(jpa.findReferenciasVisibles(ruta, ids)).thenReturn(List.of(ref));
        var result = repository.findByRutaId(ruta, null);
        assertThat(result).hasSize(2); assertThat(result.get(0).getId()).isEqualTo(first.getId());
        assertThat(result.get(0).getPrerrequisitoIds()).containsExactly(second.getId());
        assertThat(result.get(0).getRamaId()).isEqualTo(first.getRamaId());
        assertThat(result.get(1).getPrerrequisitoIds()).isEmpty();
        assertThat(result.get(1).getRamaId()).isNull(); assertThat(result.get(1).getHabilidadId()).isNull();
        verify(edges, times(1)).findDeNodosEnRuta(ids, ruta); verify(jpa, times(1)).findReferenciasVisibles(ruta, ids);
    }
}
