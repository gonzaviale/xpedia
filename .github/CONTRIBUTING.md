# Guía de Contribución

## Ramas

```
tipo/descripcion-corta
```

| Tipo | Cuándo |
|------|--------|
| `feat` | Nueva funcionalidad |
| `fix` | Bug |
| `hotfix` | Urgencia en producción |
| `refactor` | Cambio interno sin alterar el comportamiento |
| `docs` | Solo documentación |
| `test` | Agregar o corregir tests |
| `chore` | Config, dependencias, build |

```bash
✅ feat/catalogo-de-rutas
✅ fix/paginacion-de-retos
❌ Feat/Catálogo-de-Rutas      # mayúsculas y tildes
❌ feat-catalogo-de-rutas      # guion en vez de /
```

---

## Commits

Seguimos [Conventional Commits](https://www.conventionalcommits.org/):

```
tipo(scope): descripcion
```

- `scope` es opcional: el módulo afectado (`rutas`, `retos`, `microlecciones`, `puestos`, `frontend`, `backend`).
- Descripción en imperativo, en minúscula y sin punto final.
- Tipos: `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`, `build`, `ci`.

```bash
✅ feat(rutas): agregar catalogo publico con filtros y detalle
✅ fix(retos): corregir orden de criterios en la rubrica
✅ test(backend): completar cobertura de puesto
✅ chore: scaffold frontend with vite, react and typescript
❌ Agregue cosas nuevas            # sin tipo
❌ feat(rutas): Agregar catalogo.  # mayúscula y punto final
```

---

## Código

**Java (backend)**
- Clases `PascalCase`, métodos y variables `camelCase`, constantes `UPPER_SNAKE_CASE`
- Clean Architecture: `domain` no depende de `infrastructure`
- Lógica de negocio solo en `domain/service`, nunca en los controllers
- Un caso de uso por clase, con `execute()`
- Migraciones Flyway `V{n}__{descripcion}.sql`: **nunca se edita una ya aplicada**, se crea una nueva

**React (frontend)**
- Componentes `PascalCase`, hooks `useCamelCase`, todo lo demás `camelCase`
- Llamadas a la API solo desde el cliente HTTP / capa de servicios, nunca directo en componentes
- Un componente por archivo
- Respuestas de la API validadas con zod

---

## Flujo Git

```bash
git checkout dev && git pull
git checkout -b feat/nombre-corto
# ... commits ...
git rebase origin/dev
git push origin feat/nombre-corto
# Abrir PR → dev
```

**Ramas base:** `main` (producción) ← `stg` (staging) ← `dev` (integración) ← tus ramas

---

## Pull Requests

- Título con el mismo formato que un commit: `feat(rutas): agregar catalogo publico`
- Completar la plantilla de [PULL_REQUEST_TEMPLATE.md](PULL_REQUEST_TEMPLATE.md)
- Mínimo 1 aprobación, el autor no se aprueba a sí mismo
- Resolver todos los comentarios antes de mergear
- Usar **Squash and merge**; el mensaje del squash también sigue Conventional Commits
