import { useState } from 'react';
import { ArrowRightIcon } from '@phosphor-icons/react';
import { PreguntaCard, armarRespuestas, type Confianza } from '@/modules/diagnostico';
import { ApiError } from '@/shared/api';
import { Button, Dots, Feedback, PageHeading } from '@/shared/ui';
import { useRegistrarIntento } from '../api';
import type { Cuestionario, IntentoCuestionario } from '../model';

type ResponderCuestionarioProps = {
  cuestionario: Cuestionario;
  onTerminado: (intento: IntentoCuestionario) => void;
};

export function ResponderCuestionario({ cuestionario, onTerminado }: ResponderCuestionarioProps) {
  const { preguntas } = cuestionario;
  const [iniciadoEn] = useState(() => new Date().toISOString());
  const [indice, setIndice] = useState(0);
  const [elegidas, setElegidas] = useState<Record<string, number>>({});
  const [confianzas, setConfianzas] = useState<Record<string, Confianza>>({});
  const registrar = useRegistrarIntento();

  const pregunta = preguntas[indice] ?? preguntas[0];
  const esUltima = indice >= preguntas.length - 1;
  const respondida = elegidas[pregunta.id] !== undefined && confianzas[pregunta.id] !== undefined;

  function terminar() {
    const respuestas = armarRespuestas(
      preguntas.map((p) => p.id),
      elegidas,
      confianzas,
    );
    const [primera, ...resto] = respuestas;
    if (!primera) return;
    registrar.mutate(
      { actividadId: cuestionario.id, iniciadoEn, respuestas: [primera, ...resto] },
      { onSuccess: onTerminado },
    );
  }

  const error = registrar.error;

  return (
    <div className="mx-auto flex max-w-195 animate-fade-in flex-col gap-5">
      <Dots
        total={preguntas.length}
        current={indice}
        label={`Pregunta ${indice + 1} de ${preguntas.length}`}
      />
      <PageHeading
        kicker={`Cuestionario · pregunta ${indice + 1} de ${preguntas.length}`}
        title={cuestionario.titulo}
        lead="Antes de seguir, contá si lo sabías o lo adivinaste: así distinguimos lo que dominás de lo que acertaste de casualidad."
      />
      <PreguntaCard
        pregunta={pregunta}
        elegida={elegidas[pregunta.id]}
        confianza={confianzas[pregunta.id]}
        onElegir={(opcion) => setElegidas((actual) => ({ ...actual, [pregunta.id]: opcion }))}
        onConfiar={(valor) => setConfianzas((actual) => ({ ...actual, [pregunta.id]: valor }))}
      />
      {error && (
        <Feedback tone="bad" role="alert">
          {error instanceof ApiError ? error.message : 'No pudimos guardar tu intento.'}
        </Feedback>
      )}
      <div className="flex justify-between">
        <Button
          onClick={() => setIndice((actual) => Math.max(0, actual - 1))}
          disabled={indice === 0 || registrar.isPending}
        >
          Atrás
        </Button>
        {esUltima ? (
          <Button variant="solid" disabled={!respondida || registrar.isPending} onClick={terminar}>
            {registrar.isPending ? 'Corrigiendo…' : 'Ver resultado'}
          </Button>
        ) : (
          <Button variant="solid" disabled={!respondida} onClick={() => setIndice(indice + 1)}>
            Siguiente
            <ArrowRightIcon aria-hidden />
          </Button>
        )}
      </div>
    </div>
  );
}
