import type { ReactNode } from 'react';
import { InfoIcon, WarningIcon } from '@phosphor-icons/react';
import { cn } from '@/shared/lib/cn';

type NoteProps = { children: ReactNode; warn?: boolean; className?: string };

export function Note({ children, warn, className }: NoteProps) {
  const Icon = warn ? WarningIcon : InfoIcon;
  return (
    <div
      className={cn(
        'flex items-start gap-2.5 rounded-[10px] border border-line bg-neutral-900 px-3 py-2.5 text-[12.5px] text-neutral-300',
        className,
      )}
    >
      <Icon aria-hidden className={cn('mt-0.5 shrink-0', warn ? 'text-warn' : 'text-accent-300')} />
      <div>{children}</div>
    </div>
  );
}
