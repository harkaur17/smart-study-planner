requireAuth();
loadSidebarUser();

// get course id from URL
const urlParams = new URLSearchParams(window.location.search);
const courseId = urlParams.get("id");

if (!courseId) {
  window.location.href = "courses.html";
}

let courseTasks = [];
let currentCourse = null;

// load course details
apiGet("/api/courses/" + courseId).then(function (course) {
  currentCourse = course;

  // set banner color
  document.getElementById("course-banner").style.backgroundColor =
    course.color || "#6B4C3B";
  document.getElementById("course-code").textContent = course.code;
  document.getElementById("course-name").textContent = course.name;

  const meta = course.semester
    ? course.semester + " " + (course.year || "")
    : "No semester set";
  document.getElementById("course-meta").textContent = meta;
});

// load tasks for this course
apiGet("/api/courses/" + courseId + "/tasks").then(function (data) {
  courseTasks = data;
  renderCourseTasks();
  updateProgress();
});

function updateProgress() {
  const total = courseTasks.length;
  const done = courseTasks.filter(function (t) {
    return t.taskStatus === "DONE";
  }).length;
  const percent = total === 0 ? 0 : Math.round((done / total) * 100);

  document.getElementById("progress-text").textContent =
    done + "/" + total + " tasks done";
  document.getElementById("progress-bar").style.width = percent + "%";
}

function renderCourseTasks() {
  const list = document.getElementById("course-task-list");
  list.innerHTML = "";

  if (courseTasks.length === 0) {
    list.innerHTML =
      '<p style="color:#8B7355; font-size:14px;">No tasks yet. Add your first task!</p>';
    return;
  }

  courseTasks.forEach(function (task) {
    list.innerHTML += `
            <div class="task-card" style="margin-bottom:12px;">
                <div class="card-info">
                    <h3>${task.taskName}</h3>
                    <p>${task.taskType || ""}</p>
                    <p>${task.dueDate || "No due date"}</p>
                </div>
                <div class="card-right">
                    <span class="priority-badge priority-${task.priority.toLowerCase()}">${task.priority}</span>
                    <span style="font-size:11px; color:#8B7355; margin-top:4px;">${task.taskStatus}</span>
                    <div class="button-group">
                        <button class="btn-delete" onclick="deleteTask(${task.id})">Delete</button>
                    </div>
                </div>
            </div>
        `;
  });
}

// Add task modal
function openAddTask() {
  document.getElementById("task-modal").style.display = "flex";
}

document.getElementById("cancel-task").addEventListener("click", function () {
  document.getElementById("task-modal").style.display = "none";
  document.getElementById("add-task-form").reset();
});

document.getElementById("task-modal").addEventListener("click", function (e) {
  if (e.target === document.getElementById("task-modal")) {
    document.getElementById("task-modal").style.display = "none";
  }
});

document.getElementById("save-task").addEventListener("click", function () {
  const name = document.getElementById("task-name").value;
  const type = document.getElementById("task-type").value;
  const dueDate = document.getElementById("due-date").value;
  const status = document.getElementById("task-status").value;
  const priority = document.getElementById("task-priority").value;

  if (name.trim() === "") {
    alert("Task name is required!");
    return;
  }

  apiPost("/api/tasks", {
    name: name,
    type: type,
    dueDate: dueDate,
    status: status,
    priority: priority,
    courseCodes: [currentCourse.code], // auto-link to this course!
  }).then(function (data) {
    courseTasks.push(data);
    document.getElementById("task-modal").style.display = "none";
    document.getElementById("add-task-form").reset();
    renderCourseTasks();
    updateProgress();
  });
});

function deleteTask(taskId) {
  apiDelete("/api/tasks/" + taskId).then(function () {
    courseTasks = courseTasks.filter(function (t) {
      return t.id !== taskId;
    });
    renderCourseTasks();
    updateProgress();
  });
}
