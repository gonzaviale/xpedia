package com.xpedia.backend.domain.useCase.intento;

import com.xpedia.backend.domain.dto.intento.RegistrarIntentoRequest;
import com.xpedia.backend.domain.dto.intento.RegistrarIntentoResponse;
import com.xpedia.backend.domain.dto.intento.RespuestaEnviadaItem;
import com.xpedia.backend.domain.exception.BusinessRuleException;
import com.xpedia.backend.domain.mapper.intento.RegistrarIntentoMapper;
import com.xpedia.backend.domain.model.enums.Confianza;
import com.xpedia.backend.domain.model.intento.Intento;
import com.xpedia.backend.domain.model.intento.Respuesta;
import com.xpedia.backend.domain.service.intento.IntentoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarIntentoUseCaseTest {

    private static final UUID USUARIO_ID = UUID.randomUUID();
    private static final UUID INSCRIPCION_ID = UUID.randomUUID();
    private static final UUID ACTIVIDAD_ID = UUID.randomUUID();
    private static final UUID PREGUNTA_ID = UUID.randomUUID();
    private static final UUID INTENTO_ID = UUID.randomUUID();
    private static final OffsetDateTime INICIADO_EN = OffsetDateTime.parse("2026-10-09T14:55:00Z");
    private static final OffsetDateTime AHORA = OffsetDateTime.parse("2026-10-09T15:00:00Z");

    @Mock
    private IntentoService intentoService;

    private RegistrarIntentoUseCase registrarIntentoUseCase;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(Instant.parse("2026-10-09T15:00:00Z"), ZoneOffset.UTC);
        registrarIntentoUseCase = new RegistrarIntentoUseCase(intentoService, new RegistrarIntentoMapper(), clock);
    }

    @Test
    @DisplayName("Registra el intento y devuelve la respuesta mapeada")
    void executeShouldReturnMappedIntento() {
        givenServiceRegistersIntento();

        RegistrarIntentoResponse response = registrar();

        assertThat(response.id()).isEqualTo(INTENTO_ID);
        assertThat(response.actividadId()).isEqualTo(ACTIVIDAD_ID);
        assertThat(response.puntaje()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Delega al servicio con los datos del pedido, las respuestas mapeadas y la hora del reloj")
    void executeShouldDelegateToServiceWithRequestDataAndClockTime() {
        givenServiceRegistersIntento();

        registrar();

        thenServiceReceivedRequestDataAndClockTime();
    }

    @Test
    @DisplayName("Propaga el error de dominio del servicio sin convertirlo")
    void executeShouldPropagateDomainErrorWhenServiceFails() {
        BusinessRuleException error = new BusinessRuleException("Respuestas inválidas");
        when(intentoService.registrar(any(), any(), any(), any(), any(), any())).thenThrow(error);

        assertThatThrownBy(this::registrar).isSameAs(error);
    }

    // --- arrange ---
    private void givenServiceRegistersIntento() {
        Intento intento = Intento.builder()
                .id(INTENTO_ID)
                .actividadId(ACTIVIDAD_ID)
                .puntaje(BigDecimal.ZERO)
                .aprobado(false)
                .respuestas(List.of())
                .build();
        when(intentoService.registrar(
                eq(USUARIO_ID),
                eq(INSCRIPCION_ID),
                eq(ACTIVIDAD_ID),
                any(),
                eq(INICIADO_EN),
                eq(AHORA))).thenReturn(intento);
    }

    // --- act ---
    private RegistrarIntentoResponse registrar() {
        RespuestaEnviadaItem respuesta = new RespuestaEnviadaItem(PREGUNTA_ID, (short) 1, Confianza.DUDE, 1500);
        return registrarIntentoUseCase.execute(new RegistrarIntentoRequest(
                USUARIO_ID,
                INSCRIPCION_ID,
                ACTIVIDAD_ID,
                INICIADO_EN,
                List.of(respuesta)));
    }

    // --- assert ---
    @SuppressWarnings("unchecked")
    private void thenServiceReceivedRequestDataAndClockTime() {
        ArgumentCaptor<List<Respuesta>> captor = ArgumentCaptor.forClass(List.class);
        verify(intentoService).registrar(
                eq(USUARIO_ID),
                eq(INSCRIPCION_ID),
                eq(ACTIVIDAD_ID),
                captor.capture(),
                eq(INICIADO_EN),
                eq(AHORA));
        Respuesta respuesta = captor.getValue().getFirst();
        assertThat(respuesta.getPreguntaId()).isEqualTo(PREGUNTA_ID);
        assertThat(respuesta.getElegida()).isEqualTo((short) 1);
        assertThat(respuesta.getConfianza()).isEqualTo(Confianza.DUDE);
        assertThat(respuesta.getMilisegundos()).isEqualTo(1500);
    }
}
