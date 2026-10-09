package com.xpedia.backend.domain.repository.rubrica;
import com.xpedia.backend.domain.model.rubrica.Rubrica;
import java.util.*;
public interface RubricaRepository {
    Map<UUID, Rubrica> findGlobalesByIds(List<UUID> ids);
}
