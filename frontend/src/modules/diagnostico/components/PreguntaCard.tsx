import { Chip, Choice, Panel } from '@/shared/ui';
import type { Confianza, Pregunta } from '../model';
import { ConfianzaPicker } from './ConfianzaPicker';

const ETIQUETAS: Record<Pregunta['tipo'], string> = {
  CONCEPTO: 'Concepto',
  APLICACION: 'Aplicación',
  DETALLE: 'Detalle',
};

const LETRAS = 'abcdefghij';

type PreguntaCardProps = {
  pregunta: Pregunta;
  elegida?: number;
  confianza?: Confianza;
  onElegir: (opcion: number) => void;
  onConfiar: (confianza: Confianza) => void;
};

export function PreguntaCard({
  pregunta,
  elegida,
  confianza,
  onElegir,
  onConfiar,
}: PreguntaCardProps) {
  return (
    <Panel className="flex flex-col gap-4">
      <Chip tone="accent" className="self-start">
        {ETIQUETAS[pregunta.tipo]}
      </Chip>
      <h2 className="m-0 text-[17px] leading-snug">{pregunta.enunciado}</h2>
      {pregunta.cita && (
        <blockquote className="m-0 rounded-[10px] border-l-2 border-accent bg-canvas px-3.5 py-2.5 text-neutral-200 italic">
          {pregunta.cita}
        </blockquote>
      )}
      <div className="flex flex-col gap-2">
        {pregunta.opciones.map((opcion, index) => (
          <Choice
            key={opcion}
            letter={LETRAS[index]}
            selected={elegida === index}
            onClick={() => onElegir(index)}
          >
            {opcion}
          </Choice>
        ))}
      </div>
      {elegida !== undefined && <ConfianzaPicker valor={confianza} onChange={onConfiar} />}
    </Panel>
  );
}
