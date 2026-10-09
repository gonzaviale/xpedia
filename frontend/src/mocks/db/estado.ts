import type { Intento } from '@/modules/reto';
import type { Inscripcion } from '@/modules/ruta';

type Estado = {
  inscripcion: Inscripcion | null;
  intentos: Map<string, Intento>;
};

export const estado: Estado = {
  inscripcion: null,
  intentos: new Map(),
};

export function reiniciarEstado() {
  estado.inscripcion = null;
  estado.intentos = new Map();
}
