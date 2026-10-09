# Inscripción y progreso inicial

Diseño aprobado por Brian el 2026-10-09. Rama: `sesiones-Brian`.

## Alcance y contrato

Solo backend. Se implementan `POST /api/inscripciones` y
`GET /api/inscripciones/actual`, ambos con sesión de usuario activo; POST exige CSRF.
El usuario se obtiene de la sesión, nunca del cuerpo.

POST recibe `rutaId` (UUID de la versión elegida en el catálogo), `objetivo`
(`ARRANCAR`, `CAMBIAR`, `MEJORAR`), `metaPersonal` opcional (hasta 2000 caracteres,
recortada) y `ritmoMin` (entero entre 1 y 1440). Usar UUID evita que un slug con
varias versiones cambie silenciosamente la ruta elegida.

Devuelve 201 con `id`, `rutaId`, `rutaSlug`, `objetivo`, `metaPersonal`, `estado`,
`hitoActualId`, `ritmoMin`, `fechaLlegadaEstimada` y `progreso` (nodoId, estado,
dominio). No incluye resultados de diagnóstico ni considera respuestas diagnósticas.

La consulta actual devuelve el mismo formato: la inscripción ACTIVA más reciente
del usuario, ordenada por iniciadaEn e ID descendentes. No cambia el permiso de
tener varias rutas abiertas. Sin inscripción activa o con ruta oculta devuelve 404.

## Reglas y persistencia

- Solo rutas globales PUBLICADAS y nodos visibles del catálogo.
- La ruta debe tener hitos, nodos y al menos un nodo disponible asociado a un hito.
- Se conserva el primer hito ordenado por posición/ID como punto de partida.
- Un progreso por nodo visible; dominio y nivel iniciales 0. Sin prerrequisitos
  visibles de su recorrido: DISPONIBLE; con prerrequisitos: BLOQUEADO.
  Se respeta la misma proyección de pertenencia que el catálogo: referencias
  fuera de la ruta o con hitos incompatibles no forman parte del recorrido visible.
- Inscripción y progresos se guardan en una sola transacción.
- La restricción existente impide otra inscripción ACTIVA o PAUSADA de la misma
  persona/ruta: 409, también ante solicitudes concurrentes. TERMINADA permite otra.
- Llegada: fecha UTC de inicio + ceil(horasEstimadas * 60 / ritmoMin) días, suponiendo
  práctica todos los días. Si la ruta no declara duración, la fecha es null; duración
  cero/negativa es contenido inválido (400). No es una promesa ni un diagnóstico.
- V3 agrega el objetivo elegido a la inscripción y un índice para la consulta actual.
  Los registros anteriores conservan sus datos y objetivo null (desconocido), sin
  atribuirles el objetivo de la ruta. V1 y V2 no se editan.

## Arquitectura y pruebas

Controller → PresentationMapper → UseCase → InscripcionService → puertos de
repositorio → adaptadores JPA. Reglas puras en Inscripcion y ProgresoNodo; Clock
solo en el caso de uso, pasando fecha al servicio. DTOs sin modelos de dominio.

Pruebas por clase, consultas reales en PostgreSQL temporal y HTTP con sesiones/CSRF:
inicio, estados y orden, errores, privacidad, duplicados/concurrencia, rollback,
consulta actual, migración con datos previos y OpenAPI. `clean verify` exige 100%
de líneas y ramas por clase, sin exclusiones nuevas.

## Continuación del equipo

El frontend vigente envía rutaSlug/respuestas y espera diagnostico. Ese contrato
simulado requiere adaptación: resolver rutaId desde el catálogo, enviar cookies y
CSRF, y tratar diagnóstico como pendiente. Este trabajo no modifica frontend.
El diagnóstico, ajuste personalizado, cambios de ritmo posteriores e intentos
quedan para etapas separadas. El documento CONTINUIDAD.md recogerá el estado final.
