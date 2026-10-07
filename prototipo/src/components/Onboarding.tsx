import { useEffect, useMemo, useState } from 'react';
import { NODES, ROUTE, STAGES } from '../data';
import {
  currentStage, diagnose, diagnosisQuestions, eta, MASTERED, recommended,
  type AppState, type CodeLevel, type Confidence, type DiagAnswer,
} from '../engine';
import { IaBadge, Icon, Logo } from '../ui';

interface Props {
  base: AppState;
  onDone: (s: AppState) => void;
}

const STEP_MS = 520;

export function Onboarding({ base, onDone }: Props) {
  const [step, setStep] = useState(0);
  const [goal, setGoal] = useState('');
  const [code, setCode] = useState<CodeLevel>('nada');
  const [minutes, setMinutes] = useState(20);
  const [qi, setQi] = useState(0);
  const [choice, setChoice] = useState<number | null>(null);
  const [answers, setAnswers] = useState<DiagAnswer[]>([]);
  const [build, setBuild] = useState(0);
  const questions = diagnosisQuestions();
  const goalText = goal.trim() || ROUTE.goalExamples[1];

  // El resultado del diagnóstico se calcula una sola vez, al llegar al armado de la ruta.
  const ready = step >= 3;
  const result = useMemo(
    () => (ready ? diagnose(base, goalText, code, minutes, answers) : null),
    [ready, base, goalText, code, minutes, answers],
  );

  useEffect(() => {
    if (step !== 3) return undefined;
    setBuild(0);
    const timers = STAGES.map((_, i) => window.setTimeout(() => setBuild(i + 1), STEP_MS * (i + 1)));
    const end = window.setTimeout(() => setStep(4), STEP_MS * (STAGES.length + 1.5));
    return () => { timers.forEach((t) => window.clearTimeout(t)); window.clearTimeout(end); };
  }, [step]);

  const answerQ = (conf: Confidence) => {
    if (choice === null) return;
    const { node, question } = questions[qi];
    setAnswers([...answers, { node, correct: choice === question.correct, conf }]);
    setChoice(null);
    if (qi < questions.length - 1) setQi(qi + 1);
    else setStep(3);
  };

  const known = result ? NODES.filter((n) => n.kind === 'core' && (result.mastery[n.id] ?? 0) >= MASTERED) : [];

  return (
    <div className="onb">
      <div className="onb-card">
        <div className="row between"><Logo /><span className="muted small">{step < 4 ? `Paso ${Math.min(step + 1, 4)} de 4` : '¡Listo!'}</span></div>
        <div className="onb-dots">{[0, 1, 2, 3].map((i) => <span key={i} className={i <= step ? 'on' : ''} />)}</div>

        {step === 0 ? (
          <div className="stack">
            <div className="onb-hero">
              <MiniRoad />
              <div>
                <h1>¿A dónde querés llegar?</h1>
                <p className="muted">Contalo con tus palabras. Te armamos un camino, lo recorrés practicando y se ajusta según cómo te va.</p>
              </div>
            </div>
            <textarea className="input" rows={3} value={goal} onChange={(e) => setGoal(e.target.value)} placeholder="Ej.: quiero usar IA para automatizar partes de mi trabajo" />
            <div className="row wrap gap-s">{ROUTE.goalExamples.map((g) => <button key={g} type="button" className={`chip-btn sm ${goal === g ? 'on' : ''}`} onClick={() => setGoal(g)}>{g}</button>)}</div>
            <p className="ia-note"><Icon name="sparkles" size={14} />En este prototipo hay una ruta cargada: IA aplicada. En el producto, la IA arma la ruta a partir de tu objetivo.</p>
            <button type="button" className="btn btn-ia" onClick={() => setStep(1)}>Seguir <Icon name="arrow" size={15} /></button>
          </div>
        ) : null}

        {step === 1 ? (
          <div className="stack">
            <div>
              <h1>Un par de datos</h1>
              <p className="muted">Sirven para no hacerte repetir lo que ya sabés y para calcular cuándo llegás.</p>
            </div>
            <div className="stack-s">
              <span className="eyebrow">¿Programás?</span>
              <div className="seg">
                {([['nada', 'Nada'], ['algo', 'Algo'], ['bastante', 'Bastante']] as [CodeLevel, string][]).map(([v, l]) => (
                  <button key={v} type="button" className={code === v ? 'on' : ''} onClick={() => setCode(v)}>{l}</button>
                ))}
              </div>
            </div>
            <div className="stack-s">
              <span className="eyebrow">¿Cuánto tiempo por día?</span>
              <div className="seg">
                {[10, 20, 30, 60].map((m) => <button key={m} type="button" className={minutes === m ? 'on' : ''} onClick={() => setMinutes(m)}>{m} min</button>)}
              </div>
            </div>
            <div className="row gap-s">
              <button type="button" className="btn btn-ghost" onClick={() => setStep(0)}>Atrás</button>
              <button type="button" className="btn btn-ia" onClick={() => setStep(2)}>Hacer el diagnóstico <Icon name="arrow" size={15} /></button>
            </div>
          </div>
        ) : null}

        {step === 2 ? (
          <div className="stack">
            <div className="row between"><span className="eyebrow">Diagnóstico · {qi + 1} de {questions.length}</span><IaBadge label="No es un examen" /></div>
            <div className="diag-progress">{questions.map((q, i) => <span key={q.question.id} className={i < qi ? 'done' : i === qi ? 'on' : ''} />)}</div>
            <h2>{questions[qi].question.q}</h2>
            <div className="options">
              {questions[qi].question.options.map((o, i) => (
                <button key={o} type="button" className={`option ${choice === i ? 'picked' : ''}`} onClick={() => setChoice(i)}>
                  <span className="letter">{'ABCD'[i]}</span><span>{o}</span>
                </button>
              ))}
            </div>
            {choice !== null ? (
              <div className="conf">
                <span className="small">¿Lo sabías o lo adivinaste? Lo adivinado cuenta como no sabido, así no te salteamos nada.</span>
                <div className="row wrap gap-s">
                  <button type="button" className="chip-btn" onClick={() => answerQ('sabia')}><b>Lo sabía</b></button>
                  <button type="button" className="chip-btn" onClick={() => answerQ('dude')}><b>Lo dudé</b></button>
                  <button type="button" className="chip-btn" onClick={() => answerQ('adivine')}><b>Lo adiviné</b></button>
                </div>
              </div>
            ) : <p className="muted small">Elegí una opción. Si no tenés idea, elegí cualquiera y marcá «Lo adiviné».</p>}
          </div>
        ) : null}

        {step === 3 && result ? (
          <div className="stack">
            <div>
              <span className="row gap-s"><IaBadge /><span className="eyebrow">Armando tu camino</span></span>
              <h1>{goalText}</h1>
            </div>
            <ol className="build-road">
              {STAGES.map((st, i) => {
                const nodes = NODES.filter((n) => n.stage === i && n.kind === 'core');
                const knownHere = nodes.filter((n) => (result.mastery[n.id] ?? 0) >= MASTERED);
                const lit = i < build;
                const allKnown = knownHere.length === nodes.length;
                return (
                  <li key={st.title} className={`${lit ? 'on' : ''} ${lit && allKnown ? 'known' : ''}`}>
                    <span className="br-dot">{lit && allKnown ? <Icon name="check" size={14} /> : i + 1}</span>
                    <div className="br-body">
                      <b>{st.title}</b>
                      <div className="br-chips">
                        {nodes.map((n) => {
                          const k = (result.mastery[n.id] ?? 0) >= MASTERED;
                          return <span key={n.id} className={`br-chip ${lit && k ? 'known' : ''}`}>{lit && k ? <Icon name="check" size={11} /> : null}{n.title}</span>;
                        })}
                      </div>
                    </div>
                  </li>
                );
              })}
            </ol>
          </div>
        ) : null}

        {step === 4 && result ? (
          <Ready result={result} known={known.map((n) => n.title)} onGo={() => onDone(result)} />
        ) : null}
      </div>
    </div>
  );
}

