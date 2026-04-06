const courses = [
    { name: "Advanced OOP", code: "EECS2030" },
    { name: "Computer Organization", code: "EECS2021" }
];

const tasks = [
    { name: "Assignment 1", course: "EECS2030", status: "TODO" },
    { name: "Midterm", course: "EECS2021", status: "IN_PROGRESS" },
    { name: "Lab Report", course: "EECS2030", status: "DONE" }
];

// Update stat cards
document.getElementById("total-courses").textContent = courses.length;
document.getElementById("total-tasks").textContent = tasks.length;