import { z } from 'zod';

const envSchema = z.object({
  VITE_API_URL: z.string().default('/api'),
  VITE_API_MOCKS: z
    .enum(['true', 'false'])
    .default('true')
    .transform((value) => value === 'true'),
});

const parsed = envSchema.parse(import.meta.env);

export const env = {
  apiUrl: parsed.VITE_API_URL,
  mocks: parsed.VITE_API_MOCKS,
};
