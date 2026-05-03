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
    }).then(function (data) {
      courses = courses.map(function (course) {
    if (course.id === editCourseId) {
        return { id: editCourseId, name: name, code: code };
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
  courseList.innerHTML = ""; //clear the first list

  courses.forEach(function (course) {
    courseList.innerHTML += `
            <div class="course-card">
                <div class="card-info">
                    <h3>${course.code}</h3>
                    <p>${course.name}</p>
                </div>
                <div class="button-group">
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
    editCourseId = courseId;
    modal.style.display = "flex";
}

apiGet("/api/courses").then(function (data) {
  courses = data;
  renderCourses();
});
