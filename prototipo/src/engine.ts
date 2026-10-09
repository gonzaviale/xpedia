// Motor del prototipo. Todo lo que en el producto haría la IA acá son reglas sobre el grafo de
// la ruta y el historial de la persona: se puede reemplazar pieza por pieza por llamadas a un modelo.

import { DIAGNOSIS, NEWS, NODES, STAGES, TOPICS, type NewsItem, type QType, type Question, type SkillNode } from './data';

export const DAY = 86_400_000;
export const MASTERED = 0.8;
export const PREREQ_OK = 0.7;

export type Confidence = 'sabia' | 'dude' | 'adivine';
export type NodeStatus = 'mastered' | 'rusty' | 'progress' | 'available' | 'locked' | 'scheduled';
export type Decision = 'ya-esta' | 'ahora' | 'mas-adelante' | 'rama' | 'guardado';
export type CodeLevel = 'nada' | 'algo' | 'bastante';
export type ChangeKind = 'ajuste' | 'nodo' | 'refuerzo' | 'oxido' | 'logro' | 'info';

export interface Attempt { qid: string; node: string; type: QType; correct: boolean; conf: Confidence; ms: number; ts: number }
export interface SessionLog { node: string; ts: number; correct: number; total: number; xp: number }
export interface AddedNode { id: string; stage: number; kind: 'core' | 'optional'; decision: Decision; ts: number; reason: string }
export interface ParkedTopic { id: string; ts: number; stage: number; reason: string }
export interface Change { id: string; ts: number; kind: ChangeKind; text: string; node?: string }

export interface AppState {
  v: 1;
  onboarded: boolean;
  goal: string;
  codeLevel: CodeLevel;
  minutesPerDay: number;
  mastery: Record<string, number>;
  rusty: Record<string, boolean>;
  review: Record<string, string>;
  added: AddedNode[];
  parked: ParkedTopic[];
  dismissedNews: string[];
  attempts: Attempt[];
  sessions: SessionLog[];
  xp: number;
  streak: number;
  lastDay: string | null;
  changes: Change[];
  decided: Record<string, 'accepted' | 'rejected'>;
  extraPractice: boolean;
  dayOffset: number;
  simWeeks: number;
}

export interface GraphNode extends SkillNode { added?: AddedNode }

export function initialState(): AppState {
  return {
    v: 1, onboarded: false, goal: '', codeLevel: 'nada', minutesPerDay: 20,
    mastery: {}, rusty: {}, review: {}, added: [], parked: [], dismissedNews: [],
    attempts: [], sessions: [], xp: 0, streak: 0, lastDay: null, changes: [], decided: {},
    extraPractice: false, dayOffset: 0, simWeeks: 0,
  };
}

/* ── utilidades ─────────────────────────────────────────────── */

const clamp01 = (x: number) => Math.max(0, Math.min(1, x));
export const appNow = (s: AppState) => Date.now() + s.dayOffset * DAY;
const dayKey = (ts: number) => new Date(ts).toISOString().slice(0, 10);
export const mastery = (s: AppState, id: string) => s.mastery[id] ?? 0;
export const pct = (x: number) => `${Math.round(x * 100)} %`;

function change(ts: number, kind: ChangeKind, text: string, node?: string): Change {
  return { id: `${ts}-${Math.random().toString(36).slice(2, 8)}`, ts, kind, text, node };
}

export function titleOf(id: string): string {
  return (NODES.find((n) => n.id === id) ?? TOPICS.find((t) => t.id === id))?.title ?? id;
}

function joinTitles(ids: string[]): string {
  const t = ids.map((id) => `«${titleOf(id)}»`);
  return t.length <= 1 ? t.join('') : `${t.slice(0, -1).join(', ')} y ${t[t.length - 1]}`;
}

/* ── grafo ──────────────────────────────────────────────────── */

export function allNodes(s: AppState): GraphNode[] {
  const extra: GraphNode[] = s.added.flatMap((a) => {
    const t = TOPICS.find((x) => x.id === a.id);
    if (!t) return [];
    return [{ id: t.id, title: t.title, stage: a.stage, kind: a.kind, prereqs: t.prereqs, minutes: t.minutes, summary: t.summary, keywords: t.keywords, questions: t.questions, added: a }];
  });
  return [...NODES, ...extra];
}

export const findNode = (s: AppState, id: string) => allNodes(s).find((n) => n.id === id);

export function missingPrereqs(s: AppState, n: { prereqs: string[] }): string[] {
  return n.prereqs.filter((p) => mastery(s, p) < PREREQ_OK);
}

