import { queryOptions, useMutation, useQueryClient } from '@tanstack/react-query';
import { http } from '@/shared/api';
import { inscripcionActualQuery, inscripcionSchema } from '@/modules/ruta';
import { diagnosticoSchema, type CrearInscripcion } from './model';

export const diagnosticoKeys = {
  detalle: (rutaSlug: string) => ['diagnostico', rutaSlug] as const,
};

export const diagnosticoQuery = (rutaSlug: string) =>
  queryOptions({
    queryKey: diagnosticoKeys.detalle(rutaSlug),
    queryFn: ({ signal }) => http.get(`/rutas/${rutaSlug}/diagnostico`, diagnosticoSchema, signal),
    staleTime: Infinity,
  });

export function useCrearInscripcion() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (datos: CrearInscripcion) => http.post('/inscripciones', datos, inscripcionSchema),
    onSuccess: (inscripcion) => {
      queryClient.setQueryData(inscripcionActualQuery().queryKey, inscripcion);
    },
  });
}
