package com.xpedia.backend.infrastructure.repository.jpaRepository.implementation;

import com.xpedia.backend.infrastructure.repository.entity.RutaEntity;
import com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces.IRutaJpaRepository;
import com.xpedia.backend.infrastructure.repository.mapper.RutaRepositoryMapper;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;
import java.util.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static com.xpedia.backend.support.ContractData.full;

class RutaRepositoryImplTest {
    private final IRutaJpaRepository jpa = mock(IRutaJpaRepository.class);
    private final RutaRepositoryImpl repository = new RutaRepositoryImpl(jpa, new RutaRepositoryMapper());

    @Test void detallePresenteYAusenteConservanOptional() {
        var entity = full(RutaEntity.class);
        when(jpa.findPublicadaGlobalById(entity.getId())).thenReturn(Optional.of(entity));
        assertThat(repository.findPublicadaGlobalById(entity.getId()).orElseThrow())
                .usingRecursiveComparison().isEqualTo(entity);
        assertThat(repository.findPublicadaGlobalById(UUID.randomUUID())).isEmpty();
    }
    @Test void paginaConservaContenidoYMetadatos() {
        var page = PageRequest.of(1, 2); var entity = full(RutaEntity.class);
        when(jpa.findPublicadasGlobales(null, null, page)).thenReturn(new PageImpl<>(List.of(entity), page, 5));
        var result = repository.findPublicadasGlobales(null, null, page);
        assertThat(result.getTotalElements()).isEqualTo(5);
        assertThat(result.getNumber()).isEqualTo(1);
        assertThat(result.getContent().getFirst()).usingRecursiveComparison().isEqualTo(entity);
    }
}
