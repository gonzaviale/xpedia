package com.xpedia.backend.domain.service.intento;

import com.xpedia.backend.domain.exception.BusinessRuleException;
import com.xpedia.backend.domain.exception.ResourceNotFoundException;
import com.xpedia.backend.domain.model.cuestionario.Cuestionario;
import com.xpedia.backend.domain.model.cuestionario.Pregunta;
import com.xpedia.backend.domain.model.intento.Intento;
import com.xpedia.backend.domain.model.intento.Respuesta;
import com.xpedia.backend.domain.repository.intento.IntentoRepository;
import com.xpedia.backend.domain.service.cuestionario.CuestionarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IntentoServiceTest {

    private static final UUID ACTIVIDAD_ID = UUID.randomUUID();
    private static final UUID USUARIO_ID = UUID.randomUUID();
    private static final UUID INSCRIPCION_ID = UUID.randomUUID();
    private static final UUID PREGUNTA_ID = UUID.randomUUID();
    private static final OffsetDateTime INICIADO_EN = OffsetDateTime.parse("2026-10-09T14:55:00Z");
    private static final OffsetDateTime TERMINADO_EN = OffsetDateTime.parse("2026-10-09T15:00:00Z");

    @Mock
    private CuestionarioService cuestionarioService;

    @Mock
    private IntentoRepository intentoRepository;

    @InjectMocks
    private IntentoService intentoService;

    @Test
    @DisplayName("Guarda el intento corregido y devuelve el intento guardado")
    void registrarShouldSaveCorrectedIntentoAndReturnIt() {
        Intento guardado = Intento.builder().id(UUID.randomUUID()).build();
        givenCuestionarioIsFound();
        givenRepositorySaves(guardado);

        Intento result = registrar(respuestaElegida((short) 0));

        assertThat(result).isSameAs(guardado);
        thenSavedIntentoIsCorrected();
    }

    @Test
    @DisplayName("No guarda el intento cuando las respuestas no son válidas")
    void registrarShouldNotSaveWhenRespuestasAreInvalid() {
        givenCuestionarioIsFound();

        assertThatThrownBy(() -> registrar(respuestaElegida((short) 9)))
                .isInstanceOf(BusinessRuleException.class);

        verifyNoInteractions(intentoRepository);
    }

    @Test
    @DisplayName("Propaga ResourceNotFoundException cuando el cuestionario no es visible")
    void registrarShouldPropagateNotFoundWhenCuestionarioIsNotVisible() {
        givenCuestionarioIsNotFound();

        assertThatThrownBy(() -> registrar(respuestaElegida((short) 0)))
                .isInstanceOf(ResourceNotFoundException.class);

        verifyNoInteractions(intentoRepository);
    }

    // --- arrange ---
    private void givenCuestionarioIsFound() {
        when(cuestionarioService.obtenerAprobado(ACTIVIDAD_ID)).thenReturn(cuestionario());
    }

    private void givenCuestionarioIsNotFound() {
        when(cuestionarioService.obtenerAprobado(ACTIVIDAD_ID))
                .thenThrow(new ResourceNotFoundException("cuestionario", "id", ACTIVIDAD_ID));
    }

    private void givenRepositorySaves(Intento guardado) {
        when(intentoRepository.save(any(Intento.class))).thenReturn(guardado);
    }

    // --- helpers ---
    private Cuestionario cuestionario() {
        Pregunta pregunta = Pregunta.builder()
                .id(PREGUNTA_ID)
                .posicion((short) 0)
                .opciones(List.of("Correcta", "Incorrecta"))
                .correcta((short) 0)
                .build();
        return Cuestionario.builder()
                .id(ACTIVIDAD_ID)
                .preguntas(List.of(pregunta))
                .build();
    }

    private Respuesta respuestaElegida(Short elegida) {
        return Respuesta.builder()
                .preguntaId(PREGUNTA_ID)
                .elegida(elegida)
                .build();
    }

    // --- act ---
    private Intento registrar(Respuesta respuesta) {
        return intentoService.registrar(
                USUARIO_ID,
                INSCRIPCION_ID,
                ACTIVIDAD_ID,
                List.of(respuesta),
                INICIADO_EN,
                TERMINADO_EN);
    }

    // --- assert ---
    private void thenSavedIntentoIsCorrected() {
        ArgumentCaptor<Intento> captor = ArgumentCaptor.forClass(Intento.class);
        verify(intentoRepository).save(captor.capture());
        Intento intento = captor.getValue();
        assertThat(intento.getUsuarioId()).isEqualTo(USUARIO_ID);
        assertThat(intento.getInscripcionId()).isEqualTo(INSCRIPCION_ID);
        assertThat(intento.getActividadId()).isEqualTo(ACTIVIDAD_ID);
        assertThat(intento.getPuntaje()).isEqualByComparingTo("100");
        assertThat(intento.getAprobado()).isTrue();
        assertThat(intento.getIniciadoEn()).isEqualTo(INICIADO_EN);
        assertThat(intento.getTerminadoEn()).isEqualTo(TERMINADO_EN);
    }
}
