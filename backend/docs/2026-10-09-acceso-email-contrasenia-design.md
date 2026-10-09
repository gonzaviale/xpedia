# Acceso con email y contraseña

Estado: aprobado por Brian el 2026-10-09. Rama de implementación: sesiones-Brian. Google queda para otra etapa.

## Objetivo y alcance

Implementar cuentas individuales reales y una identidad autenticada que luego pueda
ser usada por inscripciones e intentos. Esta etapa modifica solamente el backend.
No implementa todavía inscripciones, diagnóstico, corrección con IA, recuperación
de contraseña, verificación por email ni administración de empresas.

El registro público crea únicamente usuarios PERSONA y ACTIVO. Nunca acepta un
tipo o estado enviados por el cliente. No constituye por sí solo el PBI completo
de vistas diferenciadas para personas, empresas y administradores.

## Alternativas y elección recomendada

- Sesión del servidor con cookie HttpOnly y persistencia JDBC: recomendada para la
  web actual. Logout invalida la sesión; requiere CSRF y envío de credenciales por
  el frontend.
- JWT de acceso y renovación: requiere gestionar expiración, renovación y
  revocación; no aporta una necesidad concreta a esta primera etapa.

Usar Spring Security y Spring Session JDBC con las versiones administradas por
Spring Boot. No implementar algoritmos de contraseña ni autenticación propios.
La duración inicial es de 30 minutos de inactividad, configurable. El identificador
de sesión rota al autenticarse. Cookie HttpOnly, SameSite=Lax y Secure en HTTPS;
para desarrollo HTTP local, Secure se configura explícitamente como false.
El despliegue debe servir web y API bajo el mismo sitio; otro esquema de dominios
necesitará revisar las cookies y CORS antes de integrarlo.

## Contrato HTTP

| Endpoint | Entrada | Resultado |
|---|---|---|
| GET /api/auth/csrf | Sin cuerpo | 200 con token y headerName; establece una sesión anónima si hace falta |
| POST /api/auth/registro | nombre, email, contrasenia | 201 con el usuario creado; no inicia sesión automáticamente |
| POST /api/auth/login | email, contrasenia | 200 con usuario y cookie de sesión |
| GET /api/auth/actual | Cookie de sesión | 200 con usuario actual; 401 sin sesión válida |
| POST /api/auth/logout | Cookie y token CSRF | 204; invalida sesión y elimina cookie |

El usuario HTTP contiene id, nombre, email y tipo. Nunca contiene contraseña,
hash ni datos de otras personas. Las respuestas personales usan Cache-Control:
no-store. El cliente obtiene un token CSRF antes de registro/login y vuelve a
obtenerlo después de login/logout. Las escrituras requieren ese token.

Validaciones: nombre no vacío, máximo 100 caracteres; email válido, máximo 254
caracteres, normalizado con trim y minúsculas; contraseña de al menos 12 caracteres
y no más de 72 bytes UTF-8 para BCrypt. La contraseña no se recorta ni normaliza.
Utilizar PasswordEncoder con formato delegable para poder actualizar el algoritmo.

Email duplicado devuelve 409. Entrada inválida devuelve 400. Email desconocido,
contraseña incorrecta, hash ausente y usuario suspendido devuelven el mismo 401
genérico. No diferenciar esos casos mediante mensajes ni evitar el trabajo de
hash cuando no existe el usuario. CSRF inválido devuelve 403. Los errores usan
el ErrorResponse existente, también cuando proceden de filtros de seguridad.
Comprobar que el usuario siga ACTIVO al acceder a recursos protegidos; no basta
con el estado que tenía al iniciar sesión. Logout con token CSRF válido es
idempotente, aunque la sesión ya no tenga un usuario autenticado.

El catálogo GET de rutas, hitos, nodos, microlecciones y retos sigue siendo público.
Swagger mantiene su disponibilidad por perfil. El ABM de puestos se limita a
ADMIN_XPEDIA y no se habilita mediante registro público. Las futuras inscripciones
obtendrán el usuario desde la sesión, nunca desde un usuarioId confiado al cliente.

## Arquitectura y persistencia

Respetar Controller -> PresentationMapper -> UseCase -> Service -> Repository.
Separar usuario y acceso. Usar modelos, DTOs y mappers de dominio y presentación;
use cases sin anotaciones Spring y wiring en UsuarioUseCaseConfig/AuthUseCaseConfig.
La comparación y generación de hashes se expone mediante un puerto de dominio
implementado en infraestructura. Los detalles HTTP, cookies, SecurityContext,
CSRF y sesiones permanecen en infraestructura. Los controllers delegan el
establecimiento/cierre de sesión a un adaptador, sin reglas de negocio inline.

Reutilizar la tabla usuario de V1. Una nueva migración V2 agrega las tablas e índices
de Spring Session JDBC y unicidad por lower(trim(email)). Si hay emails existentes
que colisionan al normalizarlos, la migración debe fallar con diagnóstico y conservar
todos los registros; no fusionar, borrar ni sobrescribir usuarios automáticamente.
Flyway administra el esquema; deshabilitar la creación automática de tablas de
Spring Session. V1 y los datos existentes no se modifican.

CORS admite únicamente el origen configurado, credenciales y el header CSRF.
La integración futura del frontend requiere envío de cookies y token CSRF. Esta
etapa documenta ese requisito sin modificar frontend ni declarar conectado el
onboarding actual.

## Pruebas y etapas de entrega

1. Trabajar en backlog-Brian actualizada con main, conservar los refactors y
   restaurar el check de JaCoCo de LINE y BRANCH al 100% por clase. Completar los
   casos faltantes de la base actual sin relajar reglas ni excluir clases nuevas.
2. Registro: pruebas de modelo, servicio, use case, mappers, adaptador y repositorio
   PostgreSQL; contrato HTTP y OpenAPI; duplicados normalizados, carrera de registros,
   campos inválidos, rol fijo y hash no expuesto.
3. Sesiones: pruebas por clase y HTTP real con PostgreSQL; login, cookie, rotación,
   persistencia, expiración, logout, CSRF, CORS, suspensión posterior al login,
   credenciales incorrectas y permisos de puestos. Comprobar que el catálogo siga
   siendo público y que los errores sean JSON, sin redirecciones a un formulario.
4. Ejecutar clean verify antes de cada commit de código y publicar únicamente las
   etapas validadas. Commits separados con títulos y descripciones concretos;
   confirmar el hash remoto de backlog-Brian después de subirlos.

Los tests de PostgreSQL usan contenedores temporales; nunca la base local.
Xpedia.sql y .agents no se agregan a los commits. No detener el servidor del usuario
ni borrar volúmenes. Docker no estaba disponible durante la revisión del diseño;
la suite completa sigue pendiente y no se reutiliza el conteo histórico de 272 tests
como evidencia de la base actual.

## Después: elegir ruta y ritmo

La siguiente etapa agrega POST /api/inscripciones y lectura de la inscripción.
Resuelve meta personal libre frente a objetivo enum, selección de la inscripción
actual cuando hay varias rutas, diagnóstico separado y fecha estimada. Inicializa
el progreso en una transacción: DISPONIBLE si no hay prerrequisitos y BLOQUEADO en
caso contrario, dominio y nivel cero. SIGUIENTE necesita una decisión explícita
porque hoy no existe en la base ni en el contrato del frontend.

## Referencias técnicas

- https://docs.spring.io/spring-security/reference/7.0/servlet/authentication/session-management.html
- https://docs.spring.io/spring-security/reference/7.0/servlet/exploits/csrf.html
- https://docs.spring.io/spring-session/reference/guides/boot-jdbc.html
