import { env } from '@/shared/config/env';

export const api = (path: string) => `${env.apiUrl}${path}`;

export const LATENCIA_MS = 250;
export const LATENCIA_BACKEND_MS = 100;
export const LATENCIA_EVALUACION_MS = 900;
