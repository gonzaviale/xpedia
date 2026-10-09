import type { Diagnostico } from '@/modules/diagnostico';
import type { Microleccion } from '@/modules/leccion';
import type { Reto } from '@/modules/reto';
import type { Ruta } from '@/modules/ruta';

type NodoSeed = Ruta['hitos'][number]['nodos'][number];

function nodo(
  codigo: string,
  titulo: string,
  minutosEstimados: number,
  actividades: NodoSeed['actividades'] = [],
): NodoSeed {
  return {
    id: `nodo-${codigo.toLowerCase()}`,
    codigo,
    titulo,
    tipo: 'NUCLEO',
    minutosEstimados,
    actividades,
  };
}

export const ruta: Ruta = {
  id: 'ruta-atencion',
  slug: 'atencion-al-cliente-remota',
  titulo: 'Atención al cliente remota',
  meta: 'Listo/a para postularte, con evidencia: respuestas evaluadas, un roleplay aprobado y un CV traducido.',
  horasEstimadas: 16,
  validacion: 'BORRADOR_IA',
  practicaEstrella: {
    nodoId: 'nodo-a3',
    nombre: 'Cliente enojado',
    descripcion:
      'Respondé por chat a un cliente que escribió tres veces y recibe tu respuesta con un profesor de IA que evalúa con una rúbrica.',
  },
  hitos: [
    {
      id: 'hito-1',
      posicion: 1,
      titulo: 'Del mostrador a la pantalla',
      objetivo: 'Entender el trabajo y traducir tu experiencia.',
      horasEstimadas: 1.5,
      esFinal: false,
      evidenciaEsperada: 'mapa de habilidades transferibles y 3 logros traducidos',
      nodos: [
        nodo('D1', 'Tu puesto remoto', 20),
        nodo('A1', 'Escuchar e indagar', 15, ['MICROLECCION']),
      ],
    },
    {
      id: 'hito-2',
      posicion: 2,
      titulo: 'Escribir para clientes',
      objetivo: 'Responder chats y mails claros, cálidos y con un próximo paso.',
      horasEstimadas: 3,
      esFinal: false,
      evidenciaEsperada: '3 respuestas con su primera versión, la final y el puntaje',
      nodos: [
        nodo('B1', 'Lenguaje claro', 20),
        nodo('B2', 'Chat', 20),
        nodo('B3', 'Mail', 20),
        nodo('B5', 'Ortografía y tipeo', 10),
      ],
    },
    {
      id: 'hito-3',
      posicion: 3,
      titulo: 'Clientes enojados y malas noticias',
      objetivo: 'Bajar el enojo y resolver dentro de la política, sin prometer de más.',
      horasEstimadas: 3,
      esFinal: false,
      evidenciaEsperada: 'transcripción del roleplay de nivel 2 en modo validación',
      nodos: [
        nodo('A2', 'Empatía específica', 15),
        nodo('A3', 'Desescalar un reclamo', 25, ['MICROLECCION', 'RETO']),
        nodo('A4', 'Dar malas noticias y decir que no', 20),
        nodo('A5', 'Cerrar y hacer seguimiento', 15),
      ],
    },
    {
      id: 'hito-4',
      posicion: 4,
      titulo: 'Tickets, macros y datos personales',
      objetivo: 'Trabajar una bandeja como en un help desk real.',
      horasEstimadas: 2.5,
      esFinal: false,
      evidenciaEsperada: 'reporte de la bandeja simulada',
      nodos: [
        nodo('C1', 'Ciclo de un ticket', 20),
        nodo('C2', 'Base de conocimiento y macros', 20),
        nodo('C3', 'Notas internas y escalamiento', 15),
        nodo('C4', 'Datos personales y verificación de identidad', 20),
        nodo('C5', 'Métricas de atención', 15),
      ],
    },
    {
      id: 'hito-5',
      posicion: 5,
      titulo: 'Teléfono sin cara',
      objetivo: 'Resolver una llamada completa solo con la voz.',
      horasEstimadas: 1.5,
      esFinal: false,
      evidenciaEsperada: 'transcripción con su puntaje',
      nodos: [nodo('B4', 'Teléfono sin cara', 25)],
    },
    {
      id: 'hito-6',
      posicion: 6,
      titulo: 'Turno simulado remoto',
      objetivo: 'Juntar todo bajo presión de tiempo.',
      horasEstimadas: 1,
      esFinal: false,
      evidenciaEsperada: 'reporte del turno',
      nodos: [nodo('D2', 'Turnos y autogestión', 30)],
    },
    {
      id: 'hito-7',
      posicion: 7,
      titulo: 'Listo/a para postularte',
      objetivo: 'Pasar el CV, la entrevista y la prueba práctica.',
      horasEstimadas: 3,
      esFinal: true,
      evidenciaEsperada: 'CV, ficha de entrevista y resultado del simulacro',
      nodos: [
        nodo('D3', 'CV traducido', 30),
        nodo('D4', 'Entrevista', 30),
        nodo('D5', 'Prueba práctica y búsqueda', 30),
      ],
    },
  ],
};

