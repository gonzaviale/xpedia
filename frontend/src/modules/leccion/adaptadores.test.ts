import { describe, expect, it } from 'vitest';
import type { Nodo } from '@/modules/ruta';
import { aMicroleccion } from './adaptadores';
import type { MicroleccionBackend } from './contrato';

const NODO: Nodo = {
  id: 'n1',
  codigo: 'N1',
  titulo: 'Nodo',
  tipo: 'TEMA',
  minutosEstimados: 20,
  actividades: ['MICROLECCION'],
};

function dto(contenido: Record<string, unknown> | null): MicroleccionBackend {
  return { id: 'm1', nodoId: 'n1', titulo: 'Lección', contenido, fuentes: [] };
}

describe('aMicroleccion', () => {
  it('usa los bloques estructurados cuando el contenido los trae', () => {
    const bloques = [{ tipo: 'PARRAFO', texto: 'Hola' }];

    const leccion = aMicroleccion(dto({ bloques }), NODO);

    expect(leccion.bloques).toEqual(bloques);
  });

  it('arma los bloques desde las claves simples del contenido', () => {
    const leccion = aMicroleccion(
      dto({ texto: 'Explicación', ejemplo: 'Hola', nota: 'Borrador' }),
      NODO,
    );

    expect(leccion.bloques).toEqual([
      { tipo: 'PARRAFO', texto: 'Explicación' },
      { tipo: 'LISTA', titulo: 'Ejemplo', items: ['Hola'] },
      { tipo: 'PARRAFO', texto: 'Borrador' },
    ]);
  });

  it('devuelve una lección sin bloques si el contenido es nulo', () => {
    expect(aMicroleccion(dto(null), NODO).bloques).toEqual([]);
  });

  it('toma los minutos del nodo salvo que el contenido los indique', () => {
    expect(aMicroleccion(dto({}), NODO).minutos).toBe(20);
    expect(aMicroleccion(dto({ minutos: 2 }), NODO).minutos).toBe(2);
  });

  it('habilita el reto solo si el nodo lo tiene', () => {
    const conReto: Nodo = { ...NODO, actividades: ['MICROLECCION', 'RETO'] };

    expect(aMicroleccion(dto({}), NODO).retoDisponible).toBe(false);
    expect(aMicroleccion(dto({}), conReto).retoDisponible).toBe(true);
  });

  it('normaliza el uso de las fuentes', () => {
    const leccion = aMicroleccion(
      {
        ...dto({}),
        fuentes: [
          { titulo: 'A', licencia: 'x', uso: 'SOLO_ENLACE' },
          { titulo: 'B', licencia: 'x', uso: 'otro' },
        ],
      },
      NODO,
    );

    expect(leccion.fuentes.map((f) => f.uso)).toEqual(['SOLO_ENLACE', 'ADAPTABLE']);
  });
});
