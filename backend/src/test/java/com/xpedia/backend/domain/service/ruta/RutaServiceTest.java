package com.xpedia.backend.domain.service.ruta;

import com.xpedia.backend.domain.model.ruta.Ruta;
import com.xpedia.backend.domain.repository.ruta.RutaRepository;
import com.xpedia.backend.domain.service.ruta.RutaService;
import com.xpedia.backend.domain.exception.ResourceNotFoundException;
import org.springframework.data.domain.*;

import org.junit.jupiter.api.Test;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static com.xpedia.backend.support.ContractData.*;

class RutaServiceTest {
    private final RutaRepository repository = mock(RutaRepository.class);
    private final RutaService service = new RutaService(repository);
    @Test void obtenerConservaElModeloDelRepositorio() {
        var ruta = full(Ruta.class);
        when(repository.findPublicadaGlobalById(ruta.getId())).thenReturn(Optional.of(ruta));
        assertThat(service.obtener(ruta.getId())).isSameAs(ruta);
    }
    @Test void obtenerAusenteDevuelveErrorDeDominio() {
        var id = UUID.randomUUID();
        when(repository.findPublicadaGlobalById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.obtener(id)).isInstanceOf(ResourceNotFoundException.class);
    }
    @Test void listarConservaFiltrosPaginaYResultado() {
        var page = PageRequest.of(2, 5);
        var result = new PageImpl<Ruta>(List.of(full(Ruta.class)), page, 20);
        var tipo = com.xpedia.backend.domain.model.enums.TipoRuta.CAMBIO_RUBRO;
        var objetivo = com.xpedia.backend.domain.model.enums.ObjetivoRuta.CAMBIAR;
        when(repository.findPublicadasGlobales(tipo, objetivo, page)).thenReturn(result);
        assertThat(service.listar(tipo, objetivo, page)).isSameAs(result);
        verify(repository).findPublicadasGlobales(tipo, objetivo, page);
    }
    @Test void listarSinFiltrosConservaPaginaVacia() {
        var page = PageRequest.of(0, 20);
        when(repository.findPublicadasGlobales(null, null, page)).thenReturn(Page.empty(page));
        assertThat(service.listar(null, null, page)).isEmpty();
    }
}
