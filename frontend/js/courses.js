requireAuth();
loadSidebarUser();
let courses = [];

//Get modal elements
const modal = document.querySelector(".modal");
const addCourseBtn = document.querySelector(".header-card button");
const cancelBtn = document.getElementById("cancel-course");
const saveBtn = document.getElementById("save-course");
let editCourseId = null;

//open modal
addCourseBtn.addEventListener("click", function () {
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

//save course
saveBtn.addEventListener("click", function () {
  //read values from form fields
  const name = document.getElementById("course-name").value;
  const code = document.getElementById("course-code").value;
  const color = document.getElementById("course-color").value;
  const semester = document.getElementById("course-semester").value;
  const yearValue = document.getElementById("course-year").value;
  const year = yearValue ? Number(yearValue) : null;

  // Validate
  if (name.trim() === "") {
    alert("Course name is required!");
    return;
  }
  if (code.trim() === "") {
    alert("Course code is required!");
    return;
  }

  //create new course object
  const newCourse = {
    name: name,
    code: code,
    color: color,
    semester: semester,
    year: year,
  };

  //Add course
  if (editCourseId === null) {
    apiPost("/api/courses", newCourse).then(function (data) {
      courses.push(data);
      modal.style.display = "none";
      renderCourses();
      document.getElementById("add-course-form").reset();
    });
    return;
  } else {
    apiPut("/api/courses/" + editCourseId, {
      newName: name,
      newCode: code,
      newColor: color,
      newSemester: semester,
      newYear: year,
    }).then(function (data) {
      courses = courses.map(function (course) {
        if (course.id === editCourseId) {
          return {
            id: editCourseId,
            name: name,
            code: code,
            color: color,
            semester: semester,
            year: year,
          };
        }
        return course;
      });
      editCourseId = null;
      modal.style.display = "none";
      renderCourses();
      document.getElementById("add-course-form").reset();
    });
    return;
  }
});

function renderCourses() {
  const courseList = document.getElementById("course-list");
  courseList.innerHTML = "";

  courses.forEach(function (course) {
    const color = course.color || "#1D9E75";
    const lightColor = color + "22"; // transparent version for background

    courseList.innerHTML += `
      <div class="course-card-new" onclick="window.location.href='course-detail.html?id=${course.id}'">
        <div class="course-card-banner" style="background-color: ${color};">
          <span class="course-card-code">${course.code}</span>
        </div>
        <div class="course-card-body">
          <h3 class="course-card-name">${course.name}</h3>
          <p class="course-card-meta">${course.semester ? course.semester + " " + (course.year || "") : "No semester set"}</p>
          <div class="course-card-footer">
            <button class="btn-edit" onclick="event.stopPropagation(); openEditCourse(${course.id})">Edit</button>
            <button class="btn-delete" onclick="event.stopPropagation(); deleteCourse(${course.id})">Delete</button>
          </div>
        </div>
      </div>
    `;
  });
}

function deleteCourse(courseId) {
  apiDelete("/api/courses/" + courseId).then(function () {
    courses = courses.filter(function (course) {
      return course.id !== courseId;
    });
    renderCourses();
  });
}

function openEditCourse(courseId) {
  const course = courses.find(function (c) {
    return c.id === courseId;
  });
  //pre-fill all form fields
  document.getElementById("course-name").value = course.name;
  document.getElementById("course-code").value = course.code;
  document.getElementById("course-color").value = course.color || "#6B4C3B";
  document.getElementById("course-semester").value = course.semester || "";
  document.getElementById("course-year").value = course.year || "";
  editCourseId = courseId;
  modal.style.display = "flex";
}

apiGet("/api/courses").then(function (data) {
  courses = data;
  renderCourses();
});
