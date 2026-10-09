import { describe, expect, it } from 'vitest';
import { formatearPuntaje, lecturaDeRespuesta } from './lib';
import type { Correccion } from './model';

function correccion(parcial: Partial<Correccion>): Correccion {
  return { preguntaId: 'p1', elegida: 1, correcta: true, opcionCorrecta: 1, ...parcial };
}

describe('lecturaDeRespuesta', () => {
  it('da por sabido lo que se acertó con seguridad', () => {
    const lectura = lecturaDeRespuesta(correccion({ correcta: true, confianza: 'SABIA' }));

    expect(lectura).toEqual({ texto: 'Lo sabías', tono: 'ok' });
  });

  it('advierte que acertar dudando conviene repasarlo', () => {
    const lectura = lecturaDeRespuesta(correccion({ correcta: true, confianza: 'DUDE' }));

    expect(lectura.tono).toBe('warn');
    expect(lectura.texto).toMatch(/dudaste/);
  });

  it('no cuenta como dominado lo que se acertó adivinando', () => {
    const lectura = lecturaDeRespuesta(correccion({ correcta: true, confianza: 'ADIVINE' }));

    expect(lectura.tono).toBe('warn');
    expect(lectura.texto).toMatch(/adivinando/);
  });

  it('marca como error serio fallar con seguridad', () => {
    const lectura = lecturaDeRespuesta(correccion({ correcta: false, confianza: 'SABIA' }));

    expect(lectura.tono).toBe('bad');
    expect(lectura.texto).toMatch(/seguro/);
  });

  it.each(['DUDE', 'ADIVINE'] as const)(
    'trata un fallo con confianza %s como desconocimiento',
    (c) => {
      const lectura = lecturaDeRespuesta(correccion({ correcta: false, confianza: c }));

      expect(lectura.tono).toBe('bad');
      expect(lectura.texto).toMatch(/No lo sabías/);
    },
  );

  it('trata un fallo sin confianza informada como desconocimiento', () => {
    const lectura = lecturaDeRespuesta(correccion({ correcta: false, confianza: null }));

    expect(lectura.texto).toMatch(/No lo sabías/);
  });
});

describe('formatearPuntaje', () => {
  it('muestra los enteros sin decimales', () => {
    expect(formatearPuntaje(100)).toBe('100 %');
  });

  it('muestra dos decimales con coma cuando no es entero', () => {
    expect(formatearPuntaje(66.67)).toBe('66,67 %');
  });
});