function Ready({ result, known, onGo }: { result: AppState; known: string[]; onGo: () => void }) {
  const cur = currentStage(result);
  const e = eta(result);
  const first = recommended(result);
  return (
    <div className="stack">
      <div>
        <span className="eyebrow">Tu camino está listo</span>
        <h1>{result.goal}</h1>
      </div>
      <div className="ready-grid">
        <div className="ready-item xp">
          <Icon name="check" size={18} />
          <div><b>{known.length ? `Salteás ${known.length} ${known.length === 1 ? 'tema' : 'temas'}` : 'Arrancás desde el principio'}</b>
            <span className="muted small">{known.length ? known.join(', ') : 'Vamos de a poco, sin saltear nada.'}</span></div>
        </div>
        <div className="ready-item">
          <Icon name="map" size={18} />
          <div><b>Arrancás en el hito {cur + 1}</b><span className="muted small">{STAGES[cur].title}</span></div>
        </div>
        <div className="ready-item">
          <Icon name="calendar" size={18} />
          <div><b>Llegás en {e.label}</b><span className="muted small">Con {result.minutesPerDay} minutos por día. Lo podés cambiar cuando quieras.</span></div>
        </div>
        {first ? (
          <div className="ready-item ia">
            <Icon name="play" size={18} />
            <div><b>Primer paso: {first.node.title}</b><span className="muted small">{first.reason}</span></div>
          </div>
        ) : null}
      </div>
      <button type="button" className="btn btn-xp btn-lg" onClick={onGo}>Ver mi camino <Icon name="arrow" size={16} /></button>
    </div>
  );
}

/** Dibujito del camino vertical para la pantalla de bienvenida. */
function MiniRoad() {
  return (
    <svg className="mini-road" viewBox="0 0 64 120" aria-hidden="true">
      <path d="M32 10 C32 30 14 30 14 46 M32 10 C32 30 50 30 50 46 M14 58 C14 74 32 72 32 86 M50 58 C50 74 32 72 32 86" fill="none" stroke="#FF7A1A" strokeWidth="3" strokeLinecap="round" />
      <path d="M50 58 C58 66 60 76 58 88" fill="none" stroke="#19C3F0" strokeWidth="2.5" strokeDasharray="4 4" strokeLinecap="round" />
      <circle cx="32" cy="8" r="7" fill="#FFD21F" />
      <circle cx="14" cy="52" r="7" fill="#FF7A1A" />
      <circle cx="50" cy="52" r="7" fill="#FFFFFF" stroke="#CBD3DD" strokeWidth="2" />
      <circle cx="58" cy="94" r="5" fill="#FFFFFF" stroke="#19C3F0" strokeWidth="2" strokeDasharray="3 2" />
      <circle cx="32" cy="94" r="8" fill="url(#mr)" />
      <defs><linearGradient id="mr" x1="0" y1="0" x2="1" y2="1"><stop offset="0" stopColor="#1F5BFF" /><stop offset="1" stopColor="#19C3F0" /></linearGradient></defs>
    </svg>
  );
}
