requireAuth();
loadSidebarUser();

const STORAGE_KEY = "studyhive_active_session";

let allCourses = [];
let allTasks = [];
let selectedCourseIds = [];
let courseSessionCounts = {};
let selectedMode = "CLASSIC";
let sessionCount = 4;
let skipBreaks = false;
let tickInterval = null;
let currentCompletedTask = null;
let sessionHistory = null;
let interleaveCourseId = null;
let interleaveCourseName = null;
let interleaveDismissed = false;

const MODE_DEFAULTS = {
  CLASSIC: { focus: 25, break: 5 },
  FLOWTIME: { focus: 52, break: 17 },
};

const RING_CIRCUMFERENCE = 565.48;
const DEFAULT_TITLE = document.title;

// load courses for the picker
apiGet("/api/courses").then(function (courses) {
  allCourses = courses;
  renderCoursePicker();
  checkInterleaveSuggestion();
});

// load open tasks for the "focusing on" picker
apiGet("/api/tasks").then(function (tasks) {
  allTasks = (tasks || []).filter(function (t) {
    return t.taskStatus !== "DONE";
  });
  renderTaskPicker();
});

function renderCoursePicker() {
  const container = document.getElementById("course-picker");
  container.innerHTML = "";
  allCourses.forEach(function (course) {
    const isSelected = selectedCourseIds.includes(course.id);
    const btn = document.createElement("button");
    btn.textContent = course.code;
    btn.style.cssText =
      "font-size:12px; padding:6px 12px; border-radius:20px; cursor:pointer; font-family:inherit; border:1px solid " +
      (isSelected ? "#D2A050" : "#E8DDD0") +
      "; background:" +
      (isSelected ? "#FAEEDA" : "#fff") +
      "; color:" +
      (isSelected ? "#8B5A0B" : "#1C1410") +
      ";";
    btn.onclick = function () {
      if (isSelected) {
        selectedCourseIds = selectedCourseIds.filter(function (id) {
          return id !== course.id;
        });
        delete courseSessionCounts[course.id];
      } else if (isInterleaveEnabled()) {
        selectedCourseIds.push(course.id);
        courseSessionCounts[course.id] = 1;
      } else {
        selectedCourseIds = [course.id];
        courseSessionCounts = {};
      }
      renderCoursePicker();
      renderTaskPicker();
      updateSessionSectionVisibility();
    };
    container.appendChild(btn);
  });
}

function updateSessionSectionVisibility() {
  const multi = selectedCourseIds.length > 1;
  document.getElementById("single-session-section").style.display = multi ? "none" : "block";
  document.getElementById("multi-session-section").style.display = multi ? "block" : "none";
  if (multi) {
    renderMultiSessionBreakdown();
  }
}

function renderMultiSessionBreakdown() {
  const container = document.getElementById("multi-session-list");
  container.innerHTML = "";

  selectedCourseIds.forEach(function (id) {
    const course = allCourses.find(function (c) {
      return c.id === id;
    });
    if (!course) return;

    const row = document.createElement("div");
    row.className = "study-multi-session-row";
    row.innerHTML =
      "<span>" + course.code + "</span>" +
      '<div class="study-stepper">' +
        '<button onclick="changeCourseSessionCount(' + id + ', -1)">−</button>' +
        "<span>" + (courseSessionCounts[id] || 1) + "</span>" +
        '<button onclick="changeCourseSessionCount(' + id + ', 1)">+</button>' +
      "</div>";
    container.appendChild(row);
  });

  const defaults = MODE_DEFAULTS[selectedMode];
  const total = getMultiSessionTotal();
  const focusTotal = total * defaults.focus;
  const breakTotal = skipBreaks ? 0 : (total - 1) * defaults.break;
  document.getElementById("multi-session-total").textContent =
    "Total: " + total + " sessions · " + (focusTotal + breakTotal) + " min total";
}

function changeCourseSessionCount(courseId, delta) {
  const current = courseSessionCounts[courseId] || 1;
  courseSessionCounts[courseId] = Math.max(1, current + delta);
  renderMultiSessionBreakdown();
}

