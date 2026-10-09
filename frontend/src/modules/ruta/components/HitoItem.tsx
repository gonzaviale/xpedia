import { CheckIcon, CrownIcon, FolderOpenIcon, LockSimpleIcon } from '@phosphor-icons/react';
import { cn } from '@/shared/lib/cn';
import type { EstadoHito } from '../lib';
import type { Hito, ProgresoNodo } from '../model';
import { NodoItem } from './NodoItem';

type HitoItemProps = {
  hito: Hito;
  estado: EstadoHito;
  progresoDe: (nodoId: string) => ProgresoNodo;
};

function MarcaDeHito({ hito, estado }: Pick<HitoItemProps, 'hito' | 'estado'>) {
  if (estado === 'completado') return <CheckIcon aria-label="Completado" />;
  if (estado === 'actual') return <span>{hito.posicion}</span>;
  return hito.esFinal ? (
    <CrownIcon aria-label="Prueba final" />
  ) : (
    <LockSimpleIcon aria-label="Bloqueado" />
  );
}

export function HitoItem({ hito, estado, progresoDe }: HitoItemProps) {
  const nodos =
    estado === 'actual'
      ? hito.nodos
      : hito.nodos.filter((nodo) => progresoDe(nodo.id).estado !== 'BLOQUEADO');

  return (
    <li
      aria-current={estado === 'actual' ? 'step' : undefined}
      className={cn(
        'relative flex gap-3.5 not-last:before:absolute not-last:before:top-8.5 not-last:before:-bottom-2.5 not-last:before:left-4 not-last:before:w-0.5 not-last:before:bg-line',
        estado === 'completado' && 'not-last:before:bg-ok/45',
      )}
    >
      <span
        className={cn(
          'z-10 flex size-8.5 shrink-0 items-center justify-center rounded-full border-[1.5px] text-[13px]',
          estado === 'completado' && 'border-ok bg-ok/16 text-ok',
          estado === 'actual' && 'animate-glow border-accent bg-accent-800 text-accent-100',
          estado === 'bloqueado' && 'border-line bg-canvas text-muted',
        )}
      >
        <MarcaDeHito hito={hito} estado={estado} />
      </span>
      <div
        className={cn(
          'min-w-0 flex-1 py-1.5',
          estado === 'actual' && 'rounded-xl border border-accent-700 bg-canvas p-3.5',
          estado === 'bloqueado' && 'opacity-65',
        )}
      >
        <h3 className="m-0 text-[15px]">{hito.titulo}</h3>
        <p className="mt-1 text-[12.5px] text-soft">{hito.objetivo}</p>
        {nodos.length > 0 && (
          <ul className="mt-2 flex flex-col divide-y divide-fg/6">
            {nodos.map((nodo) => (
              <NodoItem key={nodo.id} nodo={nodo} progreso={progresoDe(nodo.id)} />
            ))}
          </ul>
        )}
        <p className="mt-2 flex items-center gap-1.5 text-[11px] text-muted">
          <FolderOpenIcon aria-hidden />
          Evidencia: {hito.evidenciaEsperada}
        </p>
      </div>
    </li>
  );
}
