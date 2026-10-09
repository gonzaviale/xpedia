# Xpedia

**Aprendé haciendo, no mirando.** Decís a dónde querés llegar y Xpedia te arma el camino: cada paso es practicar algo real (responder a un cliente, presentar una idea, arreglar un bug), la IA te corrige en el momento y ajusta la ruta según cómo te va. Más detalle en [docs/pitch.md](docs/pitch.md).

## Estructura del repo

| Carpeta | Contenido |
|---------|-----------|
| `backend/` | API REST: Spring Boot 4, Java 21, PostgreSQL 17 + pgvector, Flyway. Ver [backend/README.md](backend/README.md) y [backend/TESTING.md](backend/TESTING.md) |
| `frontend/` | App web: Vite, React 19, TypeScript, TanStack Router/Query, Tailwind 4, zod, MSW para mocks |
| `prototipo/` | Prototipo inicial. Ver [prototipo/README.md](prototipo/README.md) |
| `mockups/` | Versiones de los mockups |
| `docs/` | Pitch, backlog, modelo de datos y rutas de aprendizaje |
| `.claude/skills/` | Skills de Claude Code del proyecto (crear use case, escribir tests, refactor) |

---

## Cómo levantar el proyecto

### Backend

Requiere JDK 21 y Docker.

```bash
cd backend
docker compose up -d postgres
./mvnw spring-boot:run
```

- Swagger: http://localhost:8080/swagger-ui.html
- Tests: `./mvnw test` (Docker debe estar funcionando, usa Testcontainers)
- Detalle de endpoints, migraciones y piloto local en [backend/README.md](backend/README.md)

### Frontend

```bash
cd frontend
npm install
npm run dev
```

| Script | Para qué |
|--------|----------|
| `npm run dev` | Servidor de desarrollo |
| `npm run build` | Typecheck + build de producción |
| `npm run typecheck` | Chequeo de tipos |
| `npm run lint` | ESLint |
| `npm run format` | Prettier |
| `npm test` | Tests con Vitest |

---

## Contribuir

Las reglas completas están en [.github/CONTRIBUTING.md](.github/CONTRIBUTING.md). Resumen:

- **Ramas:** `tipo/descripcion-corta` (`feat/catalogo-de-rutas`), con `main` ← `stg` ← `dev` ← tus ramas.
- **Commits:** [Conventional Commits](https://www.conventionalcommits.org/), `tipo(scope): descripcion`.
- **Pull Requests:** van a `dev`, con la [plantilla](.github/PULL_REQUEST_TEMPLATE.md), 1 aprobación mínima y **Squash and merge**.
