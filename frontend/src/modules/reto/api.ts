import { queryOptions, useMutation, useQueryClient } from '@tanstack/react-query';
import { http } from '@/shared/api';
import { intentoSchema, retoSchema, type EnviarIntento } from './model';

export const retoKeys = {
  nodo: (nodoId: string) => ['reto', nodoId] as const,
  intento: (intentoId: string) => ['intento', intentoId] as const,
};

export const retoQuery = (nodoId: string) =>
  queryOptions({
    queryKey: retoKeys.nodo(nodoId),
    queryFn: ({ signal }) => http.get(`/nodos/${nodoId}/reto`, retoSchema, signal),
    staleTime: 5 * 60_000,
  });

export const intentoQuery = (intentoId: string) =>
  queryOptions({
    queryKey: retoKeys.intento(intentoId),
    queryFn: ({ signal }) => http.get(`/intentos/${intentoId}`, intentoSchema, signal),
    staleTime: Infinity,
  });

export function useEnviarIntento(actividadId: string) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (datos: EnviarIntento) =>
      http.post(`/actividades/${actividadId}/intentos`, datos, intentoSchema),
    onSuccess: (intento) => {
      queryClient.setQueryData(intentoQuery(intento.id).queryKey, intento);
    },
  });
}
