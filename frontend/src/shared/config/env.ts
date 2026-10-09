import { z } from 'zod';

// completo: todo lo atiende MSW. parcial: MSW solo atiende lo que el backend todavía no tiene.
// ninguno: todo va al backend real. "true" y "false" se mantienen por compatibilidad.
const modoMocksSchema = z
  .enum(['completo', 'parcial', 'ninguno', 'true', 'false'])
  .default('completo')
  .transform((value) => {
    if (value === 'true') return 'completo';
    if (value === 'false') return 'ninguno';
    return value;
  });

const envSchema = z.object({
  VITE_API_URL: z.string().default('/api'),
  VITE_API_MOCKS: modoMocksSchema,
  VITE_RUTA_SLUG: z.string().default('atencion-al-cliente-remota'),
  // Provisorio hasta que haya identificación de personas: viaja en el cuerpo de POST /intentos.
  VITE_USUARIO_ID: z.string().default('00000000-0000-4000-8000-000000000001'),
});

const parsed = envSchema.parse(import.meta.env);

export type ModoMocks = typeof parsed.VITE_API_MOCKS;

export const env = {
  apiUrl: parsed.VITE_API_URL,
  mocks: parsed.VITE_API_MOCKS,
  rutaSlug: parsed.VITE_RUTA_SLUG,
  usuarioId: parsed.VITE_USUARIO_ID,
};
