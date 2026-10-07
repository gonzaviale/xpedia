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

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PuestoServiceTest {

    private static final UUID PUESTO_ID = UUID.randomUUID();
    private static final UUID ORGANIZACION_ID = UUID.randomUUID();
    private static final String NOMBRE = "Voz N1";

    @Mock
    private PuestoRepository puestoRepository;

    @InjectMocks
    private PuestoService puestoService;

    @Test
    @DisplayName("Al crear, recorta espacios del nombre y guarda el puesto")
    void crearShouldTrimNombreAndSave() {
        givenNombreIsFree();
        givenRepositoryReturnsSaved();

        Puesto creado = puestoService.crear(nuevoPuesto("  " + NOMBRE + "  "));

        assertThat(creado.getNombre()).isEqualTo(NOMBRE);
        verify(puestoRepository).save(any(Puesto.class));
    }

    @Test
    @DisplayName("Al crear, falla si ya existe un puesto con ese nombre en la organización")
    void crearShouldThrowWhenNombreIsTaken() {
        when(puestoRepository.existsByOrganizacionIdAndNombre(ORGANIZACION_ID, NOMBRE)).thenReturn(true);

        assertThatThrownBy(() -> puestoService.crear(nuevoPuesto(NOMBRE)))
                .isInstanceOf(DuplicateResourceException.class);
        verify(puestoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Al actualizar, falla si el puesto no existe")
    void actualizarShouldThrowWhenPuestoDoesNotExist() {
        when(puestoRepository.findById(PUESTO_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> puestoService.actualizar(PUESTO_ID, nuevoPuesto(NOMBRE)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Al eliminar, falla si el puesto no existe")
    void eliminarShouldThrowWhenPuestoDoesNotExist() {
        when(puestoRepository.existsById(PUESTO_ID)).thenReturn(false);

        assertThatThrownBy(() -> puestoService.eliminar(PUESTO_ID))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(puestoRepository, never()).deleteById(any());
    }

    // --- arrange ---
    private void givenNombreIsFree() {
        when(puestoRepository.existsByOrganizacionIdAndNombre(ORGANIZACION_ID, NOMBRE)).thenReturn(false);
    }

    private void givenRepositoryReturnsSaved() {
        when(puestoRepository.save(any(Puesto.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    private Puesto nuevoPuesto(String nombre) {
        return Puesto.builder()
                .organizacionId(ORGANIZACION_ID)
                .nombre(nombre)
                .build();
    }
}
