import { createFileRoute } from '@tanstack/react-router';
import { DevolucionPage, intentoQuery } from '@/modules/reto';

export const Route = createFileRoute('/_app/intentos/$intentoId')({
  loader: ({ context, params }) =>
    context.queryClient.ensureQueryData(intentoQuery(params.intentoId)),
  component: function Devolucion() {
    const { intentoId } = Route.useParams();
    return <DevolucionPage intentoId={intentoId} />;
  },
});
