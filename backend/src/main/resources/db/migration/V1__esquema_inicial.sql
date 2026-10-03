-- Xpedia · esquema inicial
-- Detalle y diagrama en docs/modelo-datos.md
-- Convención: organizacion_id NULL = contenido global de Xpedia; con valor = contenido propio de una empresa.

-- =========================================================
-- Identidad y empresas
-- =========================================================

CREATE TABLE usuario (
    id                uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    email             text NOT NULL UNIQUE,
    nombre            text NOT NULL,
    hash_contrasenia  text,
    pais              text,
    creado_en         timestamptz NOT NULL DEFAULT now(),
    actualizado_en    timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE organizacion (
    id              uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre          text NOT NULL,
    dominio         text UNIQUE,
    plan            text NOT NULL DEFAULT 'PRUEBA',
    creado_en       timestamptz NOT NULL DEFAULT now(),
    actualizado_en  timestamptz NOT NULL DEFAULT now()
);

-- =========================================================
-- Competencias
-- =========================================================

CREATE TABLE puesto (
    id               uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    organizacion_id  uuid REFERENCES organizacion (id) ON DELETE CASCADE,
    nombre           text NOT NULL,
    creado_en        timestamptz NOT NULL DEFAULT now(),
    actualizado_en   timestamptz NOT NULL DEFAULT now(),
    UNIQUE NULLS NOT DISTINCT (organizacion_id, nombre)
);

CREATE TABLE miembro_organizacion (
    id               uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    organizacion_id  uuid NOT NULL REFERENCES organizacion (id) ON DELETE CASCADE,
    usuario_id       uuid NOT NULL REFERENCES usuario (id) ON DELETE CASCADE,
    rol              text NOT NULL DEFAULT 'MIEMBRO' CHECK (rol IN ('ADMIN', 'MIEMBRO')),
    puesto_id        uuid REFERENCES puesto (id) ON DELETE SET NULL,
    estado           text NOT NULL DEFAULT 'INVITADO' CHECK (estado IN ('INVITADO', 'ACTIVO', 'BAJA')),
    creado_en        timestamptz NOT NULL DEFAULT now(),
    actualizado_en   timestamptz NOT NULL DEFAULT now(),
    UNIQUE (organizacion_id, usuario_id)
);

CREATE TABLE habilidad (
    id                  uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    organizacion_id     uuid REFERENCES organizacion (id) ON DELETE CASCADE,
    codigo              text NOT NULL,
    nombre              text NOT NULL,
    descripcion         text,
    referencia_externa  text, -- O*NET / ESCO
    creado_en           timestamptz NOT NULL DEFAULT now(),
    actualizado_en      timestamptz NOT NULL DEFAULT now(),
    UNIQUE NULLS NOT DISTINCT (organizacion_id, codigo)
);

CREATE TABLE puesto_habilidad (
    puesto_id       uuid NOT NULL REFERENCES puesto (id) ON DELETE CASCADE,
    habilidad_id    uuid NOT NULL REFERENCES habilidad (id) ON DELETE CASCADE,
    nivel_esperado  smallint NOT NULL CHECK (nivel_esperado BETWEEN 0 AND 3),
    creado_en       timestamptz NOT NULL DEFAULT now(),
    actualizado_en  timestamptz NOT NULL DEFAULT now(),
    PRIMARY KEY (puesto_id, habilidad_id)
);

-- =========================================================
-- Contenido de rutas
-- =========================================================

CREATE TABLE fuente (
    id               uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    organizacion_id  uuid REFERENCES organizacion (id) ON DELETE CASCADE,
    titulo           text NOT NULL,
    url              text,
    licencia         text NOT NULL, -- p. ej. 'CC BY-SA 2.5', 'Documento interno'
    uso              text NOT NULL CHECK (uso IN ('ADAPTABLE', 'SOLO_ENLACE')),
    creado_en        timestamptz NOT NULL DEFAULT now(),
    actualizado_en   timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE ruta (
    id                     uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    organizacion_id        uuid REFERENCES organizacion (id) ON DELETE CASCADE,
    slug                   text NOT NULL,
    version                integer NOT NULL DEFAULT 1,
    titulo                 text NOT NULL,
    tipo                   text NOT NULL CHECK (tipo IN ('TECNICA', 'CAMBIO_RUBRO', 'HABILIDAD_BLANDA')),
    objetivo               text NOT NULL CHECK (objetivo IN ('ARRANCAR', 'CAMBIAR', 'MEJORAR')),
    meta                   text NOT NULL,
    perfil_inicial         text,
    ritmo_recomendado_min  smallint NOT NULL DEFAULT 20 CHECK (ritmo_recomendado_min > 0),
    estado                 text NOT NULL DEFAULT 'BORRADOR' CHECK (estado IN ('BORRADOR', 'PUBLICADA', 'ARCHIVADA')),
    creado_en              timestamptz NOT NULL DEFAULT now(),
    actualizado_en         timestamptz NOT NULL DEFAULT now(),
    UNIQUE NULLS NOT DISTINCT (organizacion_id, slug, version)
);

CREATE TABLE hito (
    id                  uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    ruta_id             uuid NOT NULL REFERENCES ruta (id) ON DELETE CASCADE,
    posicion            smallint NOT NULL,
    titulo              text NOT NULL,
    objetivo            text,
    horas_estimadas     numeric(6, 1),
    es_final            boolean NOT NULL DEFAULT false,
    evidencia_esperada  text,
    creado_en           timestamptz NOT NULL DEFAULT now(),
    actualizado_en      timestamptz NOT NULL DEFAULT now(),
    UNIQUE (ruta_id, posicion)
);

-- Nodo del árbol de habilidades de una ruta
CREATE TABLE nodo (
    id              uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    ruta_id         uuid NOT NULL REFERENCES ruta (id) ON DELETE CASCADE,
    hito_id         uuid NOT NULL REFERENCES hito (id) ON DELETE CASCADE,
    habilidad_id    uuid REFERENCES habilidad (id) ON DELETE SET NULL,
    rama            text NOT NULL, -- p. ej. 'A · Fundamentos'
    codigo          text NOT NULL, -- p. ej. 'A3'
    titulo          text NOT NULL,
    posicion        smallint NOT NULL,
    opcional        boolean NOT NULL DEFAULT false,
    creado_en       timestamptz NOT NULL DEFAULT now(),
    actualizado_en  timestamptz NOT NULL DEFAULT now(),
    UNIQUE (ruta_id, codigo)
);

-- Contenido de la mecánica en JSONB: pasos del ticket, turnos del roleplay, preguntas del diagnóstico, etc.
CREATE TABLE actividad (
    id              uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    ruta_id         uuid NOT NULL REFERENCES ruta (id) ON DELETE CASCADE,
    nodo_id         uuid REFERENCES nodo (id) ON DELETE CASCADE, -- NULL = diagnóstico de la ruta
    tipo            text NOT NULL CHECK (tipo IN ('DIAGNOSTICO', 'LECCION', 'CUESTIONARIO', 'TICKET',
                                                  'ROLEPLAY', 'ENSAYO', 'DESAFIO_REAL', 'LECTURA')),
    titulo          text NOT NULL,
    nivel           smallint NOT NULL DEFAULT 1 CHECK (nivel BETWEEN 0 AND 3),
    contenido       jsonb NOT NULL DEFAULT '{}'::jsonb,
    rubrica         jsonb,
    origen          text NOT NULL CHECK (origen IN ('IA', 'HUMANO')),
    revisado_por    uuid REFERENCES usuario (id) ON DELETE SET NULL,
    revisado_en     timestamptz,
    creado_en       timestamptz NOT NULL DEFAULT now(),
    actualizado_en  timestamptz NOT NULL DEFAULT now(),
    CHECK (nodo_id IS NOT NULL OR tipo = 'DIAGNOSTICO')
);

CREATE TABLE actividad_fuente (
    actividad_id  uuid NOT NULL REFERENCES actividad (id) ON DELETE CASCADE,
    fuente_id     uuid NOT NULL REFERENCES fuente (id) ON DELETE RESTRICT,
    ubicacion     text, -- p. ej. 'pág. 3', 'sección Operadores'
    creado_en     timestamptz NOT NULL DEFAULT now(),
    PRIMARY KEY (actividad_id, fuente_id)
);

CREATE TABLE punto_clave (
    id               uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    organizacion_id  uuid REFERENCES organizacion (id) ON DELETE CASCADE,
    texto            text NOT NULL,
    critico          boolean NOT NULL DEFAULT false,
    fuente_id        uuid REFERENCES fuente (id) ON DELETE RESTRICT,
    ubicacion        text,
    creado_en        timestamptz NOT NULL DEFAULT now(),
    actualizado_en   timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE actividad_punto_clave (
    actividad_id    uuid NOT NULL REFERENCES actividad (id) ON DELETE CASCADE,
    punto_clave_id  uuid NOT NULL REFERENCES punto_clave (id) ON DELETE CASCADE,
    creado_en       timestamptz NOT NULL DEFAULT now(),
    PRIMARY KEY (actividad_id, punto_clave_id)
);

-- =========================================================
-- Progreso del usuario
-- =========================================================

CREATE TABLE inscripcion (
    id               uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id       uuid NOT NULL REFERENCES usuario (id) ON DELETE CASCADE,
    ruta_id          uuid NOT NULL REFERENCES ruta (id) ON DELETE RESTRICT,
    organizacion_id  uuid REFERENCES organizacion (id) ON DELETE SET NULL, -- si la asignó una empresa
    ritmo_min        smallint NOT NULL CHECK (ritmo_min > 0),
    estado           text NOT NULL DEFAULT 'ACTIVA' CHECK (estado IN ('ACTIVA', 'PAUSADA', 'TERMINADA')),
    hito_actual_id   uuid REFERENCES hito (id) ON DELETE SET NULL,
    diagnostico      jsonb,
    iniciada_en      timestamptz NOT NULL DEFAULT now(),
    creado_en        timestamptz NOT NULL DEFAULT now(),
    actualizado_en   timestamptz NOT NULL DEFAULT now()
);

-- Una sola inscripción abierta por usuario y ruta
CREATE UNIQUE INDEX ux_inscripcion_abierta ON inscripcion (usuario_id, ruta_id) WHERE estado <> 'TERMINADA';

CREATE TABLE progreso_nodo (
    inscripcion_id     uuid NOT NULL REFERENCES inscripcion (id) ON DELETE CASCADE,
    nodo_id            uuid NOT NULL REFERENCES nodo (id) ON DELETE CASCADE,
    estado             text NOT NULL DEFAULT 'BLOQUEADO' CHECK (estado IN ('BLOQUEADO', 'SIGUIENTE', 'EN_CURSO',
                                                                           'DOMINADO', 'PROBADO_REAL', 'OXIDADO')),
    nivel              smallint NOT NULL DEFAULT 0 CHECK (nivel BETWEEN 0 AND 3),
    cantidad_fallos    integer NOT NULL DEFAULT 0,
    proximo_repaso_en  timestamptz,
    creado_en          timestamptz NOT NULL DEFAULT now(),
    actualizado_en     timestamptz NOT NULL DEFAULT now(),
    PRIMARY KEY (inscripcion_id, nodo_id)
);

CREATE TABLE intento (
    id              uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id      uuid NOT NULL REFERENCES usuario (id) ON DELETE CASCADE,
    inscripcion_id  uuid REFERENCES inscripcion (id) ON DELETE CASCADE,
    actividad_id    uuid NOT NULL REFERENCES actividad (id) ON DELETE RESTRICT,
    modo            text NOT NULL DEFAULT 'PRACTICA' CHECK (modo IN ('PRACTICA', 'VALIDACION')),
    puntaje         numeric(5, 2),
    aprobado        boolean,
    resultado       jsonb, -- decisiones, curva de enojo, rúbrica, métricas del ensayo, check-in
    audio_clave     text,
    audio_vence_en  timestamptz, -- el audio se borra a los 30 días
    iniciado_en     timestamptz NOT NULL DEFAULT now(),
    terminado_en    timestamptz,
    creado_en       timestamptz NOT NULL DEFAULT now(),
    actualizado_en  timestamptz NOT NULL DEFAULT now(),
    CHECK (audio_clave IS NULL OR audio_vence_en IS NOT NULL)
);

CREATE TABLE intento_punto_clave (
    intento_id      uuid NOT NULL REFERENCES intento (id) ON DELETE CASCADE,
    punto_clave_id  uuid NOT NULL REFERENCES punto_clave (id) ON DELETE CASCADE,
    correcto        boolean NOT NULL,
    seguro          boolean, -- "¿lo sabías o lo adivinaste?"
    creado_en       timestamptz NOT NULL DEFAULT now(),
    PRIMARY KEY (intento_id, punto_clave_id)
);

CREATE TABLE evidencia (
    id              uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id      uuid NOT NULL REFERENCES usuario (id) ON DELETE CASCADE,
    inscripcion_id  uuid REFERENCES inscripcion (id) ON DELETE SET NULL,
    hito_id         uuid REFERENCES hito (id) ON DELETE SET NULL,
    intento_id      uuid REFERENCES intento (id) ON DELETE SET NULL,
    tipo            text NOT NULL CHECK (tipo IN ('RUBRICA', 'PROYECTO', 'MUNDO_REAL', 'CERTIFICADO_EXTERNO')),
    titulo          text NOT NULL,
    descripcion     text,
    url             text,
    verificada      boolean NOT NULL DEFAULT false,
    publica         boolean NOT NULL DEFAULT false,
    creado_en       timestamptz NOT NULL DEFAULT now(),
    actualizado_en  timestamptz NOT NULL DEFAULT now()
);

-- La racha y el XP se calculan de acá
CREATE TABLE actividad_diaria (
    usuario_id      uuid NOT NULL REFERENCES usuario (id) ON DELETE CASCADE,
    dia             date NOT NULL,
    minutos         integer NOT NULL DEFAULT 0,
    xp              integer NOT NULL DEFAULT 0,
    creado_en       timestamptz NOT NULL DEFAULT now(),
    actualizado_en  timestamptz NOT NULL DEFAULT now(),
    PRIMARY KEY (usuario_id, dia)
);

-- =========================================================
-- Índices
-- =========================================================

CREATE INDEX ix_miembro_usuario        ON miembro_organizacion (usuario_id);
CREATE INDEX ix_hito_ruta              ON hito (ruta_id);
CREATE INDEX ix_nodo_hito              ON nodo (hito_id);
CREATE INDEX ix_actividad_nodo         ON actividad (nodo_id);
CREATE INDEX ix_actividad_ruta_tipo    ON actividad (ruta_id, tipo);
CREATE INDEX ix_inscripcion_org        ON inscripcion (organizacion_id) WHERE organizacion_id IS NOT NULL;
CREATE INDEX ix_progreso_repaso        ON progreso_nodo (proximo_repaso_en) WHERE proximo_repaso_en IS NOT NULL;
CREATE INDEX ix_intento_usuario        ON intento (usuario_id, terminado_en);
CREATE INDEX ix_intento_actividad      ON intento (actividad_id);
CREATE INDEX ix_intento_audio_vence    ON intento (audio_vence_en) WHERE audio_clave IS NOT NULL;
CREATE INDEX ix_intento_punto_clave_pc ON intento_punto_clave (punto_clave_id);
CREATE INDEX ix_evidencia_usuario      ON evidencia (usuario_id);
