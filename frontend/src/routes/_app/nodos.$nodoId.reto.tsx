import { createFileRoute } from '@tanstack/react-router';
import { RetoPage, retoQuery } from '@/modules/reto';
import { cargarRutaActual } from '@/modules/ruta';

export const Route = createFileRoute('/_app/nodos/$nodoId/reto')({
  loader: async ({ context, params }) => {
    const ruta = await cargarRutaActual(context.queryClient);
    await context.queryClient.ensureQueryData(retoQuery(ruta.id, params.nodoId));
  },
  component: function Reto() {
    const { nodoId } = Route.useParams();
    return <RetoPage nodoId={nodoId} />;
  },
});
