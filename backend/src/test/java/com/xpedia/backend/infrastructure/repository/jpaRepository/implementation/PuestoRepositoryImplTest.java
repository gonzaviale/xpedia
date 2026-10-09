package com.xpedia.backend.infrastructure.repository.jpaRepository.implementation;

import com.xpedia.backend.domain.model.puesto.Puesto;
import com.xpedia.backend.infrastructure.repository.entity.PuestoEntity;
import com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces.IPuestoJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PuestoRepositoryImplTest {

    private static final UUID PUESTO_ID = UUID.randomUUID();
    private static final UUID ORGANIZACION_ID = UUID.randomUUID();
    private static final String NOMBRE = "Voz N1";
    private static final OffsetDateTime CREADO_EN = OffsetDateTime.parse("2026-01-10T10:00:00Z");
    private static final OffsetDateTime ACTUALIZADO_EN = OffsetDateTime.parse("2026-02-11T11:30:00Z");
    private static final Pageable PAGEABLE = PageRequest.of(0, 20);

    @Mock
    private IPuestoJpaRepository jpa;

    @InjectMocks
    private PuestoRepositoryImpl repository;

    @Test
    @DisplayName("save convierte el puesto a entidad con todos sus campos")
    void saveShouldPassEntityWithEveryPuestoField() {
        givenJpaSavesEntity();

        save();

        thenJpaReceivedEntityWithEveryField();
    }

    @Test
    @DisplayName("save devuelve el puesto de dominio con los campos de la entidad guardada")
    void saveShouldReturnPuestoMappedFromSavedEntity() {
        givenJpaSavesEntity();

        Puesto saved = save();

        thenPuestoHasEveryField(saved);
    }

    @Test
    @DisplayName("findById devuelve el puesto de dominio cuando la entidad existe")
    void findByIdShouldReturnPuestoWhenEntityExists() {
        givenJpaFindsEntity();

        Optional<Puesto> found = repository.findById(PUESTO_ID);

        assertThat(found).isPresent();
        thenPuestoHasEveryField(found.get());
    }

    @Test
    @DisplayName("findById devuelve vacío cuando la entidad no existe")
    void findByIdShouldReturnEmptyWhenEntityDoesNotExist() {
        givenJpaFindsNothing();

        Optional<Puesto> found = repository.findById(PUESTO_ID);

        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("findAll devuelve la página de puestos de dominio de la organización")
    void findAllShouldReturnPageOfPuestosForOrganizacion() {
        givenJpaListsEntitiesForOrganizacion(ORGANIZACION_ID);

        Page<Puesto> page = repository.findAll(ORGANIZACION_ID, PAGEABLE);

        assertThat(page.getContent()).hasSize(1);
        thenPuestoHasEveryField(page.getContent().getFirst());
    }

    @Test
    @DisplayName("findAll consulta los puestos globales cuando la organización es null")
    void findAllShouldQueryGlobalPuestosWhenOrganizacionIsNull() {
        givenJpaListsEntitiesForOrganizacion(null);

        Page<Puesto> page = repository.findAll(null, PAGEABLE);

        assertThat(page.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("existsById delega el id al repositorio JPA")
    void existsByIdShouldDelegateToJpa() {
        when(jpa.existsById(PUESTO_ID)).thenReturn(true);

        boolean exists = repository.existsById(PUESTO_ID);

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByOrganizacionIdAndNombre delega organización y nombre al repositorio JPA")
    void existsByOrganizacionIdAndNombreShouldDelegateToJpa() {
        when(jpa.existsByOrganizacionIdAndNombre(ORGANIZACION_ID, NOMBRE)).thenReturn(true);

        boolean exists = repository.existsByOrganizacionIdAndNombre(ORGANIZACION_ID, NOMBRE);

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByOrganizacionIdAndNombreAndIdNot delega organización, nombre e id al repositorio JPA")
    void existsByOrganizacionIdAndNombreAndIdNotShouldDelegateToJpa() {
        when(jpa.existsByOrganizacionIdAndNombreAndIdNot(ORGANIZACION_ID, NOMBRE, PUESTO_ID)).thenReturn(true);

        boolean exists = repository.existsByOrganizacionIdAndNombreAndIdNot(ORGANIZACION_ID, NOMBRE, PUESTO_ID);

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("deleteById delega el id al repositorio JPA")
    void deleteByIdShouldDelegateToJpa() {
        repository.deleteById(PUESTO_ID);

        verify(jpa).deleteById(PUESTO_ID);
    }

    // --- arrange ---
    private void givenJpaSavesEntity() {
        when(jpa.save(any(PuestoEntity.class))).thenReturn(entity());
    }

    private void givenJpaFindsEntity() {
        when(jpa.findById(PUESTO_ID)).thenReturn(Optional.of(entity()));
    }

    private void givenJpaFindsNothing() {
        when(jpa.findById(PUESTO_ID)).thenReturn(Optional.empty());
    }

    private void givenJpaListsEntitiesForOrganizacion(UUID organizacionId) {
        when(jpa.findByOrganizacionId(organizacionId, PAGEABLE))
                .thenReturn(new PageImpl<>(List.of(entity()), PAGEABLE, 1));
    }

    // --- helpers ---
    private Puesto puesto() {
        return Puesto.builder()
                .id(PUESTO_ID)
                .organizacionId(ORGANIZACION_ID)
                .nombre(NOMBRE)
                .creadoEn(CREADO_EN)
                .actualizadoEn(ACTUALIZADO_EN)
                .build();
    }

    private PuestoEntity entity() {
        return PuestoEntity.builder()
                .id(PUESTO_ID)
                .organizacionId(ORGANIZACION_ID)
                .nombre(NOMBRE)
                .creadoEn(CREADO_EN)
                .actualizadoEn(ACTUALIZADO_EN)
                .build();
    }

    // --- act ---
    private Puesto save() {
        return repository.save(puesto());
    }

    // --- assert ---
    private void thenJpaReceivedEntityWithEveryField() {
        ArgumentCaptor<PuestoEntity> captor = ArgumentCaptor.forClass(PuestoEntity.class);
        verify(jpa).save(captor.capture());
        PuestoEntity captured = captor.getValue();
        assertThat(captured.getId()).isEqualTo(PUESTO_ID);
        assertThat(captured.getOrganizacionId()).isEqualTo(ORGANIZACION_ID);
        assertThat(captured.getNombre()).isEqualTo(NOMBRE);
        assertThat(captured.getCreadoEn()).isEqualTo(CREADO_EN);
        assertThat(captured.getActualizadoEn()).isEqualTo(ACTUALIZADO_EN);
    }

    private void thenPuestoHasEveryField(Puesto puesto) {
        assertThat(puesto.getId()).isEqualTo(PUESTO_ID);
        assertThat(puesto.getOrganizacionId()).isEqualTo(ORGANIZACION_ID);
        assertThat(puesto.getNombre()).isEqualTo(NOMBRE);
        assertThat(puesto.getCreadoEn()).isEqualTo(CREADO_EN);
        assertThat(puesto.getActualizadoEn()).isEqualTo(ACTUALIZADO_EN);
    }
}
