import { useEffect, useRef, useState } from 'react';
import type { Question } from '../data';
import {
  answer, finishSession, findNode, insights, mastery, pct, pickQuestions, recommendations, titleOf,
  type AppState, type Attempt, type Confidence, type SessionSummary,
} from '../engine';
import { Bar, Card, IaBadge, Icon } from '../ui';

interface Props {
  s: AppState;
  setS: (s: AppState) => void;
  nodeId: string | null;
  onPick: (id: string | null) => void;
  onMap: (id?: string) => void;
}

const CONF: { id: Confidence; label: string; hint: string }[] = [
  { id: 'sabia', label: 'Lo sabía', hint: 'Estaba seguro/a' },
  { id: 'dude', label: 'Lo dudé', hint: 'Lo razoné, sin certeza' },
  { id: 'adivine', label: 'Lo adiviné', hint: 'Elegí al azar' },
];

export function Practice({ s, setS, nodeId, onPick, onMap }: Props) {
  // Cada sesión nueva (aunque sea del mismo tema) arranca de cero.
  const [nonce, setNonce] = useState(0);
  const pick = (id: string | null) => { setNonce((n) => n + 1); onPick(id); };
  if (!nodeId) return <Picker s={s} onPick={pick} />;
  return <Session key={`${nodeId}-${nonce}`} s={s} setS={setS} nodeId={nodeId} onPick={pick} onMap={onMap} />;
}

function Picker({ s, onPick }: { s: AppState; onPick: (id: string) => void }) {
  const recs = recommendations(s).slice(0, 4);
  return (
    <div className="narrow stack">
      <div>
        <span className="eyebrow">Practicar</span>
        <h1>¿Qué practicamos hoy?</h1>
        <p className="muted">Sesiones de 3 preguntas. Antes de ver si acertaste, contás si lo sabías: así el sistema distingue lo que sabés de lo que adivinás.</p>
      </div>
      {recs.length ? recs.map((r, i) => (
        <button key={r.node.id} type="button" className={`pick ${i === 0 ? 'first' : ''}`} onClick={() => onPick(r.node.id)}>
          <span className="pick-main">
            <span className="row gap-s">{i === 0 ? <IaBadge label="Recomendado" /> : null}<span className="eyebrow">Hito {r.node.stage + 1}</span></span>
            <b>{r.node.title}</b>
            <span className="muted small">{r.reason}</span>
          </span>
          <span className="pick-side"><span className="small">{pct(mastery(s, r.node.id))}</span><Icon name="arrow" /></span>
        </button>
      )) : <Card><p>No hay temas abiertos. Sumá uno desde «¿Qué te interesa?».</p></Card>}
    </div>
  );
}

