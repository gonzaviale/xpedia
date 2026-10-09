import { queryOptions, useSuspenseQuery, type QueryClient } from '@tanstack/react-query';
import { z } from 'zod';
import { ApiError, http, pageSchema } from '@/shared/api';
import { env } from '@/shared/config/env';
import { aRuta } from './adaptadores';
import {
  hitosBackendSchema,
  microleccionResumenBackendSchema,
  nodosBackendSchema,
  rutaBackendSchema,
  retoResumenBackendSchema,
  rutaResumenBackendSchema,
} from './contrato';
import { inscripcionSchema, type Inscripcion, type Ruta } from './model';

export const RUTA_SLUG = env.rutaSlug;

const TAMANO_PAGINA = 100;

export const rutaKeys = {
  detalle: (slug: string) => ['ruta', slug] as const,
  inscripcionActual: ['inscripcion', 'actual'] as const,
};

async function buscarRutaPorSlug(slug: string, signal?: AbortSignal) {
  for (let pagina = 0; ; pagina++) {
    const resultado = await http.get(
      `/rutas?page=${pagina}&size=${TAMANO_PAGINA}`,
      pageSchema(rutaResumenBackendSchema),
      signal,
    );
    const encontrada = resultado.content.find((ruta) => ruta.slug === slug);
    if (encontrada) return encontrada;
    if (resultado.last) throw new ApiError(404, 'La ruta no existe');
  }
}

// El backend todavía no expone el árbol completo: se arma con ruta + hitos + nodos y, por cada
// nodo, sus actividades (N+1 aceptado hasta que el backend informe qué actividades tiene cada nodo).
export async function obtenerRuta(slug: string, signal?: AbortSignal): Promise<Ruta> {
  const resumen = await buscarRutaPorSlug(slug, signal);
  const base = `/rutas/${resumen.id}`;

  const [ruta, hitos, nodos] = await Promise.all([
    http.get(base, rutaBackendSchema, signal),
    http.get(`${base}/hitos`, hitosBackendSchema, signal),
    http.get(`${base}/nodos`, nodosBackendSchema, signal),
  ]);

  const contenidos = await Promise.all(
    nodos.map(async (nodo) => {
      const [microlecciones, retos] = await Promise.all([
        http.get(
          `${base}/nodos/${nodo.id}/microlecciones`,
          z.array(microleccionResumenBackendSchema),
          signal,
        ),
        http.get(`${base}/nodos/${nodo.id}/retos`, z.array(retoResumenBackendSchema), signal),
      ]);
      return {
        nodoId: nodo.id,
        tieneMicroleccion: microlecciones.length > 0,
        reto: retos[0] ?? null,
      };
    }),
  );

  return aRuta({ ruta, hitos, nodos, contenidos });
}

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
    queryFn: ({ signal }) => obtenerRuta(slug, signal),
    staleTime: 5 * 60_000,
  });

export const inscripcionActualQuery = () =>
  queryOptions({
    queryKey: rutaKeys.inscripcionActual,
    queryFn: ({ signal }) => obtenerInscripcionActual(signal),
  });

// Para los loaders de las rutas hijas: resuelve la ruta de la inscripción activa.
export async function cargarRutaActual(queryClient: QueryClient): Promise<Ruta> {
  const inscripcion = await queryClient.ensureQueryData(inscripcionActualQuery());
  if (!inscripcion) throw new Error('No hay una inscripción activa');
  return queryClient.ensureQueryData(rutaQuery(inscripcion.rutaSlug));
}

export function useInscripcionActual() {
  const { data } = useSuspenseQuery(inscripcionActualQuery());
  if (!data) throw new Error('No hay una inscripción activa');
  return data;
}

export function useRuta(slug: string) {
  return useSuspenseQuery(rutaQuery(slug)).data;
}

export function useRutaActual() {
  const inscripcion = useInscripcionActual();
  return useRuta(inscripcion.rutaSlug);
}
