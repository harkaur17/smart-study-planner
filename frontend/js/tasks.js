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

function deleteTask(taskName){
    tasks=tasks.filter(function(task){
        return task.name !== taskName;
    });
    renderTasks();
}

renderTasks();
