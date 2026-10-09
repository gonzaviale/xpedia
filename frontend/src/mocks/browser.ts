import { setupWorker } from 'msw/browser';
import { env } from '@/shared/config/env';
import { handlersPara } from './handlers';

export const worker = setupWorker(...handlersPara(env.mocks));
