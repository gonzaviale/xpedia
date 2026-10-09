package com.xpedia.backend.domain.service.hito;

import com.xpedia.backend.domain.model.hito.Hito;
import com.xpedia.backend.domain.repository.hito.HitoRepository;
import com.xpedia.backend.domain.service.hito.HitoService;
import com.xpedia.backend.domain.exception.ResourceNotFoundException;
import org.springframework.data.domain.*;
import com.xpedia.backend.domain.service.ruta.RutaService;

import org.junit.jupiter.api.Test;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static com.xpedia.backend.support.ContractData.*;

class HitoServiceTest {
    private final HitoRepository repository = mock(HitoRepository.class);
    private final RutaService rutas = mock(RutaService.class);
    private final HitoService service = new HitoService(repository, rutas);
    @Test void listarValidaRutaAntesDeConsultar() {
        var id = UUID.randomUUID(); var items = List.of(full(Hito.class));
        when(repository.findByRutaId(id)).thenReturn(items);
        assertThat(service.listar(id)).isSameAs(items);
        var order = inOrder(rutas, repository); order.verify(rutas).obtener(id); order.verify(repository).findByRutaId(id);
    }
    @Test void rutaOcultaNoConsultaHitos() {
        var id = UUID.randomUUID(); when(rutas.obtener(id)).thenThrow(new ResourceNotFoundException("ruta", "id", id));
        assertThatThrownBy(() -> service.listar(id)).isInstanceOf(ResourceNotFoundException.class);
        verifyNoInteractions(repository);
    }
    @Test void aceptaHitoDeLaRuta() {
        var id = UUID.randomUUID(); var ruta = UUID.randomUUID();
        when(repository.existsByIdAndRutaId(id, ruta)).thenReturn(true);
        service.validarPertenencia(ruta, id); verify(repository).existsByIdAndRutaId(id, ruta);
    }
    @Test void rechazaHitoAjeno() {
        var id = UUID.randomUUID(); var ruta = UUID.randomUUID();
        assertThatThrownBy(() -> service.validarPertenencia(ruta, id)).isInstanceOf(ResourceNotFoundException.class);
    }
}
