import { BookOpenIcon } from '@phosphor-icons/react';

type SourcePillProps = { titulo: string; licencia: string; url?: string; detalle?: string };

export function SourcePill({ titulo, licencia, url, detalle }: SourcePillProps) {
  const texto = [titulo, licencia, detalle].filter(Boolean).join(' · ');
  return (
    <span className="inline-flex max-w-full items-center gap-1.5 rounded-md border border-line bg-neutral-900 px-2.5 py-1 text-[11px] text-soft">
      <BookOpenIcon aria-hidden className="shrink-0" />
      {url ? (
        <a href={url} target="_blank" rel="noreferrer" className="text-soft hover:text-accent-200">
          {texto}
        </a>
      ) : (
        texto
      )}
    </span>
  );
}
