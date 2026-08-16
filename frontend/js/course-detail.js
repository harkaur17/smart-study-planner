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

let courseGrades = [];
let editGradeId = null;
let addingNewGrade = false;

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

// load grade items for this course
apiGet("/api/courses/" + courseId + "/grades").then(function (data) {
  courseGrades = data;
  renderGrades();
});

// ---- Tab switching ----
function switchTab(tab) {
  const tasksTab = document.getElementById("tab-tasks");
  const gradesTab = document.getElementById("tab-grades");
  const tasksContent = document.getElementById("tasks-tab-content");
  const gradesContent = document.getElementById("grades-tab-content");

  const activeStyle = {
    color: "#1C1410",
    fontWeight: "500",
    border: "#D2A050",
  };
  const inactiveStyle = {
    color: "#8B7355",
    fontWeight: "400",
    border: "transparent",
  };

  function apply(tabEl, style) {
    tabEl.style.color = style.color;
    tabEl.style.fontWeight = style.fontWeight;
    tabEl.style.borderBottomColor = style.border;
  }

  if (tab === "tasks") {
    apply(tasksTab, activeStyle);
    apply(gradesTab, inactiveStyle);
    tasksContent.style.display = "block";
    gradesContent.style.display = "none";
  } else {
    apply(gradesTab, activeStyle);
    apply(tasksTab, inactiveStyle);
    gradesContent.style.display = "block";
    tasksContent.style.display = "none";
  }
}

// ---- Letter grade scale (adjust if York's differs) ----
function getLetterGrade(percent) {
  if (percent === null) return "—";
  if (percent >= 90) return "A+";
  if (percent >= 85) return "A";
  if (percent >= 80) return "A-";
  if (percent >= 75) return "B+";
  if (percent >= 70) return "B";
  if (percent >= 65) return "B-";
  if (percent >= 60) return "C+";
  if (percent >= 55) return "C";
  if (percent >= 50) return "D";
  return "F";
}

// ---- Render summary + table ----
const inputStyle =
  "border:1px solid #D2A050; border-radius:4px; padding:4px 8px; font-size:13px; width:90%; color:#1C1410; font-family:inherit;";

