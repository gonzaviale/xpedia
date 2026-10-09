import { useState } from 'react';
import { useSuspenseQuery } from '@tanstack/react-query';
import { useRutaActual } from '@/modules/ruta';
import { cuestionarioQuery } from '../api';
import { ResponderCuestionario } from '../components/ResponderCuestionario';
import { ResultadoCuestionario } from '../components/ResultadoCuestionario';
import type { IntentoCuestionario } from '../model';

export function CuestionarioPage({ nodoId }: { nodoId: string }) {
  const ruta = useRutaActual();
  const cuestionario = useSuspenseQuery(cuestionarioQuery(ruta.id, nodoId)).data;
  const [intento, setIntento] = useState<IntentoCuestionario | null>(null);
  const [ronda, setRonda] = useState(0);

  if (intento) {
    return (
      <ResultadoCuestionario
        cuestionario={cuestionario}
        intento={intento}
        onReintentar={() => {
          setIntento(null);
          setRonda((actual) => actual + 1);
        }}
      />
    );
  }

  return <ResponderCuestionario key={ronda} cuestionario={cuestionario} onTerminado={setIntento} />;
}
