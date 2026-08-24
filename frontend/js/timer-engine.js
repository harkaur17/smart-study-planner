const STUDY_STORAGE_KEY = "studyhive_active_session";

// Advance through phases based on elapsed real time.
// Returns updated state, or null if the whole session is finished.
// Calling code is responsible for firing the /progress and /end API calls.
function advanceTimerPhases(state) {
  let phaseDuration = (state.phase === "focus" ? state.focusMinutes : state.breakMinutes) * 60000;
  let phaseEnd = new Date(state.phaseStartedAt).getTime() + phaseDuration;
  let justCompletedFocus = false;
  let justFinishedSession = false;
  let justStartedFocus = false;

  while (Date.now() >= phaseEnd) {
    if (state.phase === "focus") {
      state.completedSessions++;
      justCompletedFocus = true;

      if (state.completedSessions >= state.plannedSessions) {
        justFinishedSession = true;
        state._justCompletedFocus = justCompletedFocus;
        state._justFinishedSession = justFinishedSession;
        state._justStartedFocus = justStartedFocus;
        return state;
      }

      if (state.skipBreaks) {
        state.currentCycle++;
        state.phase = "focus";
        justStartedFocus = true;
      } else {
        state.phase = "break";
      }
    } else {
      state.currentCycle++;
      state.phase = "focus";
      justStartedFocus = true;
    }

    state.phaseStartedAt = new Date(phaseEnd).toISOString();
    phaseDuration = (state.phase === "focus" ? state.focusMinutes : state.breakMinutes) * 60000;
    phaseEnd = phaseEnd + phaseDuration;
  }

  state._justCompletedFocus = justCompletedFocus;
  state._justFinishedSession = justFinishedSession;
  state._justStartedFocus = justStartedFocus;
  return state;
}

// Synthesizes short chimes with the Web Audio API so no audio files are
// needed. "complete" plays when a focus block ends (accomplishment / break
// starting); "start" plays when a fresh focus block begins.
function playSound(type) {
  try {
    const AudioContextClass = window.AudioContext || window.webkitAudioContext;
    const ctx = new AudioContextClass();
    const now = ctx.currentTime;

    function tone(freq, startOffset, duration, peakGain) {
      const osc = ctx.createOscillator();
      const gain = ctx.createGain();
      osc.type = "triangle";
      osc.frequency.value = freq;
      gain.gain.setValueAtTime(0, now + startOffset);
      gain.gain.linearRampToValueAtTime(peakGain, now + startOffset + 0.03);
      gain.gain.exponentialRampToValueAtTime(0.0001, now + startOffset + duration);
      osc.connect(gain);
      gain.connect(ctx.destination);
      osc.start(now + startOffset);
      osc.stop(now + startOffset + duration + 0.05);
    }

    if (type === "complete") {
      // three-note ascending "ta-da", louder and longer, landing note held
      tone(523.25, 0, 0.3, 0.35); // C5
      tone(659.25, 0.15, 0.3, 0.35); // E5
      tone(783.99, 0.3, 0.55, 0.4); // G5
    } else if (type === "start") {
      tone(659.25, 0, 0.2, 0.3); // E5
      tone(987.77, 0.14, 0.45, 0.32); // B5
    }

    setTimeout(function () {
      ctx.close();
    }, 1400);
  } catch (e) {
    // Web Audio unavailable/blocked — fail silently, sound is a nice-to-have
  }
}

// Pause/resume just freeze and later shift phaseStartedAt — every other
// calculation in this file is already derived from that one timestamp.
function pauseState(state) {
  state.paused = true;
  state.pausedAt = new Date().toISOString();
  return state;
}

function resumeState(state) {
  const pausedMs = Date.now() - new Date(state.pausedAt).getTime();
  state.phaseStartedAt = new Date(new Date(state.phaseStartedAt).getTime() + pausedMs).toISOString();
  state.paused = false;
  delete state.pausedAt;
  return state;
}

function formatRemainingTime(state) {
  const phaseDuration = (state.phase === "focus" ? state.focusMinutes : state.breakMinutes) * 60000;
  const elapsed = new Date() - new Date(state.phaseStartedAt);
  const remainingMs = Math.max(0, phaseDuration - elapsed);
  const minutes = Math.floor(remainingMs / 60000);
  const seconds = Math.floor((remainingMs % 60000) / 1000);
  return String(minutes).padStart(2, "0") + ":" + String(seconds).padStart(2, "0");
}