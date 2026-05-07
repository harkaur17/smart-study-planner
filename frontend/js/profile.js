document.addEventListener("DOMContentLoaded", async () => {
  requireAuth();
  loadSidebarUser();

  const user = await apiGet("/api/user/me");
  const courses = await apiGet("/api/courses");
  const tasks = await apiGet("/api/tasks");

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

  // pre-fill form
  document.getElementById("edit-school").value = user.school || "";
  document.getElementById("edit-program").value = user.program || "";
  document.getElementById("edit-year").value = user.yearLevel || "";
  document.getElementById("edit-username").value = user.username || "";

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
      });

      btn.textContent = "Saved!";
      setTimeout(() => {
        btn.textContent = "Save changes";
        btn.disabled = false;
      }, 2000);

      loadSidebarUser();
    });
});