export function status(s: AppState, n: GraphNode): NodeStatus {
  const m = mastery(s, n.id);
  if (m >= MASTERED) return s.rusty[n.id] ? 'rusty' : 'mastered';
  if (missingPrereqs(s, n).length) return n.added ? 'scheduled' : 'locked';
  return m > 0 ? 'progress' : 'available';
}

export const isOpen = (st: NodeStatus) => st === 'available' || st === 'progress' || st === 'rusty';

export function ancestors(s: AppState, id: string): Set<string> {
  const out = new Set<string>();
  const walk = (x: string) => {
    const n = findNode(s, x);
    n?.prereqs.forEach((p) => { if (!out.has(p)) { out.add(p); walk(p); } });
  };
  walk(id);
  return out;
}

export function dependents(s: AppState, id: string): GraphNode[] {
  return allNodes(s).filter((n) => n.prereqs.includes(id));
}

export function currentStage(s: AppState): number {
  const pending = allNodes(s).filter((n) => n.kind === 'core' && mastery(s, n.id) < MASTERED);
  return pending.length ? Math.min(...pending.map((n) => n.stage)) : STAGES.length - 1;
}

export function progress(s: AppState) {
  const core = allNodes(s).filter((n) => n.kind === 'core');
  const done = core.filter((n) => mastery(s, n.id) >= MASTERED).length;
  return { done, total: core.length, ratio: core.length ? done / core.length : 0 };
}

export function eta(s: AppState) {
  const left = allNodes(s)
    .filter((n) => n.kind === 'core')
    .reduce((acc, n) => acc + n.minutes * Math.max(0, 1 - mastery(s, n.id) / MASTERED), 0);
  const days = Math.ceil(left / Math.max(5, s.minutesPerDay));
  return { minutesLeft: Math.round(left), days, label: durationLabel(days) };
}

export function durationLabel(days: number): string {
  if (days <= 0) return 'ya llegaste';
  if (days < 14) return `${days} días`;
  const weeks = Math.round(days / 7);
  if (weeks < 9) return `${weeks} semanas`;
  return `${Math.round(days / 30)} meses`;
}

/* ── recomendación ──────────────────────────────────────────── */

export interface Recommendation { node: GraphNode; reason: string }

export function recommendations(s: AppState): Recommendation[] {
  const cur = currentStage(s);
  return allNodes(s)
    .map((n) => ({ n, st: status(s, n) }))
    .filter((x) => isOpen(x.st))
    .map(({ n, st }) => {
      let score = n.stage * 10 + (n.kind === 'optional' ? 6 : 0) - mastery(s, n.id) * 3;
      let reason = n.stage === cur ? 'Es lo que sigue en tu hito actual.' : 'Ya tenés la base para verlo.';
      if (st === 'rusty') { score -= 100; reason = 'Se está oxidando: con un repaso corto alcanza.'; }
      else if (s.review[n.id]) { score -= 60; reason = `Te sugerí reforzarlo porque ${s.review[n.id]}.`; }
      else if (st === 'progress') { score -= 2; reason = 'Ya lo empezaste: conviene terminarlo antes de abrir otro frente.'; }
      if (n.kind === 'optional') reason = `Rama opcional. ${reason}`;
      return { node: n, reason, score };
    })
    .sort((a, b) => a.score - b.score)
    .map(({ node, reason }) => ({ node, reason }));
}

export const recommended = (s: AppState): Recommendation | null => recommendations(s)[0] ?? null;

/* ── práctica ───────────────────────────────────────────────── */

export function pickQuestions(s: AppState, nodeId: string, n = 3): Question[] {
  const node = findNode(s, nodeId);
  if (!node) return [];
  const stats = (qid: string) => {
    const list = s.attempts.filter((a) => a.qid === qid);
    return { good: list.filter((a) => a.correct && a.conf === 'sabia').length, last: list.length ? list[list.length - 1].ts : 0 };
  };
  const pick = [...node.questions]
    .sort((a, b) => stats(a.id).good - stats(b.id).good || stats(a.id).last - stats(b.id).last)
    .slice(0, n);
  if (s.extraPractice) {
    // Caso práctico extra: una pregunta de aplicación de un prerrequisito, para transferir lo aprendido.
    const extra = node.prereqs
      .flatMap((p) => findNode(s, p)?.questions ?? [])
      .find((q) => q.type === 'aplicacion' && !pick.includes(q));
    if (extra) pick.push(extra);
  }
  return pick;
}

export function nodeOfQuestion(s: AppState, qid: string): string | undefined {
  return allNodes(s).find((n) => n.questions.some((q) => q.id === qid))?.id;
}

export interface AnswerResult { state: AppState; xp: number; masteredNow: boolean; unlocked: string[] }

