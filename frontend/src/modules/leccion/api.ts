import { queryOptions } from '@tanstack/react-query';
import { http } from '@/shared/api';
import { microleccionSchema } from './model';

export const leccionKeys = {
  nodo: (nodoId: string) => ['microleccion', nodoId] as const,
};

export const microleccionQuery = (nodoId: string) =>
  queryOptions({
    queryKey: leccionKeys.nodo(nodoId),
    queryFn: ({ signal }) => http.get(`/nodos/${nodoId}/microleccion`, microleccionSchema, signal),
    staleTime: 5 * 60_000,
  });
