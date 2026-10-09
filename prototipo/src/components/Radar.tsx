import { useState } from 'react';
import { NEWS } from '../data';
import { newsView, type AppState, type NewsView } from '../engine';
import { Card, IaBadge, Icon } from '../ui';

interface Props {
  s: AppState;
  setS: (s: AppState) => void;
  onInterest: (ref: string) => void;
}

export function Radar({ s, setS, onInterest }: Props) {
  const [showNoise, setShowNoise] = useState(false);
  const view = newsView(s);
  const now = view.filter((v) => v.bucket === 'now');
  const later = view.filter((v) => v.bucket === 'later');
  const noise = view.filter((v) => v.bucket === 'noise');
  const dismiss = (id: string) => setS({ ...s, dismissedNews: [...s.dismissedNews, id] });

  return (
    <div className="narrow stack">
      <div>
        <span className="eyebrow">Radar</span>
        <h1>Novedades, sin ruido</h1>
        <p className="muted">La IA revisa lo que pasa en tu rubro y te muestra solo lo que cambia algo para tu ruta. Lo que todavía no te sirve, lo guarda para cuando llegues.</p>
      </div>

      <div className="radar-stats">
        <div className="radar-stat ia"><b>{NEWS.length}</b><span>novedades revisadas</span></div>
        <div className="radar-stat"><b>{now.length}</b><span>para vos ahora</span></div>
        <div className="radar-stat"><b>{later.length}</b><span>guardadas para más adelante</span></div>
        <div className="radar-stat muted"><b>{noise.length}</b><span>ruido filtrado</span></div>
      </div>

      <Section title="Para vos ahora" icon="target" items={now} onInterest={onInterest} onDismiss={dismiss} empty="Nada urgente esta semana." />
      <Section title="Más adelante" icon="calendar" items={later} onInterest={onInterest} onDismiss={dismiss} empty="Nada en espera." />

      <Card className="noise">
        <button type="button" className="row between noise-toggle" onClick={() => setShowNoise(!showNoise)} aria-expanded={showNoise}>
          <span><Icon name="x" size={15} /> Ocultamos {noise.length} novedades que no tienen que ver con tu ruta</span>
          <span className="muted small">{showNoise ? 'Ocultar' : 'Ver cuáles'}</span>
        </button>
        {showNoise ? (
          <ul className="noise-list">
            {noise.map((n) => <li key={n.item.id}><span>{n.item.title}</span><span className="muted small">{n.reason}</span></li>)}
          </ul>
        ) : null}
      </Card>
      <p className="muted small">Las novedades son ficticias, escritas para la demo. En el producto saldrían de una búsqueda periódica filtrada por la ruta de cada persona.</p>
    </div>
  );
}

function Section({ title, icon, items, onInterest, onDismiss, empty }: { title: string; icon: 'target' | 'calendar'; items: NewsView[]; onInterest: (ref: string) => void; onDismiss: (id: string) => void; empty: string }) {
  return (
    <div className="stack-s">
      <h3 className="section-title"><Icon name={icon} size={16} />{title} <span className="count">{items.length}</span></h3>
      {items.length ? items.map(({ item, reason, bucket }) => (
        <Card key={item.id} className={`news ${bucket}`}>
          <span className="muted small">{new Date(`${item.date}T12:00:00`).toLocaleDateString('es-AR', { day: 'numeric', month: 'long' })}</span>
          <h4>{item.title}</h4>
          <p className="muted">{item.summary}</p>
          <p className="ia-note"><Icon name="sparkles" size={14} />{reason}</p>
          <div className="row wrap gap-s">
            {item.ref ? <button type="button" className="btn btn-ia btn-sm" onClick={() => onInterest(item.ref!)}><Icon name="sparkles" size={14} />Me interesa</button> : null}
            <button type="button" className="btn btn-ghost btn-sm" onClick={() => onDismiss(item.id)}>Descartar</button>
          </div>
        </Card>
      )) : <p className="muted small">{empty}</p>}
      {items.length && title === 'Más adelante' ? <p className="muted small"><IaBadge /> Igual podés tocar «Me interesa»: la IA analiza si conviene sumarlo ahora, agendarlo o abrir una rama.</p> : null}
    </div>
  );
}
