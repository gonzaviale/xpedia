package com.xpedia.backend.infrastructure.config.useCase;
import com.xpedia.backend.domain.mapper.reto.RetoMapper;
import com.xpedia.backend.domain.service.reto.RetoService;
import com.xpedia.backend.domain.useCase.reto.*;
import org.springframework.context.annotation.*;
@Configuration
public class RetoUseCaseConfig {
    @Bean public RetoMapper retoMapper() { return new RetoMapper(); }
    @Bean public ListarRetosUseCase listarRetosUseCase(RetoService service, RetoMapper mapper) { return new ListarRetosUseCase(service, mapper); }
    @Bean public ObtenerRetoUseCase obtenerRetoUseCase(RetoService service, RetoMapper mapper) { return new ObtenerRetoUseCase(service, mapper); }
}
