import { Note, SourcePill } from '@/shared/ui';
import type { Fuente } from '../model';

const DETALLE_POR_USO: Record<Fuente['uso'], string> = {
  ADAPTABLE: 'adaptado por Xpedia',
  SOLO_ENLACE: 'lectura recomendada',
};

export function FuentesCitadas({ fuentes }: { fuentes: Fuente[] }) {
  return (
    <section aria-labelledby="fuentes" className="flex flex-col gap-3">
      <h2 id="fuentes" className="m-0 text-[15px]">
        Fuentes
      </h2>
      <ul className="m-0 flex list-none flex-col gap-2 p-0">
        {fuentes.map((fuente) => (
          <li key={fuente.titulo}>
            <SourcePill
              titulo={fuente.titulo}
              licencia={fuente.licencia}
              url={fuente.url ?? undefined}
              detalle={DETALLE_POR_USO[fuente.uso]}
            />
          </li>
        ))}
      </ul>
      <Note>
        La IA adapta y cita: no inventa el contenido. Cada lección lleva su fuente y su licencia.
      </Note>
    </section>
  );
}
