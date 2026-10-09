import { HttpResponse, delay, http } from 'msw';
import { crearInscripcionSchema } from '@/modules/diagnostico';
import { enviarIntentoSchema } from '@/modules/reto';
import { env } from '@/shared/config/env';
import { diagnostico, microlecciones, reto, ruta } from '../db/atencion';
import { estado } from '../db/estado';
import { crearInscripcion } from '../diagnosticar';
import { errorResponse } from '../errores';
import { evaluarReto } from '../evaluar-reto';

const api = (path: string) => `${env.apiUrl}${path}`;
const LATENCIA_MS = 250;
const LATENCIA_EVALUACION_MS = 900;

export const handlers = [
  http.get(api('/rutas/:slug'), async ({ params, request }) => {
    await delay(LATENCIA_MS);
    if (params.slug !== ruta.slug) {
      return errorResponse(404, 'La ruta no existe', new URL(request.url).pathname);
    }
    return HttpResponse.json(ruta);
  }),

  http.get(api('/rutas/:slug/diagnostico'), async ({ params, request }) => {
    await delay(LATENCIA_MS);
    if (params.slug !== ruta.slug) {
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
    const cuerpo = crearInscripcionSchema.safeParse(await request.json());
    if (!cuerpo.success) {
      return errorResponse(
        400,
        'Error de validación en los datos enviados',
        new URL(request.url).pathname,
      );
    }
    estado.inscripcion = crearInscripcion(cuerpo.data);
    return HttpResponse.json(estado.inscripcion, { status: 201 });
  }),

  http.get(api('/nodos/:nodoId/microleccion'), async ({ params, request }) => {
    await delay(LATENCIA_MS);
    const leccion = microlecciones[String(params.nodoId)];
    if (!leccion) {
      return errorResponse(
        404,
        'Este tema todavía no tiene microlección',
        new URL(request.url).pathname,
      );
    }
    return HttpResponse.json(leccion);
  }),

  http.get(api('/nodos/:nodoId/reto'), async ({ params, request }) => {
    await delay(LATENCIA_MS);
    if (params.nodoId !== reto.nodoId) {
      return errorResponse(404, 'Este tema todavía no tiene reto', new URL(request.url).pathname);
    }
    return HttpResponse.json(reto);
  }),

  http.post(api('/actividades/:actividadId/intentos'), async ({ params, request }) => {
    await delay(LATENCIA_EVALUACION_MS);
    const path = new URL(request.url).pathname;
    if (params.actividadId !== reto.id) {
      return errorResponse(404, 'La actividad no existe', path);
    }
    const cuerpo = enviarIntentoSchema.safeParse(await request.json());
    if (!cuerpo.success) {
      const mensaje = cuerpo.error.issues[0]?.message ?? 'Respuesta inválida';
      return errorResponse(400, 'Error de validación en los datos enviados', path, {
        respuesta: mensaje,
      });
    }
    const intento = evaluarReto(reto.id, cuerpo.data.respuesta);
    estado.intentos.set(intento.id, intento);
    return HttpResponse.json(intento, { status: 201 });
  }),

  http.get(api('/intentos/:intentoId'), async ({ params, request }) => {
    await delay(LATENCIA_MS);
    const intento = estado.intentos.get(String(params.intentoId));
    if (!intento) {
      return errorResponse(404, 'El intento no existe', new URL(request.url).pathname);
    }
    return HttpResponse.json(intento);
  }),
];
