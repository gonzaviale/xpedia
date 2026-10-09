# Consulta de retos y rúbricas

Etapa autorizada después de microlecciones: consultar consignas y criterios de evaluación. Backend únicamente, estructura de README, rama `backlog-Brian`, commits detallados y pruebas por clase. Evaluación con IA, envío de respuestas e intentos quedan para la etapa siguiente.

## Contrato

- `GET /api/rutas/{rutaId}/nodos/{nodoId}/retos`: array de retos visibles de ese nodo.
- `GET /api/rutas/{rutaId}/nodos/{nodoId}/retos/{retoId}`: detalle con el mismo formato.
- Tipos de esta etapa: `ENSAYO`, `RETO_PROYECTO`, `DESAFIO_REAL`. Se excluyen otras mecánicas, incluidos roleplay y prueba final.
- Cada reto incluye identificadores de ruta/nodo/hito, tipo, título, nivel, origen, revisión, contenido público y rúbrica con criterios ordenados.
- `contenido` solo permite los campos de texto `consigna`, `contexto` y `formatoEntrega`. No se serializa el JSON almacenado completo. La consigna debe ser texto no vacío; los campos opcionales de otro tipo se omiten.
- Rúbrica: ID, nombre, descripción, puntaje de aprobación, máximo ponderado y criterios (ID, posición, nombre, descripción, puntaje máximo, peso y eliminatorio). Máximo ponderado = suma de `puntajeMax * peso`. Un criterio eliminatorio en cero desaprueba; esta etapa solo informa esa regla, no corrige respuestas.

## Visibilidad y datos inválidos

- Ruta global publicada y nodo de esa ruta, con hito compatible: obligatorios.
- Actividad aprobada, asociada al nodo y ruta consultados; su hito es nulo o coincide con el del nodo.
- Rúbrica global con al menos un criterio, pesos positivos y umbral entre cero y el máximo ponderado. No se muestran rúbricas privadas, vacías ni inconsistentes.
- Actividades con alguna fuente privada también se excluyen. Para consignas originales no se exige una fuente.
- UUID inválido: 400. Ruta/nodo/reto oculto o inexistente: 404. Nodo válido sin retos: 200 con `[]`.
- Sin IDs de revisores, datos de empresas, respuestas esperadas, soluciones ni configuraciones internas del evaluador.

## Persistencia y rendimiento

- Reutilizar `actividad` y mapear `rubrica_id` en la entidad existente. Agregar entidades y repositorios de rúbrica/criterio; conservar V1.
- Consultar actividades, rúbricas y criterios en lote, con cinco consultas en total incluyendo las validaciones de ruta y nodo, independientemente del número de retos.
- Retos ordenados por nivel, creación e ID. Criterios por posición e ID.
- Las rúbricas ausentes entre consultas se omiten; el detalle devuelve 404.

## Datos y pruebas

- Agregar al piloto una consigna ficticia para el nodo DEMO-03 y una rúbrica de escritura de cuatro criterios (máximo 12, aprobación 8, política eliminatoria). Es material DEMO, no acredita revisión editorial.
- Carga manual, transaccional y repetible; conservar datos y ediciones existentes.
- Pruebas por servicio, casos de uso, mappers, adaptadores, interfaces JPA y controladores; pruebas de la fórmula de dominio.
- PostgreSQL real con Flyway y validación de entidades; filtros de privacidad, referencias cruzadas, JSON mal formado, umbrales/pesos, nulos, orden estable, UUID, OpenAPI, doble carga y cantidad de consultas.
- Ejecutar `clean verify` y mantener el control de cobertura al 100% de líneas/ramas por clase analizada; después verificar el recorrido local y publicar en la rama autorizada.
