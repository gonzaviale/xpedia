package com.xpedia.backend.infrastructure.repository.jpaRepository.implementation;

import com.xpedia.backend.domain.model.puesto.Puesto;
import com.xpedia.backend.infrastructure.repository.entity.PuestoEntity;
import com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces.IPuestoJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static com.xpedia.backend.support.ContractData.*;

class PuestoRepositoryImplTest {
    private final IPuestoJpaRepository jpa = mock(IPuestoJpaRepository.class);
    private final PuestoRepositoryImpl repository = new PuestoRepositoryImpl(jpa);
    @Test void guardarMapeaEntradaYSalidaCompletas() {
        var input = full(Puesto.class); var saved = full(PuestoEntity.class);
        when(jpa.save(any())).thenAnswer(call -> {
            assertThat(call.<PuestoEntity>getArgument(0)).usingRecursiveComparison().isEqualTo(input); return saved;
        });
        assertThat(repository.save(input)).usingRecursiveComparison().isEqualTo(saved);
    }
    @Test void consultaPresenteAusenteYListadoConservanContratos() {
        var saved = full(PuestoEntity.class); var page = PageRequest.of(0, 20);
        when(jpa.findById(saved.getId())).thenReturn(Optional.of(saved));
        assertThat(repository.findById(saved.getId()).orElseThrow()).usingRecursiveComparison().isEqualTo(saved);
        assertThat(repository.findById(UUID.randomUUID())).isEmpty();
        when(jpa.findByOrganizacionId(null, page)).thenReturn(new PageImpl<>(List.of(saved), page, 1));
        assertThat(repository.findAll(null, page).getContent().getFirst()).usingRecursiveComparison().isEqualTo(saved);
    }
    @Test void existenciaDuplicadosYEliminacionDeleganParametros() {
        var id = UUID.randomUUID(); var org = UUID.randomUUID();
        when(jpa.existsById(id)).thenReturn(true); when(jpa.existsByOrganizacionIdAndNombre(org,"Nombre")).thenReturn(true);
        when(jpa.existsByOrganizacionIdAndNombreAndIdNot(org,"Nombre",id)).thenReturn(true);
        assertThat(repository.existsById(id)).isTrue(); assertThat(repository.existsByOrganizacionIdAndNombre(org,"Nombre")).isTrue();
        assertThat(repository.existsByOrganizacionIdAndNombreAndIdNot(org,"Nombre",id)).isTrue();
        repository.deleteById(id); verify(jpa).deleteById(id);
    }
}
