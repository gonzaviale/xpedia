import { describe, expect, it } from 'vitest';
import { aReto } from './adaptadores';
import type { RetoBackend } from './contrato';

function dto(contenido: Record<string, unknown> | null): RetoBackend {
  return {
    id: 'r1',
    nodoId: 'n1',
    titulo: 'Responder un chat',
    contenido,
    rubrica: {
      nombre: 'Rúbrica',
      puntajeAprobacion: 8,
      criterios: [{ id: 'c1', nombre: 'Claridad', puntajeMax: 3, eliminatorio: false }],
    },
  };
}

describe('aReto', () => {
  it('usa la consigna del contenido y, si falta, el título', () => {
    expect(aReto(dto({ consigna: 'Escribí una respuesta' })).consigna).toBe(
      'Escribí una respuesta',
    );
    expect(aReto(dto({})).consigna).toBe('Responder un chat');
  });

  it('acepta un contexto estructurado', () => {
    const reto = aReto(
      dto({
        contexto: {
          cliente: { nombre: 'Marcelo', descripcion: 'x' },
          mensaje: 'Hola',
          hechos: ['a'],
        },
      }),
    );

    expect(reto.contexto.cliente?.nombre).toBe('Marcelo');
    expect(reto.contexto.mensaje).toBe('Hola');
    expect(reto.contexto.hechos).toEqual(['a']);
    expect(reto.contexto.politica).toEqual([]);
  });

  it('convierte un contexto de texto en un hecho', () => {
    const reto = aReto(dto({ contexto: 'Caso ficticio' }));

    expect(reto.contexto).toEqual({ hechos: ['Caso ficticio'], politica: [] });
  });

  it('devuelve un contexto vacío si el contenido no lo trae', () => {
    expect(aReto(dto(null)).contexto).toEqual({ hechos: [], politica: [] });
  });

  it('completa la descripción de los criterios que no la traen', () => {
    expect(aReto(dto({})).rubrica.criterios[0]?.descripcion).toBe('');
  });
});