export function answer(s: AppState, q: Question, nodeId: string, correct: boolean, conf: Confidence, ms: number, ts = appNow(s)): AnswerResult {
  const target = nodeOfQuestion(s, q.id) ?? nodeId;
  const before = mastery(s, target);
  const delta = correct ? (conf === 'sabia' ? 0.22 : conf === 'dude' ? 0.12 : 0.05) : -0.08;
  const after = clamp01(before + delta);
  let xp = correct ? (conf === 'sabia' ? 10 : conf === 'dude' ? 6 : 3) : 1;
  const lockedBefore = new Set(allNodes(s).filter((n) => !isOpen(status(s, n)) && status(s, n) !== 'mastered').map((n) => n.id));
  const next: AppState = {
    ...s,
    mastery: { ...s.mastery, [target]: after },
    attempts: [...s.attempts, { qid: q.id, node: target, type: q.type, correct, conf, ms, ts }],
  };
  if (correct && conf === 'sabia' && s.rusty[target]) next.rusty = { ...s.rusty, [target]: false };
  const masteredNow = before < MASTERED && after >= MASTERED;
  if (masteredNow) {
    xp += 50;
    next.changes = [...next.changes, change(ts, 'logro', `Dominaste «${titleOf(target)}».`, target)];
  }
  next.xp = s.xp + xp;
  const unlocked = allNodes(next).filter((n) => lockedBefore.has(n.id) && isOpen(status(next, n))).map((n) => n.id);
  return { state: next, xp, masteredNow, unlocked };
}

export interface SessionSummary {
  node: string; correct: number; total: number; xp: number; before: number; after: number;
  unlocked: string[]; adaptations: Change[];
}

export function finishSession(s: AppState, nodeId: string, atts: Attempt[], before: number, unlocked: string[], xp: number, ts = appNow(s)): { state: AppState; summary: SessionSummary } {
  const node = findNode(s, nodeId);
  const total = atts.length;
  const correct = atts.filter((a) => a.correct).length;
  const wrong = total - correct;
  const title = node?.title ?? titleOf(nodeId);
  const review = { ...s.review };
  const adaptations: Change[] = [];

  if (wrong >= 2 && node) {
    const weakest = node.prereqs.filter((p) => findNode(s, p)).sort((a, b) => mastery(s, a) - mastery(s, b))[0];
    if (weakest && mastery(s, weakest) < 0.95) {
      review[weakest] = `fallaste ${wrong} de ${total} en «${title}»`;
      adaptations.push(change(ts, 'refuerzo', `Agregué un repaso de «${titleOf(weakest)}» antes de seguir con «${title}»: fallaste ${wrong} de ${total}.`, weakest));
    } else {
      adaptations.push(change(ts, 'ajuste', `«${title}» vuelve mañana con otras preguntas: fallaste ${wrong} de ${total}.`, nodeId));
    }
  }
  const guessed = atts.filter((a) => a.correct && a.conf === 'adivine').length;
  if (guessed === 1) adaptations.push(change(ts, 'ajuste', 'Un acierto fue adivinado: esa pregunta vuelve en el repaso, porque todavía no cuenta como sabida.', nodeId));
  if (guessed > 1) adaptations.push(change(ts, 'ajuste', `${guessed} aciertos fueron adivinados: esas preguntas vuelven en el repaso, porque todavía no cuentan como sabidas.`, nodeId));
  if (s.review[nodeId] && correct >= Math.ceil(total * 0.66)) {
    delete review[nodeId];
    adaptations.push(change(ts, 'ajuste', `Repaso de «${title}» cumplido: seguimos con la ruta.`, nodeId));
  }
  const avgMs = total ? atts.reduce((a, x) => a + x.ms, 0) / total : 0;
  if (avgMs > 45_000) adaptations.push(change(ts, 'ajuste', `Tardaste ${Math.round(avgMs / 1000)} s por pregunta en «${title}». La próxima vez arrancamos con un ejemplo resuelto.`, nodeId));
  unlocked.forEach((u) => adaptations.push(change(ts, 'nodo', `Se desbloqueó «${titleOf(u)}».`, u)));

  const today = dayKey(ts);
  const streak = s.lastDay === today ? s.streak : s.lastDay === dayKey(ts - DAY) ? s.streak + 1 : 1;
  const state: AppState = {
    ...s, review, streak, lastDay: today,
    sessions: [...s.sessions, { node: nodeId, ts, correct, total, xp }],
    changes: [...s.changes, ...adaptations],
  };
  return { state, summary: { node: nodeId, correct, total, xp, before, after: mastery(state, nodeId), unlocked, adaptations } };
}

/* ── ineficiencias y propuestas ─────────────────────────────── */

export interface Insight { id: string; title: string; detail: string; tip: string; proposal?: string }

