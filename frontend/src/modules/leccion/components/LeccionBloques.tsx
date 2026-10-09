import { CheckIcon, XIcon } from '@phosphor-icons/react';
import { Panel } from '@/shared/ui';
import type { Bloque } from '../model';

function BloqueView({ bloque }: { bloque: Bloque }) {
  switch (bloque.tipo) {
    case 'PARRAFO':
      return <p className="text-[15px] leading-relaxed text-neutral-200">{bloque.texto}</p>;
    case 'LISTA':
      return (
        <div className="flex flex-col gap-2">
          {bloque.titulo && <h3 className="m-0 text-[15px]">{bloque.titulo}</h3>}
          <ul className="m-0 flex list-disc flex-col gap-1.5 pl-5 text-neutral-300">
            {bloque.items.map((item) => (
              <li key={item}>{item}</li>
            ))}
          </ul>
        </div>
      );
    case 'EJEMPLO':
      return (
        <div className="grid gap-3 md:grid-cols-2">
          <Panel flat tight className="flex flex-col gap-1.5 border-bad/40">
            <p className="flex items-center gap-1.5 text-[11px] tracking-widest text-bad uppercase">
              <XIcon aria-hidden /> Antes
            </p>
            <p className="text-neutral-300">{bloque.antes}</p>
          </Panel>
          <Panel flat tight className="flex flex-col gap-1.5 border-ok/40">
            <p className="flex items-center gap-1.5 text-[11px] tracking-widest text-ok uppercase">
              <CheckIcon aria-hidden /> Después
            </p>
            <p className="text-neutral-300">{bloque.despues}</p>
          </Panel>
        </div>
      );
  }
}

export function LeccionBloques({ bloques }: { bloques: Bloque[] }) {
  return (
    <div className="flex flex-col gap-5">
      {bloques.map((bloque, index) => (
        <BloqueView key={index} bloque={bloque} />
      ))}
    </div>
  );
}
