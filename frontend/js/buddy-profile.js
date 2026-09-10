requireAuth();
loadSidebarUser();

const urlParams = new URLSearchParams(window.location.search);
const buddyUserId = urlParams.get("id");

if (!buddyUserId) {
  window.location.href = "profile.html";
}

function buddyProfileInitials(name) {
  return name
    .split(" ")
    .map((n) => n[0])
    .join("")
    .toUpperCase();
}

function followButtonState(profile) {
  const btn = document.getElementById("follow-btn");
  btn.className = "buddy-btn";

  if (profile.relationshipStatus === "ACCEPTED") {
    btn.textContent = "Following ✕";
    btn.onclick = () => toggleFollow(profile, false);
  } else if (profile.relationshipStatus === "PENDING") {
    btn.className = "buddy-btn pending";
    btn.textContent = "Requested ✕";
    btn.onclick = () => toggleFollow(profile, false);
  } else {
    btn.className = "buddy-btn follow";
    btn.textContent = "Follow";
    btn.onclick = () => toggleFollow(profile, true);
  }
}

async function toggleFollow(profile, follow) {
  if (follow) {
    await apiPost("/api/study-buddies/" + profile.id, {});
  } else {
    await apiDelete("/api/study-buddies/" + profile.id);
  }
  loadBuddyProfile();
}

async function loadBuddyProfile() {
  const profile = await apiGet("/api/study-buddies/" + buddyUserId + "/profile");
  if (!profile) {
    window.location.href = "profile.html";
    return;
  }

  document.getElementById("profile-avatar").textContent = buddyProfileInitials(profile.name);
  document.getElementById("profile-name").textContent = profile.name;
  document.getElementById("profile-username").textContent = "@" + profile.username;
  followButtonState(profile);

  document.getElementById("locked-card").style.display = profile.visible ? "none" : "block";
  document.getElementById("xp-card").style.display = profile.visible ? "block" : "none";
  document.getElementById("badges-card").style.display = profile.visible ? "block" : "none";
  document.getElementById("profile-streak-badge").style.display = profile.visible ? "flex" : "none";

  if (!profile.visible) return;

  document.getElementById("profile-streak").textContent = profile.streakCount || 0;

  const levels = [
    { name: "Freshman", min: 0, max: 100 },
    { name: "Sophomore", min: 100, max: 300 },
    { name: "Junior", min: 300, max: 600 },
    { name: "Senior", min: 600, max: 1000 },
    { name: "Graduate", min: 1000, max: Infinity },
  ];

  const xp = profile.xpTotal || 0;
  const level =
    levels.find((l) => xp >= l.min && xp < l.max) || levels[levels.length - 1];
  const isMaxLevel = level.max === Infinity;
  const progress = isMaxLevel
    ? 100
    : ((xp - level.min) / (level.max - level.min)) * 100;

  document.getElementById("profile-level").textContent = level.name;
  document.getElementById("profile-xp").textContent = xp;
  document.getElementById("profile-xp-fill").style.width = progress + "%";
  document.getElementById("profile-xp-next").textContent = isMaxLevel
    ? "Max level reached! 🎓"
    : `${level.max - xp} XP to next level`;

  const allBadges = [
    { type: "FIRST_TASK", emoji: "🎯", name: "First Step" },
    { type: "FIRST_COURSE", emoji: "📖", name: "Scholar" },
    { type: "TASKS_5", emoji: "✅", name: "Getting Started" },
    { type: "TASKS_25", emoji: "🏆", name: "Overachiever" },
    { type: "TASKS_100", emoji: "👑", name: "Legend" },
    { type: "COURSES_3", emoji: "📚", name: "Bookworm" },
    { type: "COURSES_5", emoji: "🎓", name: "Full Load" },
    { type: "STREAK_3", emoji: "🔥", name: "On Fire" },
    { type: "STREAK_7", emoji: "⚡", name: "Unstoppable" },
    { type: "STREAK_30", emoji: "💎", name: "Legend Streak" },
    { type: "XP_100", emoji: "⭐", name: "Rising Star" },
    { type: "XP_500", emoji: "🚀", name: "High Achiever" },
    { type: "XP_1000", emoji: "🌟", name: "Elite" },
    { type: "COMEBACK", emoji: "💪", name: "Comeback Kid" },
  ];

  const badges = profile.badges || [];
  const earnedTypes = badges.map((b) => b.badgeType);
  const grid = document.getElementById("profile-badges-grid");

  grid.innerHTML = allBadges
    .map((b) => {
      const earned = earnedTypes.includes(b.type);
      const earnedBadge = badges.find((eb) => eb.badgeType === b.type);
      return `
      <div class="badge-item ${earned ? "" : "locked"}">
        <div class="badge-emoji">${b.emoji}</div>
        <div class="badge-name">${b.name}</div>
        <div class="badge-earned">${earned ? "Earned " + earnedBadge.earnedAt : "Locked"}</div>
      </div>
    `;
    })
    .join("");
}

loadBuddyProfile();