const TYPE_LABEL: Record<QType, string> = { concepto: 'concepto', aplicacion: 'aplicación', detalle: 'detalle' };

export function insights(s: AppState): Insight[] {
  const recent = s.attempts.slice(-40);
  const out: Insight[] = [];

  const corrects = recent.filter((a) => a.correct);
  const guessed = corrects.filter((a) => a.conf === 'adivine');
  if (corrects.length >= 4 && guessed.length / corrects.length >= 0.25) {
    out.push({ id: 'adivinas', title: 'Adivinás bastante', detail: `${guessed.length} de tus ${corrects.length} aciertos recientes fueron adivinados.`, tip: 'Marcar «Lo adiviné» no te resta puntos: hace que el repaso vaya justo a lo que todavía no sabés.' });
  }

  const worst = (['concepto', 'aplicacion', 'detalle'] as QType[])
    .map((t) => { const list = recent.filter((a) => a.type === t); return { t, n: list.length, err: list.length ? list.filter((a) => !a.correct).length / list.length : 0 }; })
    .filter((x) => x.n >= 3 && x.err >= 0.45)
    .sort((a, b) => b.err - a.err)[0];
  if (worst) {
    const titles: Record<QType, string> = { aplicacion: 'La teoría sale; aplicarla cuesta', detalle: 'Se te escapan los detalles', concepto: 'Hay conceptos flojos' };
    out.push({
      id: `tipo-${worst.t}`, title: titles[worst.t],
      detail: `Fallaste el ${Math.round(worst.err * 100)} % de las preguntas de ${TYPE_LABEL[worst.t]} (${worst.n} en total).`,
      tip: worst.t === 'aplicacion' ? 'Te propongo sumar un caso práctico en cada sesión.' : 'Te propongo repasar con ejemplos antes de seguir.',
      proposal: worst.t === 'aplicacion' && !s.extraPractice ? 'p-casos' : undefined,
    });
  }

  const byNode = new Map<string, Attempt[]>();
  recent.forEach((a) => byNode.set(a.node, [...(byNode.get(a.node) ?? []), a]));
  byNode.forEach((list, nodeId) => {
    const node = findNode(s, nodeId);
    if (!node) return;
    const last = list.slice(-4);
    const wrong = last.filter((a) => !a.correct).length;
    if (wrong >= 2 && mastery(s, nodeId) < MASTERED) {
      const weakest = node.prereqs.filter((p) => findNode(s, p)).sort((a, b) => mastery(s, a) - mastery(s, b))[0];
      out.push({
        id: `trabado-${nodeId}`, title: `Te trabaste en «${node.title}»`,
        detail: `Fallaste ${wrong} de las últimas ${last.length} preguntas.`,
        tip: weakest ? `Su base es «${titleOf(weakest)}» (${pct(mastery(s, weakest))}): reforzarla suele destrabarlo.` : 'Probá con la microlección antes de volver a practicar.',
        proposal: weakest && !s.review[weakest] ? `p-repaso-${weakest}-${nodeId}` : undefined,
      });
    }
    const avg = list.reduce((a, x) => a + x.ms, 0) / list.length;
    if (list.length >= 3 && avg > 45_000) {
      out.push({ id: `lento-${nodeId}`, title: `Vas lento en «${node.title}»`, detail: `Tardás unos ${Math.round(avg / 1000)} s por pregunta.`, tip: 'Suele ser señal de que falta un ejemplo concreto: lo sumo al principio de la próxima sesión.' });
    }
    const last3 = list.slice(-3);
    if (last3.length === 3 && last3.every((a) => a.correct && a.conf === 'sabia') && mastery(s, nodeId) >= MASTERED) {
      const next = dependents(s, nodeId).find((d) => d.kind === 'core' && status(s, d) === 'available');
      if (next) out.push({ id: `fuerte-${next.id}`, title: `Vas sobrado en «${node.title}»`, detail: 'Respondiste las últimas 3 bien y sabiéndolo.', tip: `Podés saltear «${next.title}» con una prueba corta.`, proposal: `p-salto-${next.id}` });
    }
  });

  allNodes(s).filter((n) => status(s, n) === 'rusty').forEach((n) => {
    out.push({ id: `oxido-${n.id}`, title: `«${n.title}» se está oxidando`, detail: 'Hace días que no lo practicás.', tip: 'Con un repaso de 5 minutos vuelve a estar firme.', proposal: !s.review[n.id] ? `p-oxido-${n.id}` : undefined });
  });
  return out;
}

export interface Proposal { id: string; title: string; detail: string }

