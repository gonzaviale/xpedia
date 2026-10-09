import { queryOptions } from '@tanstack/react-query';
import { ApiError, http } from '@/shared/api';
import type { Ruta } from '@/modules/ruta';
import { aMicroleccion } from './adaptadores';
import { microleccionesBackendSchema } from './contrato';

export const leccionKeys = {
  nodo: (rutaId: string, nodoId: string) => ['microleccion', rutaId, nodoId] as const,
};

export const microleccionQuery = (ruta: Ruta, nodoId: string) =>
  queryOptions({
    queryKey: leccionKeys.nodo(ruta.id, nodoId),
    queryFn: async ({ signal }) => {
      const nodo = ruta.hitos.flatMap((hito) => hito.nodos).find((item) => item.id === nodoId);
      const microlecciones = await http.get(
        `/rutas/${ruta.id}/nodos/${nodoId}/microlecciones`,
        microleccionesBackendSchema,
        signal,
      );
      const [primera] = microlecciones;
      if (!nodo || !primera) throw new ApiError(404, 'Este tema todavía no tiene microlección');
      return aMicroleccion(primera, nodo);
    },
    staleTime: 5 * 60_000,
  });
