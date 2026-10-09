import type { Intento, PuntajeCriterio, Reto } from '@/modules/reto';
import { fuentesDelReto } from './db/atencion';

type Nivel = 0 | 1 | 2 | 3;
type CriterioId = 'empatia' | 'cargo' | 'politica' | 'claridad' | 'cierre';

const COMENTARIOS: Record<CriterioId, [string, string, string, string]> = {
  empatia: [
    'Usaste una frase hecha («lamentamos las molestias») que Marcelo ya rechazó antes.',
    'Reconociste el enojo, pero sin nombrar lo que le pasó: los mails sin respuesta o el domingo.',
    'Nombraste una parte de lo que vivió. Sumá la otra para que se sienta escuchado.',
    'Nombraste lo concreto (los mails sin respuesta, la carrera del domingo) sin exagerar las disculpas.',
  ],
  cargo: [
    'Le echaste la culpa a otro. Marcelo le compró a la tienda, no al correo.',
    'No dijiste qué ibas a hacer vos ni cuándo.',
    'Te hiciste cargo, pero faltó avisar cuánto tardás.',
    'Te hiciste cargo y avisaste los tiempos de la espera.',
  ],
  politica: [
    'Prometiste algo que el sistema no muestra, y eso es lo que más enoja si después no se cumple.',
    'No usaste los datos del sistema ni ofreciste una salida real.',
    'Ofreciste una opción real. Con una segunda alternativa, Marcelo puede elegir.',
    'Usaste los datos del sistema y ofreciste opciones reales sin prometer de más.',
  ],
  claridad: [
    'El mensaje no tiene datos concretos o es demasiado corto para resolver algo.',
    'El mensaje es muy largo y sin horas ni fechas concretas.',
    'Se entiende, pero partí el mensaje en ideas cortas y poné horas exactas.',
    'Mensaje corto, con horas y fechas concretas.',
  ],
  cierre: [
    'No confirmaste nada ni dijiste cuándo volvés a escribir.',
    'Cerraste, pero sin resumir lo acordado ni decir cuándo hay novedades.',
    'Falta una parte del cierre: quién escribe y a qué hora, o el resumen de lo acordado.',
    'Resumiste lo acordado y dijiste quién y cuándo hace el seguimiento.',
  ],
};

const ALTERNATIVAS: Record<CriterioId, string> = {
  empatia:
    'Tenés razón, escribiste dos veces y no te respondimos, y el domingo corrés. Me ocupo yo.',
  cargo: 'Me ocupo yo de tu pedido. Dame 2 minutos que reviso el envío y te escribo enseguida.',
  politica:
    'El correo no nos da fecha, así que no te lo puedo asegurar. Mañana desde las 14 podés retirar el mismo modelo en gris en Rosario Centro, o esperar el negro y, si no llega en 15 días, te reintegramos todo.',
  claridad:
    'Partí tu mensaje en dos y poné horas exactas: «mañana desde las 14», «te escribo a las 15».',
  cierre:
    'Quedamos así: reservo el gris para mañana desde las 14 y te escribo a las 15 para confirmar.',
};

const REGEX = {
  fraseHecha: /lamentamos las molestias/,
  concretoMails: /mail|escribiste|dos veces|tres veces|no te (respondimos|contestamos)/,
  concretoDomingo: /domingo|correr|carrera/,
  concretoEspera: /12 dias|esper/,
  seHaceCargo: /me ocupo|me encargo|me hago cargo|yo me ocupo|reviso/,
  avisaEspera: /minuto|enseguida|ahora mismo/,
  culpaAjena: /(problema|culpa) del correo/,
  promesa: /seguro (te )?llega|te llega manana|te lo garantizo|seguramente llegue|llega el lunes/,
  opcionReal: /gris|retir|punto de retiro|reembols|reintegr|devuelvo/,
  desaliento:
    /no hace falta (reclamar|denunciar)|no (vale la pena|te conviene) reclamar|no reclames/,
  seguimiento: /te escribo|te aviso|te contacto|te vuelvo a escribir|te confirmo/,
  horario: /manana|\d{1,2} ?(h|hs)\b|a las \d/,
  resumen: /resumo|quedamos|reserv|codigo/,
};

function normalizar(texto: string) {
  return texto.toLowerCase().normalize('NFD').replace(/[̀-ͯ]/g, '');
}

function contar(texto: string, expresiones: RegExp[]) {
  return expresiones.filter((expresion) => expresion.test(texto)).length;
}

function nivel(cantidad: number): Nivel {
  return Math.min(cantidad, 3) as Nivel;
}

function puntuar(respuesta: string): {
  niveles: Record<CriterioId, Nivel>;
  faltaAutomatica: string | null;
} {
  const texto = normalizar(respuesta);
  const palabras = texto.split(/\s+/).filter(Boolean).length;
  const concretos = contar(texto, [
    REGEX.concretoMails,
    REGEX.concretoDomingo,
    REGEX.concretoEspera,
  ]);
  const opciones =
    contar(texto, [REGEX.opcionReal]) +
    (texto.includes('reembols') && texto.includes('gris') ? 1 : 0);

  const empatia = REGEX.fraseHecha.test(texto) ? 0 : nivel(concretos === 0 ? 1 : concretos + 1);
  const cargo = REGEX.culpaAjena.test(texto)
    ? 0
    : nivel(1 + contar(texto, [REGEX.seHaceCargo, REGEX.avisaEspera]));
  const politica = REGEX.promesa.test(texto) ? 0 : nivel(opciones === 0 ? 1 : opciones + 1);
  const claridad: Nivel =
    palabras < 10 ? 0 : palabras <= 120 && /\d/.test(texto) ? 3 : palabras <= 180 ? 2 : 1;
  const cierre = nivel(contar(texto, [REGEX.seguimiento, REGEX.horario, REGEX.resumen]));

  return {
    niveles: { empatia, cargo, politica, claridad, cierre },
    faltaAutomatica: REGEX.desaliento.test(texto)
      ? 'Desalentar un reclamo ante Defensa del Consumidor es falta automática.'
      : null,
  };
}