export function proposals(s: AppState): Proposal[] {
  const out: Proposal[] = [];
  insights(s).forEach((i) => {
    if (!i.proposal || out.some((p) => p.id === i.proposal)) return;
    const id = i.proposal;
    if (id === 'p-casos') out.push({ id, title: 'Sumar un caso práctico extra en cada sesión', detail: 'Porque fallás más las preguntas de aplicación que las de teoría.' });
    else if (id.startsWith('p-repaso-')) { const [, , pre, ...rest] = id.split('-'); const preId = `${pre}-${rest[0]}`; const nodeId = rest.slice(1).join('-'); out.push({ id, title: `Repasar «${titleOf(preId)}» antes de seguir con «${titleOf(nodeId)}»`, detail: i.detail }); }
    else if (id.startsWith('p-salto-')) out.push({ id, title: `Saltear «${titleOf(id.slice(8))}» con una prueba corta`, detail: i.detail });
    else if (id.startsWith('p-oxido-')) out.push({ id, title: `Repaso de 5 minutos de «${titleOf(id.slice(8))}»`, detail: i.detail });
  });
  const e = eta(s);
  if (s.minutesPerDay < 30 && e.days > 60) {
    const faster = durationLabel(Math.ceil(e.minutesLeft / 30));
    out.push({ id: 'p-ritmo', title: 'Subir a 30 minutos por día', detail: `Llegarías en ${faster} en lugar de ${e.label}.` });
  }
  return out.filter((p) => !s.decided[p.id]);
}

export function decideProposal(s: AppState, id: string, accept: boolean): AppState {
  const ts = appNow(s);
  let next: AppState = { ...s, decided: { ...s.decided, [id]: accept ? 'accepted' : 'rejected' } };
  if (!accept) return next;
  if (id === 'p-casos') next = { ...next, extraPractice: true, changes: [...next.changes, change(ts, 'ajuste', 'Desde ahora, cada sesión suma un caso práctico de un tema que ya viste.')] };
  else if (id.startsWith('p-repaso-')) {
    const parts = id.split('-');
    const preId = `${parts[2]}-${parts[3]}`;
    next = { ...next, review: { ...next.review, [preId]: 'lo aceptaste en tu informe' }, changes: [...next.changes, change(ts, 'refuerzo', `Agregué un repaso de «${titleOf(preId)}».`, preId)] };
  } else if (id.startsWith('p-salto-')) {
    const nodeId = id.slice(8);
    next = { ...next, mastery: { ...next.mastery, [nodeId]: MASTERED }, changes: [...next.changes, change(ts, 'logro', `Salteaste «${titleOf(nodeId)}» con una prueba (en el prototipo, aceptar la da por aprobada).`, nodeId)] };
  } else if (id.startsWith('p-oxido-')) {
    const nodeId = id.slice(8);
    next = { ...next, review: { ...next.review, [nodeId]: 'se estaba oxidando' }, changes: [...next.changes, change(ts, 'refuerzo', `Sumé un repaso corto de «${titleOf(nodeId)}».`, nodeId)] };
  } else if (id === 'p-ritmo') {
    next = { ...next, minutesPerDay: 30, changes: [...next.changes, change(ts, 'ajuste', 'Subiste a 30 minutos por día.')] };
  }
  return next;
}

/* ── novedades ──────────────────────────────────────────────── */

export interface NewsView { item: NewsItem; bucket: 'now' | 'later' | 'noise'; reason: string }

export function newsView(s: AppState): NewsView[] {
  return NEWS.filter((n) => !s.dismissedNews.includes(n.id)).map((item): NewsView => {
    if (!item.ref) return { item, bucket: 'noise', reason: 'No tiene relación con tu ruta ni con lo que marcaste como interés.' };
    const g = findNode(s, item.ref);
    if (g) {
      const st = status(s, g);
      if (st === 'locked' || st === 'scheduled') return { item, bucket: 'later', reason: `Habla de «${g.title}», que llega en el hito ${g.stage + 1}. Te la muestro cuando domines ${joinTitles(missingPrereqs(s, g))}.` };
      if (st === 'mastered' || st === 'rusty') return { item, bucket: 'now', reason: `Actualiza algo que ya dominás: «${g.title}».` };
      return { item, bucket: 'now', reason: `Tiene que ver con «${g.title}», que ya podés practicar.` };
    }
    const t = TOPICS.find((x) => x.id === item.ref);
    if (!t) return { item, bucket: 'noise', reason: 'No tiene relación con tu ruta.' };
    if (s.parked.some((p) => p.id === t.id)) return { item, bucket: 'later', reason: `Guardaste «${t.title}» para más adelante.` };
    const miss = missingPrereqs(s, t);
    if (!miss.length) return { item, bucket: 'now', reason: `«${t.title}» no está en tu ruta, pero ya tenés la base para sumarlo.` };
    return { item, bucket: 'later', reason: `Es sobre «${t.title}». Para aprovecharla ${miss.length === 1 ? 'te falta' : 'te faltan'} ${joinTitles(miss)}.` };
  });
}

