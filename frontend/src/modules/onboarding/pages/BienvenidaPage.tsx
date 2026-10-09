import { useNavigate } from '@tanstack/react-router';
import { ArrowRightIcon } from '@phosphor-icons/react';
import { formatearMinutos } from '@/shared/lib/format';
import { Button, Chip, ChipButton, Choice, Dots, PageHeading, Panel } from '@/shared/ui';
import {
  RITMOS_MIN,
  RITMO_RECOMENDADO_MIN,
  useOnboardingStore,
  type ObjetivoElegido,
} from '../store';

const OBJETIVOS: { valor: ObjetivoElegido; titulo: string; detalle: string }[] = [
  {
    valor: 'CAMBIAR',
    titulo: 'Cambiar de rubro',
    detalle: 'Vengo de otro trabajo y quiero pasar a atención al cliente remota.',
  },
  {
    valor: 'ARRANCAR',
    titulo: 'Arrancar de cero',
    detalle: 'Es mi primera experiencia atendiendo clientes.',
  },
  {
    valor: 'MEJORAR',
    titulo: 'Mejorar lo que hago',
    detalle: 'Ya atiendo clientes y quiero hacerlo mejor.',
  },
];

export function BienvenidaPage() {
  const navigate = useNavigate();
  const { paso, objetivo, ritmoMin, elegirObjetivo, elegirRitmo, irAlPaso } = useOnboardingStore();

  return (
    <main className="mx-auto flex min-h-screen max-w-195 animate-fade-in flex-col gap-6 px-4 py-10">
      <Dots total={2} current={paso} label={`Paso ${paso + 1} de 2`} />
      {paso === 0 ? (
        <>
          <PageHeading
            kicker="Atención al cliente remota"
            title="¿Qué querés lograr?"
            lead="Con tu respuesta armamos el punto de partida de tu ruta."
          />
          <div className="flex flex-col gap-2.5">
            {OBJETIVOS.map((opcion) => (
              <Choice
                key={opcion.valor}
                selected={objetivo === opcion.valor}
                onClick={() => elegirObjetivo(opcion.valor)}
              >
                <span className="block font-medium">{opcion.titulo}</span>
                <span className="text-soft">{opcion.detalle}</span>
              </Choice>
            ))}
          </div>
          <Button
            variant="solid"
            className="self-end"
            disabled={!objetivo}
            onClick={() => irAlPaso(1)}
          >
            Siguiente
            <ArrowRightIcon aria-hidden />
          </Button>
        </>
      ) : (
        <>
          <PageHeading
            title="¿Cuánto tiempo tenés por día?"
            lead="Es solo una estimación para calcular tu fecha de llegada. Podés cambiarlo después."
          />
          <Panel className="flex flex-wrap gap-2.5">
            {RITMOS_MIN.map((minutos) => (
              <ChipButton
                key={minutos}
                selected={ritmoMin === minutos}
                onClick={() => elegirRitmo(minutos)}
              >
                {formatearMinutos(minutos)}
                {minutos === RITMO_RECOMENDADO_MIN && (
                  <Chip tone="accent" className="ml-2">
                    Recomendado
                  </Chip>
                )}
              </ChipButton>
            ))}
          </Panel>
          <div className="flex justify-between">
            <Button onClick={() => irAlPaso(0)}>Atrás</Button>
            <Button variant="solid" onClick={() => void navigate({ to: '/diagnostico' })}>
              Empezar el diagnóstico
              <ArrowRightIcon aria-hidden />
            </Button>
          </div>
        </>
      )}
    </main>
  );
}
