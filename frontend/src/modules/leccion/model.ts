import { z } from 'zod';

export const bloqueSchema = z.discriminatedUnion('tipo', [
  z.object({ tipo: z.literal('PARRAFO'), texto: z.string() }),
  z.object({ tipo: z.literal('LISTA'), titulo: z.string().nullish(), items: z.array(z.string()) }),
  z.object({ tipo: z.literal('EJEMPLO'), antes: z.string(), despues: z.string() }),
]);

export const fuenteSchema = z.object({
  titulo: z.string(),
  url: z.string().nullish(),
  licencia: z.string(),
  uso: z.enum(['ADAPTABLE', 'SOLO_ENLACE']),
});

export const microleccionSchema = z.object({
  id: z.string(),
  nodoId: z.string(),
  titulo: z.string(),
  minutos: z.number(),
  bloques: z.array(bloqueSchema),
  fuentes: z.array(fuenteSchema),
  retoDisponible: z.boolean(),
});

export type Bloque = z.infer<typeof bloqueSchema>;
export type Fuente = z.infer<typeof fuenteSchema>;
export type Microleccion = z.infer<typeof microleccionSchema>;
