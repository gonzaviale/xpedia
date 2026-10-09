import { CheckIcon } from '@phosphor-icons/react';
import { cn } from '@/shared/lib/cn';

type StepsProps = { items: string[]; current: number };

export function Steps({ items, current }: StepsProps) {
  return (
    <ol className="flex flex-wrap gap-1.5">
      {items.map((item, index) => (
        <li
          key={item}
          aria-current={index === current ? 'step' : undefined}
          className={cn(
            'inline-flex items-center gap-1 rounded-full border px-2.5 py-1 text-[11px]',
            index < current && 'border-ok/40 text-ok',
            index === current && 'border-accent text-accent-200',
            index > current && 'border-line text-muted',
          )}
        >
          {index < current && <CheckIcon aria-hidden />}
          {item}
        </li>
      ))}
    </ol>
  );
}

type DotsProps = { total: number; current: number; label: string };

export function Dots({ total, current, label }: DotsProps) {
  return (
    <div className="flex gap-1.5" role="img" aria-label={label}>
      {Array.from({ length: total }, (_, index) => (
        <span
          key={index}
          className={cn(
            'h-1 w-6.5 rounded-xs',
            index <= current ? 'bg-accent-500' : 'bg-neutral-800',
          )}
        />
      ))}
    </div>
  );
}