function getMultiSessionTotal() {
  return selectedCourseIds.reduce(function (sum, id) {
    return sum + (courseSessionCounts[id] || 1);
  }, 0);
}

function getEffectiveSessionCount() {
  return selectedCourseIds.length > 1 ? getMultiSessionTotal() : sessionCount;
}

// Rebuilds the task dropdown, restricted to tasks linked to the selected
// course(s) — or every open task when no course is selected.
function renderTaskPicker() {
  const picker = document.getElementById("task-picker");
  const previousValue = picker.value;

  const visibleTasks =
    selectedCourseIds.length === 0
      ? allTasks
      : allTasks.filter(function (t) {
          return (
            t.courses &&
            t.courses.some(function (c) {
              return selectedCourseIds.includes(c.id);
            })
          );
        });

  picker.innerHTML = '<option value="">No specific task</option>';
  visibleTasks.forEach(function (t) {
    const option = document.createElement("option");
    option.value = t.id;
    option.textContent =
      t.taskName + (t.courses && t.courses.length ? " · " + t.courses[0].code : "");
    picker.appendChild(option);
  });

  // keep the current selection if it's still in the filtered list
  const stillVisible = visibleTasks.some(function (t) {
    return String(t.id) === previousValue;
  });
  picker.value = stillVisible ? previousValue : "";
}

function selectMode(mode) {
  selectedMode = mode;
  document.getElementById("mode-classic").style.borderColor =
    mode === "CLASSIC" ? "#D2A050" : "#E8DDD0";
  document.getElementById("mode-classic").style.background =
    mode === "CLASSIC" ? "#FAEEDA" : "#fff";
  document.getElementById("mode-flowtime").style.borderColor =
    mode === "FLOWTIME" ? "#D2A050" : "#E8DDD0";
  document.getElementById("mode-flowtime").style.background =
    mode === "FLOWTIME" ? "#FAEEDA" : "#fff";
  updateTotalMinutes();
}
selectMode("CLASSIC"); // set initial highlight

function changeSessionCount(delta) {
  sessionCount = Math.max(1, sessionCount + delta);
  document.getElementById("session-count-display").textContent = sessionCount;
  updateTotalMinutes();
}

function toggleSkipBreaks() {
  skipBreaks = !skipBreaks;
  document.getElementById("skip-breaks-dot").style.left = skipBreaks
    ? "18px"
    : "2px";
  document.getElementById("skip-breaks-toggle").style.background = skipBreaks
    ? "#D2A050"
    : "#E8DDD0";
  updateTotalMinutes();
}

function updateTotalMinutes() {
  const defaults = MODE_DEFAULTS[selectedMode];
  const focusTotal = sessionCount * defaults.focus;
  const breakTotal = skipBreaks ? 0 : (sessionCount - 1) * defaults.break;
  const total = focusTotal + breakTotal;
  document.getElementById("total-minutes-display").textContent =
    sessionCount + " sessions · " + total + " min total";

  if (selectedCourseIds.length > 1) {
    renderMultiSessionBreakdown();
  }
}
updateTotalMinutes();

function isInterleaveEnabled() {
  return localStorage.getItem("studyhive_interleave_enabled") !== "false";
}

function toggleInterleave() {
  const enabled = !isInterleaveEnabled();
  localStorage.setItem("studyhive_interleave_enabled", String(enabled));
  updateInterleaveToggleUI(enabled);

  if (!enabled && selectedCourseIds.length > 1) {
    selectedCourseIds = selectedCourseIds.slice(0, 1);
    courseSessionCounts = {};
    renderCoursePicker();
    renderTaskPicker();
    updateSessionSectionVisibility();
  }

  checkInterleaveSuggestion();
}

function updateInterleaveToggleUI(enabled) {
  document.getElementById("interleave-toggle-dot").style.left = enabled ? "18px" : "2px";
  document.getElementById("interleave-toggle").style.background = enabled ? "#D2A050" : "#E8DDD0";
}

updateInterleaveToggleUI(isInterleaveEnabled());

