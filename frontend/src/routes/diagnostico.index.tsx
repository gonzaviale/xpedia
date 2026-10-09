import { createFileRoute } from '@tanstack/react-router';
import { DiagnosticoPage, diagnosticoQuery } from '@/modules/diagnostico';
import { RUTA_SLUG } from '@/modules/ruta';

export const Route = createFileRoute('/diagnostico/')({
  loader: ({ context }) => context.queryClient.ensureQueryData(diagnosticoQuery(RUTA_SLUG)),
  component: DiagnosticoPage,
});
