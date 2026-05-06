//track the month
let currentDate = new Date();

document.addEventListener("DOMContentLoaded", async () => {
  requireAuth();
  loadSidebarUser();

  const tasks = await loadTasks();
  renderCalendar(tasks);

  document.getElementById("prevMonth").addEventListener("click", () => {
    currentDate.setMonth(currentDate.getMonth() - 1);
    renderCalendar(tasks);
  });

  document.getElementById("nextMonth").addEventListener("click", () => {
    currentDate.setMonth(currentDate.getMonth() + 1);
    renderCalendar(tasks);
  });
});

//fetch tasks from API
async function loadTasks() {
  const data = await apiGet("/api/tasks");
  return data;
}

//render the calendar
function renderCalendar(tasks) {
  const year = currentDate.getFullYear();
  const month = currentDate.getMonth();

  //update the title
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

  //grid shape
  const firstDayOfMonth = new Date(year, month, 1).getDay(); //0-6
  const daysInMonth = new Date(year, month + 1, 0).getDate(); //28-31
  const today = new Date();

  let html = "";
  
  //empty cells before day 1
  for (let i = 0; i < firstDayOfMonth; i++) {
    html += `<div class="calendar-cell other-month"></div>`;
  }

  //one cell per day
  for (let day = 1; day <= daysInMonth; day++) {
    const isToday =
      day === today.getDate() &&
      month === today.getMonth() &&
      year === today.getFullYear();

    //build the date string eg. "2026-05-06"
    const mm = String(month + 1).padStart(2, "0");
    const dd = String(day).padStart(2, "0");
    const dateStr = `${year}-${mm}-${dd}`;

    //find tasks due on this day
    const dayTasks = tasks.filter((task) => task.dueDate === dateStr);

    //build task chips
    let chipsHtml = "";
    for (const task of dayTasks) {
      const priorityClass =
        task.priority === "HIGH"
          ? "chip-high"
          : task.priority === "MEDIUM"
            ? "chip-med"
            : "chip-low";

      const statusClass =
        task.status === "DONE"
          ? "chip-done"
          : task.status === "IN_PROGRESS"
            ? "chip-progress"
            : "chip-todo";
      chipsHtml += `<div class="task-chip ${priorityClass} ${statusClass}">${task.taskName}</div>`;
    }

    html += `
    <div class="calendar-cell ${isToday ? 'today' : ''}">
        <div class="cell-day-num ${isToday ? 'today-num' : ''}">${day}</div>
        ${chipsHtml}
    </div>
`;
  }
  document.getElementById("calendarGrid").innerHTML = html;
}
