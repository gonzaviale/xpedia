import { HttpResponse } from 'msw';

const REASON: Record<number, string> = {
  400: 'Bad Request',
  404: 'Not Found',
};

export function errorResponse(
  status: number,
  message: string,
  path: string,
  errors?: Record<string, string>,
) {
  return HttpResponse.json(
    {
      timestamp: new Date().toISOString().slice(0, 19),
      status,
      error: REASON[status] ?? 'Error',
      message,
      path,
      traceId: crypto.randomUUID(),
      ...(errors && { errors }),
    },
    { status },
  );
}
