package com.xpedia.backend.infrastructure.repository.jpaRepository.implementation;
import com.xpedia.backend.infrastructure.repository.entity.*;
import com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces.*;
import com.xpedia.backend.infrastructure.repository.mapper.RubricaRepositoryMapper;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static com.xpedia.backend.support.ContractData.full;
class RubricaRepositoryImplTest {
    private final IRubricaJpaRepository rubricas = mock(IRubricaJpaRepository.class);
    private final IRubricaCriterioJpaRepository criterios = mock(IRubricaCriterioJpaRepository.class);
    private final RubricaRepositoryImpl repository = new RubricaRepositoryImpl(rubricas,criterios,new RubricaRepositoryMapper());
    @Test void idsVaciosNoConsultanBase() { assertThat(repository.findGlobalesByIds(List.of())).isEmpty(); verifyNoInteractions(rubricas,criterios); }
    @Test void sinRubricasVisiblesNoConsultaCriterios() {
        var ids = List.of(UUID.randomUUID()); when(rubricas.findGlobalesByIds(ids)).thenReturn(List.of());
        assertThat(repository.findGlobalesByIds(ids)).isEmpty(); verifyNoInteractions(criterios);
    }
    @Test void agrupaCriteriosDeRubricasVisiblesEnUnaConsultaYConservaOrden() {
        var a = full(RubricaEntity.class); var b = full(RubricaEntity.class); b.setId(UUID.randomUUID());
        var ids = List.of(a.getId(),b.getId()); when(rubricas.findGlobalesByIds(ids)).thenReturn(List.of(a,b));
        var c = full(RubricaCriterioEntity.class); c.setRubricaId(a.getId());
        when(criterios.findGlobalesByRubricaIds(ids)).thenReturn(List.of(c));
        var result = repository.findGlobalesByIds(ids);
        assertThat(result).containsOnlyKeys(a.getId(),b.getId());
        assertThat(result.get(a.getId()).criterios()).extracting("id").containsExactly(c.getId());
        assertThat(result.get(b.getId()).criterios()).isEmpty(); verify(criterios,times(1)).findGlobalesByRubricaIds(ids);
    }
}
