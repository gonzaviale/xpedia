import { Link } from '@tanstack/react-router';
import { useSuspenseQuery } from '@tanstack/react-query';
import { SwordIcon } from '@phosphor-icons/react';
import { useRutaActual } from '@/modules/ruta';
import { formatearMinutos } from '@/shared/lib/format';
import { Panel, PageHeading, buttonClasses } from '@/shared/ui';
import { microleccionQuery } from '../api';
import { FuentesCitadas } from '../components/FuentesCitadas';
import { LeccionBloques } from '../components/LeccionBloques';

export function LeccionPage({ nodoId }: { nodoId: string }) {
  const ruta = useRutaActual();
  const leccion = useSuspenseQuery(microleccionQuery(ruta, nodoId)).data;

  return (
    <article className="mx-auto flex max-w-195 animate-fade-in flex-col gap-6">
      <PageHeading
        kicker={`Microlección · ${formatearMinutos(leccion.minutos)}`}
        title={leccion.titulo}
      />
      <Panel className="flex flex-col gap-6">
        <LeccionBloques bloques={leccion.bloques} />
      </Panel>
      <FuentesCitadas fuentes={leccion.fuentes} />
      <nav className="flex justify-between" aria-label="Siguiente paso">
        <Link to="/ruta" className={buttonClasses()}>
          Volver a mi ruta
        </Link>
        {leccion.retoDisponible && (
          <Link
            to="/nodos/$nodoId/reto"
            params={{ nodoId }}
            className={buttonClasses({ variant: 'solid' })}
          >
            <SwordIcon aria-hidden />
            Practicar con un reto
          </Link>
        )}
      </nav>
    </article>
  );
}