function checkInterleaveSuggestion() {
  const banner = document.getElementById("interleave-banner");

  if (
    interleaveDismissed ||
    interleaveCourseId ||
    !isInterleaveEnabled() ||
    !sessionHistory ||
    allCourses.length < 2 ||
    selectedCourseIds.length > 1
  ) {
    banner.style.display = "none";
    return;
  }

  const recent = sessionHistory.slice(0, 3);
  if (recent.length < 3) {
    banner.style.display = "none";
    return;
  }

  const firstCourseId = recent[0].courses && recent[0].courses.length === 1 ? recent[0].courses[0].id : null;
  if (!firstCourseId) {
    banner.style.display = "none";
    return;
  }

  const sameCourse = recent.every(function (s) {
    return s.courses && s.courses.length === 1 && s.courses[0].id === firstCourseId;
  });
  if (!sameCourse) {
    banner.style.display = "none";
    return;
  }

  const repeatedCourseCode = recent[0].courses[0].code;

  let suggestion = null;
  let oldestSeen = Infinity;
  allCourses.forEach(function (c) {
    if (c.id === firstCourseId) return;
    const lastSeen = sessionHistory.find(function (s) {
      return s.courses && s.courses.some(function (sc) { return sc.id === c.id; });
    });
    const lastSeenTime = lastSeen ? new Date(lastSeen.startedAt).getTime() : 0;
    if (lastSeenTime < oldestSeen) {
      oldestSeen = lastSeenTime;
      suggestion = c;
    }
  });

  if (!suggestion) {
    banner.style.display = "none";
    return;
  }

  renderInterleaveBanner(repeatedCourseCode, suggestion);
}

function renderInterleaveBanner(repeatedCourseCode, suggestion) {
  const banner = document.getElementById("interleave-banner");
  const mainPct = (sessionCount / (sessionCount + 1)) * 100;
  const extraPct = 100 - mainPct;

  banner.style.display = "block";
  banner.innerHTML =
    '<div class="study-interleave-text">🔀 ' + repeatedCourseCode + " three sessions in a row — mix in " + suggestion.code + "?</div>" +
    '<div class="study-interleave-bar">' +
      '<div class="study-interleave-bar-main" style="width:' + mainPct + '%"></div>' +
      '<div class="study-interleave-bar-extra" style="width:' + extraPct + '%"></div>' +
    "</div>" +
    '<div class="study-interleave-legend"><span>' + repeatedCourseCode + " · " + sessionCount + " blocks</span><span>" + suggestion.code + " · 1 block</span></div>" +
    '<div class="study-interleave-actions">' +
      '<button class="study-start-btn" style="width:auto; padding:10px 18px;" onclick="addInterleaveBlock(' + suggestion.id + ', \'' + suggestion.code + '\')">Add ' + suggestion.code + " block</button>" +
      '<button class="study-end-btn" onclick="dismissInterleave()">Not now</button>' +
    "</div>";
}

function addInterleaveBlock(courseId, courseCode) {
  interleaveCourseId = courseId;
  interleaveCourseName = courseCode;
  document.getElementById("interleave-banner").innerHTML =
    '<div class="study-interleave-confirmed">✓ Added a ' + courseCode + ' block at the end of this session</div>';
}

function dismissInterleave() {
  interleaveDismissed = true;
  document.getElementById("interleave-banner").style.display = "none";
}

function buildCourseBlocks() {
  const blocks = [];
  if (selectedCourseIds.length > 1) {
    selectedCourseIds.forEach(function (id) {
      blocks.push({ courseId: id, sessionCount: courseSessionCounts[id] || 1 });
    });
  }
  if (interleaveCourseId) {
    blocks.push({ courseId: interleaveCourseId, sessionCount: 1 });
  }
  return blocks;
}

function buildCycleCourseCodes(courseBlocks, plannedSessions) {
  if (!courseBlocks || courseBlocks.length === 0) return null;
  const codes = [];
  courseBlocks.forEach(function (b) {
    for (let i = 0; i < b.sessionCount; i++) codes.push(b.courseCode);
  });
  while (codes.length < plannedSessions) codes.push(null);
  return codes;
}

