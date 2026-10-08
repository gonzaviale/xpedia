import { useCallback, useEffect, useState, type FormEvent } from 'react';
import { InterestModal } from './components/InterestModal';
import { MapView } from './components/MapView';
import { Motor } from './components/Motor';
import { Onboarding } from './components/Onboarding';
import { Practice } from './components/Practice';
import { Radar } from './components/Radar';
import { Report } from './components/Report';
import { initialState, newsView, proposals, simulateWeek, type AppState } from './engine';
import { useAppState } from './store';
import { Icon, Logo, type IconName } from './ui';

type Tab = 'mapa' | 'practicar' | 'radar' | 'informe' | 'motor';

const TABS: { id: Tab; label: string; icon: IconName }[] = [
  { id: 'mapa', label: 'Mi ruta', icon: 'map' },
  { id: 'practicar', label: 'Practicar', icon: 'play' },
  { id: 'radar', label: 'Radar', icon: 'radar' },
  { id: 'informe', label: 'Informe', icon: 'doc' },
  { id: 'motor', label: 'Cómo funciona', icon: 'sparkles' },
];

export default function App() {
  const [s, setS] = useAppState();
  const [tab, setTab] = useState<Tab>('mapa');
  const [selected, setSelected] = useState<string | null>(null);
  const [practiceNode, setPracticeNode] = useState<string | null>(null);
  const [interest, setInterest] = useState<{ ref: string | null; query: string } | null>(null);
  const [query, setQuery] = useState('');
  const [toast, setToast] = useState<string | null>(null);
  const [flash, setFlash] = useState<string | null>(null);

  const say = useCallback((msg: string) => setToast(msg), []);
  useEffect(() => {
    if (!toast) return undefined;
    const t = window.setTimeout(() => setToast(null), 3600);
    return () => window.clearTimeout(t);
  }, [toast]);
  useEffect(() => {
    if (!flash) return undefined;
    const t = window.setTimeout(() => setFlash(null), 2400);
    return () => window.clearTimeout(t);
  }, [flash]);

  const finishOnboarding = useCallback((next: AppState) => { setS(next); setTab('mapa'); }, [setS]);

  if (!s.onboarded) return <Onboarding base={initialState()} onDone={finishOnboarding} />;

  const goMap = (id?: string) => { setTab('mapa'); if (id) { setSelected(id); setFlash(id); } window.scrollTo({ top: 0 }); };
  const practice = (id: string | null) => { setPracticeNode(id); setTab('practicar'); window.scrollTo({ top: 0 }); };
  const simulate = () => {
    const next = simulateWeek(s);
    const newSessions = next.sessions.length - s.sessions.length;
    setS(next);
    say(`Pasó una semana (simulada): ${newSessions} sesiones, la ruta se ajustó y el informe ya tiene datos.`);
  };
  const reset = () => {
    if (window.confirm('¿Borrar todo el progreso y volver a empezar la demo?')) {
      setS(initialState()); setTab('mapa'); setSelected(null); setPracticeNode(null);
    }
  };
  const submitQuery = (e: FormEvent) => {
    e.preventDefault();
    if (query.trim()) setInterest({ ref: null, query: query.trim() });
  };

  const nowNews = newsView(s).filter((n) => n.bucket === 'now').length;
  const pending = proposals(s).length;

  return (
    <div className="app">
      <header className="topbar">
        <div className="topbar-row">
          <Logo />
          <nav className="tabs" aria-label="Secciones">
            {TABS.map((t) => (
              <button key={t.id} type="button" className={`tab ${tab === t.id ? 'on' : ''}`} onClick={() => { setTab(t.id); if (t.id === 'practicar') setPracticeNode(null); }}>
                <Icon name={t.icon} size={16} /><span>{t.label}</span>
                {t.id === 'radar' && nowNews ? <span className="tab-count ia">{nowNews}</span> : null}
                {t.id === 'informe' && pending ? <span className="tab-count xp">{pending}</span> : null}
              </button>
            ))}
          </nav>
          <div className="topbar-right">
            <span className="xp-pill" title="Experiencia"><Icon name="bolt" size={15} />{s.xp} XP</span>
            <span className="streak-pill" title="Días seguidos practicando"><Icon name="flame" size={15} />{s.streak}</span>
            <button type="button" className="btn btn-ghost btn-sm" onClick={simulate} title="Avanza 7 días con práctica simulada"><Icon name="fast" size={15} /><span className="hide-sm">Simular semana</span></button>
            <button type="button" className="btn btn-ghost btn-icon" onClick={reset} title="Reiniciar la demo" aria-label="Reiniciar la demo"><Icon name="refresh" size={16} /></button>
          </div>
        </div>
        <form className="interest-bar" onSubmit={submitQuery}>
          <Icon name="sparkles" size={16} />
          <input value={query} onChange={(e) => setQuery(e.target.value)} placeholder="¿Qué te interesa? Ej.: agentes, MCP, modelos locales…" aria-label="¿Qué te interesa?" />
          <button type="submit" className="btn btn-ia btn-sm">Analizar</button>
        </form>
      </header>

      <main className="main">
        {tab === 'mapa' ? <MapView s={s} setS={setS} selected={selected} onSelect={setSelected} onPractice={practice} flash={flash} /> : null}
        {tab === 'practicar' ? <Practice s={s} setS={setS} nodeId={practiceNode} onPick={setPracticeNode} onMap={goMap} /> : null}
        {tab === 'radar' ? <Radar s={s} setS={setS} onInterest={(ref) => setInterest({ ref, query: '' })} /> : null}
        {tab === 'informe' ? <Report s={s} setS={setS} onSimulate={simulate} onSelectNode={goMap} onPractice={practice} /> : null}
        {tab === 'motor' ? <Motor s={s} onTab={(t) => setTab(t)} /> : null}
      </main>

      <footer className="foot muted small">
        Prototipo de Xpedia · La «IA» de esta demo son reglas sobre el grafo de la ruta y tu historial; los lugares donde entra un modelo están marcados con <span className="ia-dot" /> IA.
      </footer>

      {interest ? (
        <InterestModal
          s={s}
          setS={setS}
          initialRef={interest.ref}
          query={interest.query}
          onClose={() => setInterest(null)}
          onApplied={(ref, msg) => { setInterest(null); setQuery(''); say(msg); goMap(ref); }}
        />
      ) : null}
      {toast ? <div className="toast" role="status"><Icon name="sparkles" size={15} />{toast}</div> : null}
    </div>
  );
}
