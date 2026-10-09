-- No se inventa el objetivo personal de inscripciones previas: permanece NULL.
ALTER TABLE inscripcion
    ADD COLUMN objetivo text CHECK (objetivo IN ('ARRANCAR', 'CAMBIAR', 'MEJORAR'));

CREATE INDEX ix_inscripcion_actual
    ON inscripcion (usuario_id, iniciada_en DESC, id DESC)
    WHERE estado = 'ACTIVA';

