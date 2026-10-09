import {
  CalendarBlankIcon,
  ClockIcon,
  PathIcon,
  SealCheckIcon,
  SealQuestionIcon,
} from '@phosphor-icons/react';
import { formatearFecha, formatearMinutos } from '@/shared/lib/format';
import { Bar, Chip, IconBox, Panel } from '@/shared/ui';
import { avanceGeneral, hitoActual } from '../lib';
import type { Inscripcion, Ruta } from '../model';

type RutaHeroProps = { ruta: Ruta; inscripcion: Inscripcion };

export function RutaHero({ ruta, inscripcion }: RutaHeroProps) {
  const actual = hitoActual(ruta, inscripcion);
  const revisada = ruta.validacion === 'REVISADA';
  const avance = avanceGeneral(inscripcion);

  return (
    <Panel className="flex flex-col gap-5 md:flex-row md:items-center md:justify-between">
      <div className="flex min-w-0 items-start gap-3.5">
        <IconBox>
          <PathIcon aria-hidden />
        </IconBox>
        <div className="flex min-w-0 flex-col gap-2">
          <h1 className="m-0 text-[22px]">{ruta.titulo}</h1>
          <p className="text-neutral-300">{ruta.meta}</p>
          <div className="flex flex-wrap gap-2">
            <Chip tone={revisada ? 'ok' : 'warn'}>
              {revisada ? <SealCheckIcon aria-hidden /> : <SealQuestionIcon aria-hidden />}
              {revisada ? 'Revisada por una persona' : 'Borrador de IA'}
            </Chip>
            <Chip>
              <ClockIcon aria-hidden />
              {formatearMinutos(inscripcion.ritmoMin)} por día
            </Chip>
            <Chip>
              <CalendarBlankIcon aria-hidden />
              Llegada estimada: {formatearFecha(inscripcion.fechaLlegadaEstimada)}
            </Chip>
          </div>
        </div>
      </div>
      <div className="flex w-full shrink-0 flex-col gap-2 md:w-56">
        <p className="text-[22px]">
          Hito {actual.posicion} <span className="text-muted">de {ruta.hitos.length}</span>
        </p>
        <Bar value={avance} label="Avance de la ruta" />
        <p className="text-[11px] text-muted">{Math.round(avance * 100)} % de la ruta dominado</p>
      </div>
    </Panel>
  );
}
