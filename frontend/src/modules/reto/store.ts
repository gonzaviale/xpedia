import { create } from 'zustand';
import { persist } from 'zustand/middleware';

type BorradoresState = {
  porActividad: Record<string, string>;
  guardar: (actividadId: string, texto: string) => void;
  descartar: (actividadId: string) => void;
};

export const useBorradoresStore = create<BorradoresState>()(
  persist(
    (set) => ({
      porActividad: {},
      guardar: (actividadId, texto) =>
        set((s) => ({ porActividad: { ...s.porActividad, [actividadId]: texto } })),
      descartar: (actividadId) =>
        set((s) => ({
          porActividad: Object.fromEntries(
            Object.entries(s.porActividad).filter(([id]) => id !== actividadId),
          ),
        })),
    }),
    { name: 'xpedia-reto-borradores', version: 1 },
  ),
);
