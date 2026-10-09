# Plan de implementación del acceso

Diseño aprobado: 2026-10-09-acceso-email-contrasenia-design.md.
Rama: sesiones-Brian. Alcance: backend solamente.

1. Restaurar JaCoCo por clase, ejecutar clean verify y completar los casos de
   cobertura que dejaron los refactors actuales. Commit de base validada.
2. Agregar usuario (modelo, puerto de hash, persistencia, servicio, DTOs,
   mapper, use case, wiring y presentación). Registro normalizado como PERSONA.
   Pruebas por clase y PostgreSQL, sin exponer el hash. Conservar V1.
3. Agregar V2 para sesiones JDBC y unicidad de email normalizado; probar una
   base previa, duplicados y datos preservados en contenedores de pruebas.
4. Implementar login, actual y logout con Spring Security, adaptador de sesión,
   errores JSON, CSRF, CORS y permisos de puestos. Pruebas por clase y HTTP
   real sobre PostgreSQL, incluyendo suspensión posterior y rotación/expiración.
5. Documentar contratos, ejecutar clean verify y verificar los conteos y el
   reporte por clase. Commit(s) específicos y push de los cambios validados;
   confirmar el hash remoto de sesiones-Brian.

No usar fixtures destructivos en desarrollo. No agregar Xpedia.sql ni .agents.
No detener el servidor del usuario ni borrar volúmenes. La verificación local
con PostgreSQL se realiza únicamente cuando Docker está disponible.
