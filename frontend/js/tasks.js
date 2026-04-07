let tasks = [
  {
    name: "Assignment 1",
    type: "Assignment",
    course: "EECS2030",
    status: "TODO",
    priority: "HIGH",
    dueDate: "2026-04-10",
  },
  {
    name: "Midterm",
    type: "Test",
    course: "EECS2021",
    status: "IN_PROGRESS",
    priority: "MEDIUM",
    dueDate: "2026-04-15",
  },
];

//Get modal elements
const modal = document.querySelector(".modal");
const addTaskBtn = document.querySelector(".header-card button");
const cancelBtn = document.getElementById("cancel-task");
const saveBtn = document.getElementById("save-task");
const detailModal = document.getElementById("detail-modal");
let editTaskName = null;

//open modal
addTaskBtn.addEventListener("click", function () {
  modal.style.display = "flex";
});

//close modal - cancel button
cancelBtn.addEventListener("click", function () {
  modal.style.display = "none";
});

//class modal - clicking outside
modal.addEventListener("click", function (event) {
  if (event.target === modal) {
    modal.style.display = "none";
  }
});

//save task
saveBtn.addEventListener("click", function () {
  //Read values from form fields
  const name = document.getElementById("task-name").value;
  const type = document.getElementById("task-type").value;
  const course = document.getElementById("course-code").value;
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
    course: course,
    status: status,
    priority: priority,
    dueDate: dueDate,
  };

  //Add to tasks array
  if (editTaskName === null) {
    tasks.push(newTask);
  } else {
    tasks = tasks.map(function (task) {
      if (task.name === editTaskName) {
        return newTask; //replace with updated task
      }
      return task; //keep everything else
    });
    editTaskName = null; //reset back to add mode
  }

  modal.style.display = "none";
  renderTasks();
  document.getElementById("add-task-form").reset();
});

function renderTasks() {
  const taskList = document.getElementById("task-list");
  taskList.innerHTML = ""; //clear the first list

  tasks.forEach(function (task) {
    taskList.innerHTML += `
        <div class="task-card" onclick="openTaskDetail('${task.name}')">
                <div class="card-info">
                    <h3>${task.name}</h3>
                    <p>${task.course}</p> 
                    <p>${task.dueDate}</p>
                </div>
                <div class="card-right">
                  <span class="priority-badge priority-${task.priority.toLowerCase()}">${task.priority}</span>
                  <div class="button-group">
                    <button class="btn-edit" onclick="event.stopPropagation(); openEditTask('${task.name}')">Edit</button>
                    <button class="btn-delete" onclick="event.stopPropagation(); deleteTask('${task.name}')">Delete</button>
                  </div>
                </div>
            </div>
        `;
  });
}

function deleteTask(taskName) {
  tasks = tasks.filter(function (task) {
    return task.name !== taskName;
  });
  renderTasks();
}

function openEditTask(taskName) {
  const task = tasks.find(function (t) {
    return t.name === taskName;
  });
  //pre fill all form fields
  document.getElementById("task-name").value = task.name;
  document.getElementById("task-type").value = task.type;
  document.getElementById("course-code").value = task.course;
  document.getElementById("due-date").value = task.dueDate;
  document.getElementById("task-status").value = task.status;
  document.getElementById("task-priority").value = task.priority;

  editTaskName = taskName;
  modal.style.display = "flex";
}

function openTaskDetail(taskName) {
  const task = tasks.find(function (t) {
    return t.name === taskName;
  });
  document.getElementById("detail-name").textContent = task.name;
  document.getElementById("detail-type").textContent = task.type;
  document.getElementById("detail-course").textContent = task.course;
  document.getElementById("detail-dueDate").textContent = task.dueDate;
  document.getElementById("detail-status").textContent = task.status;
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

renderTasks();
