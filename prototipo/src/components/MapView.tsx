import { useEffect, useState, type CSSProperties } from 'react';
import { STAGES } from '../data';
import {
  allNodes, ancestors, currentStage, dependents, eta, findNode, isOpen, mastery, missingPrereqs, MASTERED, pct,
  progress, recommended, status, titleOf, type AppState, type GraphNode, type NodeStatus,
} from '../engine';
import { Bar, Card, IaBadge, Icon, type IconName } from '../ui';

const STATUS: Record<NodeStatus, { label: string; icon: IconName }> = {
  mastered: { label: 'Dominado', icon: 'check' },
  rusty: { label: 'Se está oxidando', icon: 'warn' },
  progress: { label: 'En curso', icon: 'bolt' },
  available: { label: 'Disponible', icon: 'play' },
  locked: { label: 'Bloqueado', icon: 'lock' },
  scheduled: { label: 'Agendado', icon: 'calendar' },
};

interface Props {
  s: AppState;
  setS: (s: AppState) => void;
  selected: string | null;
  onSelect: (id: string | null) => void;
  onPractice: (id: string) => void;
  flash: string | null;
}

type ViewMode = 'camino' | 'grafo';

export function MapView({ s, setS, selected, onSelect, onPractice, flash }: Props) {
  const [mode, setMode] = useState<ViewMode>('camino');
  const cur = currentStage(s);
  const prog = progress(s);
  const e = eta(s);
  const rec = recommended(s);
  const focus = selected ? findNode(s, selected) : null;

  return (
    <div className="map-page">
      <Card className="map-head">
        <div className="map-goal">
          <span className="eyebrow">Tu objetivo</span>
          <h1>{s.goal || 'IA aplicada'}</h1>
          <p className="muted">Ruta base: IA aplicada, de usarla a construir con ella · hito {cur + 1} de {STAGES.length}: {STAGES[cur].title}</p>
        </div>
        <div className="map-stats">
          <div className="stat">
            <span className="eyebrow">Progreso</span>
            <b>{prog.done}/{prog.total} temas</b>
            <Bar value={prog.ratio} />
          </div>
          <div className="stat">
            <span className="eyebrow">Llegás en</span>
            <b>{e.label}</b>
            <label className="pace">
              <Icon name="clock" size={14} />
              <select value={s.minutesPerDay} onChange={(ev) => setS({ ...s, minutesPerDay: Number(ev.target.value) })} aria-label="Minutos por día">
                {[10, 20, 30, 60].map((m) => <option key={m} value={m}>{m} min por día</option>)}
              </select>
            </label>
          </div>
        </div>
      </Card>

      <div className="map-body">
        <Card className="graph-card">
          <div className="map-toolbar">
            <div className="seg seg-sm" role="tablist" aria-label="Vista del mapa">
              <button type="button" role="tab" aria-selected={mode === 'camino'} className={mode === 'camino' ? 'on' : ''} onClick={() => setMode('camino')}><Icon name="map" size={14} />Camino</button>
              <button type="button" role="tab" aria-selected={mode === 'grafo'} className={mode === 'grafo' ? 'on' : ''} onClick={() => setMode('grafo')}><Icon name="branch" size={14} />Grafo completo</button>
            </div>
            <div className="legend">
              {(['mastered', 'progress', 'available', 'locked', 'scheduled', 'rusty'] as NodeStatus[]).map((k) => (
                <span key={k} className={`legend-item st-${k}`}><Icon name={STATUS[k].icon} size={13} />{STATUS[k].label}</span>
              ))}
              <span className="legend-item opt"><Icon name="branch" size={13} />Rama opcional</span>
              <span className="legend-item"><IaBadge />Sumado por interés</span>
            </div>
          </div>
          {mode === 'camino'
            ? <RoadPath s={s} selected={selected} onSelect={onSelect} flash={flash} recId={rec?.node.id ?? null} />
            : <GraphView s={s} selected={selected} onSelect={onSelect} flash={flash} recId={rec?.node.id ?? null} />}
        </Card>

        <NodePanel s={s} node={focus ?? rec?.node ?? null} isRec={!focus} recReason={rec?.reason} onPractice={onPractice} onSelect={onSelect} />
      </div>

      <ChangesFeed s={s} onSelect={onSelect} />
    </div>
  );
}

/* ── tarjeta de tema (compartida por las dos vistas) ───────── */

interface CardProps { s: AppState; n: GraphNode; className: string; style: CSSProperties; onClick: () => void }

