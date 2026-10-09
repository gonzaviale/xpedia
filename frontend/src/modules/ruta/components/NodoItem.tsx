import { Link } from '@tanstack/react-router';
import {
  CheckCircleIcon,
  CircleDashedIcon,
  CircleHalfIcon,
  LockSimpleIcon,
  type Icon,
} from '@phosphor-icons/react';
import { formatearMinutos } from '@/shared/lib/format';
import { buttonClasses } from '@/shared/ui';
import type { EstadoNodo, Nodo, ProgresoNodo } from '../model';

const iconos: Record<EstadoNodo, { icono: Icon; clase: string; texto: string }> = {
  DOMINADO: { icono: CheckCircleIcon, clase: 'text-ok', texto: 'Dominado' },
  EN_CURSO: { icono: CircleHalfIcon, clase: 'text-accent-300', texto: 'En curso' },
  DISPONIBLE: { icono: CircleDashedIcon, clase: 'text-accent-300', texto: 'Disponible' },
  BLOQUEADO: { icono: LockSimpleIcon, clase: 'text-muted', texto: 'Bloqueado' },
};

type NodoItemProps = { nodo: Nodo; progreso: ProgresoNodo };

export function NodoItem({ nodo, progreso }: NodoItemProps) {
  const { icono: Estado, clase, texto } = iconos[progreso.estado];
  const habilitado = progreso.estado !== 'BLOQUEADO';

  return (
    <li className="flex flex-wrap items-center gap-2 py-1.5 text-[13px]">
      <Estado aria-label={texto} size={16} className={clase} />
      <span className={habilitado ? undefined : 'text-soft'}>{nodo.titulo}</span>
      <span className="text-[11px] text-muted">{formatearMinutos(nodo.minutosEstimados)}</span>
      {habilitado && (
        <span className="ml-auto flex gap-2">
          {nodo.actividades.includes('MICROLECCION') && (
            <Link
              to="/nodos/$nodoId/leccion"
              params={{ nodoId: nodo.id }}
              className={buttonClasses({ size: 'sm' })}
            >
              Lección
            </Link>
          )}
          {nodo.actividades.includes('CUESTIONARIO') && (
            <Link
              to="/nodos/$nodoId/cuestionario"
              params={{ nodoId: nodo.id }}
              className={buttonClasses({ size: 'sm' })}
            >
              Cuestionario
            </Link>
          )}
          {nodo.actividades.includes('RETO') && (
            <Link
              to="/nodos/$nodoId/reto"
              params={{ nodoId: nodo.id }}
              className={buttonClasses({ size: 'sm', variant: 'primary' })}
            >
              Reto
            </Link>
          )}
        </span>
      )}
    </li>
  );
}
