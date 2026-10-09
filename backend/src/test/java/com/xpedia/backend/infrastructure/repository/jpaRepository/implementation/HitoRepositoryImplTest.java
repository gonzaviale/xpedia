package com.xpedia.backend.infrastructure.repository.jpaRepository.implementation;

import com.xpedia.backend.infrastructure.repository.entity.HitoEntity;
import com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces.IHitoJpaRepository;
import com.xpedia.backend.infrastructure.repository.mapper.HitoRepositoryMapper;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static com.xpedia.backend.support.ContractData.full;

class HitoRepositoryImplTest {
    private final IHitoJpaRepository jpa = mock(IHitoJpaRepository.class);
    private final HitoRepositoryImpl repository = new HitoRepositoryImpl(jpa, new HitoRepositoryMapper());

    @Test void conservaOrdenYCamposDeLaConsulta() {
        var ruta = UUID.randomUUID(); var first = full(HitoEntity.class); var second = new HitoEntity();
        when(jpa.findByRutaIdOrderByPosicionAscIdAsc(ruta)).thenReturn(List.of(first, second));
        var result = repository.findByRutaId(ruta);
        assertThat(result).hasSize(2);
        assertThat(result.get(0)).usingRecursiveComparison().isEqualTo(first);
        assertThat(result.get(1)).usingRecursiveComparison().isEqualTo(second);
    }
    @Test void propagaExistenciaSinCambiarOrdenDeIds() {
        var ruta = UUID.randomUUID(); var id = UUID.randomUUID();
        when(jpa.existsByIdAndRutaId(id, ruta)).thenReturn(true, false);
        assertThat(repository.existsByIdAndRutaId(id, ruta)).isTrue();
        assertThat(repository.existsByIdAndRutaId(id, ruta)).isFalse();
    }
    @Test void coleccionVaciaNoInventaHitos() {
        var ruta = UUID.randomUUID(); when(jpa.findByRutaIdOrderByPosicionAscIdAsc(ruta)).thenReturn(List.of());
        assertThat(repository.findByRutaId(ruta)).isEmpty();
    }
}
