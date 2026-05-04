requireAuth();
loadSidebarUser();
let tasks = [];
let selectedCourses = []; //array of {code, name} objects
let allCourses = [];

//Get modal elements
const modal = document.querySelector(".modal");
const addTaskBtn = document.querySelector(".header-card button");
const cancelBtn = document.getElementById("cancel-task");
const saveBtn = document.getElementById("save-task");
const detailModal = document.getElementById("detail-modal");
let editTaskId = null;

//open modal
addTaskBtn.addEventListener("click", function () {
  modal.style.display = "flex";
});

//close modal - cancel button
cancelBtn.addEventListener("click", function () {
  modal.style.display = "none";
  selectedCourses = [];
  renderChips();
});

//class modal - clicking outside
modal.addEventListener("click", function (event) {
  if (event.target === modal) {
    modal.style.display = "none";
    selectedCourses = [];
    renderChips();
  }
});

//save task
saveBtn.addEventListener("click", function () {
  //Read values from form fields
  const name = document.getElementById("task-name").value;
  const type = document.getElementById("task-type").value;
  const dueDate = document.getElementById("due-date").value;
  const status = document.getElementById("task-status").value;
  const priority = document.getElementById("task-priority").value;

  // Validate
  if (name.trim() === "") {
    alert("Task name is required!");
    return;
  }

  //Create new task object
  const newTask = {
    name: name,
    type: type,
    courseCodes: selectedCourses.map(function (c) {
      return c.code;
    }),
    status: status,
    priority: priority,
    dueDate: dueDate,
  };

  //Add to tasks array
  if (editTaskId === null) {
    apiPost("/api/tasks", newTask).then(function (data) {
      tasks.push(data);
      modal.style.display = "none";
      renderTasks();
      document.getElementById("add-task-form").reset();
    });
    return;
  } else {
    console.log("Editing task id:", editTaskId); //TEMPORARY
    apiPut("/api/tasks/" + editTaskId, {
      newName: name,
      newType: type,
      newDueDate: dueDate,
      newStatus: status,
      newPriority: priority,
      newCourseCodes: selectedCourses.map(function (c) {
        return c.code;
      }),
    }).then(function (data) {
      tasks = tasks.map(function (task) {
        if (task.id === editTaskId) {
          return data; // use server response directly!
        }
        return task;
      });
      editTaskId = null;
      selectedCourses = [];
      renderChips();
      modal.style.display = "none";
      renderTasks();
      document.getElementById("add-task-form").reset();
    });
    return;
  }
});

document
  .getElementById("course-dropdown-btn")
  .addEventListener("click", function () {
    const dropdown = document.getElementById("course-dropdown");
    if (dropdown.style.display === "none") {
      dropdown.style.display = "block";
    } else {
      dropdown.style.display = "none";
    }
  });

function renderChips() {
  const chipsDiv = document.getElementById("selected-chips");
  chipsDiv.innerHTML = "";
  selectedCourses.forEach(function (course) {
    chipsDiv.innerHTML += `
    <span class = "chip">
      ${course.code}
      <span class = "chip-remove" onclick="removeChip('${course.code}')">×</span>
    </span>
    `;
  });
}

function removeChip(code) {
  selectedCourses = selectedCourses.filter(function (c) {
    return c.code !== code;
  });
  renderChips();
  updateDropDownOptions();
}

function updateDropDownOptions() {
  const dropdown = document.getElementById("course-dropdown");
  dropdown.innerHTML = "";
  allCourses.forEach(function (course) {
    const isSelected = selectedCourses.find(function (c) {
      return c.code === course.code;
    });
    dropdown.innerHTML += `
            <div class="chip-option ${isSelected ? "selected" : ""}" 
                 onclick="toggleCourse('${course.code}', '${course.name}')">
                ${isSelected ? "✓ " : ""}${course.code} - ${course.name}
            </div>
        `;
  });
}

function toggleCourse(code, name) {
  const exists = selectedCourses.find(function (c) {
    return c.code === code;
  });
  if (exists) {
    removeChip(code);
  } else {
    selectedCourses.push({ code: code, name: name });
    renderChips();
    updateDropDownOptions();
  }
}

function renderTasks() {
  const taskList = document.getElementById("task-list");
  taskList.innerHTML = ""; //clear the first list

  tasks.forEach(function (task) {
    taskList.innerHTML += `
        <div class="task-card" onclick="openTaskDetail(${task.id})">
            <div class="card-info">
                <h3>${task.taskName}</h3>
                <p>${
                  task.courses && task.courses.length > 0
                    ? task.courses
                        .map(function (c) {
                          return c.code;
                        })
                        .join(", ")
                    : "No course"
                }</p> 
                <p>${task.dueDate || "No due date"}</p>
            </div>
            <div class="card-right">
                <span class="priority-badge priority-${task.priority.toLowerCase()}">${task.priority}</span>
                <div class="button-group">
                    <button class="btn-edit" onclick="event.stopPropagation(); openEditTask(${task.id})">Edit</button>
                    <button class="btn-delete" onclick="event.stopPropagation(); deleteTask(${task.id})">Delete</button>
                </div>
            </div>
        </div>
    `;
  });
}

function deleteTask(taskId) {
  apiDelete("/api/tasks/" + taskId).then(function () {
    tasks = tasks.filter(function (task) {
      return task.id !== taskId;
    });
    renderTasks();
  });
}

function openEditTask(taskId) {
  const task = tasks.find(function (t) {
    return t.id === taskId;
  });
  //pre fill all form fields
  document.getElementById("task-name").value = task.taskName;
  document.getElementById("task-type").value = task.taskType;
  selectedCourses = task.courses
    ? task.courses.map(function (c) {
        return { code: c.code, name: c.name };
      })
    : [];
  renderChips();
  updateDropDownOptions();
  document.getElementById("due-date").value = task.dueDate;
  document.getElementById("task-status").value = task.taskStatus;
  document.getElementById("task-priority").value = task.priority;

  editTaskId = taskId;
  modal.style.display = "flex";
}

function openTaskDetail(taskId) {
  const task = tasks.find(function (t) {
    return t.id === taskId;
  });
  document.getElementById("detail-name").textContent = task.taskName;
  document.getElementById("detail-type").textContent = task.taskType;
  document.getElementById("detail-course").textContent =
    task.courses && task.courses.length > 0
      ? task.courses
          .map(function (c) {
            return c.code;
          })
          .join(", ")
      : "None";
  document.getElementById("detail-dueDate").textContent =
    task.dueDate || "No due date";
  document.getElementById("detail-status").textContent = task.taskStatus;
  document.getElementById("detail-priority").textContent = task.priority;
  detailModal.style.display = "flex";
}

document.getElementById("close-detail").addEventListener("click", function () {
  detailModal.style.display = "none";
});

detailModal.addEventListener("click", function (event) {
  if (event.target === detailModal) {
    detailModal.style.display = "none";
  }
});

apiGet("/api/tasks").then(function (data) {
  tasks = data;
  renderTasks();
});

apiGet("/api/courses").then(function (data) {
  allCourses = data;
  updateDropDownOptions();
});