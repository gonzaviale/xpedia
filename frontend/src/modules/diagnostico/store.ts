import { create } from 'zustand';
import type { Confianza, RespuestaDiagnostico } from './model';

type DiagnosticoState = {
  indice: number;
  elegidas: Record<string, number>;
  confianzas: Record<string, Confianza>;
  elegir: (preguntaId: string, opcion: number) => void;
  confiar: (preguntaId: string, confianza: Confianza) => void;
  avanzar: () => void;
  retroceder: () => void;
  reiniciar: () => void;
};

export const useDiagnosticoStore = create<DiagnosticoState>()((set) => ({
  indice: 0,
  elegidas: {},
  confianzas: {},
  elegir: (preguntaId, opcion) =>
    set((s) => ({ elegidas: { ...s.elegidas, [preguntaId]: opcion } })),
  confiar: (preguntaId, confianza) =>
    set((s) => ({ confianzas: { ...s.confianzas, [preguntaId]: confianza } })),
  avanzar: () => set((s) => ({ indice: s.indice + 1 })),
  retroceder: () => set((s) => ({ indice: Math.max(0, s.indice - 1) })),
  reiniciar: () => set({ indice: 0, elegidas: {}, confianzas: {} }),
}));

export function armarRespuestas(
  preguntaIds: string[],
  elegidas: Record<string, number>,
  confianzas: Record<string, Confianza>,
): RespuestaDiagnostico[] {
  return preguntaIds.flatMap((preguntaId) => {
    const elegida = elegidas[preguntaId];
    const confianza = confianzas[preguntaId];
    return elegida === undefined || confianza === undefined
      ? []
      : [{ preguntaId, elegida, confianza }];
  });
}
