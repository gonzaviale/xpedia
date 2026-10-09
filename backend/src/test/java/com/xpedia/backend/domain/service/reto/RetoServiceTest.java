package com.xpedia.backend.domain.service.reto;

import com.xpedia.backend.domain.exception.ResourceNotFoundException;
import com.xpedia.backend.domain.repository.nodo.NodoRepository;
import com.xpedia.backend.domain.repository.reto.RetoRepository;
import com.xpedia.backend.domain.service.ruta.RutaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static com.xpedia.backend.support.RetoTestData.reto;

class RetoServiceTest {
    private final RutaService rutas = mock(RutaService.class);
    private final NodoRepository nodos = mock(NodoRepository.class);
    private final RetoRepository repository = mock(RetoRepository.class);
    private final RetoService service = new RetoService(rutas, nodos, repository);
    private final UUID ruta = UUID.randomUUID(), nodo = UUID.randomUUID(), id = UUID.randomUUID();
    private Object invoke(boolean detalle) { return detalle ? service.obtener(ruta, nodo, id) : service.listar(ruta, nodo); }

    @ParameterizedTest @ValueSource(booleans={false,true})
    void validaRutaYNodoAntesDeConsultar(boolean detalle) {
        var item = reto(); when(nodos.existsVisibleByIdAndRutaId(nodo, ruta)).thenReturn(true);
        if (detalle) when(repository.findAprobadoById(ruta,nodo,id)).thenReturn(Optional.of(item));
        else when(repository.findAprobadosByNodo(ruta,nodo)).thenReturn(List.of(item));
        assertThat(invoke(detalle)).isEqualTo(detalle ? item : List.of(item));
        var order = inOrder(rutas,nodos,repository); order.verify(rutas).obtener(ruta);
        order.verify(nodos).existsVisibleByIdAndRutaId(nodo,ruta);
        if (detalle) order.verify(repository).findAprobadoById(ruta,nodo,id);
        else order.verify(repository).findAprobadosByNodo(ruta,nodo);
    }
    @ParameterizedTest @ValueSource(booleans={false,true})
    void rutaOcultaNoConsultaNodoNiRetos(boolean detalle) {
        when(rutas.obtener(ruta)).thenThrow(new ResourceNotFoundException("Oculta"));
        assertThatThrownBy(() -> invoke(detalle)).isInstanceOf(ResourceNotFoundException.class);
        verifyNoInteractions(nodos,repository);
    }
    @ParameterizedTest @ValueSource(booleans={false,true})
    void nodoAjenoNoConsultaRetos(boolean detalle) {
        assertThatThrownBy(() -> invoke(detalle)).isInstanceOf(ResourceNotFoundException.class);
        verifyNoInteractions(repository);
    }
    @Test void detalleOcultoOAUSenteDevuelveErrorDeDominio() {
        when(nodos.existsVisibleByIdAndRutaId(nodo,ruta)).thenReturn(true);
        when(repository.findAprobadoById(ruta,nodo,id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.obtener(ruta,nodo,id)).isInstanceOf(ResourceNotFoundException.class);
    }
    @Test void nodoSinRetosDevuelveListaVacia() {
        when(nodos.existsVisibleByIdAndRutaId(nodo,ruta)).thenReturn(true);
        when(repository.findAprobadosByNodo(ruta,nodo)).thenReturn(List.of());
        assertThat(service.listar(ruta,nodo)).isEmpty();
    }
}
