import { z } from 'zod';
import type { Nodo } from '@/modules/ruta';
import type { MicroleccionBackend } from './contrato';
import { bloqueSchema, type Bloque, type Fuente, type Microleccion } from './model';

const bloquesSchema = z.array(bloqueSchema);

function texto(valor: unknown): string | null {
  return typeof valor === 'string' && valor.trim() ? valor : null;
}

function bloquesDe(contenido: Record<string, unknown>): Bloque[] {
  const estructurados = bloquesSchema.safeParse(contenido.bloques);
  if (estructurados.success && estructurados.data.length > 0) return estructurados.data;

  const bloques: Bloque[] = [];
  const principal = texto(contenido.texto);
  const ejemplo = texto(contenido.ejemplo);
  const nota = texto(contenido.nota);
  if (principal) bloques.push({ tipo: 'PARRAFO', texto: principal });
  if (ejemplo) bloques.push({ tipo: 'LISTA', titulo: 'Ejemplo', items: [ejemplo] });
  if (nota) bloques.push({ tipo: 'PARRAFO', texto: nota });
  return bloques;
}

function usoDe(uso: string | null | undefined): Fuente['uso'] {
  return uso === 'SOLO_ENLACE' ? 'SOLO_ENLACE' : 'ADAPTABLE';
}

export function aMicroleccion(dto: MicroleccionBackend, nodo: Nodo): Microleccion {
  const contenido = dto.contenido ?? {};
  return {
    id: dto.id,
    nodoId: nodo.id,
    titulo: dto.titulo,
    minutos: typeof contenido.minutos === 'number' ? contenido.minutos : nodo.minutosEstimados,
    bloques: bloquesDe(contenido),
    fuentes: dto.fuentes.map((fuente) => ({
      titulo: fuente.titulo,
      url: fuente.url,
      licencia: fuente.licencia,
      uso: usoDe(fuente.uso),
    })),
    retoDisponible: nodo.actividades.includes('RETO'),
  };
}
