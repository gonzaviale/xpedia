import type { ModoMocks } from '@/shared/config/env';
import { handlersBackend } from './backend';
import { handlersPendientes } from './pendientes';

export function handlersPara(modo: ModoMocks) {
  if (modo === 'completo') return [...handlersBackend, ...handlersPendientes];
  if (modo === 'parcial') return handlersPendientes;
  return [];
}
