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

  /* ---------- Slider de capturas ---------- */
  var slider = document.getElementById("gallerySlider");
  if (slider) {
    var track = slider.querySelector(".slider__track");
    var slides = Array.prototype.slice.call(slider.querySelectorAll(".slider__slide"));
    var dotsWrap = slider.querySelector(".slider__dots");
    var prevBtn = slider.querySelector(".slider__arrow--prev");
    var nextBtn = slider.querySelector(".slider__arrow--next");
    var current = 0;

    slides.forEach(function (_, i) {
      var dot = document.createElement("button");
      dot.type = "button";
      dot.setAttribute("aria-label", "Ir a la captura " + (i + 1));
      dot.addEventListener("click", function () { goTo(i); });
      dotsWrap.appendChild(dot);
    });
    var dots = Array.prototype.slice.call(dotsWrap.children);

    function goTo(index) {
      current = (index + slides.length) % slides.length;
      track.style.transform = "translateX(-" + (current * 100) + "%)";
      dots.forEach(function (d, i) { d.classList.toggle("is-active", i === current); });
    }

    if (prevBtn) prevBtn.addEventListener("click", function () { goTo(current - 1); });
    if (nextBtn) nextBtn.addEventListener("click", function () { goTo(current + 1); });

    slider.setAttribute("tabindex", "0");
    slider.addEventListener("keydown", function (e) {
      if (e.key === "ArrowLeft") goTo(current - 1);
      if (e.key === "ArrowRight") goTo(current + 1);
    });

    goTo(0);
  }
})();
