import { Panel } from '@/shared/ui';
import type { Reto } from '../model';

type ContextoClienteProps = { contexto: Reto['contexto'] };

function Lista({ titulo, items }: { titulo: string; items: string[] }) {
  return (
    <Panel tight className="flex flex-col gap-2">
      <h3 className="m-0 text-[13px] text-neutral-300">{titulo}</h3>
      <ul className="m-0 flex list-disc flex-col gap-1.5 pl-4 text-[12.5px] text-soft">
        {items.map((item) => (
          <li key={item}>{item}</li>
        ))}
      </ul>
    </Panel>
  );
}

export function ContextoCliente({ contexto }: ContextoClienteProps) {
  return (
    <aside className="flex flex-col gap-3" aria-label="Contexto del caso">
      <Panel tight className="flex flex-col gap-1">
        <h3 className="m-0 text-[13px] text-neutral-300">Ficha del cliente</h3>
        <p className="font-medium">{contexto.cliente.nombre}</p>
        <p className="text-[12.5px] text-soft">{contexto.cliente.descripcion}</p>
      </Panel>
      <Lista titulo="Sistema de pedidos" items={contexto.hechos} />
      <Lista titulo="Política de Tienda Andén" items={contexto.politica} />
    </aside>
  );
}
