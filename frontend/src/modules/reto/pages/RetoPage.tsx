import { useNavigate } from '@tanstack/react-router';
import { useSuspenseQuery } from '@tanstack/react-query';
import { useRutaActual } from '@/modules/ruta';
import { Panel, PageHeading } from '@/shared/ui';
import { retoQuery } from '../api';
import { ContextoCliente } from '../components/ContextoCliente';
import { FormularioReto } from '../components/FormularioReto';

export function RetoPage({ nodoId }: { nodoId: string }) {
  const navigate = useNavigate();
  const ruta = useRutaActual();
  const reto = useSuspenseQuery(retoQuery(ruta.id, nodoId)).data;
  const { cliente, mensaje } = reto.contexto;

  return (
    <div className="mx-auto flex max-w-280 animate-fade-in flex-col gap-5">
      <PageHeading kicker="Reto" title={reto.titulo} lead={reto.consigna} />
      <div className="grid items-start gap-5 lg:grid-cols-[minmax(0,1.55fr)_minmax(0,1fr)]">
        <div className="flex flex-col gap-4">
          {mensaje && (
            <Panel className="flex flex-col gap-3">
              {cliente && <p className="text-[12px] text-muted">{cliente.nombre} escribe:</p>}
              <blockquote className="m-0 max-w-[84%] rounded-2xl rounded-tl-sm bg-neutral-800 px-3.5 py-2.5 text-[13.5px] leading-normal">
                {mensaje}
              </blockquote>
            </Panel>
          )}
          <Panel>
            <FormularioReto
              destino={{ rutaId: ruta.id, nodoId, retoId: reto.id }}
              onEnviado={(intento) =>
                void navigate({ to: '/intentos/$intentoId', params: { intentoId: intento.id } })
              }
            />
          </Panel>
        </div>
        <ContextoCliente contexto={reto.contexto} />
      </div>
    </div>
  );
}