function renderGrades() {
  const totalWeight = courseGrades.reduce(function (sum, g) {
    return sum + g.weight;
  }, 0);
  const gradedItems = courseGrades.filter(function (g) {
    return g.grade !== null;
  });
  const gradedWeight = gradedItems.reduce(function (sum, g) {
    return sum + g.weight;
  }, 0);
  const earnedPoints = gradedItems.reduce(function (sum, g) {
    return sum + (g.weight * g.grade) / 100;
  }, 0);
  const weightRemaining = totalWeight - gradedWeight;

  const currentGrade =
    gradedWeight === 0 ? null : (earnedPoints / gradedWeight) * 100;

  document.getElementById("current-grade-num").textContent =
    currentGrade === null ? "—" : currentGrade.toFixed(1) + "%";
  document.getElementById("letter-grade-num").textContent =
    getLetterGrade(currentGrade);
  document.getElementById("weight-remaining-num").textContent =
    weightRemaining.toFixed(0) + "%";

  // ---- Projected grade: uses real grade where present, else expected ----
  const projectedItems = courseGrades.filter(function (g) {
    return (
      g.grade !== null ||
      (g.expectedGrade !== null && g.expectedGrade !== undefined)
    );
  });
  const projectedWeight = projectedItems.reduce(function (sum, g) {
    return sum + g.weight;
  }, 0);
  const projectedPoints = projectedItems.reduce(function (sum, g) {
    const effectiveGrade = g.grade !== null ? g.grade : g.expectedGrade;
    return sum + (g.weight * effectiveGrade) / 100;
  }, 0);
  const projectedGrade =
    projectedWeight === 0 ? null : (projectedPoints / projectedWeight) * 100;

  document.getElementById("projected-grade-num").textContent =
    projectedGrade === null ? "—" : "~" + projectedGrade.toFixed(1) + "%";

  const tableBody = document.getElementById("grades-table-body");
  tableBody.innerHTML = "";

  if (courseGrades.length === 0 && !addingNewGrade) {
    tableBody.innerHTML =
      '<div style="padding:16px; color:#8B7355; font-size:13px;">No grade components yet. Add your first one!</div>';
  } else {
    courseGrades.forEach(function (g) {
      if (g.id === editGradeId) {
        // ---- Editable row ----
        tableBody.innerHTML += `
            <div style="display:grid; grid-template-columns:2fr 1fr 1fr 1fr 70px; padding:8px 16px; border-bottom:0.5px solid #E8DDD0; align-items:center; background:#FAF6EF;">
            <div><input id="edit-grade-name-${g.id}" type="text" value="${g.name}" style="${inputStyle}" onkeydown="if(event.key==='Enter') saveEditGrade(${g.id});"/></div>
            <div><input id="edit-grade-weight-${g.id}" type="number" value="${g.weight}" style="${inputStyle}" onkeydown="if(event.key==='Enter') saveEditGrade(${g.id});"/></div>
            <div style="display:flex; flex-direction:column; gap:4px;">
              <input id="edit-grade-grade-${g.id}" type="number" value="${g.grade !== null ? g.grade : ""}" placeholder="Grade" style="${inputStyle}" onkeydown="if(event.key==='Enter') saveEditGrade(${g.id});"/>
              <input id="edit-grade-expected-${g.id}" type="number" value="${g.expectedGrade !== null && g.expectedGrade !== undefined ? g.expectedGrade : ""}" placeholder="Guess" style="${inputStyle} font-style:italic;" onkeydown="if(event.key==='Enter') saveEditGrade(${g.id});"/>
              </div>
            <div style="font-size:13px; color:#8B7355;">—</div>
            <div style="display:flex; gap:6px;">
              <button onclick="saveEditGrade(${g.id})" style="background:#1C1410; color:#F9F5EE; border:none; border-radius:4px; padding:4px 8px; font-size:12px; cursor:pointer;">✓</button>
              <button onclick="cancelGradeEdit()" style="background:none; border:1px solid #E8DDD0; border-radius:4px; padding:4px 8px; font-size:12px; cursor:pointer; color:#8B7355;">✕</button>
            </div>
          </div>
        `;
      } else {
        // ---- Static row ----
        let weighted, gradeDisplay;

        if (g.grade !== null) {
          weighted = ((g.weight * g.grade) / 100).toFixed(1);
          gradeDisplay = g.grade + "%";
        } else if (g.expectedGrade !== null && g.expectedGrade !== undefined) {
          weighted = ((g.weight * g.expectedGrade) / 100).toFixed(1);
          gradeDisplay =
            '<span style="font-style:italic; color:#378ADD;">' +
            g.expectedGrade +
            "%</span>";
          weighted =
            '<span style="font-style:italic; color:#378ADD;">' +
            weighted +
            "</span>";
        } else {
          weighted = "—";
          gradeDisplay =
            '<span style="color:#8B7355; font-style:italic;">Not graded</span>';
        }

        tableBody.innerHTML += `
    <div style="display:grid; grid-template-columns:2fr 1fr 1fr 1fr 70px; padding:11px 16px; border-bottom:0.5px solid #E8DDD0; align-items:center; cursor:pointer;" onclick="startEditGrade(${g.id})">
      <div style="font-size:13px; color:#1C1410;">${g.name}</div>
      <div style="font-size:13px; color:#1C1410;">${g.weight}%</div>
      <div style="font-size:13px;">${gradeDisplay}</div>
      <div style="font-size:13px; font-weight:500; color:${g.grade !== null ? "#3B6B4C" : "inherit"};">${weighted}</div>
      <div>
        <button class="btn-delete" onclick="event.stopPropagation(); deleteGrade(${g.id})">Delete</button>
      </div>
    </div>
  `;
      }
    });
  }

  // ---- Add-new row (either the trigger link or the input row) ----
  const addRow = document.getElementById("grades-add-row");
  if (addingNewGrade) {
    addRow.innerHTML = `
  <div style="display:grid; grid-template-columns:2fr 1fr 1fr 1fr 70px; padding:8px 16px; border-top:0.5px solid #E8DDD0; align-items:center; background:#FAF6EF;">
    <div><input id="new-grade-name" type="text" placeholder="e.g. Midterm 1" style="${inputStyle}" onkeydown="if(event.key==='Enter') saveNewGrade();"/></div>
    <div><input id="new-grade-weight" type="number" placeholder="—" style="${inputStyle}" onkeydown="if(event.key==='Enter') saveNewGrade();"/></div>
    <div style="display:flex; flex-direction:column; gap:4px;">
      <input id="new-grade-grade" type="number" placeholder="Grade" style="${inputStyle}" onkeydown="if(event.key==='Enter') saveNewGrade();"/>
      <input id="new-grade-expected" type="number" placeholder="Guess" style="${inputStyle} font-style:italic;" onkeydown="if(event.key==='Enter') saveNewGrade();"/>
    </div>
        <div style="font-size:13px; color:#8B7355;">—</div>
        <div style="display:flex; gap:6px;">
          <button onclick="saveNewGrade()" style="background:#1C1410; color:#F9F5EE; border:none; border-radius:4px; padding:4px 8px; font-size:12px; cursor:pointer;">✓</button>
          <button onclick="cancelGradeEdit()" style="background:none; border:1px solid #E8DDD0; border-radius:4px; padding:4px 8px; font-size:12px; cursor:pointer; color:#8B7355;">✕</button>
        </div>
      </div>
    `;
  } else {
    addRow.innerHTML = `
      <div onclick="startAddGrade()" style="display:flex; align-items:center; gap:8px; padding:10px 16px; border-top:0.5px solid #E8DDD0; cursor:pointer; color:#8B7355; font-size:12px;">
        <div style="width:20px; height:20px; border-radius:50%; border:1px solid #8B7355; display:flex; align-items:center; justify-content:center; font-size:14px;">+</div>
        Add component
      </div>
    `;
  }

  calculateTarget();
}

