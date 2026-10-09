import { queryOptions, useMutation } from '@tanstack/react-query';
import { z } from 'zod';
import { ApiError, http } from '@/shared/api';
import { env } from '@/shared/config/env';
import { cuestionarioSchema, intentoCuestionarioSchema, type EnviarCuestionario } from './model';

export const cuestionarioKeys = {
  nodo: (rutaId: string, nodoId: string) => ['cuestionario', rutaId, nodoId] as const,
};

export const cuestionarioQuery = (rutaId: string, nodoId: string) =>
  queryOptions({
    queryKey: cuestionarioKeys.nodo(rutaId, nodoId),
    queryFn: async ({ signal }) => {
      const cuestionarios = await http.get(
        `/rutas/${rutaId}/nodos/${nodoId}/cuestionarios`,
        z.array(cuestionarioSchema),
        signal,
      );
      const [primero] = cuestionarios;
      if (!primero) throw new ApiError(404, 'Este tema todavía no tiene cuestionario');
      return primero;
    },
    staleTime: 5 * 60_000,
  });

export function useRegistrarIntento() {
  return useMutation({
    mutationFn: (datos: EnviarCuestionario) =>
      http.post('/intentos', { usuarioId: env.usuarioId, ...datos }, intentoCuestionarioSchema),
  });
}