export const diagnostico: Diagnostico = {
  actividadId: 'act-diagnostico-atencion',
  preguntas: [
    {
      id: 'dx-1',
      tipo: 'APLICACION',
      nodoId: 'nodo-a3',
      enunciado:
        'Viernes, 21 h, salón lleno. La pizza de una mesa llegó fría después de 50 minutos. ¿Qué hacés primero?',
      opciones: [
        'Explicás que la cocina está colapsada',
        'Te disculpás, ofrecés cambiarla y decís cuánto va a tardar',
        'Llamás al encargado',
        'Traés un postre sin preguntar',
      ],
    },
    {
      id: 'dx-2',
      tipo: 'APLICACION',
      nodoId: 'nodo-b2',
      enunciado: 'Un cliente te escribe por chat. ¿Cuál de estas respuestas escritas elegís?',
      cita: 'Pedí delivery hace 1 h 10 y llegó todo frío. Nunca más.',
      opciones: [
        'Lamentamos las molestias ocasionadas. Su caso fue derivado al área correspondiente.',
        'Tenés razón, una hora y diez es demasiado y llegó frío. Ya te pido el reenvío y te escribo en 15 minutos con la hora exacta.',
        'Es un problema del delivery, nosotros entregamos la comida bien.',
      ],
    },
    {
      id: 'dx-3',
      tipo: 'APLICACION',
      nodoId: 'nodo-b3',
      enunciado: '¿Cuál es la mejor respuesta a este mensaje?',
      cita: 'hola compre unas zapatillas x la web y me llegaron en otro talle. las necesito ya q hago??',
      opciones: [
        'Hola. Para ayudarte necesito tu DNI, el número de tarjeta y una foto del producto.',
        'Hola, qué mal que te llegó otro talle. Pasame el número de pedido y te armo el cambio hoy; te confirmo el envío antes de las 18 h.',
        'Su solicitud será evaluada por el área correspondiente en un plazo de 10 días hábiles.',
      ],
    },
    {
      id: 'dx-4',
      tipo: 'CONCEPTO',
      nodoId: 'nodo-c5',
      enunciado: '¿Qué mide la «primera respuesta» en un equipo de atención?',
      opciones: [
        'El tiempo hasta resolver el caso por completo',
        'El tiempo hasta que el cliente recibe la primera respuesta de una persona',
        'La cantidad de mensajes que escribió el agente',
        'La nota que el cliente le pone al agente',
      ],
    },
  ],
};

export const respuestasCorrectas: Record<string, number> = {
  'dx-1': 1,
  'dx-2': 1,
  'dx-3': 1,
  'dx-4': 1,
};

export const mapaPorPregunta: Record<string, { hoy: string; remoto: string }> = {
  'dx-1': { hoy: 'Calmar a una mesa que esperó demasiado', remoto: 'Desescalar un reclamo' },
  'dx-2': { hoy: 'Escribirle a un cliente', remoto: 'Lenguaje claro en chat' },
  'dx-3': { hoy: 'Tomar un pedido y confirmarlo', remoto: 'Responder un mail con próximo paso' },
  'dx-4': { hoy: 'Cumplir los tiempos en hora pico', remoto: 'Métricas de atención' },
};

export const microlecciones: Record<string, Microleccion> = {
  'nodo-a3': {
    id: 'leccion-a3',
    nodoId: 'nodo-a3',
    titulo: 'Escuchar, reconocer, resolver, confirmar',
    minutos: 2,
    retoDisponible: true,
    bloques: [
      {
        tipo: 'PARRAFO',
        texto:
          'Un cliente enojado casi nunca quiere una disculpa larga: quiere que alguien se haga cargo y le diga qué va a pasar. Esta guía de cuatro pasos ordena la respuesta.',
      },
      {
        tipo: 'LISTA',
        titulo: 'Los cuatro pasos',
        items: [
          'Escuchar: leé el mensaje completo y anotá qué pasó y qué necesita la persona.',
          'Reconocer: nombrá lo concreto (los mails sin respuesta, la fecha límite) y evitá las frases hechas.',
          'Resolver: ofrecé opciones reales con los datos del sistema, sin prometer lo que no podés asegurar.',
          'Confirmar: resumí lo acordado y decí quién vuelve a escribir y cuándo.',
        ],
      },
      {
        tipo: 'EJEMPLO',
        antes: 'Lamentamos las molestias ocasionadas. Seguramente llegue el lunes.',
        despues:
          'Tenés razón, escribiste dos veces y no te respondimos. El correo no nos da fecha, así que no puedo asegurarte el domingo. Mañana desde las 14 podés retirar el mismo modelo en gris en Rosario Centro.',
      },
      {
        tipo: 'LISTA',
        titulo: 'Lo que nunca va',
        items: [
          'Prometer una fecha que el sistema no muestra.',
          'Pedir los datos completos de una tarjeta.',
          'Desalentar un reclamo ante Defensa del Consumidor.',
        ],
      },
    ],
    fuentes: [
      {
        titulo: 'Conocé tus derechos, Defensa del Consumidor (argentina.gob.ar)',
        url: 'https://www.argentina.gob.ar/produccion/defensadelconsumidor/conoce-tus-derechos',
        licencia: 'CC BY 4.0',
        uso: 'ADAPTABLE',
      },
      {
        titulo: 'Guía Escuchar, Reconocer, Resolver, Confirmar (criterio de expertos Xpedia)',
        licencia: 'Producción propia Xpedia',
        uso: 'ADAPTABLE',
      },
      {
        titulo: 'Customer Centric Strategy, cap. 2 (Kerri Shields)',
        url: 'https://open.umn.edu/opentextbooks/textbooks/customer-centric-strategy',
        licencia: 'CC BY-NC-SA',
        uso: 'SOLO_ENLACE',
      },
    ],
  },
  'nodo-a1': {
    id: 'leccion-a1',
    nodoId: 'nodo-a1',
    titulo: 'Preguntá lo justo antes de resolver',
    minutos: 2,
    retoDisponible: false,
    bloques: [
      {
        tipo: 'PARRAFO',
        texto:
          'Indagar no es interrogar. Pedí solo el dato que necesitás para resolver y fijate si ya lo tenés antes de volver a preguntarlo.',
      },
      {
        tipo: 'LISTA',
        titulo: 'Antes de preguntar',
        items: [
          'Revisá el historial y la ficha del cliente: si el dato ya está, no lo pidas de nuevo.',
          'Hacé una pregunta por mensaje y explicá para qué la hacés.',
          'Repetí lo que entendiste antes de proponer una solución.',
        ],
      },
    ],
    fuentes: [
      {
        titulo: 'Criterio de expertos Xpedia (borrador pendiente de revisión)',
        licencia: 'Producción propia Xpedia',
        uso: 'ADAPTABLE',
      },
    ],
  },
};

