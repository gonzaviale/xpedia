DELETE FROM ruta;
DELETE FROM organizacion;

INSERT INTO organizacion (id, nombre) VALUES ('00000000-0000-0000-0000-000000000100', 'Empresa de prueba');

INSERT INTO ruta (id, slug, titulo, tipo, objetivo, meta, pais, horas_estimadas, ritmo_recomendado_min, estado, validacion)
VALUES
('00000000-0000-0000-0000-000000000201', 'tecnica-a', 'Alfa', 'TECNICA', 'ARRANCAR', 'Meta técnica A', 'AR', 10.5, 30, 'PUBLICADA', 'BORRADOR_IA'),
('00000000-0000-0000-0000-000000000202', 'tecnica-b', 'Alfa', 'TECNICA', 'ARRANCAR', 'Meta técnica B', 'AR', 12.0, 45, 'PUBLICADA', 'BORRADOR_IA'),
('00000000-0000-0000-0000-000000000203', 'atencion-test', 'Beta', 'CAMBIO_RUBRO', 'CAMBIAR', 'Meta atención', 'AR', 20.0, 20, 'PUBLICADA', 'BORRADOR_IA'),
('00000000-0000-0000-0000-000000000204', 'oratoria-test', 'Gamma', 'HABILIDAD_BLANDA', 'MEJORAR', 'Meta oratoria', 'AR', NULL, 15, 'PUBLICADA', 'BORRADOR_IA'),
('00000000-0000-0000-0000-000000000205', 'borrador-test', 'Oculta borrador', 'TECNICA', 'ARRANCAR', 'Meta', 'AR', 5.0, 20, 'BORRADOR', 'BORRADOR_IA'),
('00000000-0000-0000-0000-000000000206', 'armado-test', 'Oculta armado', 'TECNICA', 'ARRANCAR', 'Meta', 'AR', 5.0, 20, 'EN_ARMADO', 'BORRADOR_IA'),
('00000000-0000-0000-0000-000000000207', 'archivada-test', 'Oculta archivada', 'TECNICA', 'ARRANCAR', 'Meta', 'AR', 5.0, 20, 'ARCHIVADA', 'BORRADOR_IA');

UPDATE ruta SET validacion = 'REVISADA', revisada_en = now(), perfil_inicial = 'Personas con experiencia'
WHERE id = '00000000-0000-0000-0000-000000000204';

INSERT INTO ruta (id, organizacion_id, slug, titulo, tipo, objetivo, meta, estado)
VALUES ('00000000-0000-0000-0000-000000000208', '00000000-0000-0000-0000-000000000100',
        'empresa-test', 'Privada empresa', 'TECNICA', 'ARRANCAR', 'Meta privada', 'PUBLICADA');
