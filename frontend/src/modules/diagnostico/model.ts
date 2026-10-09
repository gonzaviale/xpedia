import { z } from 'zod';
import { objetivoSchema } from '@/modules/ruta';

export const confianzaSchema = z.enum(['SABIA', 'DUDE', 'ADIVINE']);

export const preguntaSchema = z.object({
  id: z.string(),
  tipo: z.enum(['CONCEPTO', 'APLICACION', 'DETALLE']),
  enunciado: z.string(),
  cita: z.string().nullish(),
  opciones: z.array(z.string()),
  nodoId: z.string(),
});

export const diagnosticoSchema = z.object({
  actividadId: z.string(),
  preguntas: z.array(preguntaSchema).nonempty(),
});

export const respuestaDiagnosticoSchema = z.object({
  preguntaId: z.string(),
  elegida: z.number().int().min(0),
  confianza: confianzaSchema,
});

export const crearInscripcionSchema = z.object({
  rutaSlug: z.string(),
  objetivo: objetivoSchema,
  ritmoMin: z.number().int().positive(),
  respuestas: z.array(respuestaDiagnosticoSchema),
});

export type Confianza = z.infer<typeof confianzaSchema>;
export type Pregunta = z.infer<typeof preguntaSchema>;
export type Diagnostico = z.infer<typeof diagnosticoSchema>;
export type RespuestaDiagnostico = z.infer<typeof respuestaDiagnosticoSchema>;
export type CrearInscripcion = z.infer<typeof crearInscripcionSchema>;
