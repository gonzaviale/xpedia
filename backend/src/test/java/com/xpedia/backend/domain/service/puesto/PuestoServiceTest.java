package com.xpedia.backend.domain.service.puesto;

import com.xpedia.backend.domain.exception.DuplicateResourceException;
import com.xpedia.backend.domain.exception.ResourceNotFoundException;
import com.xpedia.backend.domain.model.puesto.Puesto;
import com.xpedia.backend.domain.repository.puesto.PuestoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PuestoServiceTest {

    private static final UUID PUESTO_ID = UUID.randomUUID();
    private static final UUID ORGANIZACION_ID = UUID.randomUUID();
    private static final UUID OTRA_ORGANIZACION_ID = UUID.randomUUID();
    private static final String NOMBRE = "Voz N1";
    private static final String NOMBRE_ANTERIOR = "Anterior";
    private static final String NOMBRE_NUEVO = "Nuevo";
    private static final Pageable PAGEABLE = PageRequest.of(1, 5);

    @Mock
    private PuestoRepository puestoRepository;

    @InjectMocks
    private PuestoService puestoService;

    @Test
    @DisplayName("Al crear, recorta espacios del nombre y guarda el puesto")
    void crearShouldTrimNombreAndSave() {
        givenNombreIsFree();
        givenRepositoryReturnsSaved();

        Puesto creado = crear("  " + NOMBRE + "  ");

        thenCreatedPuestoHasNombre(creado, NOMBRE);
    }

    @Test
    @DisplayName("Al crear, falla si ya existe un puesto con ese nombre en la organización")
    void crearShouldThrowWhenNombreIsTaken() {
        givenNombreIsTaken();

        assertThatThrownBy(() -> crear(NOMBRE)).isInstanceOf(DuplicateResourceException.class);
        thenNothingWasSaved();
    }

    @Test
    @DisplayName("Al actualizar, normaliza el nombre y conserva la organización del puesto existente")
    void actualizarShouldNormalizeNombreAndKeepOrganizacion() {
        Puesto existente = givenExistingPuesto();
        givenNombreIsNotUsedByOther(NOMBRE_NUEVO);
        givenRepositorySavesExisting(existente);

        Puesto actualizado = actualizar(cambios("  " + NOMBRE_NUEVO + "  ", OTRA_ORGANIZACION_ID));

        thenUpdatedPuestoHasNombreAndOrganizacion(actualizado, NOMBRE_NUEVO, ORGANIZACION_ID);
    }

    @Test
    @DisplayName("Al actualizar, falla y no guarda si otro puesto ya usa ese nombre")
    void actualizarShouldThrowAndNotSaveWhenNombreIsUsedByOther() {
        Puesto existente = givenExistingPuesto();
        givenNombreIsUsedByOther(NOMBRE);

        assertThatThrownBy(() -> actualizar(cambios(NOMBRE, null))).isInstanceOf(DuplicateResourceException.class);
        thenExistingPuestoWasNotChanged(existente);
        thenNothingWasSaved();
    }

    @Test
    @DisplayName("Al actualizar, falla si el puesto no existe")
    void actualizarShouldThrowWhenPuestoDoesNotExist() {
        givenPuestoDoesNotExist();

        assertThatThrownBy(() -> actualizar(cambios(NOMBRE, null))).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Al eliminar, borra el puesto cuando existe")
    void eliminarShouldDeletePuestoWhenItExists() {
        givenPuestoExistsById();

        puestoService.eliminar(PUESTO_ID);

        thenRepositoryDeleted();
    }

    @Test
    @DisplayName("Al eliminar, falla si el puesto no existe")
    void eliminarShouldThrowWhenPuestoDoesNotExist() {
        givenPuestoDoesNotExistById();

        assertThatThrownBy(() -> puestoService.eliminar(PUESTO_ID)).isInstanceOf(ResourceNotFoundException.class);
        thenNothingWasDeleted();
    }

    @Test
    @DisplayName("Al obtener, devuelve el puesto del repositorio")
    void obtenerShouldReturnPuestoFromRepository() {
        Puesto puesto = givenExistingPuesto();

        Puesto obtenido = puestoService.obtener(PUESTO_ID);

        assertThat(obtenido).isSameAs(puesto);
    }

    @Test
    @DisplayName("Al obtener, falla si el puesto no existe")
    void obtenerShouldThrowWhenPuestoDoesNotExist() {
        givenPuestoDoesNotExist();

        assertThatThrownBy(() -> puestoService.obtener(PUESTO_ID)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Al listar, devuelve la página del repositorio para la organización")
    void listarShouldReturnPageFromRepository() {
        Page<Puesto> page = givenRepositoryReturnsPage();

        Page<Puesto> listado = puestoService.listar(ORGANIZACION_ID, PAGEABLE);

        assertThat(listado).isSameAs(page);
    }

    // --- arrange ---
    private void givenNombreIsFree() {
        when(puestoRepository.existsByOrganizacionIdAndNombre(ORGANIZACION_ID, NOMBRE)).thenReturn(false);
    }

    private void givenNombreIsTaken() {
        when(puestoRepository.existsByOrganizacionIdAndNombre(ORGANIZACION_ID, NOMBRE)).thenReturn(true);
    }

    private void givenRepositoryReturnsSaved() {
        when(puestoRepository.save(any(Puesto.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    private Puesto givenExistingPuesto() {
        Puesto existente = puestoExistente();
        when(puestoRepository.findById(PUESTO_ID)).thenReturn(Optional.of(existente));
        return existente;
    }

    private void givenPuestoDoesNotExist() {
        when(puestoRepository.findById(PUESTO_ID)).thenReturn(Optional.empty());
    }

    private void givenNombreIsNotUsedByOther(String nombre) {
        when(puestoRepository.existsByOrganizacionIdAndNombreAndIdNot(ORGANIZACION_ID, nombre, PUESTO_ID))
                .thenReturn(false);
    }

    private void givenNombreIsUsedByOther(String nombre) {
        when(puestoRepository.existsByOrganizacionIdAndNombreAndIdNot(ORGANIZACION_ID, nombre, PUESTO_ID))
                .thenReturn(true);
    }

    private void givenRepositorySavesExisting(Puesto existente) {
        when(puestoRepository.save(existente)).thenReturn(existente);
    }

    private void givenPuestoExistsById() {
        when(puestoRepository.existsById(PUESTO_ID)).thenReturn(true);
    }

    private void givenPuestoDoesNotExistById() {
        when(puestoRepository.existsById(PUESTO_ID)).thenReturn(false);
    }

    private Page<Puesto> givenRepositoryReturnsPage() {
        Page<Puesto> page = new PageImpl<>(List.of(nuevoPuesto(NOMBRE)), PAGEABLE, 10);
        when(puestoRepository.findAll(ORGANIZACION_ID, PAGEABLE)).thenReturn(page);
        return page;
    }

    // --- helpers ---
    private Puesto nuevoPuesto(String nombre) {
        return Puesto.builder()
                .organizacionId(ORGANIZACION_ID)
                .nombre(nombre)
                .build();
    }

    private Puesto puestoExistente() {
        return Puesto.builder()
                .id(PUESTO_ID)
                .organizacionId(ORGANIZACION_ID)
                .nombre(NOMBRE_ANTERIOR)
                .build();
    }

    private Puesto cambios(String nombre, UUID organizacionId) {
        return Puesto.builder()
                .organizacionId(organizacionId)
                .nombre(nombre)
                .build();
    }

    // --- act ---
    private Puesto crear(String nombre) {
        return puestoService.crear(nuevoPuesto(nombre));
    }

    private Puesto actualizar(Puesto cambios) {
        return puestoService.actualizar(PUESTO_ID, cambios);
    }

    // --- assert ---
    private void thenCreatedPuestoHasNombre(Puesto creado, String nombre) {
        assertThat(creado.getNombre()).isEqualTo(nombre);
        verify(puestoRepository).save(creado);
    }

    private void thenUpdatedPuestoHasNombreAndOrganizacion(Puesto actualizado, String nombre, UUID organizacionId) {
        assertThat(actualizado.getNombre()).isEqualTo(nombre);
        assertThat(actualizado.getOrganizacionId()).isEqualTo(organizacionId);
    }

    private void thenExistingPuestoWasNotChanged(Puesto existente) {
        assertThat(existente.getNombre()).isEqualTo(NOMBRE_ANTERIOR);
    }

    private void thenNothingWasSaved() {
        verify(puestoRepository, never()).save(any(Puesto.class));
    }

    private void thenRepositoryDeleted() {
        verify(puestoRepository).deleteById(PUESTO_ID);
    }

    private void thenNothingWasDeleted() {
        verify(puestoRepository, never()).deleteById(any(UUID.class));
    }
}
