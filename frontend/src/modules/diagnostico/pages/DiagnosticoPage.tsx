import { useNavigate } from '@tanstack/react-router';
import { useSuspenseQuery } from '@tanstack/react-query';
import { ArrowRightIcon } from '@phosphor-icons/react';
import { RUTA_SLUG } from '@/modules/ruta';
import { useOnboardingStore } from '@/modules/onboarding';
import { ApiError } from '@/shared/api';
import { Button, Dots, Feedback, PageHeading } from '@/shared/ui';
import { diagnosticoQuery, useCrearInscripcion } from '../api';
import { PreguntaCard } from '../components/PreguntaCard';
import { armarRespuestas, useDiagnosticoStore } from '../store';

export function DiagnosticoPage() {
  const navigate = useNavigate();
  const { preguntas } = useSuspenseQuery(diagnosticoQuery(RUTA_SLUG)).data;
  const { objetivo, ritmoMin } = useOnboardingStore();
  const { indice, elegidas, confianzas, elegir, confiar, avanzar, retroceder, reiniciar } =
    useDiagnosticoStore();
  const crearInscripcion = useCrearInscripcion();

  const pregunta = preguntas[indice] ?? preguntas[0];
  const esUltima = indice >= preguntas.length - 1;
  const respondida = elegidas[pregunta.id] !== undefined && confianzas[pregunta.id] !== undefined;

  function finalizar() {
    if (!objetivo) return;
    crearInscripcion.mutate(
      {
        rutaSlug: RUTA_SLUG,
        objetivo,
        ritmoMin,
        respuestas: armarRespuestas(
          preguntas.map((p) => p.id),
          elegidas,
          confianzas,
        ),
      },
      {
        onSuccess: () => {
          reiniciar();
          void navigate({ to: '/diagnostico/resultado' });
        },
      },
    );
  }

  const error = crearInscripcion.error;

  return (
    <main className="mx-auto flex min-h-screen max-w-195 animate-fade-in flex-col gap-5 px-4 py-10">
      <Dots
        total={preguntas.length}
        current={indice}
        label={`Pregunta ${indice + 1} de ${preguntas.length}`}
      />
      <PageHeading
        kicker={`Diagnóstico · pregunta ${indice + 1} de ${preguntas.length}`}
        title="Tu punto de partida"
        lead="Mide qué ya traés de tu trabajo actual. No hay respuestas que te perjudiquen."
      />
      <PreguntaCard
        pregunta={pregunta}
        elegida={elegidas[pregunta.id]}
        confianza={confianzas[pregunta.id]}
        onElegir={(opcion) => elegir(pregunta.id, opcion)}
        onConfiar={(valor) => confiar(pregunta.id, valor)}
      />
      {error && (
        <Feedback tone="bad" role="alert">
          {error instanceof ApiError ? error.message : 'No pudimos guardar tu diagnóstico.'}
        </Feedback>
      )}
      <div className="flex justify-between">
        <Button onClick={retroceder} disabled={indice === 0 || crearInscripcion.isPending}>
          Atrás
        </Button>
        {esUltima ? (
          <Button
            variant="solid"
            disabled={!respondida || !objetivo || crearInscripcion.isPending}
            onClick={finalizar}
          >
            {crearInscripcion.isPending ? 'Armando tu ruta…' : 'Ver resultado'}
          </Button>
        ) : (
          <Button variant="solid" disabled={!respondida} onClick={avanzar}>
            Siguiente
            <ArrowRightIcon aria-hidden />
          </Button>
        )}
      </div>
    </main>
  );
}
