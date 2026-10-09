import { z } from 'zod';

export const fuenteBackendSchema = z.object({
  titulo: z.string(),
  url: z.string().nullish(),
  licencia: z.string(),
  uso: z.string().nullish(),
});

export const microleccionBackendSchema = z.object({
  id: z.string(),
  nodoId: z.string(),
  titulo: z.string(),
  contenido: z.record(z.unknown()).nullish(),
  fuentes: z.array(fuenteBackendSchema).default([]),
});

export const microleccionesBackendSchema = z.array(microleccionBackendSchema);

export type MicroleccionBackend = z.infer<typeof microleccionBackendSchema>;
