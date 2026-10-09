package com.xpedia.backend.infrastructure.repository.jpaRepository.implementation;

import com.xpedia.backend.domain.model.enums.Confianza;
import com.xpedia.backend.domain.model.enums.ModoIntento;
import com.xpedia.backend.domain.model.intento.Intento;
import com.xpedia.backend.domain.model.intento.Respuesta;
import com.xpedia.backend.infrastructure.repository.entity.IntentoEntity;
import com.xpedia.backend.infrastructure.repository.entity.RespuestaEntity;
import com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces.IIntentoJpaRepository;
import com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces.IRespuestaJpaRepository;
import com.xpedia.backend.infrastructure.repository.mapper.IntentoRepositoryMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IntentoRepositoryImplTest {

    private static final UUID INTENTO_ID = UUID.randomUUID();
    private static final UUID USUARIO_ID = UUID.randomUUID();
    private static final UUID ACTIVIDAD_ID = UUID.randomUUID();
    private static final UUID PREGUNTA_UNO_ID = UUID.randomUUID();
    private static final UUID PREGUNTA_DOS_ID = UUID.randomUUID();

    @Mock
    private IIntentoJpaRepository intentos;

    @Mock
    private IRespuestaJpaRepository respuestas;

    private IntentoRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new IntentoRepositoryImpl(intentos, respuestas, new IntentoRepositoryMapper());
    }

    @Test
    @DisplayName("Guarda todas las respuestas con el id del intento guardado en una sola llamada")
    void saveShouldSaveAllRespuestasWithSavedIntentoIdInOneCall() {
        givenIntentoIsSavedWithId();

        repository.save(intento());

        thenRespuestasWereSavedWithIntentoId();
    }

    @Test
    @DisplayName("Devuelve el intento con el id generado y las respuestas corregidas en memoria")
    void saveShouldReturnIntentoWithGeneratedIdAndCorrectedRespuestas() {
        givenIntentoIsSavedWithId();
        Intento intento = intento();

        Intento result = repository.save(intento);

        assertThat(result.getId()).isEqualTo(INTENTO_ID);
        assertThat(result.getRespuestas()).isSameAs(intento.getRespuestas());
        assertThat(result.getPuntaje()).isEqualByComparingTo("50");
    }

    // --- arrange ---
    private void givenIntentoIsSavedWithId() {
        IntentoEntity guardado = IntentoEntity.builder()
                .id(INTENTO_ID)
                .usuarioId(USUARIO_ID)
                .actividadId(ACTIVIDAD_ID)
                .modo("PRACTICA")
                .puntaje(new BigDecimal("50.00"))
                .aprobado(false)
                .build();
        when(intentos.save(any(IntentoEntity.class))).thenReturn(guardado);
    }

    // --- helpers ---
    private Intento intento() {
        return Intento.builder()
                .usuarioId(USUARIO_ID)
                .actividadId(ACTIVIDAD_ID)
                .modo(ModoIntento.PRACTICA)
                .puntaje(new BigDecimal("50.00"))
                .aprobado(false)
                .respuestas(List.of(respuesta(PREGUNTA_UNO_ID, true), respuesta(PREGUNTA_DOS_ID, false)))
                .build();
    }

    private Respuesta respuesta(UUID preguntaId, boolean correcta) {
        return Respuesta.builder()
                .preguntaId(preguntaId)
                .elegida((short) 0)
                .correcta(correcta)
                .confianza(Confianza.SABIA)
                .milisegundos(1000)
                .build();
    }

    // --- assert ---
    @SuppressWarnings("unchecked")
    private void thenRespuestasWereSavedWithIntentoId() {
        ArgumentCaptor<List<RespuestaEntity>> captor = ArgumentCaptor.forClass(List.class);
        verify(respuestas).saveAll(captor.capture());
        assertThat(captor.getValue())
                .extracting(RespuestaEntity::getIntentoId)
                .containsOnly(INTENTO_ID);
        assertThat(captor.getValue())
                .extracting(RespuestaEntity::getPreguntaId)
                .containsExactly(PREGUNTA_UNO_ID, PREGUNTA_DOS_ID);
    }
}
