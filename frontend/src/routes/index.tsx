import { createFileRoute, redirect } from '@tanstack/react-router';
import { inscripcionActualQuery } from '@/modules/ruta';

export const Route = createFileRoute('/')({
  beforeLoad: async ({ context }) => {
    const inscripcion = await context.queryClient.ensureQueryData(inscripcionActualQuery());
    throw redirect({ to: inscripcion ? '/ruta' : '/bienvenida' });
  },
});
