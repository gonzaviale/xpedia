# Continuidad del backend de Xpedia

Estado de trabajo: 2026-10-09. Responsable de estas etapas: Brian.
Rama de entrega: **sesiones-Brian**. No se integró esta rama a main.

## Qué está implementado

1. Catálogo público de rutas globales PUBLICADAS, con filtros y paginación.
2. Hitos, nodos y prerrequisitos visibles de cada ruta, con orden estable.
3. Microlecciones aprobadas con fuentes; consulta de retos con rúbricas.
4. Registro con nombre, email y contraseña; login, usuario actual y logout.
5. Inscripción a una versión de ruta, objetivo elegido, meta personal opcional,
   ritmo diario y llegada estimada; creación transaccional del progreso inicial.
6. Consulta de la inscripción ACTIVA más reciente del usuario autenticado.

Las respuestas del catálogo representan contenido, mientras que progreso_nodo
representa el avance de una persona en una inscripción. El mismo nodo puede tener
progresos distintos en distintas inscripciones.

## Decisiones y límites acordados

- Solo se modificó backend. No se conectaron formularios de acceso ni onboarding
  del frontend a estos endpoints nuevos; las inscripciones/diagnóstico siguen en MSW.
- Autenticación inicial: email y contraseña, sesiones persistidas en PostgreSQL.
  Google, verificación por email y recuperación de contraseña quedan para después.
- La cuenta pública siempre se registra como PERSONA y ACTIVO, sin elegir rol.
  El registro no inicia sesión: se debe ejecutar login después.
- BCrypt guarda hashes con salt; nunca se devuelve contraseña ni hash.
- La cookie SESSION es HttpOnly, SameSite=Lax, se rota al iniciar sesión y tiene
  vencimiento por inactividad (30 minutos configurables). Secure=false solo para
  desarrollo HTTP; revisar configuración de cookies/CORS al desplegar.
- Las escrituras exigen CSRF. Después de login/logout se debe renovar el token.
- El catálogo sigue siendo público; puestos exige ADMIN_XPEDIA. Las inscripciones
  pertenecen al usuario de la sesión, no al usuario que alguien envíe en el JSON.
- Diagnóstico y personalización están **pendientes**. El progreso inicial no mide
  conocimientos: dominio y nivel son 0, y no se aprueban actividades automáticamente.
- DISPONIBLE es el estado persistido y del frontend equivalente a "SIGUIENTE".
- Una sola inscripción abierta (ACTIVA o PAUSADA) por persona y versión de ruta;
  puede haber varias rutas. TERMINADA permite una nueva inscripción independiente.
- La inscripción actual es la ACTIVA más reciente, con desempate por ID. Una ruta
  oculta devuelve 404; no se elige otra inscripción anterior silenciosamente.
- V3 permite objetivo null en datos anteriores porque no conocemos esa elección.

## Endpoints y contratos

Acceso:

| Método y ruta | Entrada | Resultado |
|---|---|---|
| GET /api/auth/csrf | Ninguna | Token y nombre de header |
| POST /api/auth/registro | nombre, email, contrasenia | 201, usuario público |
| POST /api/auth/login | email, contrasenia | 200, usuario y cookie |
| GET /api/auth/actual | Cookie de sesión | 200, usuario público |
| POST /api/auth/logout | Cookie y CSRF | 204 |

Inscripción:

```json
{
  "rutaId": "b1000000-0000-4000-8000-000000000001",
  "objetivo": "CAMBIAR",
  "metaPersonal": "Conseguir trabajo en atención remota",
  "ritmoMin": 20
}
```

POST /api/inscripciones: 201. GET /api/inscripciones/actual: 200.
Ambos devuelven id, rutaId, rutaSlug, objetivo, metaPersonal, estado, hitoActualId,
ritmoMin, fechaLlegadaEstimada y progreso [{nodoId, estado, dominio}].
No incluyen usuarioId ni diagnóstico. La llegada puede ser null; el objetivo
también puede ser null para una inscripción anterior a esta implementación.