function NodeCard({ s, n, className, style, onClick }: CardProps) {
  const st = status(s, n);
  return (
    <button type="button" className={`gnode st-${st} ${n.kind === 'optional' ? 'opt' : ''} ${className}`} style={style} onClick={onClick} aria-label={`${n.title}: ${STATUS[st].label}`}>
      <span className="gnode-icon"><Icon name={STATUS[st].icon} size={15} /></span>
      <span className="gnode-body">
        <span className="gnode-title">{n.title}</span>
        <span className="gnode-meta">
          {n.added ? <IaBadge /> : null}
          {s.review[n.id] ? <span className="mini-tag">Repaso</span> : null}
          {n.kind === 'optional' && !n.added ? <span className="mini-tag cyan">Opcional</span> : null}
          {!n.added && !s.review[n.id] && n.kind === 'core' ? <span>{STATUS[st].label}</span> : null}
        </span>
        <Bar value={mastery(s, n.id) / MASTERED} kind={st === 'locked' || st === 'scheduled' ? 'muted' : 'xp'} />
      </span>
    </button>
  );
}

interface ViewProps { s: AppState; selected: string | null; onSelect: (id: string | null) => void; flash: string | null; recId: string | null }

/* ── vista «Camino»: vertical, por hitos ────────────────────── */

const ROW = 118;
const CARD_H = 88;
const PAD = 26;

function useCompact() {
  const query = '(max-width: 640px)';
  const [compact, setCompact] = useState(() => window.matchMedia(query).matches);
  useEffect(() => {
    const m = window.matchMedia(query);
    const on = () => setCompact(m.matches);
    m.addEventListener('change', on);
    return () => m.removeEventListener('change', on);
  }, []);
  return compact;
}

interface Placed { node: GraphNode; top: number; x: number; w: number; lane: 'main' | 'branch' }
interface Edge { key: string; d: string; cls: string }

/** Ubica los temas de un hito en filas: los que dependen de otro del mismo hito bajan una fila
 *  (así se ve la bifurcación) y las ramas opcionales salen hacia un carril lateral. */
