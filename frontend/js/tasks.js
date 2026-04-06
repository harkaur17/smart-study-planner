let tasks = [
  {
    name: "Assignment 1",
    course: "EECS2030",
    status: "TODO",
    priority: "HIGH",
    dueDate: "2026-04-10",
  },
  {
    name: "Midterm",
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
  tasks.push(newTask);
  modal.style.display = "none";
  renderTasks();

  document.getElementById("add-task-form").reset();
});

function renderTasks() {
  const taskList = document.getElementById("task-list");
  taskList.innerHTML = ""; //clear the first list

  tasks.forEach(function (task) {
    taskList.innerHTML += `
        <div class="task-card">
                <div class="card-info">
                    <h3>${task.name}</h3>
                    <p>${task.course}</p>
                    <p>${task.status}</p>
                    <p>${task.dueDate}</p>
                </div>
                <div class="button-group">
                    <button class="btn-edit">Edit</button>
                    <button class="btn-delete" onClick="deleteTask('${task.name}')">
                        Delete
                    </button>
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

renderTasks();
