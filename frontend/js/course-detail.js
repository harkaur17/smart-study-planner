requireAuth();
loadSidebarUser();

let editTaskId = null;

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
    const isDone = task.taskStatus === "DONE";
    const isInProgress = task.taskStatus === "IN_PROGRESS";

    const toggleBg = isDone ? "#E1F5EE" : isInProgress ? "#FAEEDA" : "#E8DDD0";
    const dotBg = isDone ? "#1D9E75" : isInProgress ? "#D2A050" : "#8B7355";
    const dotAlign = isDone || isInProgress ? "flex-end" : "flex-start";
    const dotContent = isDone
      ? '<span style="color:#fff; font-size:10px;">✓</span>'
      : "";
    const statusLabel = isDone ? "DONE" : isInProgress ? "IN PROGRESS" : "TODO";
    const statusColor = isDone
      ? "#0F6E56"
      : isInProgress
        ? "#854F0B"
        : "#8B7355";

    const priorityBg =
      task.priority === "HIGH"
        ? "#FCEBEB"
        : task.priority === "MEDIUM"
          ? "#FAEEDA"
          : "#EAF3DE";
    const priorityColor =
      task.priority === "HIGH"
        ? "#791F1F"
        : task.priority === "MEDIUM"
          ? "#633806"
          : "#27500A";

    list.innerHTML += `
            <div style="background:#fff; border-radius:12px; border:0.5px solid #E8DDD0; padding:16px 20px; margin-bottom:8px; display:flex; align-items:center; gap:16px;">
                <div onclick="cycleStatus(${task.id})" style="display:flex; flex-direction:column; align-items:center; gap:4px; cursor:pointer; flex-shrink:0;">
                    <div style="width:36px; height:22px; border-radius:20px; background:${toggleBg}; display:flex; align-items:center; justify-content:${dotAlign}; padding:2px;">
                        <div style="width:18px; height:18px; border-radius:50%; background:${dotBg}; display:flex; align-items:center; justify-content:center;">
                            ${dotContent}
                        </div>
                    </div>
                    <span style="font-size:10px; color:${statusColor}; font-weight:500;">${statusLabel}</span>
                </div>
                <div style="flex:1;">
                    <p style="font-size:14px; font-weight:500; color:${isDone ? "#8B7355" : "#1C1410"}; margin:0; ${isDone ? "text-decoration:line-through;" : ""}">${task.taskName}</p>
                    <p style="font-size:12px; color:#8B7355; margin:3px 0 0;">${task.taskType || ""} ${task.dueDate ? "· Due " + task.dueDate : ""}</p>
                </div>
                <span style="background:${priorityBg}; color:${priorityColor}; font-size:11px; padding:3px 8px; border-radius:4px; flex-shrink:0;">${task.priority}</span>
                <button class="btn-edit" onclick="openEditTask(${task.id})">Edit</button>
                <button class="btn-delete" onclick="deleteTask(${task.id})">Delete</button>
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

  if (editTaskId === null) {
    apiPost("/api/tasks", {
      name: name,
      type: type,
      dueDate: dueDate,
      status: status,
      priority: priority,
      courseCodes: [currentCourse.code],
    }).then(function (data) {
      courseTasks.push(data);
      document.getElementById("task-modal").style.display = "none";
      document.getElementById("add-task-form").reset();
      renderCourseTasks();
      updateProgress();
    });
  } else {
    apiPut("/api/tasks/" + editTaskId, {
      newName: name,
      newType: type,
      newDueDate: dueDate,
      newStatus: status,
      newPriority: priority,
      newCourseCodes: [currentCourse.code],
    }).then(function (data) {
      courseTasks = courseTasks.map(function (t) {
        return t.id === editTaskId ? data : t;
      });
      editTaskId = null;
      document.getElementById("task-modal").style.display = "none";
      document.getElementById("add-task-form").reset();
      renderCourseTasks();
      updateProgress();
    });
  }
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

function markDone(taskId) {
  const task = courseTasks.find(function (t) {
    return t.id === taskId;
  });
  apiPut("/api/tasks/" + taskId, {
    newName: task.taskName,
    newType: task.taskType,
    newDueDate: task.dueDate,
    newStatus: "DONE",
    newPriority: task.priority,
    newCourseCodes: [currentCourse.code],
  }).then(function (data) {
    courseTasks = courseTasks.map(function (t) {
      return t.id === taskId ? data : t;
    });
    renderCourseTasks();
    updateProgress();
  });
}

function markTodo(taskId) {
  const task = courseTasks.find(function (t) {
    return t.id === taskId;
  });
  apiPut("/api/tasks/" + taskId, {
    newName: task.taskName,
    newType: task.taskType,
    newDueDate: task.dueDate,
    newStatus: "TODO",
    newPriority: task.priority,
    newCourseCodes: [currentCourse.code],
  }).then(function (data) {
    courseTasks = courseTasks.map(function (t) {
      return t.id === taskId ? data : t;
    });
    renderCourseTasks();
    updateProgress();
  });
}

function openEditTask(taskId) {
  const task = courseTasks.find(function (t) {
    return t.id === taskId;
  });
  document.getElementById("task-name").value = task.taskName;
  document.getElementById("task-type").value = task.taskType || "";
  document.getElementById("due-date").value = task.dueDate || "";
  document.getElementById("task-status").value = task.taskStatus;
  document.getElementById("task-priority").value = task.priority;
  editTaskId = taskId;
  document.getElementById("task-modal").style.display = "flex";
}
function toggleDone(taskId) {
  const task = courseTasks.find(function (t) {
    return t.id === taskId;
  });
  const newStatus = task.taskStatus === "DONE" ? "TODO" : "DONE";
  apiPut("/api/tasks/" + taskId, {
    newName: task.taskName,
    newType: task.taskType,
    newDueDate: task.dueDate,
    newStatus: newStatus,
    newPriority: task.priority,
    newCourseCodes: [currentCourse.code],
  }).then(function (data) {
    courseTasks = courseTasks.map(function (t) {
      return t.id === taskId ? data : t;
    });
    renderCourseTasks();
    updateProgress();
  });
}

function cycleStatus(taskId) {
  const task = courseTasks.find(function (t) {
    return t.id === taskId;
  });
  const nextStatus =
    task.taskStatus === "TODO"
      ? "IN_PROGRESS"
      : task.taskStatus === "IN_PROGRESS"
        ? "DONE"
        : "TODO";
  apiPut("/api/tasks/" + taskId, {
    newName: task.taskName,
    newType: task.taskType,
    newDueDate: task.dueDate,
    newStatus: nextStatus,
    newPriority: task.priority,
    newCourseCodes: [currentCourse.code],
  }).then(function (data) {
    courseTasks = courseTasks.map(function (t) {
      return t.id === taskId ? data : t;
    });
    renderCourseTasks();
    updateProgress();
  });
}
