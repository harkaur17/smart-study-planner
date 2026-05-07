let currentDate = new Date();

document.addEventListener("DOMContentLoaded", async () => {
  requireAuth();
  loadSidebarUser();

  let tasks = await loadTasks();
  renderCalendar(tasks);
  await loadCoursesDropdown();

  document.getElementById("prevMonth").addEventListener("click", () => {
    currentDate.setMonth(currentDate.getMonth() - 1);
    renderCalendar(tasks);
  });

  document.getElementById("nextMonth").addEventListener("click", () => {
    currentDate.setMonth(currentDate.getMonth() + 1);
    renderCalendar(tasks);
  });

  document.getElementById("calendarGrid").addEventListener("click", (e) => {
    if (e.target.classList.contains("task-chip")) {
      const taskId = parseInt(e.target.dataset.taskId);
      const task = tasks.find((t) => t.id === taskId);
      if (task) openTaskDetail(task);
    }
  });

  document.getElementById("calendarGrid").addEventListener("click", (e) => {
    if (e.target.classList.contains("cal-plus-btn")) {
      const date = e.target.dataset.date;
      document.getElementById("cal-task-due").value = date;
      document.getElementById("cal-task-course").value = "";
      document.getElementById("cal-save-btn").textContent = "Save Task";
      delete document.getElementById("cal-save-btn").dataset.editId;
      openModal("add-task-modal");
    }
  });

  document
    .getElementById("detail-close-btn")
    .addEventListener("click", () => closeModal("task-detail-modal"));
  document
    .getElementById("cal-cancel-btn")
    .addEventListener("click", () => closeModal("add-task-modal"));

  // ONE save listener — handles both create and edit
  document
    .getElementById("cal-save-btn")
    .addEventListener("click", async () => {
      const name = document.getElementById("cal-task-name").value;
      const type = document.getElementById("cal-task-type").value;
      const due = document.getElementById("cal-task-due").value;
      const priority = document.getElementById("cal-task-priority").value;
      const status = document.getElementById("cal-task-status").value;
      const courseCode = document.getElementById("cal-task-course").value;
      const saveBtn = document.getElementById("cal-save-btn");
      const editId = saveBtn.dataset.editId;

      if (!name || !due) {
        alert("Please enter a task name and due date.");
        return;
      }

      if (editId) {
        await apiPut("/api/tasks/" + editId, {
          newName: name,
          newType: type,
          newDueDate: due,
          newPriority: priority,
          newStatus: status,
          newCourseCodes: courseCode ? [courseCode] : [],
        });
        saveBtn.textContent = "Save Task";
        delete saveBtn.dataset.editId;
      } else {
        await apiPost("/api/tasks", {
          name: name,
          type: type,
          dueDate: due,
          priority: priority,
          status: status,
          courseCodes: courseCode ? [courseCode] : [],
        });
      }

      closeModal("add-task-modal");
      document.getElementById("cal-task-name").value = "";
      document.getElementById("cal-task-type").value = "";
      tasks = await loadTasks();
      renderCalendar(tasks);
      loadSidebarUser();
    });
});

async function loadTasks() {
  const data = await apiGet("/api/tasks");
  return data;
}

async function loadCoursesDropdown() {
  const courses = await apiGet("/api/courses");
  const select = document.getElementById("cal-task-course");
  select.innerHTML = `<option value="">No course</option>`;
  for (const course of courses) {
    select.innerHTML += `<option value="${course.code}">${course.code} - ${course.name}</option>`;
  }
}

function openModal(id) {
  document.getElementById(id).style.display = "flex";
}

function closeModal(id) {
  document.getElementById(id).style.display = "none";
}

function openTaskDetail(task) {
  document.getElementById("detail-task-name").textContent = task.taskName;
  document.getElementById("detail-task-type").textContent = task.taskType;
  document.getElementById("detail-task-due").textContent = task.dueDate;
  document.getElementById("detail-task-status").textContent = task.taskStatus;
  document.getElementById("detail-task-priority").textContent = task.priority;

  const courses = task.courses
    ? task.courses.map((c) => c.code).join(", ")
    : "None";
  document.getElementById("detail-task-course").textContent = courses;

  document.getElementById("detail-delete-btn").onclick = async () => {
    await apiDelete("/api/tasks/" + task.id);
    closeModal("task-detail-modal");
    const updatedTasks = await loadTasks();
    renderCalendar(updatedTasks);
    loadSidebarUser();
  };

  document.getElementById("detail-edit-btn").onclick = () => {
    closeModal("task-detail-modal");

    document.getElementById("cal-task-name").value = task.taskName;
    document.getElementById("cal-task-type").value = task.taskType || "";
    document.getElementById("cal-task-due").value = task.dueDate;
    document.getElementById("cal-task-priority").value = task.priority;
    document.getElementById("cal-task-status").value = task.taskStatus;

    if (task.courses && task.courses.length > 0) {
      document.getElementById("cal-task-course").value = task.courses[0].code;
    } else {
      document.getElementById("cal-task-course").value = "";
    }

    const saveBtn = document.getElementById("cal-save-btn");
    saveBtn.textContent = "Update Task";
    saveBtn.dataset.editId = task.id;

    openModal("add-task-modal");
  };

  openModal("task-detail-modal");
}

function renderCalendar(tasks) {
  const year = currentDate.getFullYear();
  const month = currentDate.getMonth();

  const monthNames = [
    "January",
    "February",
    "March",
    "April",
    "May",
    "June",
    "July",
    "August",
    "September",
    "October",
    "November",
    "December",
  ];
  document.getElementById("monthTitle").textContent =
    `${monthNames[month]} ${year}`;

  const firstDayOfMonth = new Date(year, month, 1).getDay();
  const daysInMonth = new Date(year, month + 1, 0).getDate();
  const today = new Date();

  let html = "";

  for (let i = 0; i < firstDayOfMonth; i++) {
    html += `<div class="calendar-cell other-month"></div>`;
  }

  for (let day = 1; day <= daysInMonth; day++) {
    const isToday =
      day === today.getDate() &&
      month === today.getMonth() &&
      year === today.getFullYear();

    const mm = String(month + 1).padStart(2, "0");
    const dd = String(day).padStart(2, "0");
    const dateStr = `${year}-${mm}-${dd}`;

    const dayTasks = tasks.filter((task) => task.dueDate === dateStr);

    let chipsHtml = "";
    for (const task of dayTasks) {
      const priorityClass =
        task.priority === "HIGH"
          ? "chip-high"
          : task.priority === "MEDIUM"
            ? "chip-med"
            : "chip-low";
      const statusClass =
        task.taskStatus === "DONE"
          ? "chip-done"
          : task.taskStatus === "IN_PROGRESS"
            ? "chip-progress"
            : "chip-todo";
      chipsHtml += `<div class="task-chip ${priorityClass} ${statusClass}" data-task-id="${task.id}">${task.taskName}</div>`;
    }

    html += `
      <div class="calendar-cell ${isToday ? "today" : ""}">
        <div class="cell-day-num ${isToday ? "today-num" : ""}">${day}</div>
        ${chipsHtml}
        <div class="cal-plus-btn" data-date="${dateStr}">+</div>
      </div>
    `;
  }

  document.getElementById("calendarGrid").innerHTML = html;
}