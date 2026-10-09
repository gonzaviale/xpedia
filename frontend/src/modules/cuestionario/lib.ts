import type { Tone } from '@/shared/ui';
import type { Correccion } from './model';

export const PUNTAJE_APROBACION = 80;

export type Lectura = { texto: string; tono: Tone };

export function lecturaDeRespuesta({ correcta, confianza }: Correccion): Lectura {
  if (correcta) {
    if (confianza === 'ADIVINE') {
      return { texto: 'Acertaste adivinando: todavía no lo dominás', tono: 'warn' };
    }
    if (confianza === 'DUDE') {
      return { texto: 'Acertaste, pero dudaste: conviene repasarlo', tono: 'warn' };
    }
    return { texto: 'Lo sabías', tono: 'ok' };
  }
  if (confianza === 'SABIA') {
    return { texto: 'Estabas seguro/a y fallaste: revisalo con atención', tono: 'bad' };
  }
  return { texto: 'No lo sabías: ahora ya tenés la respuesta', tono: 'bad' };
}

export function formatearPuntaje(puntaje: number) {
  return `${Number.isInteger(puntaje) ? puntaje : puntaje.toFixed(2).replace('.', ',')} %`;
}
