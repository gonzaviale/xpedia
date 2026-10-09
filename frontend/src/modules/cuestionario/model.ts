import { z } from 'zod';
import { confianzaSchema } from '@/modules/diagnostico';

export const preguntaSchema = z.object({
  id: z.string(),
  posicion: z.number(),
  tipo: z.enum(['CONCEPTO', 'APLICACION', 'DETALLE']),
  enunciado: z.string(),
  opciones: z.array(z.string()),
});

export const cuestionarioSchema = z.object({
  id: z.string(),
  rutaId: z.string(),
  nodoId: z.string(),
  titulo: z.string(),
  nivel: z.number(),
  preguntas: z.array(preguntaSchema).nonempty(),
});

export const respuestaEnviadaSchema = z.object({
  preguntaId: z.string(),
  elegida: z.number().int().min(0),
  confianza: confianzaSchema,
});

export const enviarCuestionarioSchema = z.object({
  actividadId: z.string(),
  iniciadoEn: z.string(),
  respuestas: z.array(respuestaEnviadaSchema).nonempty(),
});

export const correccionSchema = z.object({
  preguntaId: z.string(),
  elegida: z.number(),
  correcta: z.boolean(),
  opcionCorrecta: z.number(),
  confianza: confianzaSchema.nullish(),
  explicacion: z.string().nullish(),
});

export const intentoCuestionarioSchema = z.object({
  id: z.string(),
  actividadId: z.string(),
  puntaje: z.coerce.number(),
  aprobado: z.boolean(),
  correctas: z.number(),
  total: z.number(),
  correcciones: z.array(correccionSchema),
});

export type Pregunta = z.infer<typeof preguntaSchema>;
export type Cuestionario = z.infer<typeof cuestionarioSchema>;
export type EnviarCuestionario = z.infer<typeof enviarCuestionarioSchema>;
export type Correccion = z.infer<typeof correccionSchema>;
export type IntentoCuestionario = z.infer<typeof intentoCuestionarioSchema>;
