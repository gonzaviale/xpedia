package com.xpedia.backend.infrastructure.repository.mapper;

import com.xpedia.backend.infrastructure.repository.entity.ActividadEntity;
import com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces.IActividadFuenteJpaRepository.FuenteVisible;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static com.xpedia.backend.support.ContractData.full;

class MicroleccionRepositoryMapperTest {
    @Test void conservaCamposDelMaterialYSoloLasFuentesRecibidas() {
        var entity = full(ActividadEntity.class);
        var result = new MicroleccionRepositoryMapper().toDomain(entity, List.of());
        assertThat(result).usingRecursiveComparison().ignoringFields("fuentes").isEqualTo(entity);
        assertThat(result.getFuentes()).isEmpty();
    }
    @Test void fuenteConservaLicenciaPermisosYNulos() {
        var input = mock(FuenteVisible.class); var id = java.util.UUID.randomUUID();
        when(input.getId()).thenReturn(id); when(input.getTitulo()).thenReturn("Fuente");
        when(input.getLicencia()).thenReturn("Solo pruebas"); when(input.getUso()).thenReturn("SOLO_ENLACE");
        when(input.getPermiteUsoComercial()).thenReturn(false);
        var result = new MicroleccionRepositoryMapper().toFuente(input);
        assertThat(result.id()).isEqualTo(id); assertThat(result.titulo()).isEqualTo("Fuente");
        assertThat(result.licencia()).isEqualTo("Solo pruebas"); assertThat(result.uso()).isEqualTo("SOLO_ENLACE");
        assertThat(result.permiteUsoComercial()).isFalse(); assertThat(result.url()).isNull(); assertThat(result.ubicacion()).isNull();
    }
}
