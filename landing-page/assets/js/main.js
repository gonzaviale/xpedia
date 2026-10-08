(function () {
  "use strict";

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

  /* ---------- Mapa de nodos: resaltar sección activa ---------- */
  var navLinks = Array.prototype.slice.call(document.querySelectorAll(".route-nav__nodes a"));
  var sections = navLinks
    .map(function (link) {
      var id = link.getAttribute("data-node");
      return document.getElementById(id);
    })
    .filter(Boolean);

  function setActive(id) {
    navLinks.forEach(function (link) {
      link.classList.toggle("is-active", link.getAttribute("data-node") === id);
    });
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
})();
