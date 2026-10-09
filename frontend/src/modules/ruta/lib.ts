import type { Hito, Inscripcion, ProgresoNodo, Ruta } from './model';

export type EstadoHito = 'completado' | 'actual' | 'bloqueado';

export function hitoActual(ruta: Ruta, inscripcion: Inscripcion): Hito {
  return ruta.hitos.find((hito) => hito.id === inscripcion.hitoActualId) ?? ruta.hitos[0];
}

export function estadoDeHito(hito: Hito, actual: Hito): EstadoHito {
  if (hito.posicion < actual.posicion) return 'completado';
  if (hito.posicion === actual.posicion) return 'actual';
  return 'bloqueado';
}

export function progresoDeNodo(inscripcion: Inscripcion, nodoId: string): ProgresoNodo {
  return (
    inscripcion.progreso.find((progreso) => progreso.nodoId === nodoId) ?? {
      nodoId,
      estado: 'BLOQUEADO',
      dominio: 0,
    }
  );
}

export function avanceGeneral(inscripcion: Inscripcion) {
  const { progreso } = inscripcion;
  if (progreso.length === 0) return 0;
  return progreso.reduce((total, item) => total + item.dominio, 0) / progreso.length;
}
