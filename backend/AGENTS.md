# Guía para trabajar en el backend

Antes de modificar código, revisar Git y leer:

1. README.md: arquitectura, contratos y ejecución local.
2. TESTING.md: matriz de pruebas, estilo y cobertura obligatoria.
3. docs/CONTINUIDAD.md: decisiones, estado de entrega e integración pendiente.
4. Skills pertinentes del repositorio en ../.claude/skills. Puede existir una copia
   local equivalente en ../.agents/skills; no asumir que está disponible para todos.

## Acuerdos del equipo

- Explicar el plan antes de modificar y comentar decisiones durante el trabajo.
- Mantener el alcance backend salvo instrucción expresa del usuario.
- Respetar la rama indicada por el usuario; verificar estado y remoto antes de mover
  ramas. La entrega documentada en CONTINUIDAD está en sesiones-Brian, no en main.
- Hacer commits específicos por etapas y subir únicamente cambios validados cuando
  el usuario haya autorizado commit/push.
- No editar migraciones aplicadas ni borrar datos/volúmenes para corregir errores.
- No incluir Xpedia.sql ni configuraciones secretas en commits.
- No detener un servidor del usuario sin avisarle.

## Implementación y validación

- Domain no depende de infrastructure. Seguir el flujo Controller → mapper web →
  UseCase → Service → puerto Repository → adaptador → JPA del README.
- Use cases planos registrados como beans; dominio en español; reglas puras en
  modelos y reglas con persistencia en servicios. Clock solo en casos de uso.
- No devolver modelos/entidades por HTTP ni incluir modelos dentro de DTOs.
- Reutilizar visibilidad y pertenencia del catálogo; consultar relaciones en lote.
- Pruebas por clase con comportamiento, repositorio real e integración HTTP.
  Usar helpers given/acción/then, DisplayName en español e imports explícitos.
- Ejecutar clean verify con Docker: JaCoCo exige 100% de líneas y ramas por clase,
  sin relajar ni excluir clases para aprobar. Los fixtures solo van a PostgreSQL temporal.
- Actualizar README, TESTING y CONTINUIDAD si cambian contratos o pendientes.

Inscripciones y acceso son reales; diagnóstico, personalización e intentos siguen
pendientes. Los mocks del frontend no prueban que esas funciones estén implementadas.
