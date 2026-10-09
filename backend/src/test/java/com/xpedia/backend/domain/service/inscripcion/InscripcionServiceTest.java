package com.xpedia.backend.domain.service.inscripcion;

import com.xpedia.backend.domain.exception.BusinessRuleException;
import com.xpedia.backend.domain.exception.CredencialesInvalidasException;
import com.xpedia.backend.domain.exception.DuplicateResourceException;
import com.xpedia.backend.domain.exception.ResourceNotFoundException;
import com.xpedia.backend.domain.model.enums.ObjetivoRuta;
import com.xpedia.backend.domain.model.hito.Hito;
import com.xpedia.backend.domain.model.inscripcion.Inscripcion;
import com.xpedia.backend.domain.model.inscripcion.ProgresoNodo;
import com.xpedia.backend.domain.model.inscripcion.RecorridoPersonal;
import com.xpedia.backend.domain.model.nodo.Nodo;
import com.xpedia.backend.domain.model.ruta.Ruta;
import com.xpedia.backend.domain.repository.inscripcion.InscripcionRepository;
import com.xpedia.backend.domain.repository.inscripcion.ProgresoNodoRepository;
import com.xpedia.backend.domain.service.hito.HitoService;
import com.xpedia.backend.domain.service.nodo.NodoService;
import com.xpedia.backend.domain.service.ruta.RutaService;
import com.xpedia.backend.domain.service.usuario.UsuarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.xpedia.backend.support.InscripcionTestData.FECHA;
import static com.xpedia.backend.support.InscripcionTestData.HITO_ID;
import static com.xpedia.backend.support.InscripcionTestData.INSCRIPCION_ID;
import static com.xpedia.backend.support.InscripcionTestData.META;
import static com.xpedia.backend.support.InscripcionTestData.NODO_ID;
import static com.xpedia.backend.support.InscripcionTestData.RUTA_ID;
import static com.xpedia.backend.support.InscripcionTestData.SEGUNDO_NODO_ID;
import static com.xpedia.backend.support.InscripcionTestData.SLUG;
import static com.xpedia.backend.support.InscripcionTestData.USUARIO_ID;
import static com.xpedia.backend.support.InscripcionTestData.hito;
import static com.xpedia.backend.support.InscripcionTestData.inscripcion;
import static com.xpedia.backend.support.InscripcionTestData.nodo;
import static com.xpedia.backend.support.InscripcionTestData.recorrido;
import static com.xpedia.backend.support.InscripcionTestData.ruta;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InscripcionServiceTest {

    private static final UUID SEGUNDO_HITO_ID = UUID.fromString("00000000-0000-0000-0000-000000000402");

    private static final UUID TEMA_ID = UUID.fromString("00000000-0000-0000-0000-000000000504");

    @Mock
    private InscripcionRepository inscripcionRepository;
    @Mock
    private ProgresoNodoRepository progresoNodoRepository;
    @Mock
    private UsuarioService usuarioService;
    @Mock
    private RutaService rutaService;
    @Mock
    private HitoService hitoService;
    @Mock
    private NodoService nodoService;

    @InjectMocks
    private InscripcionService inscripcionService;

    @Test
    @DisplayName("Crea inscripción y un progreso por nodo con la disponibilidad y el orden del catálogo")
    void crearShouldInitializeEnrollmentAndAllVisibleProgress() {
        givenRutaAndContent();
        givenSaveSucceeds();

        RecorridoPersonal result = crear();

        thenEnrollmentAndProgressWereSaved(result);
    }

    @Test
    @DisplayName("No consulta rutas ni repositorios cuando el usuario no está activo")
    void crearShouldStopWhenUsuarioIsNotActive() {
        givenCrearShouldStopWhenUsuarioIsNotActive();

        thenCrearShouldStopWhenUsuarioIsNotActive();
    }

    @Test
    @DisplayName("No consulta inscripciones de una ruta que no es visible")
    void crearShouldStopWhenRutaIsNotVisible() {
        givenCrearShouldStopWhenRutaIsNotVisible();

        thenCrearShouldStopWhenRutaIsNotVisible();
    }

    @Test
    @DisplayName("Rechaza una inscripción abierta antes de cargar contenido")
    void crearShouldStopWhenOpenEnrollmentExists() {
        givenCrearShouldStopWhenOpenEnrollmentExists();
        givenCrearShouldStopWhenOpenEnrollmentExistsStub3();

        thenCrearShouldStopWhenOpenEnrollmentExists();
    }

    @Test
    @DisplayName("Rechaza rutas sin hitos sin escribir inscripción ni progreso")
    void crearShouldRejectRutaWithoutHitos() {
        givenContent(List.of(), List.of(nodo(NODO_ID, HITO_ID, List.of())));

        thenCrearShouldRejectRutaWithoutHitos();
    }

    @Test
    @DisplayName("Rechaza rutas sin nodos sin escribir inscripción ni progreso")
    void crearShouldRejectRutaWithoutNodos() {
        givenContent(List.of(hito(HITO_ID)), List.of());

        thenCrearShouldRejectRutaWithoutNodos();
    }

    @Test
    @DisplayName("Un tema suelto no alcanza para iniciar un hito del recorrido")
    void crearShouldRejectRutaWithOnlyStandaloneTopics() {
        givenContent(List.of(hito(HITO_ID)), List.of(nodo(TEMA_ID, null, List.of())));

        thenCrearShouldRejectRutaWithOnlyStandaloneTopics();
    }

    @Test
    @DisplayName("Rechaza un recorrido sin nodos disponibles asociados a hitos")
    void crearShouldRejectRutaWhenAllHitoNodesAreBlocked() {
        givenContent(List.of(hito(HITO_ID)), List.of(nodo(NODO_ID, HITO_ID, List.of(TEMA_ID))));

        thenCrearShouldRejectRutaWhenAllHitoNodesAreBlocked();
    }

    @Test
    @DisplayName("Selecciona el primer hito que contiene un nodo disponible")
    void crearShouldChooseFirstHitoWithAvailableNode() {
        givenContent(List.of(hito(HITO_ID), hito(SEGUNDO_HITO_ID)),
                List.of(nodo(NODO_ID, HITO_ID, List.of(SEGUNDO_NODO_ID)),
                        nodo(SEGUNDO_NODO_ID, SEGUNDO_HITO_ID, List.of())));
        givenSaveReturnsInput();

        RecorridoPersonal result = crear();

        thenCrearShouldChooseFirstHitoWithAvailableNode(result);
    }

    @Test
    @DisplayName("Sin duración conserva una llegada desconocida")
    void crearShouldKeepUnknownEstimateWhenHoursMissing() {
        givenRutaAndContent();
        Ruta ruta = ruta();
        ruta.setHorasEstimadas(null);
        givenCrearShouldKeepUnknownEstimateWhenHoursMissing(ruta);
        givenSaveReturnsInput();

        RecorridoPersonal result = crear();

        thenCrearShouldKeepUnknownEstimateWhenHoursMissing(result);
    }

    @Test
    @DisplayName("No escribe datos cuando la duración publicada es inválida")
    void crearShouldNotSaveWhenDurationInvalid() {
        givenRutaAndContent();
        Ruta ruta = ruta();
        ruta.setHorasEstimadas(BigDecimal.ZERO);
        givenCrearShouldNotSaveWhenDurationInvalid(ruta);

        thenCrearShouldNotSaveWhenDurationInvalid();
    }

    @Test
    @DisplayName("Propaga un fallo de guardado sin intentar escribir progresos")
    void crearShouldStopWhenEnrollmentSaveFails() {
        givenRutaAndContent();
        givenCrearShouldStopWhenEnrollmentSaveFails();

        thenCrearShouldStopWhenEnrollmentSaveFails();
    }

    @Test
    @DisplayName("Propaga un fallo al guardar los progresos")
    void crearShouldPropagateProgressSaveFailure() {
        givenRutaAndContent();
        givenSaveSucceeds();
        givenCrearShouldPropagateProgressSaveFailure();

        thenCrearShouldPropagateProgressSaveFailure();
    }

    @Test
    @DisplayName("Obtiene la inscripción activa con sus progresos sin compartir datos de otra persona")
    void obtenerActualShouldReturnOwnEnrollmentAndProgress() {
        givenActualEnrollment();

        RecorridoPersonal result = obtenerActual();

        thenObtenerActualShouldReturnOwnEnrollmentAndProgress(result);
    }

    @Test
    @DisplayName("Sin inscripción activa devuelve recurso no encontrado y no consulta progreso")
    void obtenerActualShouldRejectMissingEnrollment() {
        givenObtenerActualShouldRejectMissingEnrollment();

        thenObtenerActualShouldRejectMissingEnrollment();
    }

    @Test
    @DisplayName("No revela una inscripción cuando el usuario dejó de estar activo")
    void obtenerActualShouldStopWhenUsuarioIsNotActive() {
        givenObtenerActualShouldStopWhenUsuarioIsNotActive();

        thenObtenerActualShouldStopWhenUsuarioIsNotActive();
    }

    @Test
    @DisplayName("No consulta progresos si la ruta de la inscripción fue ocultada")
    void obtenerActualShouldStopWhenRutaWasHidden() {
        givenObtenerActualShouldStopWhenRutaWasHidden();
        givenObtenerActualShouldStopWhenRutaWasHiddenStub11();

        thenObtenerActualShouldStopWhenRutaWasHidden();
    }

    // --- arrange ---
    private void givenRutaAndContent() {
        givenContent(List.of(hito(HITO_ID), hito(SEGUNDO_HITO_ID)),
                List.of(nodo(NODO_ID, HITO_ID, List.of()),
                        nodo(SEGUNDO_NODO_ID, SEGUNDO_HITO_ID, List.of(NODO_ID)),
                        nodo(TEMA_ID, null, List.of())));
    }

    private void givenContent(List<Hito> hitos, List<Nodo> nodos) {
        when(rutaService.obtener(RUTA_ID)).thenReturn(ruta());
        when(hitoService.listar(RUTA_ID)).thenReturn(hitos);
        when(nodoService.listar(RUTA_ID, null)).thenReturn(nodos);
    }

    private void givenSaveSucceeds() {
        when(inscripcionRepository.save(any())).thenReturn(inscripcion());
    }

    private void givenSaveReturnsInput() {
        when(inscripcionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    private void givenActualEnrollment() {
        when(inscripcionRepository.findActualByUsuarioId(USUARIO_ID)).thenReturn(Optional.of(inscripcion()));
        when(rutaService.obtener(RUTA_ID)).thenReturn(ruta());
        when(progresoNodoRepository.findByInscripcionId(INSCRIPCION_ID)).thenReturn(recorrido().progreso());
    }

    private void givenCrearShouldStopWhenUsuarioIsNotActive() {
        when(usuarioService.obtenerActivo(USUARIO_ID)).thenThrow(new CredencialesInvalidasException());
    }

    private void givenCrearShouldStopWhenRutaIsNotVisible() {
        when(rutaService.obtener(RUTA_ID)).thenThrow(new ResourceNotFoundException("ruta", "id", RUTA_ID));
    }

    private void givenCrearShouldStopWhenOpenEnrollmentExists() {
        when(rutaService.obtener(RUTA_ID)).thenReturn(ruta());
    }

    private void givenCrearShouldStopWhenOpenEnrollmentExistsStub3() {
        when(inscripcionRepository.existsAbiertaByUsuarioIdAndRutaId(USUARIO_ID, RUTA_ID)).thenReturn(true);
    }

    private void givenCrearShouldKeepUnknownEstimateWhenHoursMissing(Ruta ruta) {
        when(rutaService.obtener(RUTA_ID)).thenReturn(ruta);
    }

    private void givenCrearShouldNotSaveWhenDurationInvalid(Ruta ruta) {
        when(rutaService.obtener(RUTA_ID)).thenReturn(ruta);
    }

    private void givenCrearShouldStopWhenEnrollmentSaveFails() {
        when(inscripcionRepository.save(any())).thenThrow(new IllegalStateException("fallo"));
    }

    private void givenCrearShouldPropagateProgressSaveFailure() {
        doThrow(new IllegalStateException("fallo")).when(progresoNodoRepository).saveAll(any());
    }

    private void givenObtenerActualShouldRejectMissingEnrollment() {
        when(inscripcionRepository.findActualByUsuarioId(USUARIO_ID)).thenReturn(Optional.empty());
    }

    private void givenObtenerActualShouldStopWhenUsuarioIsNotActive() {
        when(usuarioService.obtenerActivo(USUARIO_ID)).thenThrow(new CredencialesInvalidasException());
    }

    private void givenObtenerActualShouldStopWhenRutaWasHidden() {
        when(inscripcionRepository.findActualByUsuarioId(USUARIO_ID)).thenReturn(Optional.of(inscripcion()));
    }

    private void givenObtenerActualShouldStopWhenRutaWasHiddenStub11() {
        when(rutaService.obtener(RUTA_ID)).thenThrow(new ResourceNotFoundException("ruta", "id", RUTA_ID));
    }

    // --- act ---
    private RecorridoPersonal crear() {
        return inscripcionService.crear(
                USUARIO_ID, RUTA_ID, ObjetivoRuta.CAMBIAR, "  " + META + "  ", (short) 20, FECHA);
    }

    private RecorridoPersonal obtenerActual() {
        return inscripcionService.obtenerActual(USUARIO_ID);
    }

    // --- assert ---
    private void thenNothingWasSaved() {
        verify(inscripcionRepository, never()).save(any());
        verifyNoInteractions(progresoNodoRepository);
    }

    private void thenEnrollmentAndProgressWereSaved(RecorridoPersonal result) {
        ArgumentCaptor<Inscripcion> inscriptionCaptor = ArgumentCaptor.forClass(Inscripcion.class);
        verify(inscripcionRepository).save(inscriptionCaptor.capture());
        Inscripcion saved = inscriptionCaptor.getValue();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUsuarioId()).isEqualTo(USUARIO_ID);
        assertThat(saved.getRutaId()).isEqualTo(RUTA_ID);
        assertThat(saved.getObjetivo()).isEqualTo(ObjetivoRuta.CAMBIAR);
        assertThat(saved.getMetaPersonal()).isEqualTo(META);
        assertThat(saved.getRitmoMin()).isEqualTo((short) 20);
        assertThat(saved.getFechaLlegadaEstimada()).isEqualTo(FECHA.toLocalDate().plusDays(300));
        assertThat(saved.getEstado()).isEqualTo("ACTIVA");
        assertThat(saved.getHitoActualId()).isEqualTo(HITO_ID);
        assertThat(saved.getIniciadaEn()).isEqualTo(FECHA);
        assertThat(saved.getCreadoEn()).isEqualTo(FECHA);
        assertThat(saved.getActualizadoEn()).isEqualTo(FECHA);
        assertThat(result.rutaSlug()).isEqualTo(SLUG);
        List<ProgresoNodo> progreso = result.progreso();
        assertThat(progreso).extracting(ProgresoNodo::getNodoId).containsExactly(NODO_ID, SEGUNDO_NODO_ID, TEMA_ID);
        assertThat(progreso).extracting(ProgresoNodo::getEstado)
                .containsExactly("DISPONIBLE", "BLOQUEADO", "DISPONIBLE");
        assertThat(progreso).allSatisfy(p -> {
            assertThat(p.getInscripcionId()).isEqualTo(INSCRIPCION_ID);
            assertThat(p.getDominio()).isZero();
            assertThat(p.getNivel()).isZero();
            assertThat(p.getCantidadFallos()).isZero();
            assertThat(p.getCreadoEn()).isEqualTo(FECHA);
            assertThat(p.getActualizadoEn()).isEqualTo(FECHA);
        });
        var order = inOrder(usuarioService, rutaService, inscripcionRepository, progresoNodoRepository);
        order.verify(usuarioService).obtenerActivo(USUARIO_ID);
        order.verify(rutaService).obtener(RUTA_ID);
        order.verify(inscripcionRepository).existsAbiertaByUsuarioIdAndRutaId(USUARIO_ID, RUTA_ID);
        order.verify(inscripcionRepository).save(any());
        order.verify(progresoNodoRepository).saveAll(progreso);
    }

    private void thenCrearShouldStopWhenUsuarioIsNotActive() {
        assertThatThrownBy(this::crear).isInstanceOf(CredencialesInvalidasException.class);

        verifyNoInteractions(rutaService, inscripcionRepository, hitoService, nodoService, progresoNodoRepository);
    }

    private void thenCrearShouldStopWhenRutaIsNotVisible() {
        assertThatThrownBy(this::crear).isInstanceOf(ResourceNotFoundException.class);

        verifyNoInteractions(inscripcionRepository, hitoService, nodoService, progresoNodoRepository);
    }

    private void thenCrearShouldStopWhenOpenEnrollmentExists() {
        assertThatThrownBy(this::crear).isInstanceOf(DuplicateResourceException.class);

        verifyNoInteractions(hitoService, nodoService, progresoNodoRepository);
    }

    private void thenCrearShouldRejectRutaWithoutHitos() {
        assertThatThrownBy(this::crear).isInstanceOf(BusinessRuleException.class);

        thenNothingWasSaved();
    }

    private void thenCrearShouldRejectRutaWithoutNodos() {
        assertThatThrownBy(this::crear).isInstanceOf(BusinessRuleException.class);

        thenNothingWasSaved();
    }

    private void thenCrearShouldRejectRutaWithOnlyStandaloneTopics() {
        assertThatThrownBy(this::crear).isInstanceOf(BusinessRuleException.class);

        thenNothingWasSaved();
    }

    private void thenCrearShouldRejectRutaWhenAllHitoNodesAreBlocked() {
        assertThatThrownBy(this::crear).isInstanceOf(BusinessRuleException.class);

        thenNothingWasSaved();
    }

    private void thenCrearShouldChooseFirstHitoWithAvailableNode(RecorridoPersonal result) {
        assertThat(result.inscripcion().getHitoActualId()).isEqualTo(SEGUNDO_HITO_ID);
    }

    private void thenCrearShouldKeepUnknownEstimateWhenHoursMissing(RecorridoPersonal result) {
        assertThat(result.inscripcion().getFechaLlegadaEstimada()).isNull();
    }

    private void thenCrearShouldNotSaveWhenDurationInvalid() {
        assertThatThrownBy(this::crear).isInstanceOf(BusinessRuleException.class);

        thenNothingWasSaved();
    }

    private void thenCrearShouldStopWhenEnrollmentSaveFails() {
        assertThatThrownBy(this::crear).isInstanceOf(IllegalStateException.class);

        verifyNoInteractions(progresoNodoRepository);
    }

    private void thenCrearShouldPropagateProgressSaveFailure() {
        assertThatThrownBy(this::crear).isInstanceOf(IllegalStateException.class);

        verify(progresoNodoRepository).saveAll(any());
    }

    private void thenObtenerActualShouldReturnOwnEnrollmentAndProgress(RecorridoPersonal result) {
        assertThat(result.inscripcion().getId()).isEqualTo(INSCRIPCION_ID);
        assertThat(result.rutaSlug()).isEqualTo(SLUG);
        assertThat(result.progreso()).hasSize(1);
        assertThat(result.progreso().getFirst().getNodoId()).isEqualTo(NODO_ID);
        assertThat(result.progreso().getFirst().getEstado()).isEqualTo("EN_CURSO");
        assertThat(result.progreso().getFirst().getDominio()).isEqualByComparingTo("0.65");
        verify(inscripcionRepository).findActualByUsuarioId(USUARIO_ID);
    }

    private void thenObtenerActualShouldRejectMissingEnrollment() {
        assertThatThrownBy(this::obtenerActual).isInstanceOf(ResourceNotFoundException.class);

        verifyNoInteractions(rutaService, progresoNodoRepository);
    }

    private void thenObtenerActualShouldStopWhenUsuarioIsNotActive() {
        assertThatThrownBy(this::obtenerActual).isInstanceOf(CredencialesInvalidasException.class);

        verifyNoInteractions(inscripcionRepository, rutaService, progresoNodoRepository);
    }

    private void thenObtenerActualShouldStopWhenRutaWasHidden() {
        assertThatThrownBy(this::obtenerActual).isInstanceOf(ResourceNotFoundException.class);

        verifyNoInteractions(progresoNodoRepository);
    }
}
