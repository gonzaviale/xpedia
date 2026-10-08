import { useEffect, useState } from 'react';
import { initialState, type AppState } from './engine';

const KEY = 'xpedia-prototipo-v1';

function load(): AppState {
  try {
    const raw = localStorage.getItem(KEY);
    if (raw) {
      const parsed = JSON.parse(raw) as Partial<AppState>;
      if (parsed && parsed.v === 1) return { ...initialState(), ...parsed };
    }
  } catch {
    // Sin almacenamiento disponible (modo privado, etc.): arranca de cero.
  }
  return initialState();
}

export function useAppState() {
  const [state, setState] = useState<AppState>(load);
  useEffect(() => {
    try {
      localStorage.setItem(KEY, JSON.stringify(state));
    } catch {
      // Si no se puede guardar, la demo sigue funcionando en memoria.
    }
  }, [state]);
  return [state, setState] as const;
}
