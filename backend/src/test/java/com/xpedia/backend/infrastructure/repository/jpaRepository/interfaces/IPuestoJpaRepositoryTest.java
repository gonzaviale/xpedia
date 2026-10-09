package com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces;

import com.xpedia.backend.support.PostgresRepositoryTestSupport;
import com.xpedia.backend.infrastructure.repository.entity.PuestoEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;

@Transactional
class IPuestoJpaRepositoryTest extends PostgresRepositoryTestSupport {
    @Autowired private IPuestoJpaRepository repository;
    private final UUID org = UUID.fromString("00000000-0000-0000-0000-000000000100");

    @Test void filtraGlobalesYEmpresaSinMezclarlosYNombresSeComparanPorAmbito() {
        var global = repository.saveAndFlush(PuestoEntity.builder().nombre("Atención").build());
        repository.saveAndFlush(PuestoEntity.builder().organizacionId(org).nombre("Atención").build());
        assertThat(repository.findByOrganizacionId(null, PageRequest.of(0,20)).getContent()).extracting("id").containsExactly(global.getId());
        assertThat(repository.findByOrganizacionId(org, PageRequest.of(0,20)).getTotalElements()).isEqualTo(1);
        assertThat(repository.existsByOrganizacionIdAndNombre(null, "Atención")).isTrue();
        assertThat(repository.existsByOrganizacionIdAndNombreAndIdNot(null, "Atención", global.getId())).isFalse();
        assertThat(repository.existsByOrganizacionIdAndNombreAndIdNot(null, "Atención", UUID.randomUUID())).isTrue();
        assertThat(repository.existsByOrganizacionIdAndNombre(org, "Ausente")).isFalse();
    }
    @Test void callbacksAsignanFechasYActualizarConservaCreacion() {
        var entity = repository.saveAndFlush(PuestoEntity.builder().nombre("Antes").build());
        var created = entity.getCreadoEn(); assertThat(created).isNotNull(); assertThat(entity.getActualizadoEn()).isEqualTo(created);
        entity.setNombre("Después"); entity.setActualizadoEn(created.minusDays(1));
        repository.saveAndFlush(entity);
        assertThat(entity.getCreadoEn()).isEqualTo(created); assertThat(entity.getActualizadoEn()).isAfterOrEqualTo(created);
        repository.deleteById(entity.getId()); repository.flush(); assertThat(repository.existsById(entity.getId())).isFalse();
    }
    @Test void uniqueNullsNotDistinctRechazaDuplicadoGlobal() {
        repository.saveAndFlush(PuestoEntity.builder().nombre("Duplicado").build());
        assertThatThrownBy(() -> repository.saveAndFlush(PuestoEntity.builder().nombre("Duplicado").build()))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
    @Test void foreignKeyRechazaEmpresaInexistente() {
        assertThatThrownBy(() -> repository.saveAndFlush(PuestoEntity.builder().organizacionId(UUID.randomUUID()).nombre("Ajeno").build()))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
