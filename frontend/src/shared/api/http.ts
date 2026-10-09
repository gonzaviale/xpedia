import type { z } from 'zod';
import { env } from '@/shared/config/env';
import { errorResponseSchema } from './types';

const MENSAJE_SIN_CONEXION = 'No pudimos conectarnos con el servidor. Revisá tu conexión.';
const MENSAJE_INESPERADO = 'Ocurrió un error inesperado. Probá de nuevo en unos minutos.';

export class ApiError extends Error {
  readonly status: number;
  readonly fieldErrors: Record<string, string>;
  readonly traceId?: string;

  constructor(
    status: number,
    message: string,
    fieldErrors: Record<string, string> = {},
    traceId?: string,
  ) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.fieldErrors = fieldErrors;
    this.traceId = traceId;
  }

  get isNotFound() {
    return this.status === 404;
  }

  get isClientError() {
    return this.status >= 400 && this.status < 500;
  }
}

type Method = 'GET' | 'POST' | 'PUT' | 'PATCH' | 'DELETE';

type RequestOptions = {
  method?: Method;
  body?: unknown;
  signal?: AbortSignal;
};

function buildUrl(path: string) {
  return new URL(`${env.apiUrl}${path}`, window.location.origin);
}

async function toApiError(response: Response) {
  const body: unknown = await response.json().catch(() => null);
  const parsed = errorResponseSchema.safeParse(body);
  if (!parsed.success) return new ApiError(response.status, MENSAJE_INESPERADO);
  const { status, message, errors, traceId } = parsed.data;
  return new ApiError(status, message, errors, traceId);
}

async function send(path: string, { method = 'GET', body, signal }: RequestOptions) {
  try {
    return await fetch(buildUrl(path), {
      method,
      signal,
      headers: body === undefined ? undefined : { 'Content-Type': 'application/json' },
      body: body === undefined ? undefined : JSON.stringify(body),
    });
  } catch (error) {
    if (signal?.aborted) throw error;
    throw new ApiError(0, MENSAJE_SIN_CONEXION);
  }
}

async function request<S extends z.ZodTypeAny>(
  path: string,
  schema: S,
  options: RequestOptions = {},
): Promise<z.output<S>> {
  const response = await send(path, options);
  if (!response.ok) throw await toApiError(response);
  const data: unknown = response.status === 204 ? undefined : await response.json();
  return schema.parse(data);
}

export const http = {
  get: <S extends z.ZodTypeAny>(path: string, schema: S, signal?: AbortSignal) =>
    request(path, schema, { signal }),
  post: <S extends z.ZodTypeAny>(path: string, body: unknown, schema: S) =>
    request(path, schema, { method: 'POST', body }),
  put: <S extends z.ZodTypeAny>(path: string, body: unknown, schema: S) =>
    request(path, schema, { method: 'PUT', body }),
  delete: <S extends z.ZodTypeAny>(path: string, schema: S) =>
    request(path, schema, { method: 'DELETE' }),
};
