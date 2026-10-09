# Plan: inscripción y progreso inicial

Diseño aprobado: 2026-10-09-inscripcion-progreso-design.md.

1. Documentar contrato, decisiones y alcance aprobado; commit de diseño.
2. Agregar modelos/reglas, puertos, servicio y casos de uso. Agregar V3 y adaptadores
   de inscripción/progreso, presentación y configuración. Tests por clase y de JPA.
3. Verificar HTTP real: sesión/CSRF, privacidad, duplicados, concurrencia, rollback,
   consulta actual y OpenAPI; comprobar migración sobre datos anteriores.
4. Ejecutar clean verify, corregir fallos con evidencia y mantener el control JaCoCo.
5. Actualizar README, TESTING y CONTINUIDAD para compañeros/asistentes. Commit de
   implementación validada y commit de entrega; push de sesiones-Brian y verificación
   del hash remoto. Sin tocar frontend, V1/V2, Xpedia.sql ni el servidor del usuario.

No está instalada la skill writing-plans; este plan explícito sigue los patrones
de crear-use-case y escribir-tests del repositorio.