// ---- Target grade calculator ----
function calculateTarget() {
  const targetInput = document.getElementById("target-grade-input");
  const resultEl = document.getElementById("target-result");
  const target = parseFloat(targetInput.value);

  if (isNaN(target)) {
    resultEl.textContent = "";
    return;
  }

  const totalWeight = courseGrades.reduce(function (sum, g) {
    return sum + g.weight;
  }, 0);
  const gradedItems = courseGrades.filter(function (g) {
    return g.grade !== null;
  });
  const gradedWeight = gradedItems.reduce(function (sum, g) {
    return sum + g.weight;
  }, 0);
  const earnedPoints = gradedItems.reduce(function (sum, g) {
    return sum + (g.weight * g.grade) / 100;
  }, 0);
  const remainingWeight = totalWeight - gradedWeight;

  if (totalWeight === 0) {
    resultEl.textContent = "Add components first";
    return;
  }

  const targetPoints = (target * totalWeight) / 100;
  const neededPoints = targetPoints - earnedPoints;

  if (remainingWeight <= 0) {
    const currentGrade =
      gradedWeight === 0 ? 0 : (earnedPoints / gradedWeight) * 100;
    resultEl.textContent =
      currentGrade >= target
        ? "Target already achieved!"
        : "Not achievable — no ungraded components left";
    return;
  }

  const neededAvg = (neededPoints / remainingWeight) * 100;

  if (neededAvg <= 0) {
    resultEl.textContent = "Target already secured!";
  } else if (neededAvg > 100) {
    resultEl.textContent = "Not achievable — even 100% won't reach target";
  } else {
    resultEl.textContent =
      "Need " + neededAvg.toFixed(1) + "% on remaining components";
  }
}

document
  .getElementById("target-grade-input")
  .addEventListener("input", calculateTarget);

// ---- Inline add/edit for grade rows ----
function startAddGrade() {
  addingNewGrade = true;
  editGradeId = null;
  renderGrades();
}

function startEditGrade(gradeId) {
  editGradeId = gradeId;
  addingNewGrade = false;
  renderGrades();
}

function cancelGradeEdit() {
  addingNewGrade = false;
  editGradeId = null;
  renderGrades();
}

function saveNewGrade() {
  const name = document.getElementById("new-grade-name").value;
  const weight = parseFloat(document.getElementById("new-grade-weight").value);
  const gradeVal = document.getElementById("new-grade-grade").value;
  const grade = gradeVal === "" ? null : parseFloat(gradeVal);
  const expectedVal = document.getElementById("new-grade-expected").value;
  const expectedGrade = expectedVal === "" ? null : parseFloat(expectedVal);

  if (name.trim() === "" || isNaN(weight)) {
    alert("Component name and weight are required!");
    return;
  }

  apiPost("/api/courses/" + courseId + "/grades", {
    name: name,
    weight: weight,
    grade: grade,
    expectedGrade: expectedGrade,
  }).then(function (data) {
    courseGrades.push(data);
    addingNewGrade = false;
    renderGrades();
  });
}

function saveEditGrade(gradeId) {
  const name = document.getElementById("edit-grade-name-" + gradeId).value;
  const weight = parseFloat(
    document.getElementById("edit-grade-weight-" + gradeId).value,
  );
  const gradeVal = document.getElementById("edit-grade-grade-" + gradeId).value;
  const grade = gradeVal === "" ? null : parseFloat(gradeVal);
  const expectedVal = document.getElementById(
    "edit-grade-expected-" + gradeId,
  ).value;
  const expectedGrade = expectedVal === "" ? null : parseFloat(expectedVal);

  if (name.trim() === "" || isNaN(weight)) {
    alert("Component name and weight are required!");
    return;
  }

  apiPut("/api/courses/" + courseId + "/grades/" + gradeId, {
    name: name,
    weight: weight,
    grade: grade,
    expectedGrade: expectedGrade,
  }).then(function (data) {
    courseGrades = courseGrades.map(function (g) {
      return g.id === gradeId ? data : g;
    });
    editGradeId = null;
    renderGrades();
  });
}

function deleteGrade(gradeId) {
  apiDelete("/api/courses/" + courseId + "/grades/" + gradeId).then(
    function () {
      courseGrades = courseGrades.filter(function (g) {
        return g.id !== gradeId;
      });
      renderGrades();
    },
  );
}
