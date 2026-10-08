import { useState } from 'react';
import { decideProposal, pct, report, reportMarkdown, reportRange, titleOf, type AppState } from '../engine';
import { Card, IaBadge, Icon, Logo } from '../ui';

interface Props {
  s: AppState;
  setS: (s: AppState) => void;
  onSimulate: () => void;
  onSelectNode: (id: string) => void;
  onPractice: (id: string) => void;
}

export function Report({ s, setS, onSimulate, onSelectNode, onPractice }: Props) {
  const r = report(s);
  const [copied, setCopied] = useState(false);

  const copy = async () => {
    try {
      await navigator.clipboard.writeText(reportMarkdown(s, r));
      setCopied(true);
      window.setTimeout(() => setCopied(false), 2000);
    } catch {
      setCopied(false);
    }
  };

  return (
    <div className="narrow stack">
      <div className="row between wrap gap-s">
        <div>
          <span className="eyebrow">Informe semanal</span>
          <h1>Tu semana, en un vistazo</h1>
          <p className="muted">Lo arma la IA con tu práctica real. Sirve como newsletter personal: lo podés leer acá o recibirlo por mail.</p>
        </div>
        <div className="row wrap gap-s">
          <button type="button" className="btn btn-ghost" onClick={copy}><Icon name="copy" size={15} />{copied ? 'Copiado' : 'Copiar como texto'}</button>
          <button type="button" className="btn btn-xp" onClick={onSimulate}><Icon name="fast" size={15} />Simular una semana</button>
        </div>
      </div>

      <article className="newsletter">
        <header className="nl-head">
          <Logo />
          <span className="muted small">Semana {reportRange(r)}</span>
          <IaBadge label="Generado por IA" />
        </header>

        {r.sessions === 0 ? (
          <div className="nl-empty">
            <p><b>Todavía no hay práctica en esta semana.</b></p>
            <p className="muted">Hacé una sesión o tocá «Simular una semana» para ver cómo se arma el informe con datos.</p>
          </div>
        ) : (
          <>
            <section className="nl-section">
              <h3>Cómo te fue</h3>
              <div className="nl-stats">
                <div><b>{r.sessions}</b><span>sesiones</span></div>
                <div><b>{r.minutes}</b><span>minutos</span></div>
                <div><b>{pct(r.accuracy)}</b><span>de aciertos</span></div>
                <div className="xp"><b>+{r.xp}</b><span>XP</span></div>
                <div className="xp"><b>{r.streak}</b><span>días de racha</span></div>
              </div>
              {r.mastered.length ? (
                <p>Dominaste {r.mastered.map((id, i) => <span key={id}>{i ? ', ' : ''}<button type="button" className="inline-link" onClick={() => onSelectNode(id)}>{titleOf(id)}</button></span>)}.</p>
              ) : <p className="muted">Esta semana no cerraste ningún tema nuevo.</p>}
            </section>

            <section className="nl-section">
              <h3>Lo que te está costando</h3>
              {r.insights.length ? r.insights.map((i) => (
                <div key={i.id} className="insight"><b>{i.title}</b><p className="muted">{i.detail} {i.tip}</p></div>
              )) : <p className="muted">Nada para marcar: vas parejo.</p>}
            </section>
          </>
        )}

        <section className="nl-section">
          <h3>Ajustes que te propongo</h3>
          {r.proposals.length ? r.proposals.map((p) => (
            <div key={p.id} className="proposal">
              <div><b>{p.title}</b><p className="muted small">{p.detail}</p></div>
              <div className="row gap-s">
                <button type="button" className="btn btn-ia btn-sm" onClick={() => setS(decideProposal(s, p.id, true))}>Aceptar</button>
                <button type="button" className="btn btn-ghost btn-sm" onClick={() => setS(decideProposal(s, p.id, false))}>No, gracias</button>
              </div>
            </div>
          )) : <p className="muted">Ninguno por ahora: el plan sigue como está.</p>}
        </section>

        <section className="nl-section">
          <h3>Novedades que te importan</h3>
          {r.news.length ? r.news.map((n) => (
            <div key={n.item.id} className="nl-news"><b>{n.item.title}</b><p className="muted small">{n.reason}</p></div>
          )) : <p className="muted">Nada relevante para tu ruta esta semana.</p>}
          {r.parkedReady.length ? (
            <div className="ia-note"><Icon name="bookmark" size={14} />Ya tenés la base para lo que guardaste: {r.parkedReady.map((p) => `«${titleOf(p.id)}»`).join(', ')}. Buscalo en «¿Qué te interesa?» para sumarlo.</div>
          ) : null}
        </section>

        <section className="nl-section">
          <h3>Lo que sigue</h3>
          {r.next.map((n) => (
            <button key={n.node.id} type="button" className="next-row" onClick={() => onPractice(n.node.id)}>
              <span><b>{n.node.title}</b><span className="muted small">{n.reason}</span></span>
              <Icon name="play" size={15} />
            </button>
          ))}
        </section>
      </article>

      {s.parked.length ? (
        <Card>
          <h3><Icon name="bookmark" size={16} /> Guardados para más adelante</h3>
          <ul className="plain">{s.parked.map((p) => <li key={p.id}><b>{titleOf(p.id)}</b> · <span className="muted">hito {p.stage + 1}. {p.reason}</span></li>)}</ul>
        </Card>
      ) : null}
    </div>
  );
}
