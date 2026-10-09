import { createFileRoute } from '@tanstack/react-router';
import { CuestionarioPage, cuestionarioQuery } from '@/modules/cuestionario';
import { cargarRutaActual } from '@/modules/ruta';

export const Route = createFileRoute('/_app/nodos/$nodoId/cuestionario')({
  loader: async ({ context, params }) => {
    const ruta = await cargarRutaActual(context.queryClient);
    await context.queryClient.ensureQueryData(cuestionarioQuery(ruta.id, params.nodoId));
  },
  component: function Cuestionario() {
    const { nodoId } = Route.useParams();
    return <CuestionarioPage nodoId={nodoId} />;
  },
});
