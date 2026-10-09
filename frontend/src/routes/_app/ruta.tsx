import { createFileRoute } from '@tanstack/react-router';
import { RutaPage } from '@/modules/ruta';

export const Route = createFileRoute('/_app/ruta')({
  component: RutaPage,
});
