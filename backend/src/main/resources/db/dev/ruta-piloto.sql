-- SOLO DESARROLLO. Subconjunto ficticio de la ruta, no contenido editorial validado.
-- Fuera de Flyway: se ejecuta manualmente. No elimina ni sobrescribe registros.
BEGIN;

INSERT INTO ruta (id, slug, titulo, tipo, objetivo, meta, perfil_inicial, pais,
                  horas_estimadas, ritmo_recomendado_min, estado, validacion)
VALUES ('b1000000-0000-4000-8000-000000000001', 'demo-atencion-cliente-remota',
        '[DEMO] Atención al cliente remota', 'CAMBIO_RUBRO', 'CAMBIAR',
        'Recorrer contenido de prueba y verificar la API; no acredita competencias.',
        'Ejemplo para personas con experiencia atendiendo público.', 'AR', 7.5, 20,
        'PUBLICADA', 'BORRADOR_IA')
ON CONFLICT (id) DO NOTHING;

INSERT INTO hito (id, ruta_id, posicion, titulo, objetivo, horas_estimadas, evidencia_esperada)
VALUES
('b2000000-0000-4000-8000-000000000001', 'b1000000-0000-4000-8000-000000000001', 1,
 'Del mostrador a la pantalla', 'Reconocer los canales de atención remota.', 1.5, 'Descripción de un caso ficticio'),
('b2000000-0000-4000-8000-000000000002', 'b1000000-0000-4000-8000-000000000001', 2,
 'Escribir para clientes', 'Practicar respuestas claras por escrito.', 3, 'Respuesta de demostración'),
('b2000000-0000-4000-8000-000000000003', 'b1000000-0000-4000-8000-000000000001', 3,
 'Clientes enojados y malas noticias', 'Practicar empatía y próximos pasos.', 3, 'Caso ficticio resuelto')
ON CONFLICT (id) DO NOTHING;

INSERT INTO rama (id, ruta_id, codigo, nombre, posicion)
VALUES
('b3000000-0000-4000-8000-000000000001', 'b1000000-0000-4000-8000-000000000001', 'TRATO', 'Trato con clientes', 1),
('b3000000-0000-4000-8000-000000000002', 'b1000000-0000-4000-8000-000000000001', 'COMUNICACION', 'Comunicación a distancia', 2)
ON CONFLICT (id) DO NOTHING;

INSERT INTO nodo (id, ruta_id, hito_id, rama_id, codigo, titulo, resumen, minutos_estimados, palabras_clave, posicion)
VALUES
('b4000000-0000-4000-8000-000000000001', 'b1000000-0000-4000-8000-000000000001', 'b2000000-0000-4000-8000-000000000001',
 'b3000000-0000-4000-8000-000000000002', 'DEMO-01', 'Canales de atención', 'Tema de demostración: chat, mail y teléfono.', 15, ARRAY['canales','atención'], 1),
('b4000000-0000-4000-8000-000000000002', 'b1000000-0000-4000-8000-000000000001', 'b2000000-0000-4000-8000-000000000001',
 'b3000000-0000-4000-8000-000000000001', 'DEMO-02', 'Escuchar e indagar', 'Tema de demostración: entender la consulta.', 15, ARRAY['escucha'], 2),
('b4000000-0000-4000-8000-000000000003', 'b1000000-0000-4000-8000-000000000001', 'b2000000-0000-4000-8000-000000000002',
 'b3000000-0000-4000-8000-000000000002', 'DEMO-03', 'Lenguaje claro', 'Tema de demostración: respuestas breves.', 20, ARRAY['claridad'], 1),
('b4000000-0000-4000-8000-000000000004', 'b1000000-0000-4000-8000-000000000001', 'b2000000-0000-4000-8000-000000000002',
 'b3000000-0000-4000-8000-000000000002', 'DEMO-04', 'Respuesta por chat', 'Tema de demostración: ordenar una respuesta.', 20, ARRAY['chat'], 2),
('b4000000-0000-4000-8000-000000000005', 'b1000000-0000-4000-8000-000000000001', 'b2000000-0000-4000-8000-000000000003',
 'b3000000-0000-4000-8000-000000000001', 'DEMO-05', 'Empatía específica', 'Tema de demostración: reconocer el problema.', 20, ARRAY['empatía'], 1),
('b4000000-0000-4000-8000-000000000006', 'b1000000-0000-4000-8000-000000000001', 'b2000000-0000-4000-8000-000000000003',
 'b3000000-0000-4000-8000-000000000001', 'DEMO-06', 'Próximos pasos', 'Tema de demostración: proponer una acción concreta.', 20, ARRAY['seguimiento'], 2)
ON CONFLICT (id) DO NOTHING;

