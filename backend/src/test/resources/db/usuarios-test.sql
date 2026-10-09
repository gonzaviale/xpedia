DELETE FROM spring_session;
DELETE FROM usuario;

INSERT INTO usuario (id, nombre, email, tipo, estado)
VALUES
('b9000000-0000-4000-8000-000000000001', 'Persona', 'persona@example.com', 'PERSONA', 'ACTIVO'),
('b9000000-0000-4000-8000-000000000002', 'Administrador', 'admin@example.com', 'ADMIN_XPEDIA', 'ACTIVO'),
('b9000000-0000-4000-8000-000000000003', 'Anterior', ' Anterior@Example.COM ', 'PERSONA', 'ACTIVO');
