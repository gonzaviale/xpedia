import { microlecciones, reto, ruta } from './atencion';

// El seed de atencion.ts expresado con la forma de los DTOs del backend (/api/rutas/...), para que
// los adaptadores del front se ejerciten igual contra MSW que contra el backend real.

const nodos = ruta.hitos.flatMap((hito) =>
  hito.nodos.map((nodo, indice) => ({
    id: nodo.id,
    rutaId: ruta.id,
    hitoId: hito.id,
    codigo: nodo.codigo,
    titulo: nodo.titulo,
    tipo: nodo.tipo,
    minutosEstimados: nodo.minutosEstimados,
    posicion: indice + 1,
  })),
);

export const rutaResumenDto = { id: ruta.id, slug: ruta.slug, titulo: ruta.titulo };

export const rutaDto = {
  id: ruta.id,
  slug: ruta.slug,
  titulo: ruta.titulo,
  meta: ruta.meta,
  horasEstimadas: ruta.horasEstimadas,
  validacion: ruta.validacion,
};

export const hitosDto = ruta.hitos.map((hito) => ({
  id: hito.id,
  rutaId: ruta.id,
  posicion: hito.posicion,
  titulo: hito.titulo,
  objetivo: hito.objetivo,
  horasEstimadas: hito.horasEstimadas,
  esFinal: hito.esFinal,
  evidenciaEsperada: hito.evidenciaEsperada,
}));

export const nodosDto = nodos;

export function microleccionesDto(nodoId: string) {
  const leccion = microlecciones[nodoId];
  if (!leccion) return [];
  return [
    {
      id: leccion.id,
      rutaId: ruta.id,
      nodoId,
      titulo: leccion.titulo,
      nivel: 1,
      contenido: { minutos: leccion.minutos, bloques: leccion.bloques },
      origen: 'IA',
      fuentes: leccion.fuentes,
    },
  ];
}

export function retosDto(nodoId: string) {
  if (reto.nodoId !== nodoId) return [];
  return [
    {
      id: reto.id,
      rutaId: ruta.id,
      nodoId,
      tipo: 'ENSAYO',
      titulo: reto.titulo,
      nivel: 1,
      contenido: { consigna: reto.consigna, contexto: reto.contexto },
      origen: 'IA',
      rubrica: reto.rubrica,
    },
  ];
}
