import { useEffect, useState } from 'react';
import { analyzeInterest, applyInterest, pct, searchRefs, type AppState, type Decision } from '../engine';
import { IaBadge, Icon, type IconName } from '../ui';

interface Props {
  s: AppState;
  setS: (s: AppState) => void;
  initialRef: string | null;
  query: string;
  onClose: () => void;
  onApplied: (ref: string, message: string) => void;
}

const STEPS = ['Buscando el tema en el catálogo', 'Revisando qué necesita saber antes', 'Comparando con tu perfil', 'Decidiendo dónde ubicarlo'];

const DECISION: Record<Decision, { icon: IconName; cta: string; tone: string }> = {
  'ya-esta': { icon: 'map', cta: 'Ver en el mapa', tone: 'neutral' },
  ahora: { icon: 'bolt', cta: 'Sumarlo ahora', tone: 'xp' },
  'mas-adelante': { icon: 'calendar', cta: 'Agendarlo', tone: 'ia' },
  rama: { icon: 'branch', cta: 'Sumar como rama opcional', tone: 'ia' },
  guardado: { icon: 'bookmark', cta: 'Guardar y avisarme', tone: 'neutral' },
};

export function InterestModal({ s, setS, initialRef, query, onClose, onApplied }: Props) {
  const [matches] = useState(() => (initialRef ? [] : searchRefs(query)));
  const [ref, setRef] = useState<string | null>(() => initialRef ?? matches[0]?.id ?? null);
  const [step, setStep] = useState(0);

  useEffect(() => {
    if (!ref) return undefined;
    setStep(0);
    const timers = STEPS.map((_, i) => window.setTimeout(() => setStep(i + 1), 450 * (i + 1)));
    return () => timers.forEach((t) => window.clearTimeout(t));
  }, [ref]);

  useEffect(() => {
    const onKey = (e: KeyboardEvent) => { if (e.key === 'Escape') onClose(); };
    window.addEventListener('keydown', onKey);
    return () => window.removeEventListener('keydown', onKey);
  }, [onClose]);

  const analysis = ref ? analyzeInterest(s, ref) : null;
  const done = step >= STEPS.length;

  const apply = () => {
    if (!analysis) return;
    if (analysis.decision !== 'ya-esta') setS(applyInterest(s, analysis));
    onApplied(analysis.ref, analysis.decision === 'ya-esta' ? `«${analysis.title}» ya está en tu ruta.` : analysis.headline);
  };

  return (
    <div className="modal-backdrop" role="presentation" onClick={onClose}>
      <div className="modal" role="dialog" aria-modal="true" aria-labelledby="interest-title" onClick={(e) => e.stopPropagation()}>
        <div className="row between">
          <span className="row gap-s"><IaBadge /><span className="eyebrow">Me interesa esto</span></span>
          <button type="button" className="btn btn-ghost btn-icon" onClick={onClose} aria-label="Cerrar"><Icon name="x" /></button>
        </div>

        {!ref ? (
          <div className="stack-s">
            <h2 id="interest-title">«{query}»</h2>
            <p className="muted">No lo encontré en el catálogo del prototipo. En la versión con IA, se arma un tema nuevo a partir de tu pedido.</p>
            <span className="eyebrow">Probá con alguno de estos</span>
            <div className="row wrap gap-s">
              {['agentes', 'MCP', 'modelos locales', 'fine-tuning', 'automatizar sin código', 'caché de prompts', 'notebooks'].map((t) => (
                <span key={t} className="mini-tag">{t}</span>
              ))}
            </div>
          </div>
        ) : analysis ? (
          <div className="stack-s">
            {!initialRef && matches.length > 1 ? (
              <div className="row wrap gap-s">
                <span className="muted small">Encontré:</span>
                {matches.map((m) => <button key={m.id} type="button" className={`chip-btn sm ${m.id === ref ? 'on' : ''}`} onClick={() => setRef(m.id)}>{m.title}</button>)}
              </div>
            ) : null}
            <h2 id="interest-title">{analysis.title}</h2>
            <p className="muted">{analysis.summary}</p>

            <ol className="analysis-steps">
              {STEPS.map((label, i) => (
                <li key={label} className={i < step ? 'done' : i === step ? 'run' : ''}>
                  <span className="step-dot">{i < step ? <Icon name="check" size={12} /> : null}</span>{label}
                </li>
              ))}
            </ol>

            {step >= 2 && analysis.prereqs.length ? (
              <div className="prereqs">
                {analysis.prereqs.map((p) => (
                  <div key={p.id} className={`prereq ${p.ok ? 'ok' : 'miss'}`}>
                    <Icon name={p.ok ? 'check' : 'lock'} size={14} />
                    <span>{p.title}</span>
                    <span className="muted small">hito {p.stage + 1}</span>
                    <span className="prereq-bar"><span style={{ width: `${Math.min(100, (p.mastery / 0.8) * 100)}%` }} /></span>
                    <b className="small">{pct(p.mastery)}</b>
                  </div>
                ))}
              </div>
            ) : null}

            {done ? (
              <div className={`decision tone-${DECISION[analysis.decision].tone}`}>
                <div className="decision-head"><Icon name={DECISION[analysis.decision].icon} size={20} /><b>{analysis.headline}</b></div>
                <ul>{analysis.reasons.map((r) => <li key={r}>{r}</li>)}</ul>
                <div className="row wrap gap-s">
                  <button type="button" className="btn btn-ia" onClick={apply}>{DECISION[analysis.decision].cta}</button>
                  <button type="button" className="btn btn-ghost" onClick={onClose}>Ahora no</button>
                </div>
              </div>
            ) : null}
          </div>
        ) : null}
      </div>
    </div>
  );
}
