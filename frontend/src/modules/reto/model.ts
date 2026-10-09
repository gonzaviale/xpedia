import { z } from 'zod';

export const LARGO_MINIMO_RESPUESTA = 30;

export const criterioSchema = z.object({
  id: z.string(),
  nombre: z.string(),
  descripcion: z.string(),
  puntajeMax: z.number(),
  eliminatorio: z.boolean(),
});

export const retoSchema = z.object({
  id: z.string(),
  nodoId: z.string(),
  titulo: z.string(),
  consigna: z.string(),
  contexto: z.object({
    cliente: z.object({ nombre: z.string(), descripcion: z.string() }),
    mensaje: z.string(),
    hechos: z.array(z.string()),
    politica: z.array(z.string()),
  }),
  rubrica: z.object({
    nombre: z.string(),
    puntajeAprobacion: z.number(),
    criterios: z.array(criterioSchema),
  }),
});

export const enviarIntentoSchema = z.object({
  respuesta: z
    .string()
    .trim()
    .min(
      LARGO_MINIMO_RESPUESTA,
      `Escribí al menos ${LARGO_MINIMO_RESPUESTA} caracteres para que el profesor pueda evaluarte.`,
    ),
});

export const puntajeCriterioSchema = z.object({
  criterioId: z.string(),
  nombre: z.string(),
  puntaje: z.number(),
  puntajeMax: z.number(),
  eliminatorio: z.boolean(),
  comentario: z.string(),
});

export const intentoSchema = z.object({
  id: z.string(),
  actividadId: z.string(),
  nodoId: z.string(),
  respuesta: z.string(),
  puntaje: z.number(),
  puntajeMax: z.number(),
  puntajeAprobacion: z.number(),
  aprobado: z.boolean(),
  faltaAutomatica: z.string().nullable(),
  puntajePorCriterio: z.array(puntajeCriterioSchema),
  feedback: z.object({
    loBueno: z.string(),
    aMejorar: z.string(),
    alternativa: z.string().nullable(),
  }),
  fuentes: z.array(
    z.object({ titulo: z.string(), licencia: z.string(), url: z.string().nullish() }),
  ),
  creadoEn: z.string(),
});

export type Criterio = z.infer<typeof criterioSchema>;
export type Reto = z.infer<typeof retoSchema>;
export type EnviarIntento = z.infer<typeof enviarIntentoSchema>;
export type PuntajeCriterio = z.infer<typeof puntajeCriterioSchema>;
export type Intento = z.infer<typeof intentoSchema>;
