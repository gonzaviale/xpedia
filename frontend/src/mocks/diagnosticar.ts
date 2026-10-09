import type { CrearInscripcion } from '@/modules/diagnostico';
import type { Inscripcion, NivelHabilidad, ProgresoNodo } from '@/modules/ruta';
import { mapaPorPregunta, respuestasCorrectas, ruta } from './db/atencion';

const DIAS_DE_PRACTICA_POR_SEMANA = 6;
const DIAS_POR_SEMANA = 7;
const DOMINIO_POR_NIVEL: Record<NivelHabilidad, number> = { INICIAL: 0.1, MEDIO: 0.4, FUERTE: 0.7 };
const NODOS_DISPONIBLES_POR_DIAGNOSTICO = ['nodo-a1', 'nodo-a3'];

function nivelDeRespuesta(respuesta: CrearInscripcion['respuestas'][number]): NivelHabilidad {
  if (respuestasCorrectas[respuesta.preguntaId] !== respuesta.elegida) return 'INICIAL';
  return respuesta.confianza === 'SABIA' ? 'FUERTE' : 'MEDIO';
}

function fechaDeLlegada(ritmoMin: number) {
  const diasDePractica = Math.ceil((ruta.horasEstimadas * 60) / ritmoMin);
  const diasCorridos = Math.ceil((diasDePractica * DIAS_POR_SEMANA) / DIAS_DE_PRACTICA_POR_SEMANA);
  const fecha = new Date();
  fecha.setDate(fecha.getDate() + diasCorridos);
  return fecha.toISOString().slice(0, 10);
}

function ajustesDe(niveles: Record<string, NivelHabilidad>) {
  const escritura = [niveles['dx-2'], niveles['dx-3']];
  const ajustes: string[] = [];

  if (escritura.some((nivel) => nivel !== 'INICIAL')) {
    ajustes.push(
      'Tu escritura salió media o alta: el hito 2 se acorta y podés rendir directamente su prueba de salida.',
    );
  } else {
    ajustes.push('Vamos a reforzar la escritura para clientes antes de pasar a los reclamos.');
  }
  if (niveles['dx-1'] === 'FUERTE') {
    ajustes.push(
      'Tu trato con clientes salió fuerte: el roleplay del hito 3 arranca en el nivel 2.',
    );
  }
  ajustes.push('Cada prueba de salida vuelve a medir y reajusta el plan.');
  return ajustes;
}

export function crearInscripcion(datos: CrearInscripcion): Inscripcion {
  const niveles = Object.fromEntries(
    datos.respuestas.map((respuesta) => [respuesta.preguntaId, nivelDeRespuesta(respuesta)]),
  );
  const dominioInicial = DOMINIO_POR_NIVEL[niveles['dx-1'] ?? 'INICIAL'];
  const [primerHito] = ruta.hitos;

  const progreso: ProgresoNodo[] = ruta.hitos.flatMap((hito) =>
    hito.nodos.map((nodo) => {
      const disponible =
        hito.id === primerHito.id || NODOS_DISPONIBLES_POR_DIAGNOSTICO.includes(nodo.id);
      return {
        nodoId: nodo.id,
        estado: disponible ? ('DISPONIBLE' as const) : ('BLOQUEADO' as const),
        dominio: NODOS_DISPONIBLES_POR_DIAGNOSTICO.includes(nodo.id) ? dominioInicial : 0,
      };
    }),
  );

  return {
    id: crypto.randomUUID(),
    rutaSlug: datos.rutaSlug,
    objetivo: datos.objetivo,
    hitoActualId: primerHito.id,
    ritmoMin: datos.ritmoMin,
    fechaLlegadaEstimada: fechaDeLlegada(datos.ritmoMin),
    progreso,
    diagnostico: {
      mapa: datos.respuestas.flatMap((respuesta) => {
        const fila = mapaPorPregunta[respuesta.preguntaId];
        const nivel = niveles[respuesta.preguntaId];
        return fila && nivel ? [{ ...fila, nivel }] : [];
      }),
      ajustes: ajustesDe(niveles),
    },
  };
}
