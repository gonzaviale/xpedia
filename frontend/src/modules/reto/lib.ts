import type { Intento } from './model';

const MARGEN_CERCA = 3;

export function veredicto(intento: Pick<Intento, 'aprobado' | 'puntaje' | 'puntajeAprobacion'>) {
  if (intento.aprobado) return { texto: 'Aprobado', tono: 'ok' as const };
  const cerca = intento.puntaje >= intento.puntajeAprobacion - MARGEN_CERCA;
  return { texto: cerca ? 'Todavía no, pero cerca' : 'Todavía no', tono: 'warn' as const };
}

export function nivelDePuntaje(puntaje: number, puntajeMax: number) {
  const proporcion = puntajeMax === 0 ? 0 : puntaje / puntajeMax;
  if (proporcion >= 1) return { texto: 'Sólido', tono: 'ok' as const };
  if (proporcion >= 0.5) return { texto: 'En desarrollo', tono: 'warn' as const };
  return { texto: 'Inicial', tono: 'bad' as const };
}
