import type { ComponentProps } from 'react';
import { cn } from '@/shared/lib/cn';

export type Tone = 'neutral' | 'accent' | 'ok' | 'warn' | 'bad';

const tones: Record<Tone, string> = {
  neutral: 'border-line text-neutral-300',
  accent: 'border-accent-600 bg-accent-900 text-accent-200',
  ok: 'border-ok/45 text-ok',
  warn: 'border-warn/45 text-warn',
  bad: 'border-bad/45 text-bad',
};

type ChipProps = ComponentProps<'span'> & { tone?: Tone };

export function Chip({ tone = 'neutral', className, ...props }: ChipProps) {
  return (
    <span
      className={cn(
        'inline-flex shrink-0 items-center gap-1.5 rounded-full border px-2.5 py-0.5 text-[11px] whitespace-nowrap',
        tones[tone],
        className,
      )}
      {...props}
    />
  );
}