function hasMultipleCourses(codes) {
  if (!codes) return false;
  const distinct = codes.filter(function (c) {
    return c !== null;
  });
  return new Set(distinct).size > 1;
}

// ---- Start a session ----
function startSession() {
  const defaults = MODE_DEFAULTS[selectedMode];

  const taskPicker = document.getElementById("task-picker");
  const taskId = taskPicker.value ? Number(taskPicker.value) : null;

  // starting a focus session on a task that hasn't been picked up yet
  // implies it's now underway
  const selectedTask = taskId
    ? allTasks.find(function (t) {
        return t.id === taskId;
      })
    : null;
  if (selectedTask && selectedTask.taskStatus === "TODO") {
    apiPut("/api/tasks/" + taskId, { newStatus: "IN_PROGRESS" });
  }

  apiPost("/api/study-sessions", {
    courseIds: selectedCourseIds,
    taskId: taskId,
    courseBlocks: buildCourseBlocks(),
    mode: selectedMode,
    plannedSessions: getEffectiveSessionCount(),
    focusMinutes: defaults.focus,
    breakMinutes: defaults.break,
    skipBreaks: skipBreaks,
  }).then(function (session) {
    if (!session || !session.id) {
      alert("Could not start session. You may already have one active.");
      return;
    }

    const state = {
      sessionId: session.id,
      focusMinutes: session.focusMinutes,
      breakMinutes: session.breakMinutes,
      plannedSessions: session.plannedSessions,
      skipBreaks: session.skipBreaks,
      completedSessions: 0,
      currentCycle: 1,
      phase: "focus",
      phaseStartedAt: new Date().toISOString(),
      taskId: session.taskId || null,
      taskName: session.taskName || null,
      cycleCourseCodes: buildCycleCourseCodes(session.courseBlocks, session.plannedSessions),
    };
    localStorage.setItem(STORAGE_KEY, JSON.stringify(state));
    playSound("start");
    showTimerScreen();
  });
}

// ---- Check for an active session on page load ----
const savedState = localStorage.getItem(STORAGE_KEY);
if (savedState) {
  showTimerScreen();
} else {
  apiGet("/api/study-sessions/active").then(function (session) {
    if (session) {
      // reconstruct local state from what the server knows
      const state = {
        sessionId: session.id,
        focusMinutes: session.focusMinutes,
        breakMinutes: session.breakMinutes,
        plannedSessions: session.plannedSessions,
        skipBreaks: session.skipBreaks,
        completedSessions: session.completedSessions,
        currentCycle: session.completedSessions + 1,
        phase: "focus", // best guess; exact phase isn't tracked server-side
        phaseStartedAt: session.startedAt,
        taskId: session.taskId || null,
        taskName: session.taskName || null,
        cycleCourseCodes: buildCycleCourseCodes(session.courseBlocks, session.plannedSessions),
      };
      localStorage.setItem(STORAGE_KEY, JSON.stringify(state));
      showTimerScreen();
    }
  });
}

function showTimerScreen() {
  document.getElementById("setup-screen").style.display = "none";
  document.getElementById("history-section").style.display = "none";
  document.getElementById("timer-screen").style.display = "flex";
  runTimer();
  tickInterval = setInterval(runTimer, 1000);
}

// ---- Core timer loop: recompute from timestamps every tick ----
function runTimer() {
  let state = JSON.parse(localStorage.getItem(STORAGE_KEY));
  if (!state) {
    clearInterval(tickInterval);
    return;
  }

  if (!state.paused) {
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
      clearInterval(tickInterval);
      localStorage.removeItem(STORAGE_KEY);
      document.getElementById("phase-label").textContent = "Complete";
      document.getElementById("timer-display").textContent = "🎉";
      document.getElementById("timer-subtext").textContent = "All sessions done!";
      document.getElementById("timer-actions").style.display = "none";
      document.getElementById("end-session-btn").style.display = "none";
      document.title = DEFAULT_TITLE;

      if (state.taskId) {
        currentCompletedTask = { id: state.taskId, name: state.taskName };
        const doneBtn = document.getElementById("mark-task-done-btn");
        doneBtn.textContent = "Mark \"" + state.taskName + "\" as done";
        document.getElementById("complete-actions").style.display = "block";
      }

      loadHistory();
      return;
    }

    localStorage.setItem(STORAGE_KEY, JSON.stringify(state));
  }

  renderTimerUI(state);
}

