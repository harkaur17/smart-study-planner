requireAuth();
loadSidebarUser();

apiGet('/api/courses').then(function(data) {
    document.getElementById('total-courses').textContent = data.length;
});

apiGet('/api/tasks').then(function(data) {
    document.getElementById('total-tasks').textContent = data.length;
    const today = new Date();
    const nextWeek = new Date();
    nextWeek.setDate(today.getDate() + 7);
    const upcoming = data.filter(function(task) {
        if (!task.dueDate) return false;
        const due = new Date(task.dueDate);
        return due >= today && due <= nextWeek;
    });
    document.getElementById('upcoming-tasks').textContent = upcoming.length;
    const upcomingList = document.getElementById('upcoming-list');
    upcomingList.innerHTML = '';
    if (upcoming.length === 0) {
        upcomingList.innerHTML = '<p style="color:#6B5E4C; font-size:14px;">No upcoming tasks this week!</p>';
    } else {
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
    }
});

apiGet('/api/activity/recent').then(function(data) {
    const activityList = document.getElementById('activity-list');
    activityList.innerHTML = '';
    if (!data || data.length === 0) {
        activityList.innerHTML = '<p style="color:#6B5E4C; font-size:14px;">No recent activity!</p>';
        return;
    }
    data.forEach(function(activity) {
        activityList.innerHTML += `
            <div style="padding:10px 0; border-bottom:0.5px solid #EAE0D5;">
                <p style="font-size:13px; color:#1C1410; margin:0;">${activity.description}</p>
                <p style="font-size:11px; color:#6B5E4C; margin:4px 0 0;">${activity.xpEarned ? '+' + activity.xpEarned + ' XP' : ''}</p>
            </div>
        `;
    });
});