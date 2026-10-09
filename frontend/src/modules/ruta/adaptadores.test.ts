import { describe, expect, it } from 'vitest';
import { aRuta, type ContenidoNodo } from './adaptadores';
import type { HitoBackend, NodoBackend, RutaBackend } from './contrato';

const RUTA: RutaBackend = {
  id: 'r1',
  slug: 'demo',
  titulo: 'Demo',
  meta: null,
  horasEstimadas: 7.5,
  validacion: 'BORRADOR_IA',
};

function hito(id: string, posicion: number): HitoBackend {
  return { id, posicion, titulo: `Hito ${posicion}`, horasEstimadas: 1 };
}

function nodo(id: string, hitoId: string | null, posicion: number): NodoBackend {
  return {
    id,
    hitoId,
    codigo: id.toUpperCase(),
    titulo: id,
    tipo: 'TEMA',
    minutosEstimados: 15,
    posicion,
  };
}

function contenido(nodoId: string, parcial: Partial<ContenidoNodo> = {}): ContenidoNodo {
  return { nodoId, tieneMicroleccion: false, tieneCuestionario: false, reto: null, ...parcial };
}

describe('aRuta', () => {
  it('agrupa los nodos por hito y los ordena por posición', () => {
    const ruta = aRuta({
      ruta: RUTA,
      hitos: [hito('h2', 2), hito('h1', 1)],
      nodos: [
        nodo('n3', 'h2', 1),
        nodo('n2', 'h1', 2),
        nodo('n1', 'h1', 1),
        nodo('huerfano', null, 1),
      ],
      contenidos: [],
    });

    expect(ruta.hitos.map((h) => h.id)).toEqual(['h1', 'h2']);
    expect(ruta.hitos[0]?.nodos.map((n) => n.id)).toEqual(['n1', 'n2']);
    expect(ruta.hitos[1]?.nodos.map((n) => n.id)).toEqual(['n3']);
  });

  it('completa los datos opcionales que el backend no informa', () => {
    const ruta = aRuta({ ruta: RUTA, hitos: [hito('h1', 1)], nodos: [], contenidos: [] });

    expect(ruta.meta).toBe('');
    expect(ruta.hitos[0].objetivo).toBe('');
    expect(ruta.hitos[0].esFinal).toBe(false);
  });

  it('marca las actividades de cada nodo según su contenido', () => {
    const ruta = aRuta({
      ruta: RUTA,
      hitos: [hito('h1', 1)],
      nodos: [nodo('a', 'h1', 1), nodo('b', 'h1', 2), nodo('c', 'h1', 3)],
      contenidos: [
        contenido('a', { tieneMicroleccion: true }),
        contenido('b', { tieneMicroleccion: true, reto: { id: 'x', titulo: 'Reto' } }),
      ],
    });

    expect(ruta.hitos[0]?.nodos.map((n) => n.actividades)).toEqual([
      ['MICROLECCION'],
      ['MICROLECCION', 'RETO'],
      [],
    ]);
  });

  it('marca el cuestionario entre las actividades del nodo, entre la lección y el reto', () => {
    const ruta = aRuta({
      ruta: RUTA,
      hitos: [hito('h1', 1)],
      nodos: [nodo('a', 'h1', 1), nodo('b', 'h1', 2)],
      contenidos: [
        contenido('a', { tieneCuestionario: true }),
        contenido('b', {
          tieneMicroleccion: true,
          tieneCuestionario: true,
          reto: { id: 'x', titulo: 'Reto' },
        }),
      ],
    });

    expect(ruta.hitos[0]?.nodos.map((n) => n.actividades)).toEqual([
      ['CUESTIONARIO'],
      ['MICROLECCION', 'CUESTIONARIO', 'RETO'],
    ]);
  });

  it('toma la práctica estrella del primer nodo con reto', () => {
    const ruta = aRuta({
      ruta: RUTA,
      hitos: [hito('h1', 1)],
      nodos: [nodo('a', 'h1', 1), nodo('b', 'h1', 2)],
      contenidos: [
        contenido('a'),
        contenido('b', {
          reto: { id: 'x', titulo: 'Cliente enojado', contenido: { consigna: 'Respondé el chat' } },
        }),
      ],
    });

    expect(ruta.practicaEstrella).toEqual({
      nodoId: 'b',
      nombre: 'Cliente enojado',
      descripcion: 'Respondé el chat',
    });
  });

  it('deja la práctica estrella en null cuando ningún nodo tiene reto', () => {
    const ruta = aRuta({
      ruta: RUTA,
      hitos: [hito('h1', 1)],
      nodos: [nodo('a', 'h1', 1)],
      contenidos: [contenido('a', { tieneMicroleccion: true })],
    });

    expect(ruta.practicaEstrella).toBeNull();
  });

  it('falla si la ruta no tiene hitos', () => {
    expect(() => aRuta({ ruta: RUTA, hitos: [], nodos: [], contenidos: [] })).toThrow(/hitos/);
  });
});