function Session({ s, setS, nodeId, onPick, onMap }: Props & { nodeId: string }) {
  const node = findNode(s, nodeId);
  const [questions] = useState<Question[]>(() => pickQuestions(s, nodeId));
  const [idx, setIdx] = useState(0);
  const [choice, setChoice] = useState<number | null>(null);
  const [conf, setConf] = useState<Confidence | null>(null);
  const [atts, setAtts] = useState<Attempt[]>([]);
  const [xp, setXp] = useState(0);
  const [unlocked, setUnlocked] = useState<string[]>([]);
  const [summary, setSummary] = useState<SessionSummary | null>(null);
  const [before] = useState(() => mastery(s, nodeId));
  const t0 = useRef(Date.now());

  useEffect(() => { t0.current = Date.now(); }, [idx]);

  if (!node) return <Card><p>No encontré ese tema.</p></Card>;
  if (summary) return <Summary s={s} summary={summary} onPick={onPick} onMap={onMap} />;

  const q = questions[idx];
  const revealed = conf !== null;
  const isCorrect = choice === q.correct;
  const fromOther = !node.questions.includes(q);

  const pickConf = (c: Confidence) => {
    if (choice === null) return;
    const res = answer(s, q, nodeId, choice === q.correct, c, Date.now() - t0.current);
    setS(res.state);
    setAtts([...atts, res.state.attempts[res.state.attempts.length - 1]]);
    setXp(xp + res.xp);
    setUnlocked([...unlocked, ...res.unlocked]);
    setConf(c);
  };

  const next = () => {
    if (idx < questions.length - 1) {
      setIdx(idx + 1); setChoice(null); setConf(null);
      return;
    }
    const f = finishSession(s, nodeId, atts, before, unlocked, xp);
    setS(f.state);
    setSummary(f.summary);
  };

  return (
    <div className="narrow stack">
      <div className="row between">
        <div>
          <span className="eyebrow">Practicando · Hito {node.stage + 1}</span>
          <h1>{node.title}</h1>
        </div>
        <div className="xp-live"><Icon name="bolt" size={16} />+{xp} XP</div>
      </div>
      <div className="steps">{questions.map((qq, i) => <span key={qq.id} className={`step ${i < idx ? 'done' : i === idx ? 'on' : ''}`} />)}</div>
      <Card className="question">
        <div className="row between">
          <span className="eyebrow">Pregunta {idx + 1} de {questions.length} · {q.type === 'aplicacion' ? 'aplicación' : q.type}</span>
          {fromOther ? <IaBadge label="Caso práctico extra" /> : null}
        </div>
        <h2>{q.q}</h2>
        <div className="options">
          {q.options.map((o, i) => {
            const cls = revealed ? (i === q.correct ? 'right' : i === choice ? 'wrong' : 'faded') : i === choice ? 'picked' : '';
            return (
              <button key={o} type="button" className={`option ${cls}`} disabled={revealed} onClick={() => setChoice(i)}>
                <span className="letter">{'ABCD'[i]}</span><span>{o}</span>
              </button>
            );
          })}
        </div>
        {choice !== null && !revealed ? (
          <div className="conf">
            <span className="small">Antes de ver la respuesta: ¿cómo la elegiste?</span>
            <div className="row wrap gap-s">
              {CONF.map((c) => <button key={c.id} type="button" className="chip-btn" onClick={() => pickConf(c.id)}><b>{c.label}</b><span className="muted small">{c.hint}</span></button>)}
            </div>
          </div>
        ) : null}
        {revealed ? (
          <div className={`feedback ${isCorrect ? 'ok' : 'bad'}`}>
            <b>{isCorrect ? (conf === 'adivine' ? 'Correcta, pero adivinada: vuelve en el repaso.' : '¡Correcta!') : 'No era esa.'}</b>
            <p>{q.explain}</p>
            <button type="button" className="btn btn-xp" onClick={next}>{idx < questions.length - 1 ? 'Siguiente' : 'Terminar sesión'}<Icon name="arrow" size={15} /></button>
          </div>
        ) : null}
      </Card>
      <button type="button" className="btn btn-ghost" onClick={() => onPick(null)}>Elegir otro tema</button>
    </div>
  );
}

function Summary({ s, summary, onPick, onMap }: { s: AppState; summary: SessionSummary; onPick: (id: string | null) => void; onMap: (id?: string) => void }) {
  const nextRec = recommendations(s)[0];
  const related = insights(s).filter((i) => i.id.endsWith(summary.node) || i.id === 'adivinas' || i.id.startsWith('tipo-')).slice(0, 2);
  const mastered = summary.before < 0.8 && summary.after >= 0.8;
  return (
    <div className="narrow stack">
      <div className={`summary-hero ${mastered ? 'mastered' : ''}`}>
        <span className="eyebrow">{mastered ? '¡Tema dominado!' : 'Sesión terminada'}</span>
        <h1>{titleOf(summary.node)}</h1>
        <div className="summary-stats">
          <div><b>{summary.correct}/{summary.total}</b><span>correctas</span></div>
          <div className="xp-big"><b>+{summary.xp}</b><span>XP</span></div>
          <div><b>{pct(summary.before)} → {pct(summary.after)}</b><span>dominio</span></div>
        </div>
        <Bar value={summary.after / 0.8} />
      </div>
      {summary.adaptations.length ? (
        <Card className="ia-card">
          <h3><IaBadge /> Así se ajustó tu ruta</h3>
          <ul className="adapt-list">{summary.adaptations.map((a) => <li key={a.id}><Icon name={a.kind === 'nodo' ? 'branch' : a.kind === 'refuerzo' ? 'refresh' : 'sparkles'} size={15} />{a.text}</li>)}</ul>
        </Card>
      ) : <Card className="ia-card"><h3><IaBadge /> Sin cambios en la ruta</h3><p className="muted">Fue una sesión pareja: seguimos con el plan.</p></Card>}
      {related.length ? (
        <Card>
          <h3>Lo que noté</h3>
          {related.map((i) => <div key={i.id} className="insight"><b>{i.title}</b><p className="muted">{i.detail} {i.tip}</p></div>)}
        </Card>
      ) : null}
      <div className="row wrap gap-s">
        {nextRec ? <button type="button" className="btn btn-ia" onClick={() => onPick(nextRec.node.id)}><Icon name="play" size={15} />Seguir con «{nextRec.node.title}»</button> : null}
        <button type="button" className="btn btn-ghost" onClick={() => onMap(summary.node)}><Icon name="map" size={15} />Ver en el mapa</button>
      </div>
    </div>
  );
}
