requireAuth();

// fetch and display stats
apiGet('/api/courses').then(function(data) {
    document.getElementById('total-courses').textContent = data.length;
});

apiGet('/api/tasks').then(function(data) {
    document.getElementById('total-tasks').textContent = data.length;

    // count upcoming tasks (due within 7 days)
    const today = new Date();
    const nextWeek = new Date();
    nextWeek.setDate(today.getDate() + 7);

    const upcoming = data.filter(function(task) {
        if (!task.dueDate) return false;
        const due = new Date(task.dueDate);
        return due >= today && due <= nextWeek;
    });

    document.getElementById('upcoming-tasks').textContent = upcoming.length;
});