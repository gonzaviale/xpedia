import type { RetoBackend } from './contrato';
import { contextoSchema, type Reto } from './model';

export function consignaDe(dto: RetoBackend): string {
  const consigna = dto.contenido?.consigna;
  return typeof consigna === 'string' && consigna.trim() ? consigna : dto.titulo;
}

function contextoDe(contexto: unknown): Reto['contexto'] {
  if (typeof contexto === 'string') return { hechos: [contexto], politica: [] };
  const parsed = contextoSchema.safeParse(contexto ?? {});
  return parsed.success ? parsed.data : { hechos: [], politica: [] };
}

export function aReto(dto: RetoBackend): Reto {
  return {
    id: dto.id,
    nodoId: dto.nodoId,
    titulo: dto.titulo,
    consigna: consignaDe(dto),
    contexto: contextoDe(dto.contenido?.contexto),
    rubrica: {
      nombre: dto.rubrica.nombre,
      puntajeAprobacion: dto.rubrica.puntajeAprobacion,
      criterios: dto.rubrica.criterios.map((criterio) => ({
        id: criterio.id,
        nombre: criterio.nombre,
        descripcion: criterio.descripcion ?? '',
        puntajeMax: criterio.puntajeMax,
        eliminatorio: criterio.eliminatorio,
      })),
    },
  };
}
