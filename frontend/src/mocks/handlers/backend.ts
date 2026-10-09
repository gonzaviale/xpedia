import { HttpResponse, delay, http } from 'msw';
import { ruta } from '../db/atencion';
import {
  hitosDto,
  microleccionesDto,
  nodosDto,
  retosDto,
  rutaDto,
  rutaResumenDto,
} from '../db/contrato-backend';
import { errorResponse } from '../errores';
import { LATENCIA_BACKEND_MS, api } from './comun';

const RUTA_INEXISTENTE = 'La ruta no existe';

// Imitan los endpoints reales del backend (modo "completo"). En "parcial" los atiende el backend.
export const handlersBackend = [
  http.get(api('/rutas'), async () => {
    await delay(LATENCIA_BACKEND_MS);
    return HttpResponse.json({
      content: [rutaResumenDto],
      pageNumber: 0,
      pageSize: 100,
      totalElements: 1,
      totalPages: 1,
      first: true,
      last: true,
    });
  }),

  http.get(api('/rutas/:rutaId'), async ({ params, request }) => {
    await delay(LATENCIA_BACKEND_MS);
    if (params.rutaId !== ruta.id) {
      return errorResponse(404, RUTA_INEXISTENTE, new URL(request.url).pathname);
    }
    return HttpResponse.json(rutaDto);
  }),

  http.get(api('/rutas/:rutaId/hitos'), async ({ params, request }) => {
    await delay(LATENCIA_BACKEND_MS);
    if (params.rutaId !== ruta.id) {
      return errorResponse(404, RUTA_INEXISTENTE, new URL(request.url).pathname);
    }
    return HttpResponse.json(hitosDto);
  }),

  http.get(api('/rutas/:rutaId/nodos'), async ({ params, request }) => {
    await delay(LATENCIA_BACKEND_MS);
    if (params.rutaId !== ruta.id) {
      return errorResponse(404, RUTA_INEXISTENTE, new URL(request.url).pathname);
    }
    return HttpResponse.json(nodosDto);
  }),

  http.get(api('/rutas/:rutaId/nodos/:nodoId/microlecciones'), async ({ params, request }) => {
    await delay(LATENCIA_BACKEND_MS);
    if (params.rutaId !== ruta.id) {
      return errorResponse(404, RUTA_INEXISTENTE, new URL(request.url).pathname);
    }
    return HttpResponse.json(microleccionesDto(String(params.nodoId)));
  }),

  http.get(api('/rutas/:rutaId/nodos/:nodoId/retos'), async ({ params, request }) => {
    await delay(LATENCIA_BACKEND_MS);
    if (params.rutaId !== ruta.id) {
      return errorResponse(404, RUTA_INEXISTENTE, new URL(request.url).pathname);
    }
    return HttpResponse.json(retosDto(String(params.nodoId)));
  }),

  http.get(api('/rutas/:rutaId/nodos/:nodoId/retos/:retoId'), async ({ params, request }) => {
    await delay(LATENCIA_BACKEND_MS);
    const reto = params.rutaId === ruta.id ? retosDto(String(params.nodoId))[0] : undefined;
    if (reto?.id !== params.retoId) {
      return errorResponse(404, 'El reto no existe', new URL(request.url).pathname);
    }
    return HttpResponse.json(reto);
  }),
];
