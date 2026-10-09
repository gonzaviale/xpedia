import { create } from 'zustand';
import { persist } from 'zustand/middleware';

export type ObjetivoElegido = 'ARRANCAR' | 'CAMBIAR' | 'MEJORAR';

export const RITMOS_MIN = [10, 20, 60, 120] as const;
export const RITMO_RECOMENDADO_MIN = 20;

type OnboardingState = {
  paso: 0 | 1;
  objetivo: ObjetivoElegido | null;
  ritmoMin: number;
  elegirObjetivo: (objetivo: ObjetivoElegido) => void;
  elegirRitmo: (ritmoMin: number) => void;
  irAlPaso: (paso: 0 | 1) => void;
};

export const useOnboardingStore = create<OnboardingState>()(
  persist(
    (set) => ({
      paso: 0,
      objetivo: null,
      ritmoMin: RITMO_RECOMENDADO_MIN,
      elegirObjetivo: (objetivo) => set({ objetivo }),
      elegirRitmo: (ritmoMin) => set({ ritmoMin }),
      irAlPaso: (paso) => set({ paso }),
    }),
    { name: 'xpedia-onboarding', version: 1 },
  ),
);
