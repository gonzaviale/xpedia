import { z } from 'zod';

export const objetivoSchema = z.enum(['ARRANCAR', 'CAMBIAR', 'MEJORAR']);
export const estadoNodoSchema = z.enum(['BLOQUEADO', 'DISPONIBLE', 'EN_CURSO', 'DOMINADO']);
export const tipoActividadSchema = z.enum(['MICROLECCION', 'RETO', 'CUESTIONARIO']);
export const validacionSchema = z.enum(['BORRADOR_IA', 'REVISADA']);
export const nivelHabilidadSchema = z.enum(['INICIAL', 'MEDIO', 'FUERTE']);

export const nodoSchema = z.object({
  id: z.string(),
  codigo: z.string(),
  titulo: z.string(),
  tipo: z.enum(['NUCLEO', 'OPCIONAL', 'TEMA']),
  minutosEstimados: z.number(),
  actividades: z.array(tipoActividadSchema),
});

export const hitoSchema = z.object({
  id: z.string(),
  posicion: z.number(),
  titulo: z.string(),
  objetivo: z.string(),
  horasEstimadas: z.number(),
  esFinal: z.boolean(),
  evidenciaEsperada: z.string(),
  nodos: z.array(nodoSchema),
});

export const rutaSchema = z.object({
  id: z.string(),
  slug: z.string(),
  titulo: z.string(),
  meta: z.string(),
  horasEstimadas: z.number(),
  validacion: validacionSchema,
  practicaEstrella: z
    .object({
      nodoId: z.string(),
      nombre: z.string(),
      descripcion: z.string(),
    })
    .nullable(),
  hitos: z.array(hitoSchema).nonempty(),
});

export const progresoNodoSchema = z.object({
  nodoId: z.string(),
  estado: estadoNodoSchema,
  dominio: z.number().min(0).max(1),
});

export const inscripcionSchema = z.object({
  id: z.string(),
  rutaSlug: z.string(),
  objetivo: objetivoSchema,
  hitoActualId: z.string(),
  ritmoMin: z.number(),
  fechaLlegadaEstimada: z.string(),
  progreso: z.array(progresoNodoSchema),
  diagnostico: z.object({
    mapa: z.array(
      z.object({
        hoy: z.string(),
        remoto: z.string(),
        nivel: nivelHabilidadSchema,
      }),
    ),
    ajustes: z.array(z.string()),
  }),
});

export type Objetivo = z.infer<typeof objetivoSchema>;
export type EstadoNodo = z.infer<typeof estadoNodoSchema>;
export type TipoActividad = z.infer<typeof tipoActividadSchema>;
export type NivelHabilidad = z.infer<typeof nivelHabilidadSchema>;
export type Nodo = z.infer<typeof nodoSchema>;
export type Hito = z.infer<typeof hitoSchema>;
export type Ruta = z.infer<typeof rutaSchema>;
export type ProgresoNodo = z.infer<typeof progresoNodoSchema>;
export type Inscripcion = z.infer<typeof inscripcionSchema>;
