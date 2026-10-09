import { HttpResponse, http as mswHttp } from 'msw';
import { describe, expect, it } from 'vitest';
import { z } from 'zod';
import { server } from '@/mocks/node';
import { ApiError, http } from './http';

const schema = z.object({ nombre: z.string() });

describe('http', () => {
  it('devuelve el cuerpo validado con el schema', async () => {
    server.use(mswHttp.get('/api/prueba', () => HttpResponse.json({ nombre: 'Ana' })));

    await expect(http.get('/prueba', schema)).resolves.toEqual({ nombre: 'Ana' });
  });

  it('convierte el ErrorResponse del backend en ApiError', async () => {
    server.use(
      mswHttp.get('/api/prueba', () =>
        HttpResponse.json(
          {
            timestamp: '2026-10-08T10:00:00',
            status: 404,
            error: 'Not Found',
            message: 'Puesto no encontrado',
            path: '/api/prueba',
            traceId: 'abc',
          },
          { status: 404 },
        ),
      ),
    );

    const error = await http.get('/prueba', schema).catch((e: unknown) => e);

    expect(error).toBeInstanceOf(ApiError);
    expect(error).toMatchObject({ status: 404, message: 'Puesto no encontrado', isNotFound: true });
  });

  it('usa un mensaje genérico cuando el error no tiene el formato esperado', async () => {
    server.use(mswHttp.get('/api/prueba', () => new HttpResponse('boom', { status: 500 })));

    const error = await http.get('/prueba', schema).catch((e: unknown) => e);

    expect(error).toMatchObject({ status: 500, isClientError: false });
  });

  it('falla en el borde cuando el contrato no se cumple', async () => {
    server.use(mswHttp.get('/api/prueba', () => HttpResponse.json({ nombre: 3 })));

    await expect(http.get('/prueba', schema)).rejects.toThrow();
  });
});