El ritmo admite números JSON enteros de 1 a 1440. Meta personal opcional, máximo
2000 caracteres, recortada. Los nodos sin prerrequisitos visibles de la ruta quedan
DISPONIBLES; los otros BLOQUEADOS. Se elige el primer hito ordenado que contiene un
nodo disponible. Nodos con hitos incompatibles y prerrequisitos de otra ruta no
forman parte de la proyección pública usada para iniciar el recorrido.

La llegada se calcula como inicio UTC + ceil(horasEstimadas * 60 / ritmoMin) días,
suponiendo práctica todos los días. Si faltan horas no se inventa una fecha; si
son cero o negativas se rechaza la inscripción. El mock del frontend usa seis días
por semana; hay que unificar ese supuesto al integrar.

Errores comunes con ErrorResponse: 400 datos inválidos/recorrido sin punto de inicio;
401 sesión inválida o usuario suspendido; 403 CSRF; 404 recurso oculto/inexistente
o sin inscripción actual; 409 duplicado/restricción de persistencia.

## Arquitectura: cómo continuar

Leer primero [README](../README.md), [TESTING](../TESTING.md) y cualquier AGENTS.md
aplicable. [AGENTS.md](../AGENTS.md) deja instrucciones de entrada para asistentes.
Las skills del proyecto están en .claude/skills; Brian tiene además una
copia local en .agents/skills. No se debe asumir que esa copia local existe para
todos los compañeros.

Flujo: Controller → PresentationMapper → UseCase → Service → Repository (puerto)
→ RepositoryImpl → JPA. Domain no depende de infrastructure. Los DTOs de dominio
no contienen modelos y los DTOs web no exponen entidades JPA.

| Pieza | Qué hace | Dónde mirar |
|---|---|---|
| Presentación | Validación de JSON y adaptación HTTP | infrastructure/presentation/controller/InscripcionController |
| Caso de uso | Coordina los pasos y aporta fecha desde Clock | domain/useCase/inscripcion |
| Servicio | Visibilidad, duplicados, inicialización y transacción | domain/service/inscripcion/InscripcionService |
| Modelo | Reglas puras de llegada y progreso | domain/model/inscripcion |
| Puerto | Contrato de almacenamiento sin JPA | domain/repository/inscripcion |
| Adaptador | Convierte modelos y persiste | infrastructure/repository/jpaRepository/implementation |

Cada caso de uso tiene su mapper y bean en *UseCaseConfig; no se anota como
@Service. Los nombres del dominio van en español. La fecha se obtiene en el caso
de uso y se pasa al servicio; los servicios no inyectan Clock. No repetir reglas
de visibilidad del catálogo ni introducir consultas por cada nodo.

La transacción de creación cubre inscripción y todos los progresos. La restricción
de PostgreSQL es la protección final contra duplicados simultáneos. El caso de
uso y el controller delegan; no tienen lógica de persistencia.

## Base, migraciones y pruebas

- PostgreSQL local: Docker compose desde backend, puerto 5434 por defecto.
- Docker también permite las pruebas con PostgreSQL temporal, separado de la base
  local. No confundir las sesiones HTTP (spring_session) con las futuras sesiones
  diarias de práctica (tabla sesion del dominio).
- Flyway administra el esquema. V1 contiene el modelo inicial; V2, unicidad de
  email normalizado y sesiones HTTP; V3, objetivo de inscripción e índice actual.
- Nunca editar migraciones aplicadas ni usar docker down -v/reset para corregirlas.
  Crear una migración nueva y conservar los datos existentes.
- Xpedia.sql es un borrador local fuera de Flyway: no incluirlo en commits.
- Los *-test.sql eliminan/reconstruyen datos solo en contenedores temporales.
  Nunca ejecutarlos en desarrollo. Para Swagger usar scripts/cargar-ruta-piloto.ps1.
- Ejecutar desde backend: `.\mvnw.cmd clean verify`, con Docker funcionando.
  Compilación objetivo Java 21; esta estación usa un JDK 25 compatible.
