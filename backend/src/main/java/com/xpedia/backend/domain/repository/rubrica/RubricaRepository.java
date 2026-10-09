package com.xpedia.backend.domain.repository.rubrica;

import com.xpedia.backend.domain.model.rubrica.Rubrica;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface RubricaRepository {

    Map<UUID, Rubrica> findGlobalesByIds(List<UUID> ids);
}
