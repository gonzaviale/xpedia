package com.xpedia.backend.infrastructure.repository.jpaRepository.implementation;
import com.xpedia.backend.domain.repository.rubrica.RubricaRepository;
import com.xpedia.backend.infrastructure.repository.entity.ActividadEntity;
import com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces.IActividadJpaRepository;
import com.xpedia.backend.infrastructure.repository.mapper.RetoRepositoryMapper;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static com.xpedia.backend.support.ContractData.full;
import static com.xpedia.backend.support.RetoTestData.rubrica;
class RetoRepositoryImplTest {
    private final IActividadJpaRepository actividades = mock(IActividadJpaRepository.class);
    private final RubricaRepository rubricas = mock(RubricaRepository.class);
    private final RetoRepositoryImpl repository = new RetoRepositoryImpl(actividades,rubricas,new RetoRepositoryMapper());
    private final UUID ruta = UUID.randomUUID(), nodo = UUID.randomUUID(), id = UUID.randomUUID();
    private ActividadEntity actividad() {
        var a = full(ActividadEntity.class); a.setTipo("ENSAYO"); a.setContenido(Map.of("consigna","Texto")); return a;
    }
    @Test void ausenciaEnListaYDetalleNoConsultaRubricas() {
        when(actividades.findRetosAprobados(ruta,nodo)).thenReturn(List.of());
        when(actividades.findRetoAprobadoById(ruta,nodo,id)).thenReturn(Optional.empty());
        assertThat(repository.findAprobadosByNodo(ruta,nodo)).isEmpty(); assertThat(repository.findAprobadoById(ruta,nodo,id)).isEmpty();
        verifyNoInteractions(rubricas);
    }
    @Test void agrupaRubricasReutilizadasSinDuplicarConsultaYMantieneOrden() {
        var a = actividad(); var b = actividad(); b.setId(UUID.randomUUID()); var rubric = rubrica();
        a.setRubricaId(rubric.id()); b.setRubricaId(rubric.id());
        when(actividades.findRetosAprobados(ruta,nodo)).thenReturn(List.of(a,b));
        when(rubricas.findGlobalesByIds(List.of(rubric.id()))).thenReturn(Map.of(rubric.id(),rubric));
        assertThat(repository.findAprobadosByNodo(ruta,nodo)).extracting("id").containsExactly(a.getId(),b.getId());
        verify(rubricas,times(1)).findGlobalesByIds(List.of(rubric.id()));
    }
    @Test void detalleMapeaRubricaYOmiteReferenciaQueDesaparecio() {
        var a = actividad(); var rubric = rubrica(); a.setRubricaId(rubric.id());
        when(actividades.findRetoAprobadoById(ruta,nodo,id)).thenReturn(Optional.of(a));
        when(rubricas.findGlobalesByIds(List.of(rubric.id()))).thenReturn(Map.of(rubric.id(),rubric),Map.of());
        assertThat(repository.findAprobadoById(ruta,nodo,id).orElseThrow().getRubrica()).isEqualTo(rubric);
        assertThat(repository.findAprobadoById(ruta,nodo,id)).isEmpty();
    }
}
