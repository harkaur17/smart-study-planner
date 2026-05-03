requireAuth();

apiGet('/api/courses').then(function(data) {
    document.getElementById('total-courses').textContent = data.length;
});

apiGet('/api/tasks').then(function(data) {
    document.getElementById('total-tasks').textContent = data.length;

    const today = new Date();
    const nextWeek = new Date();
    nextWeek.setDate(today.getDate() + 7);

    // filter upcoming tasks
    const upcoming = data.filter(function(task) {
        if (!task.dueDate) return false;
        const due = new Date(task.dueDate);
        return due >= today && due <= nextWeek;
    });

    // update stat number
    document.getElementById('upcoming-tasks').textContent = upcoming.length;

    // render upcoming tasks in the right panel
    const upcomingList = document.querySelector('.upcoming-tasks');
    upcomingList.innerHTML = '';

    if (upcoming.length === 0) {
        upcomingList.innerHTML = '<p style="color:#6B5E4C; font-size:14px;">No upcoming tasks this week!</p>';
        return;
    }

    upcoming.forEach(function(task) {
        const courses = task.courses && task.courses.length > 0
            ? task.courses.map(function(c) { return c.code; }).join(', ')
            : 'No course';

        upcomingList.innerHTML += `
            <div class="task-card">
                <div class="card-info">
                    <h3>${task.taskName}</h3>
                    <p>${courses}</p>
                    <p>${task.dueDate}</p>
                </div>
                <div class="card-right">
                    <span class="priority-badge priority-${task.priority.toLowerCase()}">${task.priority}</span>
                </div>
            </div>
        `;
    });
});