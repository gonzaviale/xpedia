import { queryOptions, useSuspenseQuery } from '@tanstack/react-query';
import { ApiError, http } from '@/shared/api';
import { inscripcionSchema, rutaSchema, type Inscripcion } from './model';

export const RUTA_SLUG = 'atencion-al-cliente-remota';

export const rutaKeys = {
  detalle: (slug: string) => ['ruta', slug] as const,
  inscripcionActual: ['inscripcion', 'actual'] as const,
};

async function obtenerInscripcionActual(signal: AbortSignal): Promise<Inscripcion | null> {
  try {
    return await http.get('/inscripciones/actual', inscripcionSchema, signal);
  } catch (error) {
    if (error instanceof ApiError && error.isNotFound) return null;
    throw error;
  }
}

export const rutaQuery = (slug: string) =>
  queryOptions({
    queryKey: rutaKeys.detalle(slug),
    queryFn: ({ signal }) => http.get(`/rutas/${slug}`, rutaSchema, signal),
    staleTime: 5 * 60_000,
  });

export const inscripcionActualQuery = () =>
  queryOptions({
    queryKey: rutaKeys.inscripcionActual,
    queryFn: ({ signal }) => obtenerInscripcionActual(signal),
  });

export function useInscripcionActual() {
  const { data } = useSuspenseQuery(inscripcionActualQuery());
  if (!data) throw new Error('No hay una inscripción activa');
  return data;
}

export function useRuta(slug: string) {
  return useSuspenseQuery(rutaQuery(slug)).data;
}
