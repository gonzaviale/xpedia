package com.xpedia.backend.infrastructure.repository.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ProgresoNodoId implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID inscripcionId;

    private UUID nodoId;
}

