import { HexagonIcon } from '@phosphor-icons/react';

export function Header() {
  return (
    <header className="sticky top-0 z-40 flex h-13 items-center gap-4 border-b border-line bg-neutral-900 px-3 sm:px-5">
      <div className="flex items-center gap-2">
        <span className="flex size-5.5 items-center justify-center rounded-md border border-accent text-accent">
          <HexagonIcon aria-hidden size={14} />
        </span>
        <span className="text-xs tracking-[0.14em] uppercase">Xpedia</span>
        <span className="rounded-md border border-line px-1.5 py-px text-[10px] text-muted">
          Prototipo
        </span>
      </div>
    </header>
  );
}
