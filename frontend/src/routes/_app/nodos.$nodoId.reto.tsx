import { createFileRoute } from '@tanstack/react-router';
import { RetoPage, retoQuery } from '@/modules/reto';

export const Route = createFileRoute('/_app/nodos/$nodoId/reto')({
  loader: ({ context, params }) => context.queryClient.ensureQueryData(retoQuery(params.nodoId)),
  component: function Reto() {
    const { nodoId } = Route.useParams();
    return <RetoPage nodoId={nodoId} />;
  },
});
