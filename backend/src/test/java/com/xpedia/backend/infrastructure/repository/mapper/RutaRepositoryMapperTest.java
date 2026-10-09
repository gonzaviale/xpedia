package com.xpedia.backend.infrastructure.repository.mapper;

import com.xpedia.backend.infrastructure.repository.entity.RutaEntity;

import org.junit.jupiter.api.Test;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static com.xpedia.backend.support.ContractData.*;

class RutaRepositoryMapperTest {
    @Test void conservaTodosLosCamposDelModelo() { var entity = full(RutaEntity.class); assertThat(new RutaRepositoryMapper().toDomain(entity)).usingRecursiveComparison().isEqualTo(entity); }
    @Test void conservaNulosSinInventarValores() { var entity = new RutaEntity(); assertThat(new RutaRepositoryMapper().toDomain(entity)).usingRecursiveComparison().isEqualTo(entity); }
}
