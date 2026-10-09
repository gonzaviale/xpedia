import { Link } from '@tanstack/react-router';
import { ArrowCounterClockwiseIcon } from '@phosphor-icons/react';
import { Button, Chip, Panel, buttonClasses } from '@/shared/ui';
import { PUNTAJE_APROBACION, formatearPuntaje, lecturaDeRespuesta } from '../lib';
import type { Correccion, Cuestionario, IntentoCuestionario } from '../model';

type ResultadoCuestionarioProps = {
  cuestionario: Cuestionario;
  intento: IntentoCuestionario;
  onReintentar: () => void;
};

export function ResultadoCuestionario({
  cuestionario,
  intento,
  onReintentar,
}: ResultadoCuestionarioProps) {
  const preguntas = new Map(cuestionario.preguntas.map((pregunta) => [pregunta.id, pregunta]));

  return (
    <div className="mx-auto flex max-w-195 animate-fade-in flex-col gap-5">
      <header className="flex flex-col gap-2">
        <p className="text-[11px] tracking-widest text-accent-300 uppercase">
          Resultado · {cuestionario.titulo}
        </p>
        <div className="flex flex-wrap items-center gap-3">
          <h1 className="m-0 text-[23px] sm:text-[28px]">
            {intento.correctas} de {intento.total} correctas · {formatearPuntaje(intento.puntaje)}
          </h1>
          <Chip tone={intento.aprobado ? 'ok' : 'warn'}>
            {intento.aprobado ? 'Aprobado' : 'No aprobado'}
          </Chip>
        </div>
        <p className="text-muted">Se aprueba con {PUNTAJE_APROBACION} % de aciertos o más.</p>
      </header>

      <ol className="m-0 flex list-none flex-col gap-4 p-0">
        {intento.correcciones.map((correccion, indice) => {
          const pregunta = preguntas.get(correccion.preguntaId);
          if (!pregunta) return null;
          return (
            <li key={correccion.preguntaId}>
              <CorreccionPregunta
                numero={indice + 1}
                enunciado={pregunta.enunciado}
                opciones={pregunta.opciones}
                correccion={correccion}
              />
            </li>
          );
        })}
      </ol>

      <nav className="flex justify-between gap-3" aria-label="Siguiente paso">
        <Link to="/ruta" className={buttonClasses()}>
          Volver a mi ruta
        </Link>
        <Button variant="solid" onClick={onReintentar}>
          <ArrowCounterClockwiseIcon aria-hidden />
          Reintentar
        </Button>
      </nav>
    </div>
  );
}

type CorreccionPreguntaProps = {
  numero: number;
  enunciado: string;
  opciones: string[];
  correccion: Correccion;
};

function CorreccionPregunta({ numero, enunciado, opciones, correccion }: CorreccionPreguntaProps) {
  const lectura = lecturaDeRespuesta(correccion);

  return (
    <Panel className="flex flex-col gap-3">
      <div className="flex flex-wrap items-center justify-between gap-2">
        <h2 className="m-0 text-[15px] leading-snug">
          {numero}. {enunciado}
        </h2>
        <Chip tone={correccion.correcta ? 'ok' : 'bad'}>
          {correccion.correcta ? 'Correcta' : 'Incorrecta'}
        </Chip>
      </div>
      <dl className="m-0 flex flex-col gap-1 text-[13px]">
        <div className="flex gap-2">
          <dt className="text-muted">Elegiste:</dt>
          <dd className="m-0">{opciones[correccion.elegida]}</dd>
        </div>
        {!correccion.correcta && (
          <div className="flex gap-2">
            <dt className="text-muted">Respuesta correcta:</dt>
            <dd className="m-0">{opciones[correccion.opcionCorrecta]}</dd>
          </div>
        )}
      </dl>
      {correccion.explicacion && (
        <p className="rounded-[10px] bg-canvas px-3 py-2 text-[13px] text-neutral-300">
          {correccion.explicacion}
        </p>
      )}
      <Chip tone={lectura.tono} className="self-start">
        {lectura.texto}
      </Chip>
    </Panel>
  );
}
