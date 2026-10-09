import { createFileRoute, redirect } from '@tanstack/react-router';
import { AppShell } from '@/layout/AppShell';
import { inscripcionActualQuery, rutaQuery } from '@/modules/ruta';

export const Route = createFileRoute('/_app')({
  beforeLoad: async ({ context }) => {
    const inscripcion = await context.queryClient.ensureQueryData(inscripcionActualQuery());
    if (!inscripcion) throw redirect({ to: '/bienvenida' });
    await context.queryClient.ensureQueryData(rutaQuery(inscripcion.rutaSlug));
  },
  component: AppShell,
});
