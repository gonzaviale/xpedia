import { createFileRoute } from '@tanstack/react-router';
import { BienvenidaPage } from '@/modules/onboarding';

export const Route = createFileRoute('/bienvenida')({
  component: BienvenidaPage,
});
