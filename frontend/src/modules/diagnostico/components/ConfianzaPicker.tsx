import { ChipButton } from '@/shared/ui';
import type { Confianza } from '../model';

const OPCIONES: { valor: Confianza; texto: string }[] = [
  { valor: 'SABIA', texto: 'Lo sabía' },
  { valor: 'DUDE', texto: 'Dudé' },
  { valor: 'ADIVINE', texto: 'Adiviné' },
];

type ConfianzaPickerProps = { valor?: Confianza; onChange: (valor: Confianza) => void };

export function ConfianzaPicker({ valor, onChange }: ConfianzaPickerProps) {
  return (
    <fieldset className="flex flex-col gap-2 border-0 p-0">
      <legend className="mb-2 text-[12.5px] text-neutral-300">¿Lo sabías o lo adivinaste?</legend>
      <div className="flex flex-wrap gap-2">
        {OPCIONES.map((opcion) => (
          <ChipButton
            key={opcion.valor}
            selected={valor === opcion.valor}
            onClick={() => onChange(opcion.valor)}
          >
            {opcion.texto}
          </ChipButton>
        ))}
      </div>
    </fieldset>
  );
}
