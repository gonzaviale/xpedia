package com.xpedia.backend.infrastructure.repository.mapper;

import com.xpedia.backend.infrastructure.repository.entity.HitoEntity;

import org.junit.jupiter.api.Test;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static com.xpedia.backend.support.ContractData.*;

class HitoRepositoryMapperTest {
    @Test void conservaTodosLosCamposDelModelo() { var entity = full(HitoEntity.class); assertThat(new HitoRepositoryMapper().toDomain(entity)).usingRecursiveComparison().isEqualTo(entity); }
    @Test void conservaNulosSinInventarValores() { var entity = new HitoEntity(); assertThat(new HitoRepositoryMapper().toDomain(entity)).usingRecursiveComparison().isEqualTo(entity); }
}
