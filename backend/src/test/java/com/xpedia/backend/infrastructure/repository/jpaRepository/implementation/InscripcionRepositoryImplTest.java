package com.xpedia.backend.infrastructure.repository.jpaRepository.implementation;

import com.xpedia.backend.domain.model.inscripcion.Inscripcion;
import com.xpedia.backend.infrastructure.repository.entity.InscripcionEntity;
import com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces.IInscripcionJpaRepository;
import com.xpedia.backend.infrastructure.repository.mapper.InscripcionRepositoryMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.xpedia.backend.support.InscripcionTestData.INSCRIPCION_ID;
import static com.xpedia.backend.support.InscripcionTestData.RUTA_ID;
import static com.xpedia.backend.support.InscripcionTestData.USUARIO_ID;
import static com.xpedia.backend.support.InscripcionTestData.inscripcion;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InscripcionRepositoryImplTest {

    @Mock
    private IInscripcionJpaRepository jpaRepository;

    private InscripcionRepositoryImpl repository;

    private final InscripcionRepositoryMapper mapper = new InscripcionRepositoryMapper();

    @BeforeEach
    void setUp() {
        repository = new InscripcionRepositoryImpl(jpaRepository, mapper);
    }

    @Test
    @DisplayName("Consulta si la persona tiene otra inscripción abierta en la ruta")
    void existsAbiertaShouldDelegatePersonAndRoute() {
        givenExistsAbiertaShouldDelegatePersonAndRoute();

        boolean result = repository.existsAbiertaByUsuarioIdAndRutaId(USUARIO_ID, RUTA_ID);

        thenExistsAbiertaShouldDelegatePersonAndRoute(result);
    }

    @Test
    @DisplayName("Guarda y fuerza las restricciones antes de mapear la inscripción persistida")
    void saveShouldFlushAndMapPersistedEnrollment() {
        givenSaveShouldFlushAndMapPersistedEnrollment();

        Inscripcion result = repository.save(inscripcion());

        thenPersistedEnrollment(result);
    }

    @Test
    @DisplayName("Obtiene solo la inscripción ACTIVA más reciente del usuario")
    void findActualShouldMapLatestActiveEnrollment() {
        givenFindActualShouldMapLatestActiveEnrollment();

        Optional<Inscripcion> result = repository.findActualByUsuarioId(USUARIO_ID);

        thenFindActualShouldMapLatestActiveEnrollment(result);
    }

    @Test
    @DisplayName("Conserva el resultado vacío cuando no hay inscripción activa")
    void findActualShouldKeepEmptyResult() {
        Optional<Inscripcion> result = repository.findActualByUsuarioId(USUARIO_ID);

        thenFindActualShouldKeepEmptyResult(result);
    }

    // --- arrange ---
    private void givenExistsAbiertaShouldDelegatePersonAndRoute() {
        when(jpaRepository.existsAbiertaByUsuarioIdAndRutaId(USUARIO_ID, RUTA_ID)).thenReturn(true);
    }

    private void givenSaveShouldFlushAndMapPersistedEnrollment() {
        when(jpaRepository.saveAndFlush(any())).thenReturn(mapper.toEntity(inscripcion()));
    }

    private void givenFindActualShouldMapLatestActiveEnrollment() {
        when(jpaRepository.findFirstByUsuarioIdAndEstadoOrderByIniciadaEnDescIdDesc(USUARIO_ID, "ACTIVA"))
                .thenReturn(Optional.of(mapper.toEntity(inscripcion())));
    }

    // --- assert ---
    private void thenPersistedEnrollment(Inscripcion result) {
        assertThat(result.getId()).isEqualTo(INSCRIPCION_ID);
        assertThat(result.getUsuarioId()).isEqualTo(USUARIO_ID);
        ArgumentCaptor<InscripcionEntity> captor = ArgumentCaptor.forClass(InscripcionEntity.class);
        verify(jpaRepository).saveAndFlush(captor.capture());
        assertThat(captor.getValue().getRutaId()).isEqualTo(RUTA_ID);
        assertThat(captor.getValue().getMetaPersonal()).isEqualTo(inscripcion().getMetaPersonal());
    }

    private void thenExistsAbiertaShouldDelegatePersonAndRoute(boolean result) {
        assertThat(result).isTrue();
        verify(jpaRepository).existsAbiertaByUsuarioIdAndRutaId(USUARIO_ID, RUTA_ID);
    }

    private void thenFindActualShouldMapLatestActiveEnrollment(Optional<Inscripcion> result) {
        assertThat(result.orElseThrow().getId()).isEqualTo(INSCRIPCION_ID);
    }

    private void thenFindActualShouldKeepEmptyResult(Optional<Inscripcion> result) {
        assertThat(result).isEmpty();
    }
}

