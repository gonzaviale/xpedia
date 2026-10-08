-- Xpedia · esquema inicial
-- Detalle y diagrama en docs/modelo-datos.md
-- Convención: organizacion_id NULL = contenido global de Xpedia; con valor = contenido propio de una empresa.

CREATE EXTENSION IF NOT EXISTS vector;

-- =========================================================
-- Cuentas, empresas y planes
-- =========================================================

CREATE TABLE usuario (
    id                uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    email             text NOT NULL UNIQUE,
    nombre            text NOT NULL,
    hash_contrasenia  text,
    tipo              text NOT NULL DEFAULT 'PERSONA' CHECK (tipo IN ('PERSONA', 'ADMIN_XPEDIA')),
    estado            text NOT NULL DEFAULT 'ACTIVO' CHECK (estado IN ('ACTIVO', 'SUSPENDIDO')),
    pais              text,
    portfolio_slug    text UNIQUE,
    creado_en         timestamptz NOT NULL DEFAULT now(),
    actualizado_en    timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE organizacion (
    id              uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre          text NOT NULL,
    dominio         text UNIQUE,
    estado          text NOT NULL DEFAULT 'ACTIVA' CHECK (estado IN ('ACTIVA', 'SUSPENDIDA')),
    creado_en       timestamptz NOT NULL DEFAULT now(),
    actualizado_en  timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE plan (
    id                              uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    codigo                          text NOT NULL UNIQUE,
    nombre                          text NOT NULL,
    destinatario                    text NOT NULL CHECK (destinatario IN ('INDIVIDUAL', 'EMPRESA')),
    precio_por_usuario              numeric(10, 2) NOT NULL DEFAULT 0 CHECK (precio_por_usuario >= 0),
    moneda                          text NOT NULL DEFAULT 'ARS',
    limite_practicas_ia_diarias     integer CHECK (limite_practicas_ia_diarias >= 0), -- NULL = sin límite
    limite_consultas_coach_diarias  integer CHECK (limite_consultas_coach_diarias >= 0),
    incluye_roleplay_voz            boolean NOT NULL DEFAULT false,
    activo                          boolean NOT NULL DEFAULT true,
    creado_en                       timestamptz NOT NULL DEFAULT now(),
    actualizado_en                  timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE suscripcion (
    id                    uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    organizacion_id       uuid NOT NULL REFERENCES organizacion (id) ON DELETE CASCADE,
    plan_id               uuid NOT NULL REFERENCES plan (id) ON DELETE RESTRICT,
    estado                text NOT NULL DEFAULT 'ACTIVA' CHECK (estado IN ('ACTIVA', 'SUSPENDIDA', 'CANCELADA')),
    usuarios_contratados  integer NOT NULL CHECK (usuarios_contratados > 0),
    inicio_en             timestamptz NOT NULL DEFAULT now(),
    fin_en                timestamptz,
    creado_en             timestamptz NOT NULL DEFAULT now(),
    actualizado_en        timestamptz NOT NULL DEFAULT now(),
    CHECK (fin_en IS NULL OR fin_en > inicio_en)
);

CREATE UNIQUE INDEX ux_suscripcion_activa ON suscripcion (organizacion_id) WHERE estado = 'ACTIVA';

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
    area             text,
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
-- Fuentes y documentos
-- =========================================================

CREATE TABLE fuente (
    id                     uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    organizacion_id        uuid REFERENCES organizacion (id) ON DELETE CASCADE,
    titulo                 text NOT NULL,
    url                    text,
    licencia               text NOT NULL, -- p. ej. 'CC BY-SA 2.5', 'Documento interno'
    uso                    text NOT NULL CHECK (uso IN ('ADAPTABLE', 'SOLO_ENLACE')),
    permite_uso_comercial  boolean NOT NULL DEFAULT false,
    pais                   text,
    creado_en              timestamptz NOT NULL DEFAULT now(),
    actualizado_en         timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE documento (
    id               uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    organizacion_id  uuid NOT NULL REFERENCES organizacion (id) ON DELETE CASCADE,
    fuente_id        uuid REFERENCES fuente (id) ON DELETE SET NULL,
    titulo           text NOT NULL,
    archivo_clave    text NOT NULL,
    estado           text NOT NULL DEFAULT 'PROCESANDO' CHECK (estado IN ('PROCESANDO', 'PROCESADO', 'ERROR')),
    subido_por       uuid REFERENCES usuario (id) ON DELETE SET NULL,
    creado_en        timestamptz NOT NULL DEFAULT now(),
    actualizado_en   timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE fragmento_documento (
    id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    documento_id  uuid NOT NULL REFERENCES documento (id) ON DELETE CASCADE,
    posicion      integer NOT NULL,
    pagina        integer,
    texto         text NOT NULL,
    embedding     vector(1536),
    creado_en     timestamptz NOT NULL DEFAULT now(),
    UNIQUE (documento_id, posicion)
);

-- =========================================================
-- Rutas
-- =========================================================

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
    pais                   text,
    horas_estimadas        numeric(6, 1),
    ritmo_recomendado_min  smallint NOT NULL DEFAULT 20 CHECK (ritmo_recomendado_min > 0),
    estado                 text NOT NULL DEFAULT 'BORRADOR' CHECK (estado IN ('BORRADOR', 'EN_ARMADO', 'PUBLICADA', 'ARCHIVADA')),
    validacion             text NOT NULL DEFAULT 'BORRADOR_IA' CHECK (validacion IN ('BORRADOR_IA', 'REVISADA')),
    revisada_por           uuid REFERENCES usuario (id) ON DELETE SET NULL,
    revisada_en            timestamptz,
    confirmada_por         uuid REFERENCES usuario (id) ON DELETE SET NULL,
    confirmada_en          timestamptz,
    creado_en              timestamptz NOT NULL DEFAULT now(),
    actualizado_en         timestamptz NOT NULL DEFAULT now(),
    UNIQUE NULLS NOT DISTINCT (organizacion_id, slug, version),
    CHECK (validacion = 'BORRADOR_IA' OR revisada_en IS NOT NULL)
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
    estado_propuesta    text NOT NULL DEFAULT 'DE_ACUERDO' CHECK (estado_propuesta IN ('DE_ACUERDO', 'A_AJUSTAR')),
    comentario_ajuste   text,
    creado_en           timestamptz NOT NULL DEFAULT now(),
    actualizado_en      timestamptz NOT NULL DEFAULT now(),
    UNIQUE (ruta_id, posicion)
);

-- Rama del árbol de habilidades; las opcionales son bifurcaciones que no frenan la ruta principal
CREATE TABLE rama (
    id              uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    ruta_id         uuid NOT NULL REFERENCES ruta (id) ON DELETE CASCADE,
    codigo          text NOT NULL, -- p. ej. 'A'
    nombre          text NOT NULL, -- p. ej. 'Fundamentos'
    posicion        smallint NOT NULL,
    opcional        boolean NOT NULL DEFAULT false,
    creado_en       timestamptz NOT NULL DEFAULT now(),
    actualizado_en  timestamptz NOT NULL DEFAULT now(),
    UNIQUE (ruta_id, codigo)
);

-- TEMA = tema fuera del recorrido principal, sin hito; se suma por interés o por una novedad
CREATE TABLE nodo (
    id                 uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    ruta_id            uuid NOT NULL REFERENCES ruta (id) ON DELETE CASCADE,
    hito_id            uuid REFERENCES hito (id) ON DELETE CASCADE,
    rama_id            uuid REFERENCES rama (id) ON DELETE SET NULL,
    habilidad_id       uuid REFERENCES habilidad (id) ON DELETE SET NULL,
    codigo             text NOT NULL, -- p. ej. 'A3'
    titulo             text NOT NULL,
    resumen            text,
    tipo               text NOT NULL DEFAULT 'NUCLEO' CHECK (tipo IN ('NUCLEO', 'OPCIONAL', 'TEMA')),
    nivel              smallint NOT NULL DEFAULT 1 CHECK (nivel BETWEEN 0 AND 3),
    minutos_estimados  smallint,
    palabras_clave     text[] NOT NULL DEFAULT '{}',
    posicion           smallint NOT NULL,
    creado_en          timestamptz NOT NULL DEFAULT now(),
    actualizado_en     timestamptz NOT NULL DEFAULT now(),
    UNIQUE (ruta_id, codigo),
    CHECK (hito_id IS NOT NULL OR tipo = 'TEMA')
);

CREATE TABLE nodo_prerrequisito (
    nodo_id           uuid NOT NULL REFERENCES nodo (id) ON DELETE CASCADE,
    prerrequisito_id  uuid NOT NULL REFERENCES nodo (id) ON DELETE CASCADE,
    creado_en         timestamptz NOT NULL DEFAULT now(),
    PRIMARY KEY (nodo_id, prerrequisito_id),
    CHECK (nodo_id <> prerrequisito_id)
);

-- =========================================================
-- Actividades y evaluación
-- =========================================================

CREATE TABLE rubrica (
    id                  uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    organizacion_id     uuid REFERENCES organizacion (id) ON DELETE CASCADE,
    nombre              text NOT NULL,
    descripcion         text,
    puntaje_aprobacion  numeric(5, 2) NOT NULL,
    creado_en           timestamptz NOT NULL DEFAULT now(),
    actualizado_en      timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE rubrica_criterio (
    id              uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    rubrica_id      uuid NOT NULL REFERENCES rubrica (id) ON DELETE CASCADE,
    posicion        smallint NOT NULL,
    nombre          text NOT NULL,
    descripcion     text,
    puntaje_max     smallint NOT NULL CHECK (puntaje_max > 0),
    peso            numeric(4, 2) NOT NULL DEFAULT 1,
    eliminatorio    boolean NOT NULL DEFAULT false, -- un 0 en este criterio desaprueba
    creado_en       timestamptz NOT NULL DEFAULT now(),
    actualizado_en  timestamptz NOT NULL DEFAULT now(),
    UNIQUE (rubrica_id, posicion)
);

CREATE TABLE punto_clave (
    id               uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    organizacion_id  uuid REFERENCES organizacion (id) ON DELETE CASCADE,
    texto            text NOT NULL,
    critico          boolean NOT NULL DEFAULT false,
    fuente_id        uuid REFERENCES fuente (id) ON DELETE RESTRICT,
    fragmento_id     uuid REFERENCES fragmento_documento (id) ON DELETE SET NULL,
    ubicacion        text,
    creado_en        timestamptz NOT NULL DEFAULT now(),
    actualizado_en   timestamptz NOT NULL DEFAULT now()
);

-- Contenido de la mecánica en JSONB: texto de la microlección, pasos del escenario, personaje del roleplay, consigna del reto, etc.
CREATE TABLE actividad (
    id               uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    ruta_id          uuid NOT NULL REFERENCES ruta (id) ON DELETE CASCADE,
    nodo_id          uuid REFERENCES nodo (id) ON DELETE CASCADE,
    hito_id          uuid REFERENCES hito (id) ON DELETE CASCADE,
    rubrica_id       uuid REFERENCES rubrica (id) ON DELETE SET NULL,
    tipo             text NOT NULL CHECK (tipo IN ('DIAGNOSTICO', 'MICROLECCION', 'CUESTIONARIO', 'ESCENARIO', 'ROLEPLAY',
                                                   'ENSAYO', 'RETO_PROYECTO', 'DESAFIO_REAL', 'LECTURA', 'PRUEBA_FINAL')),
    titulo           text NOT NULL,
    nivel            smallint NOT NULL DEFAULT 1 CHECK (nivel BETWEEN 0 AND 3),
    contenido        jsonb NOT NULL DEFAULT '{}'::jsonb,
    origen           text NOT NULL CHECK (origen IN ('IA', 'HUMANO')),
    estado_revision  text NOT NULL DEFAULT 'PENDIENTE' CHECK (estado_revision IN ('PENDIENTE', 'APROBADA', 'RECHAZADA')),
    revisado_por     uuid REFERENCES usuario (id) ON DELETE SET NULL,
    revisado_en      timestamptz,
    creado_en        timestamptz NOT NULL DEFAULT now(),
    actualizado_en   timestamptz NOT NULL DEFAULT now(),
    CHECK (tipo = 'DIAGNOSTICO' OR tipo = 'PRUEBA_FINAL' OR nodo_id IS NOT NULL),
    CHECK (tipo <> 'PRUEBA_FINAL' OR hito_id IS NOT NULL)
);

CREATE TABLE pregunta (
    id              uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    actividad_id    uuid NOT NULL REFERENCES actividad (id) ON DELETE CASCADE,
    nodo_id         uuid REFERENCES nodo (id) ON DELETE CASCADE, -- en el diagnóstico, el nodo que mide
    punto_clave_id  uuid REFERENCES punto_clave (id) ON DELETE SET NULL,
    posicion        smallint NOT NULL,
    tipo            text NOT NULL CHECK (tipo IN ('CONCEPTO', 'APLICACION', 'DETALLE')),
    enunciado       text NOT NULL,
    opciones        jsonb NOT NULL,
    correcta        smallint NOT NULL CHECK (correcta >= 0),
    explicacion     text,
    creado_en       timestamptz NOT NULL DEFAULT now(),
    actualizado_en  timestamptz NOT NULL DEFAULT now(),
    UNIQUE (actividad_id, posicion)
);

CREATE TABLE actividad_fuente (
    actividad_id  uuid NOT NULL REFERENCES actividad (id) ON DELETE CASCADE,
    fuente_id     uuid NOT NULL REFERENCES fuente (id) ON DELETE RESTRICT,
    ubicacion     text, -- p. ej. 'pág. 3', 'sección Operadores'
    creado_en     timestamptz NOT NULL DEFAULT now(),
    PRIMARY KEY (actividad_id, fuente_id)
);

CREATE TABLE actividad_punto_clave (
    actividad_id    uuid NOT NULL REFERENCES actividad (id) ON DELETE CASCADE,
    punto_clave_id  uuid NOT NULL REFERENCES punto_clave (id) ON DELETE CASCADE,
    creado_en       timestamptz NOT NULL DEFAULT now(),
    PRIMARY KEY (actividad_id, punto_clave_id)
);

-- =========================================================
-- Empresa: asignación y acompañamiento
-- =========================================================

CREATE TABLE asignacion_ruta (
    id               uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    organizacion_id  uuid NOT NULL REFERENCES organizacion (id) ON DELETE CASCADE,
    ruta_id          uuid NOT NULL REFERENCES ruta (id) ON DELETE CASCADE,
    puesto_id        uuid REFERENCES puesto (id) ON DELETE CASCADE,
    usuario_id       uuid REFERENCES usuario (id) ON DELETE CASCADE,
    asignada_por     uuid REFERENCES usuario (id) ON DELETE SET NULL,
    fecha_limite     date,
    creado_en        timestamptz NOT NULL DEFAULT now(),
    actualizado_en   timestamptz NOT NULL DEFAULT now(),
    CHECK ((puesto_id IS NULL) <> (usuario_id IS NULL))
);

CREATE TABLE tutoria (
    id                uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    organizacion_id   uuid NOT NULL REFERENCES organizacion (id) ON DELETE CASCADE,
    tutor_usuario_id  uuid NOT NULL REFERENCES usuario (id) ON DELETE CASCADE,
    ruta_id           uuid REFERENCES ruta (id) ON DELETE CASCADE,
    tipo              text NOT NULL CHECK (tipo IN ('EMPRESA', 'COMUNIDAD')),
    estado            text NOT NULL DEFAULT 'ACTIVA' CHECK (estado IN ('ACTIVA', 'FINALIZADA')),
    creado_en         timestamptz NOT NULL DEFAULT now(),
    actualizado_en    timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE calificacion_tutor (
    id                uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    tutor_usuario_id  uuid NOT NULL REFERENCES usuario (id) ON DELETE CASCADE,
    usuario_id        uuid NOT NULL REFERENCES usuario (id) ON DELETE CASCADE,
    puntaje           smallint NOT NULL CHECK (puntaje BETWEEN 1 AND 5),
    comentario        text,
    creado_en         timestamptz NOT NULL DEFAULT now(),
    UNIQUE (tutor_usuario_id, usuario_id),
    CHECK (tutor_usuario_id <> usuario_id)
);

-- =========================================================
-- Progreso de la persona
-- =========================================================

CREATE TABLE inscripcion (
    id                      uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id              uuid NOT NULL REFERENCES usuario (id) ON DELETE CASCADE,
    ruta_id                 uuid NOT NULL REFERENCES ruta (id) ON DELETE RESTRICT,
    organizacion_id         uuid REFERENCES organizacion (id) ON DELETE SET NULL, -- si la asignó una empresa
    asignacion_id           uuid REFERENCES asignacion_ruta (id) ON DELETE SET NULL,
    meta_personal           text, -- p. ej. 'pasar de gastronomía a atención remota en 6 meses'
    ritmo_min               smallint NOT NULL CHECK (ritmo_min > 0),
    fecha_llegada_estimada  date,
    practica_extra          boolean NOT NULL DEFAULT false,
    estado                  text NOT NULL DEFAULT 'ACTIVA' CHECK (estado IN ('ACTIVA', 'PAUSADA', 'TERMINADA')),
    hito_actual_id          uuid REFERENCES hito (id) ON DELETE SET NULL,
    diagnostico             jsonb, -- respuestas y mapa de habilidades transferibles
    iniciada_en             timestamptz NOT NULL DEFAULT now(),
    creado_en               timestamptz NOT NULL DEFAULT now(),
    actualizado_en          timestamptz NOT NULL DEFAULT now()
);

-- Una sola inscripción abierta por usuario y ruta
CREATE UNIQUE INDEX ux_inscripcion_abierta ON inscripcion (usuario_id, ruta_id) WHERE estado <> 'TERMINADA';

CREATE TABLE progreso_nodo (
    inscripcion_id         uuid NOT NULL REFERENCES inscripcion (id) ON DELETE CASCADE,
    nodo_id                uuid NOT NULL REFERENCES nodo (id) ON DELETE CASCADE,
    estado                 text NOT NULL DEFAULT 'BLOQUEADO' CHECK (estado IN ('BLOQUEADO', 'DISPONIBLE', 'EN_CURSO', 'DOMINADO',
                                                                               'PROBADO_REAL', 'OXIDADO', 'AGENDADO')),
    dominio                numeric(3, 2) NOT NULL DEFAULT 0 CHECK (dominio BETWEEN 0 AND 1),
    nivel                  smallint NOT NULL DEFAULT 0 CHECK (nivel BETWEEN 0 AND 3),
    cantidad_fallos        integer NOT NULL DEFAULT 0,
    proximo_repaso_en      timestamptz,
    intervalo_repaso_dias  smallint,
    motivo_repaso          text,
    creado_en              timestamptz NOT NULL DEFAULT now(),
    actualizado_en         timestamptz NOT NULL DEFAULT now(),
    PRIMARY KEY (inscripcion_id, nodo_id)
);

-- Lo que la persona suma a su roadmap desde una novedad, un tema de interés o un ajuste
CREATE TABLE nodo_agregado (
    id              uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    inscripcion_id  uuid NOT NULL REFERENCES inscripcion (id) ON DELETE CASCADE,
    nodo_id         uuid REFERENCES nodo (id) ON DELETE CASCADE,
    texto_interes   text,
    hito_id         uuid REFERENCES hito (id) ON DELETE SET NULL,
    origen          text NOT NULL CHECK (origen IN ('NOVEDAD', 'INTERES', 'AJUSTE')),
    decision        text NOT NULL CHECK (decision IN ('AHORA', 'MAS_ADELANTE', 'RAMA', 'GUARDADO')),
    motivo          text NOT NULL,
    deshecho_en     timestamptz,
    creado_en       timestamptz NOT NULL DEFAULT now(),
    actualizado_en  timestamptz NOT NULL DEFAULT now(),
    CHECK (nodo_id IS NOT NULL OR texto_interes IS NOT NULL)
);

CREATE TABLE sesion (
    id              uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    inscripcion_id  uuid NOT NULL REFERENCES inscripcion (id) ON DELETE CASCADE,
    nodo_id         uuid REFERENCES nodo (id) ON DELETE SET NULL,
    pasos           jsonb NOT NULL DEFAULT '[]'::jsonb, -- repaso, microlección, práctica y cierre
    minutos         smallint NOT NULL DEFAULT 0,
    correctas       smallint NOT NULL DEFAULT 0,
    total           smallint NOT NULL DEFAULT 0,
    xp              integer NOT NULL DEFAULT 0,
    iniciada_en     timestamptz NOT NULL DEFAULT now(),
    terminada_en    timestamptz,
    creado_en       timestamptz NOT NULL DEFAULT now(),
    actualizado_en  timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE intento (
    id                    uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id            uuid NOT NULL REFERENCES usuario (id) ON DELETE CASCADE,
    inscripcion_id        uuid REFERENCES inscripcion (id) ON DELETE CASCADE,
    sesion_id             uuid REFERENCES sesion (id) ON DELETE SET NULL,
    actividad_id          uuid NOT NULL REFERENCES actividad (id) ON DELETE RESTRICT,
    modo                  text NOT NULL DEFAULT 'PRACTICA' CHECK (modo IN ('PRACTICA', 'VALIDACION')),
    puntaje               numeric(5, 2),
    puntaje_por_criterio  jsonb,
    aprobado              boolean,
    feedback              text,
    resultado             jsonb, -- decisiones, turnos y curva de enojo, transcripción del ensayo, check-in
    audio_clave           text,
    audio_vence_en        timestamptz, -- el audio se borra a los 30 días
    iniciado_en           timestamptz NOT NULL DEFAULT now(),
    terminado_en          timestamptz,
    creado_en             timestamptz NOT NULL DEFAULT now(),
    actualizado_en        timestamptz NOT NULL DEFAULT now(),
    CHECK (audio_clave IS NULL OR audio_vence_en IS NOT NULL)
);

CREATE TABLE respuesta (
    id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    intento_id    uuid NOT NULL REFERENCES intento (id) ON DELETE CASCADE,
    pregunta_id   uuid NOT NULL REFERENCES pregunta (id) ON DELETE CASCADE,
    elegida       smallint NOT NULL,
    correcta      boolean NOT NULL,
    confianza     text CHECK (confianza IN ('SABIA', 'DUDE', 'ADIVINE')),
    milisegundos  integer CHECK (milisegundos >= 0),
    creado_en     timestamptz NOT NULL DEFAULT now(),
    UNIQUE (intento_id, pregunta_id)
);

CREATE TABLE intento_punto_clave (
    intento_id      uuid NOT NULL REFERENCES intento (id) ON DELETE CASCADE,
    punto_clave_id  uuid NOT NULL REFERENCES punto_clave (id) ON DELETE CASCADE,
    correcto        boolean NOT NULL,
    seguro          boolean, -- "¿lo sabías o lo adivinaste?"
    creado_en       timestamptz NOT NULL DEFAULT now(),
    PRIMARY KEY (intento_id, punto_clave_id)
);

CREATE TABLE informe (
    id              uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    inscripcion_id  uuid NOT NULL REFERENCES inscripcion (id) ON DELETE CASCADE,
    desde           date NOT NULL,
    hasta           date NOT NULL,
    contenido       jsonb NOT NULL, -- avance, lo que cuesta, novedades y próximos pasos
    creado_en       timestamptz NOT NULL DEFAULT now(),
    CHECK (hasta >= desde)
);

-- Historial del roadmap vivo. Las propuestas (PROPUESTO) quedan a la espera de que la persona las acepte o rechace
CREATE TABLE cambio_roadmap (
    id                uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    inscripcion_id    uuid NOT NULL REFERENCES inscripcion (id) ON DELETE CASCADE,
    nodo_id           uuid REFERENCES nodo (id) ON DELETE SET NULL,
    informe_id        uuid REFERENCES informe (id) ON DELETE SET NULL,
    tipo              text NOT NULL CHECK (tipo IN ('AJUSTE', 'NODO', 'REFUERZO', 'OXIDO', 'LOGRO', 'INFO')),
    texto             text NOT NULL,
    codigo_propuesta  text,
    estado            text NOT NULL DEFAULT 'APLICADO' CHECK (estado IN ('APLICADO', 'PROPUESTO', 'ACEPTADO', 'RECHAZADO')),
    decidido_en       timestamptz,
    creado_en         timestamptz NOT NULL DEFAULT now(),
    actualizado_en    timestamptz NOT NULL DEFAULT now(),
    CHECK (estado = 'APLICADO' OR codigo_propuesta IS NOT NULL)
);

CREATE TABLE evidencia (
    id              uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id      uuid NOT NULL REFERENCES usuario (id) ON DELETE CASCADE,
    inscripcion_id  uuid REFERENCES inscripcion (id) ON DELETE SET NULL,
    hito_id         uuid REFERENCES hito (id) ON DELETE SET NULL,
    intento_id      uuid REFERENCES intento (id) ON DELETE SET NULL,
    tipo            text NOT NULL CHECK (tipo IN ('PRUEBA_FINAL', 'RUBRICA', 'PROYECTO', 'GITHUB', 'MUNDO_REAL',
                                                  'CERTIFICADO_EXTERNO')),
    titulo          text NOT NULL,
    descripcion     text,
    url             text,
    verificada      boolean NOT NULL DEFAULT false,
    publica         boolean NOT NULL DEFAULT false,
    creado_en       timestamptz NOT NULL DEFAULT now(),
    actualizado_en  timestamptz NOT NULL DEFAULT now()
);

-- La racha y el XP se calculan de acá; practicas_ia y consultas_coach se comparan con el límite del plan
CREATE TABLE actividad_diaria (
    usuario_id       uuid NOT NULL REFERENCES usuario (id) ON DELETE CASCADE,
    dia              date NOT NULL,
    minutos          integer NOT NULL DEFAULT 0,
    xp               integer NOT NULL DEFAULT 0,
    practicas_ia     integer NOT NULL DEFAULT 0,
    consultas_coach  integer NOT NULL DEFAULT 0,
    creado_en        timestamptz NOT NULL DEFAULT now(),
    actualizado_en   timestamptz NOT NULL DEFAULT now(),
    PRIMARY KEY (usuario_id, dia)
);

-- =========================================================
-- Novedades
-- =========================================================

CREATE TABLE novedad (
    id              uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    nodo_id         uuid REFERENCES nodo (id) ON DELETE SET NULL,
    fuente_id       uuid REFERENCES fuente (id) ON DELETE SET NULL,
    titulo          text NOT NULL,
    resumen         text NOT NULL,
    url             text,
    palabras_clave  text[] NOT NULL DEFAULT '{}',
    publicada_en    date NOT NULL,
    creado_en       timestamptz NOT NULL DEFAULT now(),
    actualizado_en  timestamptz NOT NULL DEFAULT now()
);

-- RETENIDA = tiene que ver con la ruta pero corresponde a un hito que la persona todavía no alcanzó
CREATE TABLE novedad_inscripcion (
    novedad_id        uuid NOT NULL REFERENCES novedad (id) ON DELETE CASCADE,
    inscripcion_id    uuid NOT NULL REFERENCES inscripcion (id) ON DELETE CASCADE,
    estado            text NOT NULL CHECK (estado IN ('PARA_AHORA', 'RETENIDA', 'SUMADA', 'DESCARTADA')),
    motivo            text,
    hito_id           uuid REFERENCES hito (id) ON DELETE SET NULL,
    nodo_agregado_id  uuid REFERENCES nodo_agregado (id) ON DELETE SET NULL,
    leida_en          timestamptz,
    creado_en         timestamptz NOT NULL DEFAULT now(),
    actualizado_en    timestamptz NOT NULL DEFAULT now(),
    PRIMARY KEY (novedad_id, inscripcion_id),
    CHECK (estado <> 'RETENIDA' OR hito_id IS NOT NULL)
);

-- =========================================================
-- Coach de IA, avisos e integraciones
-- =========================================================

CREATE TABLE conversacion_coach (
    id              uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id      uuid NOT NULL REFERENCES usuario (id) ON DELETE CASCADE,
    inscripcion_id  uuid REFERENCES inscripcion (id) ON DELETE CASCADE,
    hito_id         uuid REFERENCES hito (id) ON DELETE SET NULL,
    creado_en       timestamptz NOT NULL DEFAULT now(),
    actualizado_en  timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE mensaje_coach (
    id               uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    conversacion_id  uuid NOT NULL REFERENCES conversacion_coach (id) ON DELETE CASCADE,
    rol              text NOT NULL CHECK (rol IN ('PERSONA', 'COACH')),
    texto            text NOT NULL,
    fuentes          jsonb, -- fuentes y fragmentos citados en la respuesta
    creado_en        timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE notificacion (
    id          uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id  uuid NOT NULL REFERENCES usuario (id) ON DELETE CASCADE,
    tipo        text NOT NULL CHECK (tipo IN ('RECORDATORIO', 'REPASO', 'ATRASO', 'RESUMEN_SEMANAL', 'NOVEDAD', 'INFORME')),
    canal       text NOT NULL DEFAULT 'APP' CHECK (canal IN ('APP', 'EMAIL')),
    titulo      text NOT NULL,
    cuerpo      text,
    enviada_en  timestamptz,
    leida_en    timestamptz,
    creado_en   timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE conexion_github (
    usuario_id      uuid PRIMARY KEY REFERENCES usuario (id) ON DELETE CASCADE,
    github_id       bigint NOT NULL UNIQUE,
    login           text NOT NULL,
    token_cifrado   text NOT NULL,
    conectada_en    timestamptz NOT NULL DEFAULT now(),
    actualizado_en  timestamptz NOT NULL DEFAULT now()
);

-- =========================================================
-- Índices
-- =========================================================

CREATE INDEX ix_miembro_usuario             ON miembro_organizacion (usuario_id);
CREATE INDEX ix_suscripcion_plan            ON suscripcion (plan_id);
CREATE INDEX ix_documento_org               ON documento (organizacion_id);
CREATE INDEX ix_fragmento_embedding         ON fragmento_documento USING hnsw (embedding vector_cosine_ops);
CREATE INDEX ix_hito_ruta                   ON hito (ruta_id);
CREATE INDEX ix_nodo_hito                   ON nodo (hito_id);
CREATE INDEX ix_nodo_rama                   ON nodo (rama_id);
CREATE INDEX ix_nodo_prerrequisito_pre      ON nodo_prerrequisito (prerrequisito_id);
CREATE INDEX ix_actividad_nodo_tipo_nivel   ON actividad (nodo_id, tipo, nivel);
CREATE INDEX ix_actividad_ruta_tipo         ON actividad (ruta_id, tipo);
CREATE INDEX ix_actividad_pendiente         ON actividad (ruta_id) WHERE estado_revision = 'PENDIENTE';
CREATE INDEX ix_pregunta_nodo               ON pregunta (nodo_id) WHERE nodo_id IS NOT NULL;
CREATE INDEX ix_punto_clave_fuente          ON punto_clave (fuente_id);
CREATE INDEX ix_asignacion_org              ON asignacion_ruta (organizacion_id);
CREATE INDEX ix_asignacion_puesto           ON asignacion_ruta (puesto_id) WHERE puesto_id IS NOT NULL;
CREATE INDEX ix_tutoria_org                 ON tutoria (organizacion_id);
CREATE INDEX ix_inscripcion_org             ON inscripcion (organizacion_id) WHERE organizacion_id IS NOT NULL;
CREATE INDEX ix_progreso_repaso             ON progreso_nodo (proximo_repaso_en) WHERE proximo_repaso_en IS NOT NULL;
CREATE INDEX ix_nodo_agregado_inscripcion   ON nodo_agregado (inscripcion_id) WHERE deshecho_en IS NULL;
CREATE INDEX ix_sesion_inscripcion          ON sesion (inscripcion_id, iniciada_en);
CREATE INDEX ix_intento_usuario             ON intento (usuario_id, terminado_en);
CREATE INDEX ix_intento_actividad           ON intento (actividad_id);
CREATE INDEX ix_intento_sesion              ON intento (sesion_id);
CREATE INDEX ix_intento_audio_vence         ON intento (audio_vence_en) WHERE audio_clave IS NOT NULL;
CREATE INDEX ix_respuesta_pregunta          ON respuesta (pregunta_id);
CREATE INDEX ix_intento_punto_clave_pc      ON intento_punto_clave (punto_clave_id);
CREATE INDEX ix_informe_inscripcion         ON informe (inscripcion_id, hasta);
CREATE INDEX ix_cambio_roadmap_inscripcion  ON cambio_roadmap (inscripcion_id, creado_en);
CREATE INDEX ix_cambio_roadmap_propuesto    ON cambio_roadmap (inscripcion_id) WHERE estado = 'PROPUESTO';
CREATE INDEX ix_evidencia_usuario           ON evidencia (usuario_id);
CREATE INDEX ix_novedad_nodo                ON novedad (nodo_id);
CREATE INDEX ix_novedad_inscripcion_estado  ON novedad_inscripcion (inscripcion_id, estado);
CREATE INDEX ix_conversacion_usuario        ON conversacion_coach (usuario_id);
CREATE INDEX ix_mensaje_conversacion        ON mensaje_coach (conversacion_id, creado_en);
CREATE INDEX ix_notificacion_no_leida       ON notificacion (usuario_id) WHERE leida_en IS NULL;
