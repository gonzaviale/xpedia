import { Link } from '@tanstack/react-router';
import { PathIcon, SwordIcon } from '@phosphor-icons/react';
import { useInscripcionActual, useRuta } from '@/modules/ruta';
import { formatearMinutos } from '@/shared/lib/format';

const itemBase =
  'flex w-full items-center gap-2.5 rounded-lg px-2.5 py-2 text-sm whitespace-nowrap text-neutral-300 no-underline hover:bg-fg/6 hover:text-neutral-300';
const itemActivo = {
  className: 'bg-accent-900 text-accent-200 hover:bg-accent-900 hover:text-accent-200',
};

export function Sidebar() {
  const inscripcion = useInscripcionActual();
  const ruta = useRuta(inscripcion.rutaSlug);

  return (
    <nav
      aria-label="Principal"
      className="flex shrink-0 gap-0.5 overflow-x-auto border-b border-line p-2 md:w-56 md:flex-col md:overflow-visible md:border-r md:border-b-0 md:px-3 md:py-5"
    >
      <p className="hidden px-2.5 pb-2 text-[11px] tracking-widest text-muted uppercase md:block">
        Tu desarrollo
      </p>
      <Link to="/ruta" className={itemBase} activeProps={itemActivo}>
        <PathIcon aria-hidden size={17} />
        Mi ruta
      </Link>
      {ruta.practicaEstrella && (
        <Link
          to="/nodos/$nodoId/reto"
          params={{ nodoId: ruta.practicaEstrella.nodoId }}
          className={itemBase}
          activeProps={itemActivo}
        >
          <SwordIcon aria-hidden size={17} />
          Práctica estrella
        </Link>
      )}
      <div className="mt-auto hidden border-t border-line px-2.5 pt-3 md:block">
        <p className="text-[11px] text-muted">Ruta activa</p>
        <p className="text-[13px]">{ruta.titulo}</p>
        <p className="text-[11px] text-muted">{formatearMinutos(inscripcion.ritmoMin)} por día</p>
      </div>
    </nav>
  );
}