function layoutStage(s: AppState, stage: number, compact: boolean) {
  const center = compact ? 50 : 34;
  const nodes = allNodes(s).filter((n) => n.stage === stage);
  const core = nodes.filter((n) => n.kind === 'core');
  const branches = nodes.filter((n) => n.kind === 'optional');
  const coreIds = new Set(core.map((n) => n.id));
  const depth = new Map<string, number>();
  const depthOf = (n: GraphNode): number => {
    const known = depth.get(n.id);
    if (known !== undefined) return known;
    const parents = n.prereqs.filter((p) => coreIds.has(p));
    const d = parents.length ? 1 + Math.max(...parents.map((p) => depthOf(core.find((c) => c.id === p)!))) : 0;
    depth.set(n.id, d);
    return d;
  };
  core.forEach(depthOf);
  const mainRows: GraphNode[][] = [];
  core.forEach((n) => { const d = depth.get(n.id)!; (mainRows[d] ??= []).push(n); });
  const parentRow = (b: GraphNode) => {
    const parents = b.prereqs.filter((p) => coreIds.has(p));
    return parents.length ? Math.max(...parents.map((p) => depth.get(p)!)) : -1;
  };

  const xs = (k: number) => compact
    ? (k === 1 ? [50] : k === 2 ? [26, 74] : [17, 50, 83])
    : (k === 1 ? [34] : k === 2 ? [17.5, 50.5] : [11.5, 34, 56.5]);
  const widthFor = (k: number) => compact ? (k === 1 ? 64 : k === 2 ? 46 : 31) : (k >= 3 ? 21 : 30);

  const placed: Placed[] = [];
  mainRows.forEach((row, r) => {
    const pos = xs(row.length);
    row.forEach((n, i) => placed.push({ node: n, top: PAD + r * ROW, x: pos[i], w: widthFor(row.length), lane: 'main' }));
  });
  const mainBottom = PAD + Math.max(0, mainRows.length - 1) * ROW + CARD_H;
  const MERGE = 34;
  if (compact) {
    // En pantallas chicas las ramas van al final del hito, corridas a la derecha: el camino
    // principal se junta antes y baja por el centro sin cruzarlas.
    branches.forEach((b, i) => placed.push({ node: b, top: mainBottom + MERGE + i * ROW, x: 76, w: 44, lane: 'branch' }));
  } else {
    const used = new Set<number>();
    branches.forEach((b) => {
      let y = parentRow(b) + 1;
      while (used.has(y)) y += 1;
      used.add(y);
      placed.push({ node: b, top: PAD + y * ROW, x: 85, w: 28, lane: 'branch' });
    });
  }

  const height = Math.max(...placed.map((p) => p.top + CARD_H), mainBottom) + PAD;
  const top = (p: Placed) => p.top;
  const at = new Map(placed.map((p) => [p.node.id, p]));
  const curve = (x1: number, y1: number, x2: number, y2: number) => {
    const dy = (y2 - y1) / 2;
    return `M${x1},${y1} C${x1},${y1 + dy} ${x2},${y2 - dy} ${x2},${y2}`;
  };
  const side = (x1: number, y1: number, x2: number, y2: number) => `M${x1},${y1} C${x1 + 7},${y1} ${x2 - 7},${y2} ${x2},${y2}`;
  const done = (id: string) => mastery(s, id) >= MASTERED;

  const edges: Edge[] = [];
  placed.forEach((p) => {
    const parents = p.node.prereqs.filter((id) => coreIds.has(id) && at.has(id));
    const open = isOpen(status(s, p.node)) || done(p.node.id);
    if (p.lane === 'main') {
      if (!parents.length) edges.push({ key: `in-${p.node.id}`, d: curve(center, 0, p.x, top(p)), cls: open ? 'done' : '' });
      parents.forEach((id) => {
        const q = at.get(id)!;
        edges.push({ key: `${id}-${p.node.id}`, d: curve(q.x, top(q) + CARD_H, p.x, top(p)), cls: done(id) ? 'done' : '', });
      });
      const hasChild = core.some((c) => c.prereqs.includes(p.node.id));
      if (!hasChild) {
        const yb = top(p) + CARD_H;
        const d = compact && branches.length
          ? `M${p.x},${yb} C${p.x},${yb + 18} ${center},${mainBottom + 8} ${center},${mainBottom + MERGE - 6} L${center},${height}`
          : curve(p.x, yb, center, height);
        edges.push({ key: `out-${p.node.id}`, d, cls: done(p.node.id) ? 'done' : '' });
      }
    } else {
      const parent = parents.map((id) => at.get(id)!).sort((a, b) => b.top - a.top)[0];
      const midB = top(p) + CARD_H / 2;
      let d: string;
      if (compact) {
        // Por el margen derecho, para no cruzar las tarjetas del camino principal.
        const x1 = parent ? parent.x + parent.w / 2 : center;
        const y1 = parent ? top(parent) + CARD_H / 2 : 0;
        d = `M${x1},${y1} C99.5,${y1} 99.5,${midB} ${p.x + p.w / 2},${midB}`;
      } else if (!parent) d = side(center, 0, p.x - p.w / 2, midB);
      else d = side(parent.x + parent.w / 2, top(parent) + CARD_H / 2, p.x - p.w / 2, midB);
      edges.push({ key: `br-${p.node.id}`, d, cls: `opt ${parent && done(parent.node.id) ? 'done' : ''}` });
    }
  });
  return { placed, edges, height, center, hasBranches: branches.length > 0 };
}

function RoadPath({ s, selected, onSelect, flash, recId }: ViewProps) {
  const compact = useCompact();
  const cur = currentStage(s);
  const chain = selected ? ancestors(s, selected) : new Set<string>();
  const center = compact ? 50 : 34;

  return (
    <div className={`road ${compact ? 'compact' : ''}`} style={{ '--road-c': `${center}%` } as CSSProperties}>
      {STAGES.map((st, i) => {
        const lay = layoutStage(s, i, compact);
        const core = lay.placed.filter((p) => p.lane === 'main');
        const doneCount = core.filter((p) => mastery(s, p.node.id) >= MASTERED).length;
        const state = core.length && doneCount === core.length ? 'done' : i === cur ? 'current' : i < cur ? 'done' : 'locked';
        return (
          <section key={st.title} className={`road-section ${state}`} aria-label={`Hito ${i + 1}: ${st.title}`}>
            <div className={`road-head ${i === 0 ? 'first' : ''} ${i <= cur ? 'reached' : ''}`}>
              <div className={`road-pill ${state}`}>
                <span className="road-num">{state === 'done' ? <Icon name="check" size={16} /> : state === 'locked' ? <Icon name="lock" size={14} /> : i + 1}</span>
                <span className="road-pill-text">
                  <span className="road-pill-top"><span className="eyebrow">Hito {i + 1}</span>{state === 'current' ? <span className="here">Estás acá</span> : null}</span>
                  <b>{st.title}</b>
                  <span className="muted small">{doneCount} de {core.length} temas · al terminar: {st.evidence.charAt(0).toLowerCase() + st.evidence.slice(1)}</span>
                </span>
              </div>
            </div>
            <div className="road-canvas" style={{ height: lay.height }}>
              {lay.hasBranches && !compact ? <span className="lane-label"><Icon name="branch" size={12} />Ramas opcionales</span> : null}
              <svg viewBox={`0 0 100 ${lay.height}`} preserveAspectRatio="none" aria-hidden="true">
                {lay.edges.map((e) => {
                  const [a, b] = e.key.split(/-(?=[a-z]-)/);
                  const lit = selected && (b === selected || (chain.has(a) && (chain.has(b) || b === selected)));
                  return <path key={e.key} d={e.d} className={`vedge ${e.cls} ${lit ? 'lit' : ''}`} />;
                })}
              </svg>
              {lay.placed.map((p) => (
                <NodeCard
                  key={p.node.id}
                  s={s}
                  n={p.node}
                  className={`vnode ${selected === p.node.id ? 'sel' : ''} ${chain.has(p.node.id) ? 'chain' : ''} ${flash === p.node.id ? 'flash' : ''} ${recId === p.node.id ? 'rec' : ''}`}
                  style={{ left: `${p.x}%`, top: p.top, width: `${p.w}%`, height: CARD_H }}
                  onClick={() => onSelect(selected === p.node.id ? null : p.node.id)}
                />
              ))}
            </div>
          </section>
        );
      })}
      <div className="road-head reached-end">
        <div className="road-pill goal">
          <span className="road-num"><Icon name="target" size={16} /></span>
          <span className="road-pill-text"><span className="eyebrow">Meta</span><b>{s.goal || 'IA aplicada'}</b></span>
        </div>
      </div>
    </div>
  );
}

