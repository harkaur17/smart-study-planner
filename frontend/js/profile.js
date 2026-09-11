let currentBuddyTab = "following";
let buddySearchTimeout = null;
let isPrivateAccount = false;

function renderPrivateToggle() {
  document.getElementById("private-toggle-dot").style.left = isPrivateAccount ? "18px" : "2px";
  document.getElementById("private-toggle").style.background = isPrivateAccount ? "#D2A050" : "#E8DDD0";
}

function togglePrivate() {
  isPrivateAccount = !isPrivateAccount;
  renderPrivateToggle();
}

function buddyInitials(name) {
  return name
    .split(" ")
    .map((n) => n[0])
    .join("")
    .toUpperCase();
}

function buddyRowHTML(buddy, actionsHTML) {
  return `
    <div class="buddy-row">
      <a class="buddy-identity" href="buddy-profile.html?id=${buddy.id}">
        <div class="buddy-avatar">${buddyInitials(buddy.name)}</div>
        <div>
          <div class="buddy-name">${buddy.name}</div>
          <div class="buddy-username">@${buddy.username}</div>
        </div>
      </a>
      <div class="buddy-actions">${actionsHTML}</div>
    </div>
  `;
}

function followActionHTML(buddy) {
  if (buddy.relationshipStatus === "ACCEPTED" || buddy.relationshipStatus === "FOLLOWING") {
    return `<button class="buddy-btn" onclick="unfollowBuddy(${buddy.id})">Following ✕</button>`;
  }
  if (buddy.relationshipStatus === "PENDING") {
    return `<button class="buddy-btn pending" onclick="unfollowBuddy(${buddy.id})">Requested ✕</button>`;
  }
  return `<button class="buddy-btn follow" onclick="followBuddy(${buddy.id})">Follow</button>`;
}

async function followBuddy(userId) {
  await apiPost("/api/study-buddies/" + userId, {});
  runBuddySearch();
  loadBuddyCounts();
}

async function unfollowBuddy(userId) {
  await apiDelete("/api/study-buddies/" + userId);
  runBuddySearch();
  loadBuddyTab(currentBuddyTab);
  loadBuddyCounts();
}

async function acceptRequest(connectionId) {
  await apiPut("/api/study-buddies/requests/" + connectionId + "/accept", {});
  loadBuddyTab(currentBuddyTab);
  loadBuddyCounts();
}

async function declineRequest(connectionId) {
  await apiDelete("/api/study-buddies/requests/" + connectionId);
  loadBuddyTab(currentBuddyTab);
  loadBuddyCounts();
}

function switchBuddyTab(tab) {
  currentBuddyTab = tab;
  document.querySelectorAll(".buddy-tab").forEach((el) => el.classList.remove("active"));
  document.getElementById("tab-" + tab).classList.add("active");
  loadBuddyTab(tab);
}

async function loadBuddyTab(tab) {
  const endpoint =
    tab === "following"
      ? "/api/study-buddies/following"
      : tab === "followers"
        ? "/api/study-buddies/followers"
        : "/api/study-buddies/requests";

  const list = await apiGet(endpoint);
  const container = document.getElementById("buddy-list");

  if (!list || list.length === 0) {
    const emptyText =
      tab === "following"
        ? "Not following anyone yet."
        : tab === "followers"
          ? "No followers yet."
          : "No pending requests.";
    container.innerHTML = `<div class="buddy-empty">${emptyText}</div>`;
    return;
  }

  container.innerHTML = list
    .map((b) => {
      if (tab === "requests") {
        return buddyRowHTML(
          b,
          `<button class="buddy-btn accept" onclick="acceptRequest(${b.connectionId})">Accept</button>
           <button class="buddy-btn decline" onclick="declineRequest(${b.connectionId})">Decline</button>`,
        );
      }
      return buddyRowHTML(b, followActionHTML(b));
    })
    .join("");
}

async function loadBuddyCounts() {
  const [following, followers, requests] = await Promise.all([
    apiGet("/api/study-buddies/following"),
    apiGet("/api/study-buddies/followers"),
    apiGet("/api/study-buddies/requests"),
  ]);
  document.getElementById("following-count").textContent = (following || []).length;
  document.getElementById("followers-count").textContent = (followers || []).length;
  document.getElementById("requests-count").textContent = (requests || []).length;
}

async function runBuddySearch() {
  const query = document.getElementById("buddy-search-input").value.trim();
  const results = document.getElementById("buddy-search-results");

  if (!query) {
    results.innerHTML = "";
    return;
  }

  const matches = await apiGet("/api/study-buddies/search?q=" + encodeURIComponent(query));
  if (!matches || matches.length === 0) {
    results.innerHTML = '<div class="buddy-empty">No users found.</div>';
    return;
  }

  results.innerHTML = matches.map((b) => buddyRowHTML(b, followActionHTML(b))).join("");
}

document.addEventListener("DOMContentLoaded", async () => {
  requireAuth();
  loadSidebarUser();

  const user = await apiGet("/api/user/me");
  const courses = await apiGet("/api/courses");
  const tasks = await apiGet("/api/tasks");
  const badges = await apiGet("/api/user/badges");

  // fill profile card
  const initials = user.name
    .split(" ")
    .map((n) => n[0])
    .join("")
    .toUpperCase();
  document.getElementById("profile-avatar").textContent = initials;
  document.getElementById("profile-name").textContent = user.name;
  document.getElementById("profile-username").textContent = "@" + user.username;
  document.getElementById("profile-email").textContent = user.email;
  document.getElementById("profile-streak").textContent = user.streakCount || 0;

  // fill stats
  document.getElementById("stat-courses").textContent = courses.length;
  document.getElementById("stat-tasks").textContent = tasks.length;
  document.getElementById("stat-done").textContent = tasks.filter(
    (t) => t.taskStatus === "DONE",
  ).length;

  // XP and level
  const levels = [
    { name: "Freshman", min: 0, max: 100 },
    { name: "Sophomore", min: 100, max: 300 },
    { name: "Junior", min: 300, max: 600 },
    { name: "Senior", min: 600, max: 1000 },
    { name: "Graduate", min: 1000, max: Infinity },
  ];

  const xp = user.xpTotal || 0;
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

  // badge definitions
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

  // pre-fill form
  document.getElementById("edit-school").value = user.school || "";
  document.getElementById("edit-program").value = user.program || "";
  document.getElementById("edit-year").value = user.yearLevel || "";
  document.getElementById("edit-username").value = user.username || "";
  isPrivateAccount = !user.isPublic;
  renderPrivateToggle();

  // save button
  document
    .getElementById("save-profile-btn")
    .addEventListener("click", async () => {
      const btn = document.getElementById("save-profile-btn");
      btn.textContent = "Saving...";
      btn.disabled = true;

      await apiPut("/api/user/me", {
        school: document.getElementById("edit-school").value,
        program: document.getElementById("edit-program").value,
        yearLevel: document.getElementById("edit-year").value,
        username: document.getElementById("edit-username").value,
        isPublic: !isPrivateAccount,
      });

      btn.textContent = "Saved!";
      setTimeout(() => {
        btn.textContent = "Save changes";
        btn.disabled = false;
      }, 2000);

      loadSidebarUser();
    });

  // study buddies
  loadBuddyTab(currentBuddyTab);
  loadBuddyCounts();

  document.getElementById("buddy-search-input").addEventListener("input", () => {
    clearTimeout(buddySearchTimeout);
    buddySearchTimeout = setTimeout(runBuddySearch, 300);
  });
});
