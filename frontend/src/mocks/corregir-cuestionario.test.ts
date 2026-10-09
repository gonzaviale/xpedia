import { describe, expect, it } from 'vitest';
import type { EnviarCuestionario } from '@/modules/cuestionario';
import { cuestionario } from './db/atencion';
import { RespuestasInvalidas, corregirCuestionario } from './corregir-cuestionario';

const CORRECTAS = cuestionario.preguntas.map((pregunta) => pregunta.correcta);

function envio(elegidas: number[]): EnviarCuestionario {
  const [primera, ...resto] = cuestionario.preguntas.map((pregunta, indice) => ({
    preguntaId: pregunta.id,
    elegida: elegidas[indice] ?? 0,
    confianza: 'SABIA' as const,
  }));
  if (!primera) throw new Error('El seed no tiene preguntas');
  return {
    actividadId: cuestionario.id,
    iniciadoEn: '2026-10-09T15:00:00Z',
    respuestas: [primera, ...resto],
  };
}

describe('corregirCuestionario', () => {
  it('aprueba con todas las respuestas correctas', () => {
    const intento = corregirCuestionario(cuestionario, envio(CORRECTAS));

    expect(intento.puntaje).toBe(100);
    expect(intento.aprobado).toBe(true);
    expect(intento.correctas).toBe(3);
    expect(intento.total).toBe(3);
  });

  it('desaprueba con dos de tres correctas porque no llega al 80 %', () => {
    const intento = corregirCuestionario(
      cuestionario,
      envio([CORRECTAS[0] ?? 0, CORRECTAS[1] ?? 0, 3]),
    );

    expect(intento.puntaje).toBe(66.67);
    expect(intento.aprobado).toBe(false);
    expect(intento.correcciones.map((c) => c.correcta)).toEqual([true, true, false]);
  });

  it('devuelve en cada corrección la opción correcta, la explicación y la confianza', () => {
    const intento = corregirCuestionario(cuestionario, envio(CORRECTAS));

    const [primera] = intento.correcciones;
    expect(primera?.opcionCorrecta).toBe(CORRECTAS[0]);
    expect(primera?.explicacion).toBe(cuestionario.preguntas[0]?.explicacion);
    expect(primera?.confianza).toBe('SABIA');
  });

  it('rechaza una pregunta respondida dos veces', () => {
    const datos = envio(CORRECTAS);
    const [primera, ...resto] = datos.respuestas;
    if (!primera) throw new Error('Sin respuestas');

    expect(() =>
      corregirCuestionario(cuestionario, { ...datos, respuestas: [primera, primera, ...resto] }),
    ).toThrow(RespuestasInvalidas);
  });

  it('rechaza una pregunta que no pertenece al cuestionario', () => {
    const datos = envio(CORRECTAS);
    const [primera, ...resto] = datos.respuestas;
    if (!primera) throw new Error('Sin respuestas');

    expect(() =>
      corregirCuestionario(cuestionario, {
        ...datos,
        respuestas: [{ ...primera, preguntaId: 'ajena' }, ...resto],
      }),
    ).toThrow(/no pertenece/);
  });

  it('rechaza un envío al que le falta responder una pregunta', () => {
    const datos = envio(CORRECTAS);
    const [primera] = datos.respuestas;
    if (!primera) throw new Error('Sin respuestas');

    expect(() => corregirCuestionario(cuestionario, { ...datos, respuestas: [primera] })).toThrow(
      /Hay que responder las 3/,
    );
  });

  it('rechaza una opción que no existe', () => {
    expect(() => corregirCuestionario(cuestionario, envio([9, 1, 0]))).toThrow(/no existe/);
  });
});
