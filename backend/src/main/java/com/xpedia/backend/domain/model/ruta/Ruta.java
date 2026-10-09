package com.xpedia.backend.domain.model.ruta;

import com.xpedia.backend.domain.model.enums.EstadoRuta;
import com.xpedia.backend.domain.model.enums.ObjetivoRuta;
import com.xpedia.backend.domain.model.enums.TipoRuta;
import com.xpedia.backend.domain.model.enums.ValidacionRuta;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ruta {

    private UUID id;

    private UUID organizacionId;

    private String slug;

    private Integer version;

    private String titulo;

    private TipoRuta tipo;

    private ObjetivoRuta objetivo;

    private String meta;

    private String perfilInicial;

    private String pais;

    private BigDecimal horasEstimadas;

    private Short ritmoRecomendadoMin;

    private EstadoRuta estado;

    private ValidacionRuta validacion;

    private UUID revisadaPor;

    private OffsetDateTime revisadaEn;

    private UUID confirmadaPor;

    private OffsetDateTime confirmadaEn;

    private OffsetDateTime creadoEn;

    private OffsetDateTime actualizadoEn;
}
