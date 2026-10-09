import { Link } from '@tanstack/react-router';
import { ArrowRightIcon } from '@phosphor-icons/react';
import { useInscripcionActual, type NivelHabilidad } from '@/modules/ruta';
import { Chip, Panel, PageHeading, buttonClasses, type Tone } from '@/shared/ui';

const NIVELES: Record<NivelHabilidad, { texto: string; tono: Tone }> = {
  INICIAL: { texto: 'Inicial', tono: 'bad' },
  MEDIO: { texto: 'Medio', tono: 'warn' },
  FUERTE: { texto: 'Fuerte', tono: 'ok' },
};

export function ResultadoPage() {
  const { diagnostico } = useInscripcionActual();

  return (
    <main className="mx-auto flex min-h-screen max-w-195 animate-fade-in flex-col gap-5 px-4 py-10">
      <PageHeading
        kicker="Diagnóstico"
        title="Tu punto de partida"
        lead="Esto es lo que ya traés de tu trabajo actual y cómo se llama en atención remota."
      />
      <Panel className="overflow-x-auto">
        <table className="w-full border-collapse text-[13px]">
          <caption className="mb-3 text-left text-[17px]">
            Tu mapa de habilidades transferibles
          </caption>
          <thead>
            <tr className="text-left text-[11px] tracking-widest text-fg/60 uppercase">
              <th scope="col" className="p-2 font-normal">
                Lo que hacés hoy
              </th>
              <th scope="col" className="p-2 font-normal">
                En atención remota se llama
              </th>
              <th scope="col" className="p-2 font-normal">
                Nivel
              </th>
            </tr>
          </thead>
          <tbody>
            {diagnostico.mapa.map((fila) => (
              <tr key={fila.remoto} className="border-t border-fg/8 align-top">
                <td className="p-2">{fila.hoy}</td>
                <td className="p-2">{fila.remoto}</td>
                <td className="p-2">
                  <Chip tone={NIVELES[fila.nivel].tono}>{NIVELES[fila.nivel].texto}</Chip>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </Panel>
      <Panel className="flex flex-col gap-3">
        <h2 className="m-0 text-[17px]">Cómo se ajusta tu ruta</h2>
        <ul className="m-0 flex list-disc flex-col gap-1.5 pl-5 text-neutral-300">
          {diagnostico.ajustes.map((ajuste) => (
            <li key={ajuste}>{ajuste}</li>
          ))}
        </ul>
      </Panel>
      <Link to="/ruta" className={buttonClasses({ variant: 'solid', className: 'self-end' })}>
        Ver mi ruta
        <ArrowRightIcon aria-hidden />
      </Link>
    </main>
  );
}