/* ── «Me interesa esto» ─────────────────────────────────────── */

export interface PrereqCheck { id: string; title: string; mastery: number; ok: boolean; stage: number }
export interface Analysis {
  ref: string; title: string; summary: string; decision: Decision; stage: number;
  headline: string; reasons: string[]; prereqs: PrereqCheck[]; mode?: 'extends' | 'branch';
}

export function analyzeInterest(s: AppState, ref: string): Analysis | null {
  const cur = currentStage(s);
  const last = STAGES.length - 1;
  const inGraph = findNode(s, ref);
  const checks = (ids: string[]): PrereqCheck[] => ids.map((id) => ({ id, title: titleOf(id), mastery: mastery(s, id), ok: mastery(s, id) >= PREREQ_OK, stage: findNode(s, id)?.stage ?? 0 }));

  if (inGraph) {
    const st = status(s, inGraph);
    const prereqs = checks(inGraph.prereqs);
    const headline = st === 'mastered' || st === 'rusty' ? 'Ya lo dominás'
      : isOpen(st) ? 'Ya está en tu ruta y lo podés practicar ahora'
        : `Ya está en tu ruta: llega en el hito ${inGraph.stage + 1}`;
    const reasons = isOpen(st) || st === 'mastered'
      ? [`«${inGraph.title}» es parte de tu ruta${inGraph.added ? ' (lo sumaste por interés)' : ''}.`]
      : [`Para verlo bien ${missingPrereqs(s, inGraph).length === 1 ? 'te falta' : 'te faltan'} ${joinTitles(missingPrereqs(s, inGraph))}.`,'No lo adelanto: sin esa base, la práctica te frustraría más de lo que te enseña.'];
    return { ref, title: inGraph.title, summary: inGraph.summary, decision: 'ya-esta', stage: inGraph.stage, headline, reasons, prereqs };
  }

  const t = TOPICS.find((x) => x.id === ref);
  if (!t) return null;
  const prereqs = checks(t.prereqs);
  const missing = prereqs.filter((p) => !p.ok);
  const okList = prereqs.filter((p) => p.ok);
  const stage = missing.length ? Math.min(last, Math.max(...missing.map((p) => p.stage)) + 1) : Math.min(last, cur);
  const reasons: string[] = [];
  if (okList.length) reasons.push(`Ya dominás ${joinTitles(okList.map((p) => p.id))}.`);
  if (missing.length) {
    const stages = [...new Set(missing.map((p) => p.stage + 1))].sort((a, b) => a - b);
    const where = stages.length === 1 ? `el hito ${stages[0]}` : `los hitos ${stages.join(' y ')}`;
    reasons.push(`${missing.length === 1 ? 'Te falta' : 'Te faltan'} ${joinTitles(missing.map((p) => p.id))}, que ${missing.length === 1 ? 'está' : 'están'} en ${where}.`);
  }

  let decision: Decision;
  let headline: string;
  if (t.mode === 'branch') {
    decision = 'rama';
    headline = missing.length ? `Va como rama opcional: se abre en el hito ${stage + 1}` : 'Va como rama opcional, y la podés empezar ya';
    reasons.push('Es otro camino, no un paso de tu ruta principal: la sumo al costado para que no la frene.');
  } else if (!missing.length) {
    decision = 'ahora';
    headline = 'Tenés la base: lo sumo a tu hito actual';
    reasons.push(`Entra en el hito ${stage + 1}, donde estás ahora.`);
  } else if (stage - cur <= 2) {
    decision = 'mas-adelante';
    headline = `Lo agendo para el hito ${stage + 1}`;
    reasons.push('Queda cerca: aparece solo cuando tengas esa base, sin que tengas que acordarte.');
  } else {
    decision = 'guardado';
    headline = 'Por ahora lo guardo y te aviso';
    reasons.push(`Está lejos de donde estás (hito ${cur + 1}). Sumarlo ya llenaría tu ruta de cosas que todavía no podés aprovechar.`);
  }
  if (s.parked.some((p) => p.id === t.id)) reasons.push('Ya lo tenías guardado.');
  return { ref, title: t.title, summary: t.summary, decision, stage, headline, reasons, prereqs, mode: t.mode };
}