// Renders the timer card from a state object. When paused, "now" is pinned
// to the moment the pause happened, so the display stays frozen instead of
// still counting down.
function renderTimerUI(state) {
  const isFocus = state.phase === "focus";
  const referenceNow = state.paused ? new Date(state.pausedAt) : new Date();
  const phaseDuration = (isFocus ? state.focusMinutes : state.breakMinutes) * 60000;
  const elapsed = referenceNow - new Date(state.phaseStartedAt);
  const remainingMs = Math.max(0, phaseDuration - elapsed);
  const minutes = Math.floor(remainingMs / 60000);
  const seconds = Math.floor((remainingMs % 60000) / 1000);
  const remainingLabel = String(minutes).padStart(2, "0") + ":" + String(seconds).padStart(2, "0");
  const fractionElapsed = Math.min(1, Math.max(0, elapsed / phaseDuration));

  document.getElementById("timer-card").className = "study-timer-card " + (isFocus ? "phase-focus" : "phase-break");
  document.getElementById("phase-label").textContent = state.paused ? "Paused" : (isFocus ? "Focus" : "Break");
  document.getElementById("timer-display").textContent = remainingLabel;
  document.getElementById("timer-subtext").textContent =
    "Session " + state.currentCycle + " of " + state.plannedSessions;
  document.getElementById("pause-btn").textContent = state.paused ? "Resume" : "Pause";

  const taskEl = document.getElementById("timer-task");
  if (state.taskName) {
    taskEl.style.display = "inline-flex";
    taskEl.innerHTML = "🎯 <span>" + state.taskName + "</span>";
  } else {
    taskEl.style.display = "none";
  }

  const interleaveEl = document.getElementById("timer-interleave");
  const currentCourseCode = state.cycleCourseCodes ? state.cycleCourseCodes[state.currentCycle - 1] : null;
  if (currentCourseCode && hasMultipleCourses(state.cycleCourseCodes)) {
    interleaveEl.style.display = "inline-flex";
    interleaveEl.innerHTML = "🔀 <span>" + currentCourseCode + "</span>";
  } else {
    interleaveEl.style.display = "none";
  }

  document.title = state.paused
    ? "Paused — StudyHive"
    : remainingLabel + " · " + (isFocus ? "Focus" : "Break") + " — StudyHive";

  document.getElementById("ring-progress").style.strokeDashoffset =
    RING_CIRCUMFERENCE * (1 - fractionElapsed);

  renderSessionDots(state);
}

function renderSessionDots(state) {
  const container = document.getElementById("session-dots");
  container.innerHTML = "";
  const primaryCourseCode = state.cycleCourseCodes ? state.cycleCourseCodes[0] : null;
  for (let i = 1; i <= state.plannedSessions; i++) {
    const dot = document.createElement("div");
    const cycleCourseCode = state.cycleCourseCodes ? state.cycleCourseCodes[i - 1] : null;
    dot.className =
      "study-dot" +
      (i < state.currentCycle || (i === state.currentCycle && state.phase === "break") ? " done" : "") +
      (i === state.currentCycle && state.phase === "focus" ? " current" : "") +
      (cycleCourseCode && cycleCourseCode !== primaryCourseCode ? " interleave" : "");
    container.appendChild(dot);
  }
}

// ---- Pause / resume ----
function togglePause() {
  let state = JSON.parse(localStorage.getItem(STORAGE_KEY));
  if (!state) return;
  state = state.paused ? resumeState(state) : pauseState(state);
  localStorage.setItem(STORAGE_KEY, JSON.stringify(state));
  runTimer();
}

// ---- Skip the current phase (jump straight to the next one) ----
function skipPhase() {
  let state = JSON.parse(localStorage.getItem(STORAGE_KEY));
  if (!state || state.paused) return;
  const phaseDuration = (state.phase === "focus" ? state.focusMinutes : state.breakMinutes) * 60000;
  state.phaseStartedAt = new Date(Date.now() - phaseDuration - 1000).toISOString();
  localStorage.setItem(STORAGE_KEY, JSON.stringify(state));
  runTimer();
}

