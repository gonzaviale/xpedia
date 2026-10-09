import { queryOptions, useMutation, useQueryClient } from '@tanstack/react-query';
import { ApiError, http } from '@/shared/api';
import { aReto } from './adaptadores';
import { retosBackendSchema } from './contrato';
import { intentoSchema, type EnviarIntento } from './model';

export const retoKeys = {
  nodo: (rutaId: string, nodoId: string) => ['reto', rutaId, nodoId] as const,
  intento: (intentoId: string) => ['intento', intentoId] as const,
};

export const retoQuery = (rutaId: string, nodoId: string) =>
  queryOptions({
    queryKey: retoKeys.nodo(rutaId, nodoId),
    queryFn: async ({ signal }) => {
      const retos = await http.get(
        `/rutas/${rutaId}/nodos/${nodoId}/retos`,
        retosBackendSchema,
        signal,
      );
      const [primero] = retos;
      if (!primero) throw new ApiError(404, 'Este tema todavía no tiene reto');
      return aReto(primero);
    },
    staleTime: 5 * 60_000,
  });

export const intentoQuery = (intentoId: string) =>
  queryOptions({
    queryKey: retoKeys.intento(intentoId),
    queryFn: ({ signal }) => http.get(`/intentos/${intentoId}`, intentoSchema, signal),
    staleTime: Infinity,
  });

export type DestinoIntento = { rutaId: string; nodoId: string; retoId: string };

export function useEnviarIntento({ rutaId, nodoId, retoId }: DestinoIntento) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (datos: EnviarIntento) =>
      http.post(`/rutas/${rutaId}/nodos/${nodoId}/retos/${retoId}/intentos`, datos, intentoSchema),
    onSuccess: (intento) => {
      queryClient.setQueryData(intentoQuery(intento.id).queryKey, intento);
    },
  });
}
