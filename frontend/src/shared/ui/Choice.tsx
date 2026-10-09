import type { ComponentProps } from 'react';
import { cn } from '@/shared/lib/cn';

type ChoiceProps = ComponentProps<'button'> & { letter?: string; selected?: boolean };

export function Choice({ letter, selected, className, children, ...props }: ChoiceProps) {
  return (
    <button
      type="button"
      aria-pressed={selected}
      className={cn(
        'flex w-full cursor-pointer items-start gap-3 rounded-xl border border-line bg-canvas px-4 py-3 text-left text-[13.5px] text-fg transition-colors hover:border-accent-600 disabled:cursor-default',
        selected && 'border-accent shadow-[inset_0_0_0_1px_var(--color-accent)]',
        className,
      )}
      {...props}
    >
      {letter && (
        <span className="flex size-6 shrink-0 items-center justify-center rounded-full border border-line text-xs text-soft">
          {letter}
        </span>
      )}
      <span>{children}</span>
    </button>
  );
}

type ChipButtonProps = ComponentProps<'button'> & { selected?: boolean };

export function ChipButton({ selected, className, ...props }: ChipButtonProps) {
  return (
    <button
      type="button"
      aria-pressed={selected}
      className={cn(
        'cursor-pointer rounded-full border border-line bg-canvas px-3.5 py-1.5 text-[13px] text-neutral-300 hover:border-accent-600',
        selected && 'border-accent bg-accent-900 text-accent-100',
        className,
      )}
      {...props}
    />
  );
}
