import type { ComponentProps } from 'react';
import { cn } from '@/shared/lib/cn';

export type FeedbackTone = 'ok' | 'bad' | 'warn' | 'info';

const tones: Record<FeedbackTone, string> = {
  ok: 'border-ok/40 bg-ok/8',
  bad: 'border-bad/40 bg-bad/8',
  warn: 'border-warn/40 bg-warn/8',
  info: 'border-accent-700 bg-accent-900',
};

type FeedbackProps = ComponentProps<'div'> & { tone: FeedbackTone };

export function Feedback({ tone, className, ...props }: FeedbackProps) {
  return (
    <div
      className={cn(
        'animate-fade-in rounded-[10px] border px-3 py-2.5 text-[13px]',
        tones[tone],
        className,
      )}
      {...props}
    />
  );
}
