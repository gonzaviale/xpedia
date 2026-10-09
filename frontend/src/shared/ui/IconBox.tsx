import type { ReactNode } from 'react';

export function IconBox({ children }: { children: ReactNode }) {
  return (
    <span className="flex size-11 shrink-0 items-center justify-center rounded-xl border border-accent-800 bg-accent-900 text-[22px] text-accent-300">
      {children}
    </span>
  );
}
