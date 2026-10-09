import { describe, expect, it } from 'vitest';
import { evaluarReto } from './evaluar-reto';

const RESPUESTA_COMPLETA =
  'Tenés razón, escribiste dos veces y no te respondimos, y el domingo corrés. Me ocupo yo, dame 2 minutos. ' +
  'El correo no nos da fecha, no te lo puedo asegurar. Mañana desde las 14 podés retirar el gris en Rosario Centro, ' +
  'o esperar el negro y te reintegramos todo. Quedamos así: te escribo mañana a las 15 para confirmar.';

describe('evaluarReto', () => {
  it('aprueba una respuesta que reconoce, resuelve dentro de la política y cierra', () => {
    const intento = evaluarReto('act-reto-a3', RESPUESTA_COMPLETA);

    expect(intento.aprobado).toBe(true);
    expect(intento.puntaje).toBeGreaterThanOrEqual(intento.puntajeAprobacion);
  });

  it('no aprueba si promete una fecha que el sistema no muestra', () => {
    const intento = evaluarReto(
      'act-reto-a3',
      `${RESPUESTA_COMPLETA} Seguramente llegue el lunes.`,
    );

    const politica = intento.puntajePorCriterio.find((c) => c.criterioId === 'politica');
    expect(politica?.puntaje).toBe(0);
    expect(intento.aprobado).toBe(false);
  });

  it('marca falta automática si desalienta el reclamo', () => {
    const intento = evaluarReto(
      'act-reto-a3',
      `${RESPUESTA_COMPLETA} No hace falta reclamar en Defensa.`,
    );

    expect(intento.faltaAutomatica).not.toBeNull();
    expect(intento.aprobado).toBe(false);
  });

  it('penaliza las frases hechas en la empatía', () => {
    const intento = evaluarReto(
      'act-reto-a3',
      `Lamentamos las molestias ocasionadas. ${RESPUESTA_COMPLETA}`,
    );

    const empatia = intento.puntajePorCriterio.find((c) => c.criterioId === 'empatia');
    expect(empatia?.puntaje).toBe(0);
  });
});
