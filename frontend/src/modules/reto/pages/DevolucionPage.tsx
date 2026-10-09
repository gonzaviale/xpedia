import { Link } from '@tanstack/react-router';
import { useSuspenseQuery } from '@tanstack/react-query';
import {
  ArrowCounterClockwiseIcon,
  LightbulbIcon,
  ThumbsUpIcon,
  WarningCircleIcon,
} from '@phosphor-icons/react';
import { Chip, Feedback, Panel, SourcePill, buttonClasses } from '@/shared/ui';
import { intentoQuery } from '../api';
import { RubricaTabla } from '../components/RubricaTabla';
import { veredicto } from '../lib';

export function DevolucionPage({ intentoId }: { intentoId: string }) {
  const intento = useSuspenseQuery(intentoQuery(intentoId)).data;
  const { texto, tono } = veredicto(intento);
  const { feedback } = intento;

  return (
    <div className="mx-auto flex max-w-245 animate-fade-in flex-col gap-5">
      <header className="flex flex-col gap-2">
        <p className="text-[11px] tracking-widest text-accent-300 uppercase">
          Devolución del profesor de IA
        </p>
        <div className="flex flex-wrap items-center gap-3">
          <h1 className="m-0 text-[23px] sm:text-[28px]">
            {intento.puntaje}/{intento.puntajeMax}: {texto.toLowerCase()}
          </h1>
          <Chip tone={tono}>{texto}</Chip>
        </div>
        <p className="text-muted">Se aprueba con {intento.puntajeAprobacion} puntos o más.</p>
      </header>

      {intento.faltaAutomatica && (
        <Feedback tone="bad" role="alert">
          {intento.faltaAutomatica}
        </Feedback>
      )}

      <div className="grid gap-4 md:grid-cols-2">
        <Panel className="flex flex-col gap-2 border-ok/40">
          <h2 className="m-0 flex items-center gap-2 text-[15px] text-ok">
            <ThumbsUpIcon aria-hidden /> Lo mejor
          </h2>
          <p className="text-neutral-200">{feedback.loBueno}</p>
        </Panel>
        <Panel className="flex flex-col gap-2 border-warn/40">
          <h2 className="m-0 flex items-center gap-2 text-[15px] text-warn">
            <WarningCircleIcon aria-hidden /> Una cosa para mejorar
          </h2>
          <p className="text-neutral-200">{feedback.aMejorar}</p>
          {feedback.alternativa && (
            <p className="flex gap-2 rounded-[10px] bg-canvas px-3 py-2 text-[13px] text-neutral-300">
              <LightbulbIcon aria-hidden className="mt-0.5 shrink-0 text-warn" />
              <span>
                Probá con: <em>{feedback.alternativa}</em>
              </span>
            </p>
          )}
        </Panel>
      </div>

      <Panel>
        <h2 className="m-0 mb-3 text-[17px]">Rúbrica</h2>
        <RubricaTabla criterios={intento.puntajePorCriterio} />
      </Panel>

      <Panel flat className="flex flex-col gap-2">
        <h2 className="m-0 text-[15px]">Tu respuesta</h2>
        <p className="whitespace-pre-wrap text-neutral-300">{intento.respuesta}</p>
      </Panel>

      <ul className="m-0 flex list-none flex-wrap gap-2 p-0">
        {intento.fuentes.map((fuente) => (
          <li key={fuente.titulo}>
            <SourcePill
              titulo={fuente.titulo}
              licencia={fuente.licencia}
              url={fuente.url ?? undefined}
            />
          </li>
        ))}
      </ul>

      <nav className="flex justify-between gap-3" aria-label="Siguiente paso">
        <Link to="/ruta" className={buttonClasses()}>
          Volver a mi ruta
        </Link>
        <Link
          to="/nodos/$nodoId/reto"
          params={{ nodoId: intento.nodoId }}
          className={buttonClasses({ variant: 'solid' })}
        >
          <ArrowCounterClockwiseIcon aria-hidden />
          Reintentar
        </Link>
      </nav>
    </div>
  );
}