- Una clase de pruebas por clase con comportamiento, helpers given/acción/then,
  DisplayName en español e imports explícitos. JPA y HTTP se validan contra PostgreSQL.
- JaCoCo exige 100% de líneas y ramas **por clase**, sin excluir las clases nuevas.

## Integración pendiente y siguiente etapa recomendada

1. Integrar registro/login en frontend con credentials: include. Obtener token CSRF
   y enviarlo como X-CSRF-TOKEN en POST; renovarlo después de login/logout.
2. Adaptar onboarding: enviar rutaId de la versión del catálogo, no rutaSlug;
   actualizar schemas para meta personal, campos opcionales y diagnóstico pendiente.
3. Retirar únicamente los handlers MSW de acceso/inscripción cuando el flujo real
   esté conectado y verificado. Mantener identificado el diagnóstico simulado.
4. Definir el diagnóstico como etapa propia: preguntas reales, fuentes/visibilidad,
   registro de respuestas, evaluación determinista y reglas del punto de partida.
   No copiar como reglas reales los puntajes y textos fijos del mock.
5. Después, respuestas e intentos con propiedad de inscripción y estados; finalmente
   corrección con IA según rúbricas. No hay evaluación con IA implementada todavía.

Antes de incorporar nuevas etapas, revisar Git y acuerdos con Brian. El frontend
debe tratar los errores reales de sesión, CSRF y duplicados. Cambiar ritmo o pausar
rutas no tiene endpoint todavía: no confundir el texto del mock con funcionalidad real.

## Contexto breve para otro asistente

Continuar exclusivamente el backend de Xpedia sobre la rama sesiones-Brian, verificando
primero Git, README, TESTING, AGENTS aplicables y skills de .claude/.agents. Ya hay
catálogo, contenidos con fuentes/rúbricas, registro/login por email y contraseña,
sesiones JDBC con CSRF, inscripción y progreso inicial. Diagnóstico, personalización,
respuestas/intentos y corrección con IA están pendientes. Seguir la arquitectura
existente y probar cada capa, con 100% JaCoCo por clase. No editar migraciones
aplicadas, borrar datos/volúmenes, incluir Xpedia.sql ni detener el servidor sin avisar.
Explicar el plan a Brian antes de modificar y comentar decisiones durante el trabajo.
Hacer commits específicos por etapas y subir solamente cambios validados a la rama
acordada. No asumir que main ya contiene esta rama ni que el frontend está integrado.

## Entrega y evidencia de validación

- Etapa de acceso: `3a3a149` (diseño), `f0a1708` (control de cobertura) y `28b3564`
  (registro y sesiones). Su verificación pasó 589 pruebas.
- Etapa de inscripción: `5b1aa37` (diseño y plan) y `f7b247e` (implementación,
  pruebas y contratos). La documentación de continuidad y AGENTS se entrega en un
  commit posterior de la misma rama.
- Último `clean verify`: **719 pruebas**, 0 fallos, 0 errores, 0 omitidas.
  JaCoCo: **1241/1241 líneas y 92/92 ramas**, 100% por clase; 178 clases con
  código analizable. Incluye pruebas de arquitectura, PostgreSQL y HTTP reales.
- La implementación de esta etapa agrega 20 clases de pruebas. Los fallos de datos
  inválidos, duplicados y rollback se provocan deliberadamente en contenedores temporales.
- V1/V2 y frontend se conservaron. No se borraron datos/volúmenes locales ni se
  detuvo el servidor del usuario. V3 fue validada en bases temporales; no se declara
  aplicada/verificada en la base local por esta entrega.
- Para probar localmente, reiniciar el backend con el código actualizado: Flyway
  aplicará V3 al arrancar. Si un servidor anterior sigue en 8080, no contiene estos
  endpoints hasta recargarse. Swagger: http://localhost:8080/swagger-ui.html.
- Esta evidencia confirma validación local y entrega de código en la rama, no un
  despliegue de producción ni integración del frontend.