// ---- End session early ----
function endSession() {
  const state = JSON.parse(localStorage.getItem(STORAGE_KEY));
  if (state) {
    apiPut("/api/study-sessions/" + state.sessionId + "/end", {}).then(
      function () {
        localStorage.removeItem(STORAGE_KEY);
        clearInterval(tickInterval);
        location.reload();
      },
    );
  }
}

// ---- Mark the focused-on task as done, right from the completion screen ----
// Only touches status — editTask leaves any field sent as null/undefined
// untouched, so name/type/dueDate/priority/courses are preserved as-is.
function markTaskDone() {
  if (!currentCompletedTask) return;
  apiPut("/api/tasks/" + currentCompletedTask.id, { newStatus: "DONE" }).then(function (task) {
    if (task) {
      document.getElementById("mark-task-done-btn").textContent = "✓ Marked done";
      document.getElementById("mark-task-done-btn").disabled = true;
      document.getElementById("mark-task-done-btn").style.opacity = "0.7";
      document.getElementById("mark-task-done-btn").style.cursor = "default";
    }
  });
}

// ---- Session history ----
function getStartOfWeek() {
  const now = new Date();
  const day = now.getDay(); // 0 = Sun, 1 = Mon, ... 6 = Sat
  const diffToMonday = day === 0 ? 6 : day - 1;
  const monday = new Date(now.getFullYear(), now.getMonth(), now.getDate() - diffToMonday);
  monday.setHours(0, 0, 0, 0);
  return monday;
}

function loadHistory() {
  apiGet("/api/study-sessions/history").then(function (sessions) {
    const completed = (sessions || []).filter(function (s) {
      return s.endedAt;
    });

    const totalFocusMinutes = completed.reduce(function (sum, s) {
      return sum + s.completedSessions * s.focusMinutes;
    }, 0);

    const startOfWeek = getStartOfWeek();
    const sessionsThisWeek = completed
      .filter(function (s) {
        return new Date(s.startedAt) >= startOfWeek;
      })
      .reduce(function (sum, s) {
        return sum + s.completedSessions;
      }, 0);

    document.getElementById("history-total-focus").textContent = formatDuration(totalFocusMinutes);
    document.getElementById("history-sessions-completed").textContent = sessionsThisWeek;

    renderHistoryList(completed);

    sessionHistory = completed;
    checkInterleaveSuggestion();
  });
}

function formatDuration(totalMinutes) {
  const hours = Math.floor(totalMinutes / 60);
  const minutes = totalMinutes % 60;
  return hours === 0 ? minutes + "m" : hours + "h " + minutes + "m";
}

function renderHistoryList(sessions) {
  const container = document.getElementById("history-list");
  container.innerHTML = "";

  if (sessions.length === 0) {
    container.innerHTML =
      '<p class="study-history-empty">No sessions yet — your history will show up here.</p>';
    return;
  }

  sessions
    .slice()
    .sort(function (a, b) {
      return new Date(b.startedAt) - new Date(a.startedAt);
    })
    .slice(0, 8)
    .forEach(function (s) {
      const dateLabel = new Date(s.startedAt).toLocaleDateString(undefined, {
        month: "short",
        day: "numeric",
      });
      const minutes = s.completedSessions * s.focusMinutes;

      const row = document.createElement("div");
      row.className = "study-history-row";
      row.innerHTML =
        "<div>" +
        '<div class="study-history-mode">' + (s.mode === "CLASSIC" ? "Classic" : "Flowtime") + "</div>" +
        '<div class="study-history-date">' + dateLabel + (s.taskName ? " · " + s.taskName : "") + "</div>" +
        "</div>" +
        '<div class="study-history-meta">' +
        "<div>" + s.completedSessions + "/" + s.plannedSessions + " sessions</div>" +
        '<div class="study-history-mins">' + minutes + " min</div>" +
        "</div>";
      container.appendChild(row);
    });
}

loadHistory();