/* ── vista «Grafo completo»: horizontal, con todos los prerrequisitos ── */

const COL_W = 244;
const NODE_W = 204;
const NODE_H = 86;
const ROW_H = 102;
const TOP = 64;
const GPAD = 24;

function edgePath(a: { x: number; y: number }, b: { x: number; y: number }) {
  const x1 = a.x + NODE_W;
  const y1 = a.y + NODE_H / 2;
  const y2 = b.y + NODE_H / 2;
  if (b.x > a.x) {
    const dx = (b.x - x1) / 2;
    return `M${x1},${y1} C${x1 + dx},${y1} ${b.x - dx},${y2} ${b.x},${y2}`;
  }
  const lx = a.x - 20;
  return `M${a.x},${y1} C${lx},${y1} ${lx},${y2} ${b.x},${y2}`;
}

function GraphView({ s, selected, onSelect, flash, recId }: ViewProps) {
  const nodes = allNodes(s);
  const cur = currentStage(s);
  const byStage = STAGES.map((_, i) => nodes.filter((n) => n.stage === i).sort((a, b) => (a.kind === b.kind ? 0 : a.kind === 'core' ? -1 : 1)));
  const pos: Record<string, { x: number; y: number }> = {};
  byStage.forEach((list, i) => list.forEach((n, j) => { pos[n.id] = { x: GPAD + i * COL_W, y: TOP + j * ROW_H }; }));
  const rows = Math.max(...byStage.map((l) => l.length));
  const width = GPAD * 2 + (STAGES.length - 1) * COL_W + NODE_W;
  const height = TOP + rows * ROW_H + 12;
  const chain = selected ? ancestors(s, selected) : new Set<string>();

  return (
    <div className="graph-scroll">
      <div className="graph" style={{ width, height }}>
        {STAGES.map((st, i) => (
          <div key={st.title} className={`stage-col ${i === cur ? 'cur' : ''}`} style={{ left: GPAD + i * COL_W - 12, width: NODE_W + 24, height: height - 8 }}>
            <span className="stage-num">Hito {i + 1}</span>
            <span className="stage-title">{st.title}</span>
          </div>
        ))}
        <svg className="edges" width={width} height={height} aria-hidden="true">
          {nodes.flatMap((n) => n.prereqs.filter((p) => pos[p]).map((p) => {
            const lit = selected === n.id || (chain.has(p) && (chain.has(n.id) || n.id === selected));
            return <path key={`${p}-${n.id}`} d={edgePath(pos[p], pos[n.id])} className={`edge ${mastery(s, p) >= 0.7 ? 'done' : ''} ${lit ? 'lit' : ''} ${n.kind === 'optional' ? 'opt' : ''}`} />;
          }))}
        </svg>
        {nodes.map((n) => (
          <NodeCard
            key={n.id}
            s={s}
            n={n}
            className={`${selected === n.id ? 'sel' : ''} ${flash === n.id ? 'flash' : ''} ${selected && selected !== n.id && !chain.has(n.id) ? 'dim' : ''} ${recId === n.id ? 'rec' : ''}`}
            style={{ left: pos[n.id].x, top: pos[n.id].y, width: NODE_W, height: NODE_H }}
            onClick={() => onSelect(selected === n.id ? null : n.id)}
          />
        ))}
      </div>
    </div>
  );
}