function evaluarRetoDelSeed(reto: Reto, respuesta: string): Intento {
  const { niveles, faltaAutomatica } = puntuar(respuesta);

  const puntajePorCriterio: PuntajeCriterio[] = reto.rubrica.criterios.map((criterio) => {
    const id = criterio.id as CriterioId;
    return {
      criterioId: criterio.id,
      nombre: criterio.nombre,
      puntaje: niveles[id],
      puntajeMax: criterio.puntajeMax,
      eliminatorio: criterio.eliminatorio,
      comentario: COMENTARIOS[id][niveles[id]],
    };
  });

  const puntaje = puntajePorCriterio.reduce((total, criterio) => total + criterio.puntaje, 0);
  const puntajeMax = puntajePorCriterio.reduce((total, criterio) => total + criterio.puntajeMax, 0);
  const mejor = puntajePorCriterio.reduce((a, b) => (b.puntaje > a.puntaje ? b : a));
  const peor = puntajePorCriterio.reduce((a, b) => (b.puntaje < a.puntaje ? b : a));
  const perfecto = peor.puntaje === peor.puntajeMax;

  return {
    id: crypto.randomUUID(),
    actividadId: reto.id,
    nodoId: reto.nodoId,
    respuesta,
    puntaje,
    puntajeMax,
    puntajeAprobacion: reto.rubrica.puntajeAprobacion,
    aprobado:
      !faltaAutomatica && puntaje >= reto.rubrica.puntajeAprobacion && niveles.politica >= 2,
    faltaAutomatica,
    puntajePorCriterio,
    feedback: {
      loBueno: `${mejor.nombre}: ${mejor.comentario}`,
      aMejorar: perfecto
        ? 'No encontramos nada importante para corregir. Probá el nivel siguiente, con un cliente que interrumpe.'
        : `${peor.nombre}: ${peor.comentario}`,
      alternativa: perfecto ? null : ALTERNATIVAS[peor.criterioId as CriterioId],
    },
    fuentes: fuentesDelReto,
    creadoEn: new Date().toISOString(),
  };
}

const PALABRAS_MINIMAS = 10;
const PALABRAS_CORTAS = 25;
const PALABRAS_MAXIMAS = 150;
const AVISO_SIMULADA = 'Evaluación simulada: todavía no hay un profesor de IA conectado.';

function factorGenerico(respuesta: string) {
  const palabras = respuesta.trim().split(/\s+/).filter(Boolean).length;
  if (palabras < PALABRAS_MINIMAS) return 0;
  if (palabras < PALABRAS_CORTAS) return 1 / 3;
  return palabras <= PALABRAS_MAXIMAS ? 1 : 2 / 3;
}

// Para retos que vienen del backend real: la rúbrica es distinta a la del seed, así que se puntúa
// solo por largo de la respuesta, avisando que no es una evaluación de verdad.
function evaluarRetoGenerico(reto: Reto, respuesta: string): Intento {
  const factor = factorGenerico(respuesta);

  const puntajePorCriterio: PuntajeCriterio[] = reto.rubrica.criterios.map((criterio) => ({
    criterioId: criterio.id,
    nombre: criterio.nombre,
    puntaje: Math.round(criterio.puntajeMax * factor),
    puntajeMax: criterio.puntajeMax,
    eliminatorio: criterio.eliminatorio,
    comentario: AVISO_SIMULADA,
  }));

  const puntaje = puntajePorCriterio.reduce((total, criterio) => total + criterio.puntaje, 0);
  const puntajeMax = puntajePorCriterio.reduce((total, criterio) => total + criterio.puntajeMax, 0);
  const eliminatorioEnCero = puntajePorCriterio.some(
    (criterio) => criterio.eliminatorio && criterio.puntaje === 0,
  );

  return {
    id: crypto.randomUUID(),
    actividadId: reto.id,
    nodoId: reto.nodoId,
    respuesta,
    puntaje,
    puntajeMax,
    puntajeAprobacion: reto.rubrica.puntajeAprobacion,
    aprobado: !eliminatorioEnCero && puntaje >= reto.rubrica.puntajeAprobacion,
    faltaAutomatica: null,
    puntajePorCriterio,
    feedback: {
      loBueno: AVISO_SIMULADA,
      aMejorar: 'Cuando exista el profesor de IA vas a recibir una devolución por cada criterio.',
      alternativa: null,
    },
    fuentes: [],
    creadoEn: new Date().toISOString(),
  };
}

export function evaluarReto(reto: Reto, respuesta: string): Intento {
  const esDelSeed = reto.rubrica.criterios.every((criterio) => criterio.id in COMENTARIOS);
  return esDelSeed ? evaluarRetoDelSeed(reto, respuesta) : evaluarRetoGenerico(reto, respuesta);
}
