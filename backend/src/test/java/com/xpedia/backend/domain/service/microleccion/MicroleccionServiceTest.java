package com.xpedia.backend.domain.service.microleccion;

import com.xpedia.backend.domain.exception.ResourceNotFoundException;
import com.xpedia.backend.domain.repository.microleccion.MicroleccionRepository;
import com.xpedia.backend.domain.repository.nodo.NodoRepository;
import com.xpedia.backend.domain.service.ruta.RutaService;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static com.xpedia.backend.support.MicroleccionTestData.microleccion;

class MicroleccionServiceTest {
    private final RutaService rutas = mock(RutaService.class);
    private final NodoRepository nodos = mock(NodoRepository.class);
    private final MicroleccionRepository repository = mock(MicroleccionRepository.class);
    private final MicroleccionService service = new MicroleccionService(rutas, nodos, repository);
    private final UUID ruta = UUID.randomUUID(), nodo = UUID.randomUUID();

    @Test void validaVisibilidadAntesDeConsultarMaterial() {
        var result = List.of(microleccion());
        when(nodos.existsVisibleByIdAndRutaId(nodo, ruta)).thenReturn(true);
        when(repository.findAprobadasByNodo(ruta, nodo)).thenReturn(result);
        assertThat(service.listar(ruta, nodo)).isSameAs(result);
        var order = inOrder(rutas, nodos, repository);
        order.verify(rutas).obtener(ruta); order.verify(nodos).existsVisibleByIdAndRutaId(nodo, ruta);
        order.verify(repository).findAprobadasByNodo(ruta, nodo);
    }
    @Test void rutaOcultaNoConsultaNodoNiMaterial() {
        when(rutas.obtener(ruta)).thenThrow(new ResourceNotFoundException("ruta", "id", ruta));
        assertThatThrownBy(() -> service.listar(ruta, nodo)).isInstanceOf(ResourceNotFoundException.class);
        verifyNoInteractions(nodos, repository);
    }
    @Test void nodoAjenoOAUSenteNoConsultaMaterial() {
        assertThatThrownBy(() -> service.listar(ruta, nodo)).isInstanceOf(ResourceNotFoundException.class);
        verifyNoInteractions(repository);
    }
    @Test void nodoValidoSinMaterialDevuelveListaVacia() {
        when(nodos.existsVisibleByIdAndRutaId(nodo, ruta)).thenReturn(true);
        when(repository.findAprobadasByNodo(ruta, nodo)).thenReturn(List.of());
        assertThat(service.listar(ruta, nodo)).isEmpty();
    }
}