INSERT INTO nodo_prerrequisito (nodo_id, prerrequisito_id)
VALUES
('b4000000-0000-4000-8000-000000000002', 'b4000000-0000-4000-8000-000000000001'),
('b4000000-0000-4000-8000-000000000003', 'b4000000-0000-4000-8000-000000000002'),
('b4000000-0000-4000-8000-000000000004', 'b4000000-0000-4000-8000-000000000003'),
('b4000000-0000-4000-8000-000000000005', 'b4000000-0000-4000-8000-000000000004'),
('b4000000-0000-4000-8000-000000000006', 'b4000000-0000-4000-8000-000000000005')
ON CONFLICT (nodo_id, prerrequisito_id) DO NOTHING;

-- Contenido sintético para probar JSON y citas. APROBADA solo habilita esta DEMO.
-- No se declara revisión humana ni permiso comercial.
INSERT INTO fuente (id, titulo, licencia, uso, permite_uso_comercial)
VALUES ('b6000000-0000-4000-8000-000000000001', 'Material sintético de demostración Xpedia',
        'Material de prueba; sin licencia editorial asignada', 'ADAPTABLE', false)
ON CONFLICT (id) DO NOTHING;

INSERT INTO actividad (id, ruta_id, nodo_id, hito_id, tipo, titulo, nivel, contenido, origen, estado_revision)
VALUES ('b5000000-0000-4000-8000-000000000001', 'b1000000-0000-4000-8000-000000000001',
        'b4000000-0000-4000-8000-000000000003', 'b2000000-0000-4000-8000-000000000002',
        'MICROLECCION', '[DEMO] Una respuesta breve por chat', 1,
        '{"demo":true,"texto":"Ejemplo ficticio para verificar la API. Una respuesta puede reconocer la consulta y proponer un próximo paso concreto.","ejemplo":"Hola, gracias por avisarnos. ¿Podés compartir el número de pedido para revisar el caso?","nota":"No es contenido validado para capacitación."}'::jsonb,
        'IA', 'APROBADA')
ON CONFLICT (id) DO NOTHING;

INSERT INTO actividad_fuente (actividad_id, fuente_id, ubicacion)
VALUES ('b5000000-0000-4000-8000-000000000001', 'b6000000-0000-4000-8000-000000000001', 'Ejemplo ficticio de chat')
ON CONFLICT (actividad_id, fuente_id) DO NOTHING;

INSERT INTO rubrica (id, nombre, descripcion, puntaje_aprobacion)
VALUES ('b7000000-0000-4000-8000-000000000001', '[DEMO] Escritura para clientes',
        'Rúbrica ficticia para probar la API; no acredita revisión editorial.', 8)
ON CONFLICT (id) DO NOTHING;

INSERT INTO rubrica_criterio (id, rubrica_id, posicion, nombre, descripcion, puntaje_max, peso, eliminatorio)
VALUES
('b7100000-0000-4000-8000-000000000001', 'b7000000-0000-4000-8000-000000000001', 1, 'Claridad', 'Respuesta breve y comprensible.', 3, 1, false),
('b7100000-0000-4000-8000-000000000002', 'b7000000-0000-4000-8000-000000000001', 2, 'Empatía y tono', 'Reconocer la consulta con respeto.', 3, 1, false),
('b7100000-0000-4000-8000-000000000003', 'b7000000-0000-4000-8000-000000000001', 3, 'Próximo paso', 'Proponer una acción concreta.', 3, 1, false),
('b7100000-0000-4000-8000-000000000004', 'b7000000-0000-4000-8000-000000000001', 4, 'Corrección y política', 'No prometer acciones fuera de la política del caso ficticio.', 3, 1, true)
ON CONFLICT (id) DO NOTHING;

INSERT INTO actividad (id, ruta_id, nodo_id, hito_id, rubrica_id, tipo, titulo, nivel, contenido, origen, estado_revision)
VALUES ('b5000000-0000-4000-8000-000000000002', 'b1000000-0000-4000-8000-000000000001',
        'b4000000-0000-4000-8000-000000000003', 'b2000000-0000-4000-8000-000000000002',
        'b7000000-0000-4000-8000-000000000001', 'ENSAYO', '[DEMO] Responder una consulta por chat', 1,
        '{"demo":true,"consigna":"Escribí una respuesta breve a un cliente que recibió otro talle de zapatillas.","contexto":"Caso ficticio: primero se necesita el número de pedido para revisar el cambio; no prometas un plazo antes de verificarlo.","formatoEntrega":"Respuesta escrita de tres o cuatro líneas."}'::jsonb,
        'IA', 'APROBADA')
ON CONFLICT (id) DO NOTHING;

COMMIT;
