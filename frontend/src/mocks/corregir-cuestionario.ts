import type { EnviarCuestionario, IntentoCuestionario } from '@/modules/cuestionario';
import type { CuestionarioSeed } from './db/atencion';

export class RespuestasInvalidas extends Error {}

export function corregirCuestionario(
  cuestionario: CuestionarioSeed,
  { actividadId, respuestas }: EnviarCuestionario,
): IntentoCuestionario {
  const porPregunta = new Map(respuestas.map((respuesta) => [respuesta.preguntaId, respuesta]));
  if (porPregunta.size !== respuestas.length) {
    throw new RespuestasInvalidas('Hay una pregunta respondida más de una vez');
  }
  if (!respuestas.every((r) => cuestionario.preguntas.some((p) => p.id === r.preguntaId))) {
    throw new RespuestasInvalidas('Hay una pregunta que no pertenece al cuestionario');
  }
  if (porPregunta.size !== cuestionario.preguntas.length) {
    throw new RespuestasInvalidas(
      `Hay que responder las ${cuestionario.preguntas.length} preguntas del cuestionario`,
    );
  }

  const correcciones = cuestionario.preguntas.map((pregunta) => {
    const respuesta = porPregunta.get(pregunta.id);
    if (!respuesta || respuesta.elegida >= pregunta.opciones.length) {
      throw new RespuestasInvalidas('Hay una opción que no existe en la pregunta');
    }
    return {
      preguntaId: pregunta.id,
      elegida: respuesta.elegida,
      correcta: respuesta.elegida === pregunta.correcta,
      opcionCorrecta: pregunta.correcta,
      confianza: respuesta.confianza,
      explicacion: pregunta.explicacion,
    };
  });

  const correctas = correcciones.filter((correccion) => correccion.correcta).length;
  const puntaje = Math.round((correctas / correcciones.length) * 10_000) / 100;

  return {
    id: crypto.randomUUID(),
    actividadId,
    puntaje,
    aprobado: puntaje >= 80,
    correctas,
    total: correcciones.length,
    correcciones,
  };
}
