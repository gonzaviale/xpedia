import { NEWS } from '../data';
import { allNodes, insights, newsView, progress, type AppState } from '../engine';
import { Card, IaBadge, Icon, type IconName } from '../ui';

type Tab = 'mapa' | 'practicar' | 'radar' | 'informe';

interface Piece { n: number; title: string; icon: IconName; what: string; live: string; ai: string; tab: Tab }

export function Motor({ s, onTab }: { s: AppState; onTab: (t: Tab) => void }) {
  const nodes = allNodes(s);
  const edges = nodes.reduce((a, n) => a + n.prereqs.length, 0);
  const noise = newsView(s).filter((v) => v.bucket === 'noise').length;
  const pieces: Piece[] = [
    { n: 1, title: 'Ruta como grafo', icon: 'map', tab: 'mapa', what: 'Cada tema declara qué necesita saber antes. Eso permite adaptar, agendar y bifurcar sin romper nada.', live: `${nodes.length} temas y ${edges} prerrequisitos · ${s.added.length} sumados por interés`, ai: 'Genera la ruta a partir del objetivo de la persona.' },
    { n: 2, title: 'Evaluación', icon: 'target', tab: 'practicar', what: 'Preguntas de concepto, aplicación y detalle. Antes de ver la respuesta, la persona dice si lo sabía, lo dudó o lo adivinó.', live: `${s.attempts.length} respuestas registradas · ${s.sessions.length} sesiones`, ai: 'Genera variantes de preguntas y evalúa respuestas abiertas con rúbrica.' },
    { n: 3, title: 'Perfil de rendimiento', icon: 'bolt', tab: 'mapa', what: 'Dominio por tema y patrones: qué tipo de pregunta fallás, si adivinás, si vas lento, qué se está oxidando.', live: `${progress(s).done} temas dominados · ${insights(s).length} patrones detectados ahora`, ai: 'Detecta patrones más finos en respuestas abiertas.' },
    { n: 4, title: 'Adaptador', icon: 'refresh', tab: 'informe', what: 'Agrega repasos, saltea lo que ya dominás, reordena y abre ramas. Cada cambio queda explicado.', live: `${s.changes.length} ajustes hechos · ${Object.keys(s.review).length} repasos activos`, ai: 'Propone ajustes y los explica en lenguaje simple.' },
    { n: 5, title: 'Radar e informe', icon: 'radar', tab: 'radar', what: 'Entra lo que pasa en el rubro, filtrado por la ruta. Sale un informe semanal con avance, dificultades y ajustes para aceptar.', live: `${NEWS.length} novedades revisadas · ${noise} filtradas como ruido`, ai: 'Busca novedades reales y redacta el informe.' },
  ];

  return (
    <div className="narrow stack">
      <div>
        <span className="eyebrow">Cómo funciona</span>
        <h1>El motor de Xpedia, en 5 piezas</h1>
        <p className="muted">Todo lo demás (XP, racha, empresa) se monta encima de esto. Los números son de tu sesión de demo.</p>
      </div>
      <div className="loop">
        {pieces.map((p) => (
          <Card key={p.n} className="piece">
            <div className="piece-head">
              <span className="piece-n">{p.n}</span>
              <Icon name={p.icon} size={18} />
              <h3>{p.title}</h3>
            </div>
            <p>{p.what}</p>
            <p className="piece-live"><Icon name="dot" size={10} />{p.live}</p>
            <p className="ia-note"><IaBadge />{p.ai}</p>
            <button type="button" className="btn btn-ghost btn-sm" onClick={() => onTab(p.tab)}>Verlo en la demo <Icon name="arrow" size={14} /></button>
          </Card>
        ))}
      </div>
      <Card className="ia-card">
        <h3><Icon name="branch" size={16} /> El caso «me interesa esto»</h3>
        <p>Cuando marcás un tema, la IA compara sus prerrequisitos con tu perfil y decide una de cinco cosas:</p>
        <ul className="plain">
          <li><b>Ya está en tu ruta</b>: te muestra dónde.</li>
          <li><b>Ahora</b>: tenés la base, entra en tu hito actual.</li>
          <li><b>Más adelante</b>: te falta poco; se agenda en el hito donde ya vas a tener la base.</li>
          <li><b>Rama opcional</b>: es otro camino; va al costado y no frena la ruta principal.</li>
          <li><b>Guardado</b>: está lejos; te avisa cuando llegues.</li>
        </ul>
        <p className="muted small">Probalo con la barra «¿Qué te interesa?» escribiendo, por ejemplo, «agentes», «MCP», «modelos locales» o «notebooks».</p>
      </Card>
    </div>
  );
}
