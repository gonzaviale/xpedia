(function () {
  "use strict";

  var prefersReducedMotion = window.matchMedia && window.matchMedia("(prefers-reduced-motion: reduce)").matches;

  /* ---------- Modo oscuro / claro ---------- */
  var root = document.body;
  var toggle = document.getElementById("themeToggle");
  var stored = localStorage.getItem("xpedia-theme");
  var prefersDark = window.matchMedia && window.matchMedia("(prefers-color-scheme: dark)").matches;
  var theme = stored || (prefersDark ? "dark" : "light");

  function applyTheme(t) {
    root.setAttribute("data-theme", t);
    if (toggle) {
      toggle.setAttribute("aria-pressed", t === "dark" ? "true" : "false");
      toggle.setAttribute("aria-label", t === "dark" ? "Cambiar a modo claro" : "Cambiar a modo oscuro");
    }
  }

  applyTheme(theme);

  if (toggle) {
    toggle.addEventListener("click", function () {
      theme = theme === "dark" ? "light" : "dark";
      applyTheme(theme);
      try { localStorage.setItem("xpedia-theme", theme); } catch (e) {}
    });
  }

  /* ---------- Mapa de nodos: resaltar sección activa y pintar el camino recorrido ---------- */
  var navLinks = Array.prototype.slice.call(document.querySelectorAll(".route-nav__nodes a"));
  var sections = navLinks
    .map(function (link) {
      var id = link.getAttribute("data-node");
      return document.getElementById(id);
    })
    .filter(Boolean);

  var progressPath = document.getElementById("routeProgress");
  var progressLength = progressPath ? progressPath.getTotalLength() : 0;
  if (progressPath) {
    progressPath.style.strokeDasharray = progressLength;
    progressPath.style.strokeDashoffset = progressLength;
  }

  function setActive(id) {
    var activeIndex = navLinks.findIndex(function (link) {
      return link.getAttribute("data-node") === id;
    });

    navLinks.forEach(function (link, i) {
      link.classList.toggle("is-active", i === activeIndex);
      link.classList.toggle("is-passed", i < activeIndex);
      if (sections[i]) sections[i].classList.toggle("is-reached", i <= activeIndex);
    });

    if (progressPath && activeIndex > -1) {
      var ratio = navLinks.length > 1 ? activeIndex / (navLinks.length - 1) : 0;
      progressPath.style.strokeDashoffset = progressLength * (1 - ratio);
    }
  }

  if ("IntersectionObserver" in window && sections.length) {
    var observer = new IntersectionObserver(
      function (entries) {
        entries.forEach(function (entry) {
          if (entry.isIntersecting) {
            setActive(entry.target.id);
          }
        });
      },
      { rootMargin: "-40% 0px -50% 0px", threshold: 0 }
    );
    sections.forEach(function (s) { observer.observe(s); });
  } else if (navLinks.length) {
    setActive(navLinks[0].getAttribute("data-node"));
  }

  /* ---------- Animaciones que arrancan una vez, al entrar en pantalla ---------- */
  var inviewEls = Array.prototype.slice.call(document.querySelectorAll("[data-inview]"));
  if ("IntersectionObserver" in window) {
    var inviewObserver = new IntersectionObserver(function (entries) {
      entries.forEach(function (entry) {
        if (entry.isIntersecting) {
          entry.target.classList.add("is-inview");
          inviewObserver.unobserve(entry.target);
        }
      });
    }, { threshold: 0.35 });
    inviewEls.forEach(function (el) { inviewObserver.observe(el); });
  } else {
    inviewEls.forEach(function (el) { el.classList.add("is-inview"); });
  }

  /* ---------- Pestañas accesibles: clic, flechas, Inicio y Fin ---------- */
  function bindTabs(tabs, select) {
    tabs.forEach(function (tab, i) {
      tab.addEventListener("click", function () { select(i, true); });
      tab.addEventListener("keydown", function (e) {
        var next = null;
        if (e.key === "ArrowRight") next = (i + 1) % tabs.length;
        else if (e.key === "ArrowLeft") next = (i - 1 + tabs.length) % tabs.length;
        else if (e.key === "Home") next = 0;
        else if (e.key === "End") next = tabs.length - 1;
        if (next === null) return;
        e.preventDefault();
        select(next, true);
        tabs[next].focus();
      });
    });
  }

  function markTabs(tabs, index) {
    tabs.forEach(function (tab, i) {
      tab.classList.toggle("is-active", i === index);
      tab.setAttribute("aria-selected", i === index ? "true" : "false");
      tab.tabIndex = i === index ? 0 : -1;
    });
  }

  /* ---------- Qué es: una pregunta de práctica real ---------- */
  var quiz = document.getElementById("miniQuiz");
  if (quiz) {
    var quizOpts = Array.prototype.slice.call(quiz.querySelectorAll(".mini-quiz__opt"));
    var quizFeedback = quiz.querySelector(".mini-quiz__feedback");
    var quizInitial = quizFeedback.textContent;

    var resetQuiz = function () {
      quizOpts.forEach(function (o) {
        o.disabled = false;
        o.classList.remove("is-correct", "is-wrong", "is-muted");
      });
      quizFeedback.textContent = quizInitial;
      quizOpts[0].focus();
    };

    quizOpts.forEach(function (opt) {
      opt.addEventListener("click", function () {
        var right = opt.hasAttribute("data-correct");
        quizOpts.forEach(function (o) {
          o.disabled = true;
          if (o.hasAttribute("data-correct")) o.classList.add("is-correct");
          else if (o === opt) o.classList.add("is-wrong");
          else o.classList.add("is-muted");
        });
        quizFeedback.innerHTML = right
          ? '<span class="mini-quiz__xp">+10 XP</span>¡Correcta!'
          : "Casi: la correcta quedó en verde.";
        var retry = document.createElement("button");
        retry.type = "button";
        retry.className = "mini-quiz__retry";
        retry.textContent = "Probar de nuevo";
        retry.addEventListener("click", resetQuiz);
        quizFeedback.appendChild(retry);
        retry.focus();
      });
    });
  }

  /* ---------- Cómo funciona: cinco piezas sobre un mismo camino ---------- */
  var engine = document.getElementById("engine");
  if (engine) {
    var engTrack = engine.querySelector(".engine__track");
    var engSvg = engine.querySelector(".engine__line");
    var engBg = engine.querySelector(".engine__line-bg");
    var engFill = engine.querySelector(".engine__line-fill");
    var engTabs = Array.prototype.slice.call(engine.querySelectorAll(".engine__step"));
    var engPanels = Array.prototype.slice.call(engine.querySelectorAll(".engine__panel"));
    var engActive = 0;
    var engCum = [0];
    var engTotal = 0;
    var engAuto = !prefersReducedMotion;
    var engTimer = null;
    var engVisible = false;
    var engHover = false;

    /* Camino curvo que pasa por el centro de cada nodo; guarda el largo hasta cada uno */
    var engLayout = function () {
      var box = engTrack.getBoundingClientRect();
      if (!box.width) return;
      var pts = engTabs.map(function (tab) {
        var r = tab.querySelector(".engine__dot").getBoundingClientRect();
        return { x: r.left - box.left + r.width / 2, y: r.top - box.top + r.height / 2 };
      });
      engSvg.setAttribute("viewBox", "0 0 " + box.width + " " + box.height);
      var d = "M" + pts[0].x + " " + pts[0].y;
      var partials = [];
      for (var i = 1; i < pts.length; i++) {
        var a = pts[i - 1], b = pts[i], mx = (a.x + b.x) / 2;
        d += " C" + mx + " " + a.y + " " + mx + " " + b.y + " " + b.x + " " + b.y;
        partials.push(d);
      }
      engCum = [0];
      partials.forEach(function (pd) {
        engFill.setAttribute("d", pd);
        engCum.push(engFill.getTotalLength());
      });
      engTotal = engCum[engCum.length - 1];
      engBg.setAttribute("d", d);
      engFill.setAttribute("d", d);
      engFill.style.transition = "none";
      engFill.style.strokeDasharray = engTotal + " " + engTotal;
      engFill.style.strokeDashoffset = engTotal - engCum[engActive];
      engFill.getBoundingClientRect();
      engFill.style.transition = "";
    };

    var engSchedule = function () {
      clearTimeout(engTimer);
      if (!engAuto) return;
      engTimer = setTimeout(function () {
        if (!engVisible || engHover || document.hidden) { engSchedule(); return; }
        engSelect((engActive + 1) % engTabs.length, false);
      }, 6000);
    };

    var engSelect = function (i, fromUser) {
      if (fromUser) { engAuto = false; clearTimeout(engTimer); }
      engActive = i;
      markTabs(engTabs, i);
      engTabs.forEach(function (tab, k) { tab.classList.toggle("is-passed", k < i); });
      engPanels.forEach(function (panel, k) { panel.classList.toggle("is-active", k === i); });
      if (engTotal) engFill.style.strokeDashoffset = engTotal - engCum[i];
      engSchedule();
    };

    bindTabs(engTabs, engSelect);
    engine.addEventListener("pointerenter", function () { engHover = true; });
    engine.addEventListener("pointerleave", function () { engHover = false; });

    if ("IntersectionObserver" in window) {
      new IntersectionObserver(function (entries) {
        engVisible = entries[0].isIntersecting;
        if (engVisible) engSchedule();
      }, { threshold: 0.4 }).observe(engine);
    } else {
      engVisible = true;
    }

    engLayout();
    engSelect(0, false);
    window.addEventListener("load", engLayout);
    if (document.fonts && document.fonts.ready) document.fonts.ready.then(engLayout);
    var engResizeTimer;
    window.addEventListener("resize", function () {
      clearTimeout(engResizeTimer);
      engResizeTimer = setTimeout(engLayout, 120);
    });
  }

  /* ---------- La app: pestañas como las de la app, dentro de una ventana ---------- */
  var showcase = document.getElementById("showcase");
  if (showcase) {
    var shotTabs = Array.prototype.slice.call(showcase.querySelectorAll(".showcase__tab"));
    var shotImgs = Array.prototype.slice.call(showcase.querySelectorAll(".browser__screen img"));
    var shotPath = document.getElementById("shotPath");
    var shotCaption = document.getElementById("shotCaption");
    bindTabs(shotTabs, function (i) {
      markTabs(shotTabs, i);
      shotImgs.forEach(function (img, k) { img.classList.toggle("is-active", k === i); });
      shotPath.textContent = shotTabs[i].getAttribute("data-path");
      shotCaption.textContent = shotTabs[i].getAttribute("data-caption");
    });
  }

  /* ---------- Hero: demo de una ruta que se arma según el objetivo ---------- */
  var routeCard = document.getElementById("routeCard");
  if (routeCard) {
    var GOALS = [
      {
        goal: "pasar de gastronomía a tecnología",
        route: "Desarrollo web desde cero",
        nodes: ["Lógica y algoritmos", "HTML y CSS", "JavaScript", "Tu primer proyecto", "Portfolio"],
        current: 2,
        eta: "Llegás en 7 meses",
        adjust: "Sumé un repaso de CSS: en la práctica te costaron los layouts."
      },
      {
        goal: "usar IA en mi trabajo",
        route: "IA aplicada",
        nodes: ["Qué es un LLM", "Tokens y contexto", "Alucinaciones y sesgos", "Pedir bien", "Automatizar sin código"],
        current: 2,
        eta: "Llegás en 8 semanas",
        adjust: "Salteé «Tokens y contexto»: ya lo dominabas."
      },
      {
        goal: "atender clientes a distancia",
        route: "Atención al cliente remota",
        nodes: ["Escucha activa", "Tono por escrito", "Reclamos difíciles", "Herramientas de soporte", "Un caso real"],
        current: 1,
        eta: "Llegás en 6 semanas",
        adjust: "Agregué un roleplay con IA: un cliente enojado por chat."
      },
      {
        goal: "hablar en público sin trabarme",
        route: "Oratoria",
        nodes: ["Armar el mensaje", "Voz y ritmo", "Manejar los nervios", "Preguntas difíciles", "Tu charla de 5 minutos"],
        current: 3,
        eta: "Llegás en 10 semanas",
        adjust: "Te propuse grabarte para medir tu ritmo al hablar."
      }
    ];
    var OFFSETS = [6, 84, 22, 100, 40];
    var ICONS = {
      done: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><path d="M5 12.5l4.5 4.5L19 7.5"/></svg>',
      current: '<svg viewBox="0 0 24 24" fill="currentColor"><path d="M13.5 2 4 13.5h6.5L9.5 22 20 9.5h-6.6L13.5 2Z"/></svg>',
      available: '<svg viewBox="0 0 24 24" fill="currentColor"><path d="M8 5.5v13l10.5-6.5L8 5.5Z"/></svg>',
      locked: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round"><rect x="5" y="10.5" width="14" height="10" rx="2.5"/><path d="M8.5 10.5V8a3.5 3.5 0 0 1 7 0v2.5"/></svg>'
    };
    var STATE_LABEL = { done: "Dominado", current: "En curso", available: "Disponible", locked: "Bloqueado" };

    var rcGoal = document.getElementById("rcGoal");
    var rcRoute = document.getElementById("rcRoute");
    var rcMap = document.getElementById("rcMap");
    var rcLines = document.getElementById("rcLines");
    var rcNodes = document.getElementById("rcNodes");
    var rcAdjust = document.getElementById("rcAdjust");
    var rcProgress = document.getElementById("rcProgress");
    var rcBar = document.getElementById("rcBar");
    var rcEta = document.getElementById("rcEta");
    var rcXp = document.getElementById("rcXp");
    var rcAnnounce = document.getElementById("rcAnnounce");
    var pills = Array.prototype.slice.call(document.querySelectorAll(".goal-pill"));
    var reduceMotion = window.matchMedia && window.matchMedia("(prefers-reduced-motion: reduce)").matches;

    var activeGoal = 0;
    var typingTimer = null;
    var autoplayTimer = null;
    var autoplay = !reduceMotion;
    var heroVisible = true;
    var lastLinesKey = "";

    function stateFor(i, current) {
      if (i < current) return "done";
      if (i === current) return "current";
      if (i === current + 1) return "available";
      return "locked";
    }

    function renderNodes(g, animate) {
      rcNodes.innerHTML = g.nodes.map(function (name, i) {
        var st = stateFor(i, g.current);
        var here = st === "current" ? ' <em class="rnode__here">Estás acá</em>' : "";
        return '<li class="rnode rnode--' + st + (animate && !reduceMotion ? " is-entering" : "") + '" style="--x:' + OFFSETS[i] + '">' +
          '<span class="rnode__dot">' + ICONS[st] + "</span>" +
          '<span class="rnode__text"><span class="rnode__name">' + name + here + "</span>" +
          '<span class="rnode__state">' + STATE_LABEL[st] + "</span></span></li>";
      }).join("");

      if (animate && !reduceMotion) {
        Array.prototype.forEach.call(rcNodes.children, function (li, i) {
          setTimeout(function () { li.classList.remove("is-entering"); }, 60 + i * 80);
        });
      }
    }

    /* Une el centro de cada nodo con una curva. Usa offsets (no getBoundingClientRect)
       para ignorar la animación de entrada de los nodos. */
    function drawRouteLines(animate, force) {
      var w = rcMap.clientWidth, h = rcMap.clientHeight;
      if (!w) return;
      var items = rcNodes.children;
      var pts = Array.prototype.map.call(items, function (li) {
        var dot = li.firstElementChild;
        return {
          x: rcNodes.offsetLeft + li.offsetLeft + dot.offsetWidth / 2,
          y: rcNodes.offsetTop + li.offsetTop + li.offsetHeight / 2
        };
      });
      var current = GOALS[activeGoal].current;
      var key = w + "x" + h + ":" + current + ":" + pts.map(function (p) { return Math.round(p.x) + "," + Math.round(p.y); }).join(";");
      if (!force && key === lastLinesKey) return;
      lastLinesKey = key;

      rcLines.setAttribute("viewBox", "0 0 " + w + " " + h);
      var html = "";
      for (var i = 0; i < pts.length - 1; i++) {
        var a = pts[i], b = pts[i + 1], my = (a.y + b.y) / 2;
        html += '<path class="seg ' + (i < current ? "seg--done" : "seg--todo") + '" d="M' + a.x + " " + a.y +
          " C" + a.x + " " + my + " " + b.x + " " + my + " " + b.x + " " + b.y + '"/>';
      }
      rcLines.innerHTML = html;

      if (animate && !reduceMotion) {
        Array.prototype.forEach.call(rcLines.querySelectorAll(".seg--done"), function (p, i) {
          var len = p.getTotalLength();
          p.style.strokeDasharray = len;
          p.style.strokeDashoffset = len;
          p.getBoundingClientRect();
          p.style.transition = "stroke-dashoffset .45s ease " + (0.15 + i * 0.2) + "s";
          p.style.strokeDashoffset = "0";
        });
      }
    }

    function renderGoal(index, animate) {
      var g = GOALS[index];
      rcRoute.textContent = "Ruta: " + g.route;
      renderNodes(g, animate);
      drawRouteLines(animate, true);
      rcAdjust.textContent = g.adjust;
      rcProgress.textContent = g.current + " de " + g.nodes.length + " hitos";
      rcBar.style.width = (g.current / g.nodes.length * 100) + "%";
      rcEta.textContent = g.eta;
      if (animate && !reduceMotion) {
        rcXp.classList.remove("is-pop");
        void rcXp.offsetWidth;
        rcXp.classList.add("is-pop");
      }
    }

    function setPills(index) {
      pills.forEach(function (p, i) {
        p.classList.toggle("is-active", i === index);
        p.setAttribute("aria-pressed", i === index ? "true" : "false");
      });
    }

    /* Borra el objetivo actual y escribe el nuevo, letra por letra */
    function typeGoal(text, done) {
      clearTimeout(typingTimer);
      if (reduceMotion) { rcGoal.textContent = text; done(); return; }
      var from = rcGoal.textContent;
      var i = from.length;
      (function erase() {
        if (i > 0) {
          i--;
          rcGoal.textContent = from.slice(0, i);
          typingTimer = setTimeout(erase, 14);
          return;
        }
        var j = 0;
        (function type() {
          j++;
          rcGoal.textContent = text.slice(0, j);
          if (j < text.length) typingTimer = setTimeout(type, 38);
          else done();
        })();
      })();
    }

    function scheduleNext() {
      clearTimeout(autoplayTimer);
      if (!autoplay) return;
      autoplayTimer = setTimeout(function () {
        if (!heroVisible || document.hidden) { scheduleNext(); return; }
        switchTo((activeGoal + 1) % GOALS.length, false);
      }, 4800);
    }

    function switchTo(index, fromUser) {
      if (fromUser) { autoplay = false; clearTimeout(autoplayTimer); }
      activeGoal = index;
      setPills(index);
      routeCard.classList.add("is-switching");
      typeGoal(GOALS[index].goal, function () {
        routeCard.classList.remove("is-switching");
        renderGoal(index, true);
        rcAnnounce.textContent = "Ruta de ejemplo para: " + GOALS[index].goal + ".";
        scheduleNext();
      });
    }

    pills.forEach(function (p, i) {
      p.addEventListener("click", function () {
        if (i === activeGoal && !routeCard.classList.contains("is-switching")) {
          autoplay = false;
          clearTimeout(autoplayTimer);
          return;
        }
        switchTo(i, true);
      });
    });

    if ("IntersectionObserver" in window) {
      new IntersectionObserver(function (entries) {
        heroVisible = entries[0].isIntersecting;
      }, { threshold: 0.2 }).observe(routeCard);
    }

    renderGoal(0, true);
    scheduleNext();

    var redraw = function () { drawRouteLines(false, false); };
    window.addEventListener("load", redraw);
    if (document.fonts && document.fonts.ready) document.fonts.ready.then(redraw);
    var heroResizeTimer;
    window.addEventListener("resize", function () {
      clearTimeout(heroResizeTimer);
      heroResizeTimer = setTimeout(redraw, 120);
    });
  }
})();