export const reto: Reto = {
  id: 'act-reto-a3',
  nodoId: 'nodo-a3',
  titulo: 'Un cliente enojado por una entrega demorada',
  consigna:
    'Respondele a Marcelo en un solo mensaje, como lo harías por chat. Hoy es jueves y él corre el domingo.',
  contexto: {
    cliente: {
      nombre: 'Marcelo Ríos',
      descripcion:
        '47 años, Rosario. Hace 12 días compró unas zapatillas de running «Trote», negras, talle 43, con entrega prometida en 5 días hábiles. Escribió dos mails sin respuesta.',
    },
    mensaje:
      'Hola. Es la TERCERA vez que escribo. Compré unas zapatillas hace 12 días, me dijeron 5 días hábiles y el seguimiento está igual desde el viernes. Nadie me contesta los mails. Las necesito para el domingo. ¿Alguien me puede dar una respuesta de verdad o me tengo que ir a Defensa del Consumidor?',
    hechos: [
      'Pedido #48213: «En centro de distribución Rosario» desde el viernes.',
      'El correo no informa fecha de entrega.',
      'En el punto de retiro Rosario Centro queda 1 par «Trote» talle 43 gris. Negro no hay.',
    ],
    politica: [
      'Si la demora es culpa de la tienda, se puede cambiar el color sin costo.',
      'Lo que se reserva en el punto de retiro está disponible al día siguiente desde las 14 h.',
      'El reembolso vuelve al mismo medio de pago.',
      'Cupón de hasta 10 %; para más hace falta un supervisor.',
      'Nunca: prometer una fecha que el sistema no muestra, pedir los datos completos de una tarjeta ni desalentar un reclamo ante Defensa del Consumidor.',
    ],
  },
  rubrica: {
    nombre: 'Roleplay con cliente enojado',
    puntajeAprobacion: 10,
    criterios: [
      {
        id: 'empatia',
        nombre: 'Empatía específica',
        descripcion: 'Nombra lo que le pasó sin frases hechas ni disculpas exageradas.',
        puntajeMax: 3,
        eliminatorio: false,
      },
      {
        id: 'cargo',
        nombre: 'Hacerse cargo',
        descripcion: 'Se presenta, dice qué va a hacer y avisa las esperas.',
        puntajeMax: 3,
        eliminatorio: false,
      },
      {
        id: 'politica',
        nombre: 'Solución dentro de la política',
        descripcion: 'Usa los datos del sistema y ofrece opciones reales.',
        puntajeMax: 3,
        eliminatorio: true,
      },
      {
        id: 'claridad',
        nombre: 'Claridad',
        descripcion: 'Mensajes cortos, con horas y fechas concretas.',
        puntajeMax: 3,
        eliminatorio: false,
      },
      {
        id: 'cierre',
        nombre: 'Cierre y seguimiento',
        descripcion: 'Resume, da el código y dice quién y cuándo hace el seguimiento.',
        puntajeMax: 3,
        eliminatorio: false,
      },
    ],
  },
};

export const fuentesDelReto = [
  {
    titulo: 'Política de envíos de Tienda Andén (ficticia)',
    licencia: 'Producción propia Xpedia',
  },
  {
    titulo: 'Conocé tus derechos, argentina.gob.ar',
    licencia: 'CC BY 4.0',
    url: 'https://www.argentina.gob.ar/produccion/defensadelconsumidor/conoce-tus-derechos',
  },
];