/* ── panel del tema seleccionado ───────────────────────────── */

function NodePanel({ s, node, isRec, recReason, onPractice, onSelect }: { s: AppState; node: GraphNode | null; isRec: boolean; recReason?: string; onPractice: (id: string) => void; onSelect: (id: string | null) => void }) {
  if (!node) return <Card className="node-panel"><p className="muted">Terminaste la ruta. Sumá un tema desde «¿Qué te interesa?».</p></Card>;
  const st = status(s, node);
  const missing = missingPrereqs(s, node);
  const unlocks = dependents(s, node.id);
  return (
    <Card className="node-panel">
      <span className="eyebrow">{isRec ? 'Te recomiendo seguir con' : `Hito ${node.stage + 1} · ${STAGES[node.stage].title}`}</span>
      <h2>{node.title}</h2>
      <div className="row wrap gap-s">
        <span className={`status-pill st-${st}`}><Icon name={STATUS[st].icon} size={13} />{STATUS[st].label}</span>
        {node.kind === 'optional' ? <span className="mini-tag cyan">Rama opcional</span> : null}
        {node.added ? <IaBadge label="Sumado por interés" /> : null}
      </div>
      {isRec && recReason ? <p className="ia-note"><Icon name="sparkles" size={14} />{recReason}</p> : null}
      <p>{node.summary}</p>
      <div className="mastery-line"><span>Dominio</span><b>{pct(mastery(s, node.id))}</b></div>
      <Bar value={mastery(s, node.id) / MASTERED} />
      {s.review[node.id] ? <p className="ia-note"><Icon name="refresh" size={14} />Repaso sugerido: {s.review[node.id]}.</p> : null}
      {node.added ? <p className="muted small">Lo sumaste el {new Date(node.added.ts).toLocaleDateString('es-AR')}. {node.added.reason}</p> : null}
      {node.prereqs.length ? (
        <div className="list-block">
          <span className="eyebrow">Necesita</span>
          {node.prereqs.map((p) => (
            <button key={p} type="button" className="link-row" onClick={() => onSelect(p)}>
              <Icon name={mastery(s, p) >= 0.7 ? 'check' : 'lock'} size={14} />{titleOf(p)}<span className="muted">{pct(mastery(s, p))}</span>
            </button>
          ))}
        </div>
      ) : null}
      {unlocks.length ? (
        <div className="list-block">
          <span className="eyebrow">Desbloquea</span>
          {unlocks.map((d) => <button key={d.id} type="button" className="link-row" onClick={() => onSelect(d.id)}><Icon name="arrow" size={14} />{d.title}</button>)}
        </div>
      ) : null}
      {isOpen(st) || st === 'mastered' ? (
        <button type="button" className="btn btn-ia" onClick={() => onPractice(node.id)}><Icon name="play" size={15} />{st === 'mastered' ? 'Repasar' : 'Practicar'} · 3 preguntas</button>
      ) : (
        <p className="muted small">Se abre cuando domines {missing.map((m) => `«${titleOf(m)}»`).join(' y ')}.</p>
      )}
    </Card>
  );
}

/* ── historial de ajustes ──────────────────────────────────── */

function ChangesFeed({ s, onSelect }: { s: AppState; onSelect: (id: string | null) => void }) {
  const items = [...s.changes].reverse().slice(0, 8);
  if (!items.length) return null;
  const icon: Record<string, IconName> = { ajuste: 'sparkles', nodo: 'branch', refuerzo: 'refresh', oxido: 'warn', logro: 'check', info: 'sparkles' };
  return (
    <Card className="feed">
      <div className="row between"><h3><Icon name="sparkles" size={16} /> Lo que fue ajustando la IA</h3><span className="muted small">{s.changes.length} cambios en total</span></div>
      <ul>
        {items.map((c) => (
          <li key={c.id} className={`feed-item k-${c.kind}`}>
            <span className="feed-icon"><Icon name={icon[c.kind]} size={14} /></span>
            <span className="feed-text">{c.text}</span>
            {c.node ? <button type="button" className="btn btn-ghost btn-xs" onClick={() => onSelect(c.node!)}>Ver</button> : null}
            <span className="muted small">{new Date(c.ts).toLocaleDateString('es-AR', { day: 'numeric', month: 'short' })}</span>
          </li>
        ))}
      </ul>
    </Card>
  );
}
