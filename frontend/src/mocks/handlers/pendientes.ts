import { HttpResponse, delay, http } from 'msw';
import { crearInscripcionSchema } from '@/modules/diagnostico';
import { aReto, enviarIntentoSchema, retoBackendSchema } from '@/modules/reto';
import { obtenerRuta } from '@/modules/ruta';
import { ApiError, http as cliente } from '@/shared/api';
import { env } from '@/shared/config/env';
import { diagnostico } from '../db/atencion';
import { estado } from '../db/estado';
import { crearInscripcion } from '../diagnosticar';
import { errorResponse } from '../errores';
import { evaluarReto } from '../evaluar-reto';
import { LATENCIA_EVALUACION_MS, LATENCIA_MS, api } from './comun';

function respuestaDeError(error: unknown, path: string) {
  if (error instanceof ApiError) return errorResponse(error.status, error.message, path);
  throw error;
}

// Lo que el backend todavía no expone: diagnóstico, inscripciones e intentos.
export const handlersPendientes = [
  http.get(api('/rutas/:slug/diagnostico'), async ({ params, request }) => {
    await delay(LATENCIA_MS);
    if (params.slug !== env.rutaSlug) {
      return errorResponse(404, 'La ruta no existe', new URL(request.url).pathname);
    }
    return HttpResponse.json(diagnostico);
  }),

  http.get(api('/inscripciones/actual'), async ({ request }) => {
    await delay(LATENCIA_MS);
    if (!estado.inscripcion) {
      return errorResponse(404, 'No tenés una inscripción activa', new URL(request.url).pathname);
    }
    return HttpResponse.json(estado.inscripcion);
  }),

  http.post(api('/inscripciones'), async ({ request }) => {
    await delay(LATENCIA_EVALUACION_MS);
    const path = new URL(request.url).pathname;
    const cuerpo = crearInscripcionSchema.safeParse(await request.json());
    if (!cuerpo.success) {
      return errorResponse(400, 'Error de validación en los datos enviados', path);
    }
    try {
      const ruta = await obtenerRuta(cuerpo.data.rutaSlug);
      estado.inscripcion = crearInscripcion(cuerpo.data, ruta);
    } catch (error) {
      return respuestaDeError(error, path);
    }
    return HttpResponse.json(estado.inscripcion, { status: 201 });
  }),

  http.post(
    api('/rutas/:rutaId/nodos/:nodoId/retos/:retoId/intentos'),
    async ({ params, request }) => {
      await delay(LATENCIA_EVALUACION_MS);
      const path = new URL(request.url).pathname;
      const cuerpo = enviarIntentoSchema.safeParse(await request.json());
      if (!cuerpo.success) {
        const mensaje = cuerpo.error.issues[0]?.message ?? 'Respuesta inválida';
        return errorResponse(400, 'Error de validación en los datos enviados', path, {
          respuesta: mensaje,
        });
      }
      try {
        const dto = await cliente.get(
          `/rutas/${String(params.rutaId)}/nodos/${String(params.nodoId)}/retos/${String(params.retoId)}`,
          retoBackendSchema,
        );
        const intento = evaluarReto(aReto(dto), cuerpo.data.respuesta);
        estado.intentos.set(intento.id, intento);
        return HttpResponse.json(intento, { status: 201 });
      } catch (error) {
        return respuestaDeError(error, path);
      }
    },
  ),

  http.get(api('/intentos/:intentoId'), async ({ params, request }) => {
    await delay(LATENCIA_MS);
    const intento = estado.intentos.get(String(params.intentoId));
    if (!intento) {
      return errorResponse(404, 'El intento no existe', new URL(request.url).pathname);
    }
    return HttpResponse.json(intento);
  }),
];
