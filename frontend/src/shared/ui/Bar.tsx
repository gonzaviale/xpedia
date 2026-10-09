import { cn } from '@/shared/lib/cn';

type BarProps = { value: number; tone?: 'accent' | 'ok'; label: string; className?: string };

export function Bar({ value, tone = 'accent', label, className }: BarProps) {
  const percent = Math.round(Math.min(Math.max(value, 0), 1) * 100);
  return (
    <div
      role="progressbar"
      aria-label={label}
      aria-valuenow={percent}
      aria-valuemin={0}
      aria-valuemax={100}
      className={cn('h-1.5 overflow-hidden rounded-full bg-neutral-800', className)}
    >
      <span
        className={cn(
          'block h-full animate-fill rounded-[inherit]',
          tone === 'ok' ? 'bg-ok' : 'bg-accent-500',
        )}
        style={{ width: `${percent}%` }}
      />
    </div>
  );
}