export function applyInterest(s: AppState, a: Analysis): AppState {
  if (a.decision === 'ya-esta') return s;
  const ts = appNow(s);
  const reason = a.reasons[0] ?? '';
  if (a.decision === 'guardado') {
    if (s.parked.some((p) => p.id === a.ref)) return s;
    return {
      ...s,
      parked: [...s.parked, { id: a.ref, ts, stage: a.stage, reason }],
      changes: [...s.changes, change(ts, 'info', `Guardé «${a.title}»: te aviso cuando llegues al hito ${a.stage + 1}.`, a.ref)],
    };
  }
  const kind = a.decision === 'rama' ? 'optional' : 'core';
  const text = a.decision === 'rama' ? `Sumé «${a.title}» como rama opcional en el hito ${a.stage + 1}.`
    : a.decision === 'ahora' ? `Sumé «${a.title}» a tu hito actual.`
      : `Agendé «${a.title}» para el hito ${a.stage + 1}.`;
  return {
    ...s,
    added: [...s.added, { id: a.ref, stage: a.stage, kind, decision: a.decision, ts, reason }],
    parked: s.parked.filter((p) => p.id !== a.ref),
    changes: [...s.changes, change(ts, 'nodo', text, a.ref)],
  };
}

export function parkedReady(s: AppState): ParkedTopic[] {
  return s.parked.filter((p) => { const t = TOPICS.find((x) => x.id === p.id); return t && missingPrereqs(s, t).length === 0; });
}

const norm = (x: string) => x.toLowerCase().normalize('NFD').replace(/[̀-ͯ]/g, '');

export function searchRefs(text: string): { id: string; title: string }[] {
  const q = norm(text).trim();
  if (q.length < 2) return [];
  return [...NODES, ...TOPICS]
    .map((p) => {
      let score = 0;
      p.keywords.forEach((k) => {
        const kw = norm(k);
        if (q === kw) score += 6;
        else if (q.includes(kw)) score += 3;
        else if (q.length > 3 && kw.includes(q)) score += 1;
      });
      norm(p.title).split(/[^a-z0-9]+/).forEach((w) => { if (w.length > 3 && q.includes(w)) score += 1; });
      return { id: p.id, title: p.title, score };
    })
    .filter((x) => x.score > 0)
    .sort((a, b) => b.score - a.score)
    .slice(0, 4)
    .map(({ id, title }) => ({ id, title }));
}

/* ── diagnóstico ────────────────────────────────────────────── */

export interface DiagAnswer { node: string; correct: boolean; conf: Confidence }

export function diagnose(base: AppState, goal: string, codeLevel: CodeLevel, minutesPerDay: number, answers: DiagAnswer[]): AppState {
  const s: AppState = { ...base, goal, codeLevel, minutesPerDay, mastery: {} };
  const set = (id: string, v: number) => { s.mastery[id] = Math.max(s.mastery[id] ?? 0, v); };
  if (codeLevel === 'algo') { set('n-python', 0.85); set('n-json', 0.4); }
  if (codeLevel === 'bastante') ['n-python', 'n-json', 'n-http'].forEach((id) => set(id, 0.85));
  answers.forEach((a) => {
    if (a.correct && a.conf === 'sabia') { set(a.node, 0.85); ancestors(s, a.node).forEach((p) => set(p, 0.8)); }
    else if (a.correct && a.conf === 'dude') set(a.node, 0.4);
    else if (a.correct) set(a.node, 0.1);
  });
  const known = NODES.filter((n) => mastery(s, n.id) >= MASTERED);
  const ts = appNow(s);
  return {
    ...s,
    onboarded: true,
    changes: [change(ts, 'info', known.length
      ? `Armé tu ruta. Ya sabés ${known.length} ${known.length === 1 ? 'tema' : 'temas'} (${joinTitles(known.map((n) => n.id))}): no los vas a repetir.`
      : 'Armé tu ruta desde el principio: arrancás por «Qué es un LLM».')],
  };
}

export const diagnosisQuestions = () => DIAGNOSIS.map((d) => ({ node: d.node, question: NODES.find((n) => n.id === d.node)!.questions[d.q] }));

/* ── simulación de una semana (para mostrar la demo) ───────── */

function mulberry32(seed: number) {
  let a = seed;
  return () => {
    a |= 0; a = (a + 0x6d2b79f5) | 0;
    let t = Math.imul(a ^ (a >>> 15), 1 | a);
    t = (t + Math.imul(t ^ (t >>> 7), 61 | t)) ^ t;
    return ((t ^ (t >>> 14)) >>> 0) / 4294967296;
  };
}

