let courses = [
  { name: "Advanced OOP", code: "EECS2030" },
  { name: "Computer Organization", code: "EECS2021" },
];

//Get modal elements
const modal = document.querySelector(".modal");
const addCourseBtn = document.querySelector(".header-card button");
const cancelBtn = document.getElementById("cancel-course");
const saveBtn = document.getElementById("save-course");
let editCourseCode = null;

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

  //Add to course array
  if(editCourseCode === null){
  courses.push(newCourse);
  } else {
    courses = courses.map(function(course){
      if(course.code === editCourseCode){
        return newCourse; //replace with updated course
      }
      return course; //keep everything else
    });
    editCourseCode = null;
  }
  modal.style.display = "none";
  renderCourses();

  document.getElementById("add-course-form").reset();
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
                    <button class="btn-edit" onclick="event.stopPropagation(); openEditCourse('${course.code}')">Edit</button>
                    <button class="btn-delete" onclick="event.stopPropagation(); deleteCourse('${course.code}')">Delete</button>
                  </div>
                </div>
            </div>
        `;
  });
}

function deleteCourse(courseCode) {
  courses = courses.filter(function (course) {
    return course.code !== courseCode;
  });
  renderCourses();
}

function openEditCourse(courseCode) {
  const course = courses.find(function (c) {
    return c.code === courseCode;
  });
  //pre fill all form fields
  document.getElementById("course-name").value = course.name;
  document.getElementById("course-code").value = course.code;

  editCourseCode = courseCode;
  modal.style.display = "flex";
}

renderCourses();
