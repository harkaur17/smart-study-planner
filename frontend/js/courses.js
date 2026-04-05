let courses = [
  { name: "Advanced OOP", code: "EECS2030" },
  { name: "Computer Organization", code: "EECS2021" },
];

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
                    <button class="btn-edit">Edit</button>
                    <button class="btn-delete" onClick="deleteCourse('${course.code}')">
                        Delete
                    </button>
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

renderCourses();