export function simulateWeek(s0: AppState): AppState {
  const rnd = mulberry32(1000 + s0.simWeeks);
  let s = s0;
  for (let d = 1; d <= 7; d += 1) {
    const rec = recommended(s);
    if (!rec) break;
    const node = rec.node;
    const base = Date.now() + (s0.dayOffset + d) * DAY - 3 * 3_600_000;
    const before = mastery(s, node.id);
    const atts: Attempt[] = [];
    const unlocked: string[] = [];
    let xp = 0;
    pickQuestions(s, node.id).forEach((q, i) => {
      const p = (q.type === 'concepto' ? 0.88 : q.type === 'detalle' ? 0.74 : 0.4) - node.stage * 0.02;
      const correct = rnd() < p;
      const r = rnd();
      const conf: Confidence = correct ? (r < 0.55 ? 'sabia' : r < 0.78 ? 'dude' : 'adivine') : 'dude';
      const ms = Math.round(14_000 + rnd() * 22_000 + (q.type === 'aplicacion' ? 18_000 : 0));
      const res = answer(s, q, node.id, correct, conf, ms, base + i * 60_000);
      s = res.state; xp += res.xp; unlocked.push(...res.unlocked);
      atts.push(s.attempts[s.attempts.length - 1]);
    });
    s = finishSession(s, node.id, atts, before, unlocked, xp, base + 5 * 60_000).state;
  }
  const end = Date.now() + (s0.dayOffset + 7) * DAY;
  const toRust = allNodes(s).filter((n) => status(s, n) === 'mastered' && !s.attempts.some((a) => a.node === n.id && a.ts > end - 7 * DAY)).sort((a, b) => a.stage - b.stage)[0];
  if (toRust) {
    s = { ...s, rusty: { ...s.rusty, [toRust.id]: true }, changes: [...s.changes, change(end, 'oxido', `«${toRust.title}» se está oxidando: hace más de una semana que no lo practicás.`, toRust.id)] };
  }
  return { ...s, dayOffset: s0.dayOffset + 7, simWeeks: s0.simWeeks + 1 };
}

/* ── informe semanal ────────────────────────────────────────── */

export interface Report {
  from: number; to: number; sessions: number; minutes: number; questions: number; accuracy: number; xp: number;
  mastered: string[]; insights: Insight[]; news: NewsView[]; proposals: Proposal[];
  next: Recommendation[]; parkedReady: ParkedTopic[]; streak: number;
}

export function report(s: AppState): Report {
  const to = appNow(s);
  const from = to - 7 * DAY;
  const inRange = (ts: number) => ts > from && ts <= to;
  const atts = s.attempts.filter((a) => inRange(a.ts));
  const sess = s.sessions.filter((x) => inRange(x.ts));
  return {
    from, to,
    sessions: sess.length,
    minutes: Math.round(atts.reduce((a, x) => a + x.ms, 0) / 60_000 + sess.length * 3),
    questions: atts.length,
    accuracy: atts.length ? atts.filter((a) => a.correct).length / atts.length : 0,
    xp: sess.reduce((a, x) => a + x.xp, 0),
    mastered: s.changes.filter((c) => c.kind === 'logro' && inRange(c.ts) && c.node).map((c) => c.node!),
    insights: insights(s).slice(0, 4),
    news: newsView(s).filter((n) => n.bucket === 'now').slice(0, 3),
    proposals: proposals(s),
    next: recommendations(s).slice(0, 3),
    parkedReady: parkedReady(s),
    streak: s.streak,
  };
}

const fmtDate = (ts: number) => new Date(ts).toLocaleDateString('es-AR', { day: 'numeric', month: 'long' });
export const reportRange = (r: Report) => `del ${fmtDate(r.from)} al ${fmtDate(r.to)}`;

export function reportMarkdown(s: AppState, r: Report): string {
  const lines = [
    `# Tu semana en Xpedia (${reportRange(r)})`,
    '',
    `Objetivo: ${s.goal || 'IA aplicada'}`,
    '',
    '## Cómo te fue',
    `- ${r.sessions} sesiones, unos ${r.minutes} minutos y ${r.questions} preguntas (${pct(r.accuracy)} de aciertos).`,
    `- +${r.xp} XP · racha de ${r.streak} días.`,
    r.mastered.length ? `- Dominaste ${joinTitles(r.mastered)}.` : '- Esta semana no cerraste ningún tema nuevo.',
    '',
    '## Lo que te está costando',
    ...(r.insights.length ? r.insights.map((i) => `- **${i.title}.** ${i.detail} ${i.tip}`) : ['- Nada para marcar: vas parejo.']),
    '',
    '## Novedades que te importan',
    ...(r.news.length ? r.news.map((n) => `- ${n.item.title}. ${n.reason}`) : ['- Nada relevante para tu ruta esta semana.']),
    '',
    '## Ajustes que te propongo',
    ...(r.proposals.length ? r.proposals.map((p) => `- ${p.title}. ${p.detail}`) : ['- Ninguno: el plan sigue como está.']),
    '',
    '## Lo que sigue',
    ...r.next.map((n) => `- ${n.node.title}: ${n.reason}`),
  ];
  return lines.join('\n');
}
