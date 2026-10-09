package com.xpedia.backend.infrastructure.presentation.controller;

import com.xpedia.backend.domain.useCase.intento.RegistrarIntentoUseCase;
import com.xpedia.backend.infrastructure.presentation.dto.intento.IntentoRequest;
import com.xpedia.backend.infrastructure.presentation.dto.intento.IntentoResponse;
import com.xpedia.backend.infrastructure.presentation.mapper.intento.IntentoPresentationMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/intentos")
@Tag(name = "Intentos", description = "Intentos de actividades y su corrección")
public class IntentoController {

    private final RegistrarIntentoUseCase registrarIntentoUseCase;
    private final IntentoPresentationMapper intentoPresentationMapper;

    @PostMapping
    @Operation(summary = "Registrar el intento de un cuestionario",
            description = "Corrige las respuestas, guarda el intento con su puntaje y devuelve la corrección de "
                    + "cada pregunta. Aprueba con 80 % o más de aciertos. Hay que responder todas las preguntas "
                    + "una sola vez: de lo contrario, 400. Cuestionario oculto o inexistente: 404. "
                    + "Provisorio: el usuario viaja en el body hasta que haya identificación de personas.")
    public ResponseEntity<IntentoResponse> registrar(@Valid @RequestBody IntentoRequest request) {
        IntentoResponse response = intentoPresentationMapper.toResponse(
                registrarIntentoUseCase.execute(intentoPresentationMapper.toRequest(request)));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
