import { createFileRoute } from '@tanstack/react-router';
import { LeccionPage, microleccionQuery } from '@/modules/leccion';
import { cargarRutaActual } from '@/modules/ruta';

export const Route = createFileRoute('/_app/nodos/$nodoId/leccion')({
  loader: async ({ context, params }) => {
    const ruta = await cargarRutaActual(context.queryClient);
    await context.queryClient.ensureQueryData(microleccionQuery(ruta, params.nodoId));
  },
  component: function Leccion() {
    const { nodoId } = Route.useParams();
    return <LeccionPage nodoId={nodoId} />;
  },
});
