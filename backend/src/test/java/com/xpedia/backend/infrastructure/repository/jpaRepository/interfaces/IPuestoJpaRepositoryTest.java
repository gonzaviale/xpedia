package com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces;

import com.xpedia.backend.infrastructure.repository.entity.PuestoEntity;
import com.xpedia.backend.support.PostgresRepositoryTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
class IPuestoJpaRepositoryTest extends PostgresRepositoryTestSupport {

    // Organización cargada por /db/rutas-test.sql
    private static final UUID ORGANIZACION_ID = UUID.fromString("00000000-0000-0000-0000-000000000100");
    private static final UUID ORGANIZACION_INEXISTENTE_ID = UUID.randomUUID();
    private static final UUID OTRO_PUESTO_ID = UUID.randomUUID();
    private static final String NOMBRE = "Atención";
    private static final String NOMBRE_AUSENTE = "Ausente";
    private static final String NOMBRE_ANTES = "Antes";
    private static final String NOMBRE_DESPUES = "Después";
    private static final String NOMBRE_DUPLICADO = "Duplicado";
    private static final Pageable PAGEABLE = PageRequest.of(0, 20);

    @Autowired
    private IPuestoJpaRepository repository;

    @Test
    @DisplayName("findByOrganizacionId con null devuelve solo los puestos globales")
    void findByOrganizacionIdShouldReturnOnlyGlobalPuestosWhenOrganizacionIsNull() {
        PuestoEntity global = givenGlobalAndOrganizacionPuestosWithSameNombre();

        Page<PuestoEntity> page = repository.findByOrganizacionId(null, PAGEABLE);

        assertThat(page.getContent()).extracting(PuestoEntity::getId).containsExactly(global.getId());
    }

    @Test
    @DisplayName("findByOrganizacionId con una organización devuelve solo sus puestos")
    void findByOrganizacionIdShouldReturnOnlyOrganizacionPuestos() {
        givenGlobalAndOrganizacionPuestosWithSameNombre();

        Page<PuestoEntity> page = repository.findByOrganizacionId(ORGANIZACION_ID, PAGEABLE);

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().getFirst().getOrganizacionId()).isEqualTo(ORGANIZACION_ID);
    }

    @Test
    @DisplayName("existsByOrganizacionIdAndNombre encuentra un puesto global por nombre con organización null")
    void existsByOrganizacionIdAndNombreShouldMatchGlobalPuestoWhenOrganizacionIsNull() {
        givenGlobalAndOrganizacionPuestosWithSameNombre();

        boolean exists = repository.existsByOrganizacionIdAndNombre(null, NOMBRE);

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByOrganizacionIdAndNombre devuelve false cuando la organización no tiene ese nombre")
    void existsByOrganizacionIdAndNombreShouldReturnFalseWhenNombreIsAbsent() {
        givenGlobalAndOrganizacionPuestosWithSameNombre();

        boolean exists = repository.existsByOrganizacionIdAndNombre(ORGANIZACION_ID, NOMBRE_AUSENTE);

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("existsByOrganizacionIdAndNombreAndIdNot devuelve false cuando el único coincidente es el id excluido")
    void existsByOrganizacionIdAndNombreAndIdNotShouldReturnFalseWhenOnlyMatchIsExcluded() {
        PuestoEntity global = givenGlobalAndOrganizacionPuestosWithSameNombre();

        boolean exists = repository.existsByOrganizacionIdAndNombreAndIdNot(null, NOMBRE, global.getId());

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("existsByOrganizacionIdAndNombreAndIdNot devuelve true cuando coincide un puesto de otro id")
    void existsByOrganizacionIdAndNombreAndIdNotShouldReturnTrueWhenAnotherPuestoMatches() {
        givenGlobalAndOrganizacionPuestosWithSameNombre();

        boolean exists = repository.existsByOrganizacionIdAndNombreAndIdNot(null, NOMBRE, OTRO_PUESTO_ID);

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Al persistir asigna creadoEn y actualizadoEn con el mismo valor")
    void saveShouldAssignCreadoEnAndActualizadoEnWithSameValue() {
        PuestoEntity entity = repository.saveAndFlush(globalPuesto(NOMBRE_ANTES));

        assertThat(entity.getCreadoEn()).isNotNull();
        assertThat(entity.getActualizadoEn()).isEqualTo(entity.getCreadoEn());
    }

    @Test
    @DisplayName("Al actualizar conserva creadoEn y refresca actualizadoEn")
    void updateShouldKeepCreadoEnAndRefreshActualizadoEn() {
        PuestoEntity entity = repository.saveAndFlush(globalPuesto(NOMBRE_ANTES));
        OffsetDateTime creadoEn = entity.getCreadoEn();

        renameWithStaleActualizadoEn(entity, creadoEn);

        assertThat(entity.getCreadoEn()).isEqualTo(creadoEn);
        assertThat(entity.getActualizadoEn()).isAfterOrEqualTo(creadoEn);
    }

    @Test
    @DisplayName("deleteById elimina el puesto")
    void deleteByIdShouldRemovePuesto() {
        PuestoEntity entity = repository.saveAndFlush(globalPuesto(NOMBRE_ANTES));

        repository.deleteById(entity.getId());
        repository.flush();

        assertThat(repository.existsById(entity.getId())).isFalse();
    }

    @Test
    @DisplayName("La restricción única con NULLS NOT DISTINCT rechaza un nombre global duplicado")
    void saveShouldRejectDuplicatedGlobalNombre() {
        repository.saveAndFlush(globalPuesto(NOMBRE_DUPLICADO));

        assertThatThrownBy(() -> repository.saveAndFlush(globalPuesto(NOMBRE_DUPLICADO)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("La clave foránea rechaza una organización inexistente")
    void saveShouldRejectNonexistentOrganizacion() {
        PuestoEntity ajeno = organizacionPuesto(ORGANIZACION_INEXISTENTE_ID, "Ajeno");

        assertThatThrownBy(() -> repository.saveAndFlush(ajeno))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    // --- arrange ---
    private PuestoEntity givenGlobalAndOrganizacionPuestosWithSameNombre() {
        PuestoEntity global = repository.saveAndFlush(globalPuesto(NOMBRE));
        repository.saveAndFlush(organizacionPuesto(ORGANIZACION_ID, NOMBRE));
        return global;
    }

    // --- helpers ---
    private PuestoEntity globalPuesto(String nombre) {
        return PuestoEntity.builder()
                .nombre(nombre)
                .build();
    }

    private PuestoEntity organizacionPuesto(UUID organizacionId, String nombre) {
        return PuestoEntity.builder()
                .organizacionId(organizacionId)
                .nombre(nombre)
                .build();
    }

    // --- act ---
    private void renameWithStaleActualizadoEn(PuestoEntity entity, OffsetDateTime creadoEn) {
        entity.setNombre(NOMBRE_DESPUES);
        entity.setActualizadoEn(creadoEn.minusDays(1));
        repository.saveAndFlush(entity);
    }
}
