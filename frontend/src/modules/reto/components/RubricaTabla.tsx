import { Chip } from '@/shared/ui';
import { nivelDePuntaje } from '../lib';
import type { PuntajeCriterio } from '../model';

export function RubricaTabla({ criterios }: { criterios: PuntajeCriterio[] }) {
  return (
    <div className="overflow-x-auto">
      <table className="w-full border-collapse text-[13px]">
        <caption className="sr-only">Puntaje por criterio de la rúbrica</caption>
        <thead>
          <tr className="text-left text-[11px] tracking-widest text-fg/60 uppercase">
            <th scope="col" className="p-2 font-normal">
              Criterio
            </th>
            <th scope="col" className="p-2 font-normal">
              Puntaje
            </th>
            <th scope="col" className="p-2 font-normal">
              Nivel
            </th>
            <th scope="col" className="p-2 font-normal">
              Devolución
            </th>
          </tr>
        </thead>
        <tbody>
          {criterios.map((criterio) => {
            const nivel = nivelDePuntaje(criterio.puntaje, criterio.puntajeMax);
            return (
              <tr key={criterio.criterioId} className="border-t border-fg/8 align-top">
                <th scope="row" className="p-2 text-left font-medium">
                  {criterio.nombre}
                  {criterio.eliminatorio && (
                    <Chip tone="accent" className="ml-2">
                      Bloqueante
                    </Chip>
                  )}
                </th>
                <td className="p-2 whitespace-nowrap tabular-nums">
                  {criterio.puntaje} / {criterio.puntajeMax}
                </td>
                <td className="p-2">
                  <Chip tone={nivel.tono}>{nivel.texto}</Chip>
                </td>
                <td className="p-2 text-neutral-300">{criterio.comentario}</td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
}
