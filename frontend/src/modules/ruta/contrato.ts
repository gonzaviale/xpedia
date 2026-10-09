import { z } from 'zod';
import { validacionSchema } from './model';

// DTOs tal como los devuelve el backend (GET /api/rutas/...). Solo los campos que usa el front.

export const rutaResumenBackendSchema = z.object({
  id: z.string(),
  slug: z.string(),
});

export const rutaBackendSchema = z.object({
  id: z.string(),
  slug: z.string(),
  titulo: z.string(),
  meta: z.string().nullish(),
  horasEstimadas: z.coerce.number(),
  validacion: validacionSchema,
});

export const hitoBackendSchema = z.object({
  id: z.string(),
  posicion: z.number(),
  titulo: z.string(),
  objetivo: z.string().nullish(),
  horasEstimadas: z.coerce.number(),
  esFinal: z.boolean().nullish(),
  evidenciaEsperada: z.string().nullish(),
});

export const nodoBackendSchema = z.object({
  id: z.string(),
  hitoId: z.string().nullish(),
  codigo: z.string(),
  titulo: z.string(),
  tipo: z.enum(['NUCLEO', 'OPCIONAL', 'TEMA']),
  minutosEstimados: z.number().nullish(),
  posicion: z.number().nullish(),
});

export const hitosBackendSchema = z.array(hitoBackendSchema);
export const nodosBackendSchema = z.array(nodoBackendSchema);

export type RutaBackend = z.infer<typeof rutaBackendSchema>;
export type HitoBackend = z.infer<typeof hitoBackendSchema>;
export type NodoBackend = z.infer<typeof nodoBackendSchema>;

// Lo mínimo que la ruta necesita saber de las actividades de cada nodo.
export const microleccionResumenBackendSchema = z.object({ id: z.string() });

export const cuestionarioResumenBackendSchema = z.object({ id: z.string() });

export const retoResumenBackendSchema = z.object({
  id: z.string(),
  titulo: z.string(),
  contenido: z.record(z.unknown()).nullish(),
});

export type RetoResumenBackend = z.infer<typeof retoResumenBackendSchema>;
