import { createFileRoute } from '@tanstack/react-router';
import { LeccionPage, microleccionQuery } from '@/modules/leccion';

export const Route = createFileRoute('/_app/nodos/$nodoId/leccion')({
  loader: ({ context, params }) =>
    context.queryClient.ensureQueryData(microleccionQuery(params.nodoId)),
  component: function Leccion() {
    const { nodoId } = Route.useParams();
    return <LeccionPage nodoId={nodoId} />;
  },
});
