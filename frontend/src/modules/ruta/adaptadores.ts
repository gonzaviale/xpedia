import type { HitoBackend, NodoBackend, RetoResumenBackend, RutaBackend } from './contrato';
import type { Hito, Nodo, Ruta, TipoActividad } from './model';

export type ContenidoNodo = {
  nodoId: string;
  tieneMicroleccion: boolean;
  reto: RetoResumenBackend | null;
};

type Entrada = {
  ruta: RutaBackend;
  hitos: HitoBackend[];
  nodos: NodoBackend[];
  contenidos: ContenidoNodo[];
};

const porPosicion = (a: { posicion?: number | null }, b: { posicion?: number | null }) =>
  (a.posicion ?? 0) - (b.posicion ?? 0);

function actividadesDe(contenido: ContenidoNodo | undefined): TipoActividad[] {
  const actividades: TipoActividad[] = [];
  if (contenido?.tieneMicroleccion) actividades.push('MICROLECCION');
  if (contenido?.reto) actividades.push('RETO');
  return actividades;
}

function practicaEstrellaDe(
  hitos: Hito[],
  contenidos: Map<string, ContenidoNodo>,
): Ruta['practicaEstrella'] {
  for (const nodo of hitos.flatMap((hito) => hito.nodos)) {
    const reto = contenidos.get(nodo.id)?.reto;
    if (reto) return { nodoId: nodo.id, nombre: reto.titulo, descripcion: consignaDe(reto) };
  }
  return null;
}

export function aRuta({ ruta, hitos, nodos, contenidos }: Entrada): Ruta {
  const contenidoPorNodo = new Map(contenidos.map((contenido) => [contenido.nodoId, contenido]));

  const nodosDe = (hitoId: string): Nodo[] =>
    nodos
      .filter((nodo) => nodo.hitoId === hitoId)
      .sort(porPosicion)
      .map((nodo) => ({
        id: nodo.id,
        codigo: nodo.codigo,
        titulo: nodo.titulo,
        tipo: nodo.tipo,
        minutosEstimados: nodo.minutosEstimados ?? 0,
        actividades: actividadesDe(contenidoPorNodo.get(nodo.id)),
      }));

  const hitosFront: Hito[] = [...hitos].sort(porPosicion).map((hito) => ({
    id: hito.id,
    posicion: hito.posicion,
    titulo: hito.titulo,
    objetivo: hito.objetivo ?? '',
    horasEstimadas: hito.horasEstimadas,
    esFinal: hito.esFinal ?? false,
    evidenciaEsperada: hito.evidenciaEsperada ?? '',
    nodos: nodosDe(hito.id),
  }));

  const [primero, ...resto] = hitosFront;
  if (!primero) throw new Error('La ruta todavía no tiene hitos');

  return {
    id: ruta.id,
    slug: ruta.slug,
    titulo: ruta.titulo,
    meta: ruta.meta ?? '',
    horasEstimadas: ruta.horasEstimadas,
    validacion: ruta.validacion,
    practicaEstrella: practicaEstrellaDe(hitosFront, contenidoPorNodo),
    hitos: [primero, ...resto],
  };
}

function consignaDe(reto: RetoResumenBackend): string {
  const consigna = reto.contenido?.consigna;
  return typeof consigna === 'string' && consigna.trim() ? consigna : reto.titulo;
}
