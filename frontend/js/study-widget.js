// Renders a small floating timer pill on any page (except pomodoro.html itself)
// when a study session is active. Recomputes purely from timestamps, so it
// stays accurate even across full page reloads.

(function () {
  // don't show the floating widget on the pomodoro page — the full timer is already there
  if (window.location.pathname.indexOf("pomodoro.html") !== -1) return;

  let widgetTickInterval = null;

  function checkAndRenderWidget() {
    const raw = localStorage.getItem(STUDY_STORAGE_KEY);
    if (!raw) {
      removeWidget();
      return;
    }

    let state = JSON.parse(raw);

    if (state.paused) {
      renderWidget(state);
      return;
    }

    state = advanceTimerPhases(state);

    if (state._justCompletedFocus) {
      apiPut("/api/study-sessions/" + state.sessionId + "/progress", {});
      playSound("complete");
    }

    if (state._justStartedFocus && !state._justFinishedSession) {
      playSound("start");
    }

    if (state._justFinishedSession) {
      apiPut("/api/study-sessions/" + state.sessionId + "/end", {});
      localStorage.removeItem(STUDY_STORAGE_KEY);
      removeWidget();
      return;
    }

    localStorage.setItem(STUDY_STORAGE_KEY, JSON.stringify(state));
    renderWidget(state);
  }

  function renderWidget(state) {
    let widget = document.getElementById("study-floating-widget");
    if (!widget) {
      widget = document.createElement("div");
      widget.id = "study-floating-widget";
      widget.className = "study-floating-widget";
      document.body.appendChild(widget);
    }

    const taskLine = state.taskName
      ? '<div class="study-widget-task">🎯 ' + state.taskName + '</div>'
      : '';

    if (state.paused) {
      widget.innerHTML =
        '<div class="study-widget-icon"><span>⏸️</span></div>' +
        '<div class="study-widget-body">' +
          '<div class="study-widget-time">Paused</div>' +
          '<span class="study-widget-phase paused">Session ' + state.currentCycle + '/' + state.plannedSessions + '</span>' +
          taskLine +
        '</div>' +
        '<button class="study-widget-view-btn" onclick="window.location.href=\'pomodoro.html\'">Open<span class="arrow">→</span></button>';
      return;
    }

    const isFocus = state.phase === "focus";
    const phaseDuration = (isFocus ? state.focusMinutes : state.breakMinutes) * 60000;
    const elapsed = Date.now() - new Date(state.phaseStartedAt).getTime();
    const pct = Math.min(100, Math.max(0, (elapsed / phaseDuration) * 100));

    widget.innerHTML =
      '<div class="study-widget-icon" style="--pct:' + pct + '%"><span>' + (isFocus ? "⏳" : "☕") + '</span></div>' +
      '<div class="study-widget-body">' +
        '<div class="study-widget-time">' + formatRemainingTime(state) + '</div>' +
        '<span class="study-widget-phase ' + (isFocus ? "focus" : "break") + '">' +
          (isFocus ? "Focus" : "Break") + ' · session ' + state.currentCycle + '/' + state.plannedSessions +
        '</span>' +
        taskLine +
      '</div>' +
      '<button class="study-widget-view-btn" onclick="window.location.href=\'pomodoro.html\'">Open<span class="arrow">→</span></button>';
  }

  function removeWidget() {
    const widget = document.getElementById("study-floating-widget");
    if (widget) widget.remove();
    if (widgetTickInterval) clearInterval(widgetTickInterval);
  }

  checkAndRenderWidget();
  widgetTickInterval = setInterval(checkAndRenderWidget, 1000);
})();