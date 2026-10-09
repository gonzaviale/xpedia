import { z } from 'zod';

export const criterioBackendSchema = z.object({
  id: z.string(),
  nombre: z.string(),
  descripcion: z.string().nullish(),
  puntajeMax: z.number(),
  eliminatorio: z.boolean(),
});

export const retoBackendSchema = z.object({
  id: z.string(),
  nodoId: z.string(),
  titulo: z.string(),
  contenido: z.record(z.unknown()).nullish(),
  rubrica: z.object({
    nombre: z.string(),
    puntajeAprobacion: z.coerce.number(),
    criterios: z.array(criterioBackendSchema),
  }),
});

export const retosBackendSchema = z.array(retoBackendSchema);

export type RetoBackend = z.infer<typeof retoBackendSchema>;
