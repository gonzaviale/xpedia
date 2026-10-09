import { createFileRoute, redirect } from '@tanstack/react-router';
import { ResultadoPage } from '@/modules/diagnostico';
import { inscripcionActualQuery } from '@/modules/ruta';

export const Route = createFileRoute('/diagnostico/resultado')({
  beforeLoad: async ({ context }) => {
    const inscripcion = await context.queryClient.ensureQueryData(inscripcionActualQuery());
    if (!inscripcion) throw redirect({ to: '/bienvenida' });
  },
  component: ResultadoPage,
});
