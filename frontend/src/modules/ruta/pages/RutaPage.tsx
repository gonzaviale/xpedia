import { Link } from '@tanstack/react-router';
import { SwordIcon } from '@phosphor-icons/react';
import { Note, Panel, buttonClasses } from '@/shared/ui';
import { HojaDeRuta } from '../components/HojaDeRuta';
import { RutaHero } from '../components/RutaHero';
import { useInscripcionActual, useRuta } from '../api';

export function RutaPage() {
  const inscripcion = useInscripcionActual();
  const ruta = useRuta(inscripcion.rutaSlug);
  const { practicaEstrella } = ruta;

  return (
    <div className="mx-auto flex max-w-280 animate-fade-in flex-col gap-5">
      <RutaHero ruta={ruta} inscripcion={inscripcion} />
      <div className="grid items-start gap-5 lg:grid-cols-[minmax(0,1.55fr)_minmax(0,1fr)]">
        <Panel aria-labelledby="hoja-de-ruta">
          <h2 id="hoja-de-ruta" className="m-0 mb-4 text-[17px]">
            Tu hoja de ruta
          </h2>
          <HojaDeRuta ruta={ruta} inscripcion={inscripcion} />
        </Panel>
        <aside className="flex flex-col gap-4">
          <Panel className="flex flex-col gap-3">
            <p className="text-[11px] tracking-widest text-accent-300 uppercase">
              Práctica estrella
            </p>
            <h2 className="m-0 text-[17px]">{practicaEstrella.nombre}</h2>
            <p className="text-neutral-300">{practicaEstrella.descripcion}</p>
            <Link
              to="/nodos/$nodoId/reto"
              params={{ nodoId: practicaEstrella.nodoId }}
              className={buttonClasses({ variant: 'solid', className: 'self-start' })}
            >
              <SwordIcon aria-hidden />
              Empezar el reto
            </Link>
          </Panel>
          {ruta.validacion === 'BORRADOR_IA' && (
            <Note warn>
              Esta ruta es un borrador generado con IA y todavía no la revisó una persona. Cada
              lección cita su fuente para que puedas contrastarla.
            </Note>
          )}
        </aside>
      </div>
    </div>
  );
}
