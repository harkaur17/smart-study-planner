# StudyHive 🐝

A full-stack academic productivity app built for university students. StudyHive helps you manage courses, track tasks, monitor grades, run focused study sessions, maintain study streaks, and earn achievements — all in one warm, focused space.

> Built with Java Spring Boot, PostgreSQL, and Vanilla JavaScript. No frameworks.

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Frontend | HTML, CSS, Vanilla JavaScript |
| Backend | Java 17, Spring Boot 3.2 |
| Database | PostgreSQL 16 |
| Auth | JWT (JSON Web Tokens) |
| ORM | Spring Data JPA / Hibernate |
| Security | Spring Security |

---

## Features

### Core
- JWT authentication with Spring Security — register, login, protected routes
- User ownership enforced at service layer — every user sees only their own data
- Full CRUD for courses, tasks, and user profiles
- Activity logging with XP tracking on every action

### Courses
- Course cards with custom colored banners (color picker)
- Course detail page with task list and progress bar
- Task status toggle (TODO → IN PROGRESS → DONE)
- Link tasks to multiple courses

### Tasks
- Add, edit, delete tasks with priority, status, due date, and course links
- Filter and view all tasks across courses

### Grade Tracker
- Weighted grade components per course — name, weight, and grade
- Current grade divides only by *graded* weight, so an ungraded final doesn't artificially deflate your grade mid-semester
- Optional expected/projected grade per component — a personal guess, shown distinct from confirmed grades, factored only into a separate "Projected grade" card, never the real "Current grade"
- Target grade calculator — reverse weighted-average math tells you what average you need on remaining components to hit a goal
- Inline-editable table rows — click to edit in place, no modal

### Calendar
- Monthly calendar view with tasks rendered by due date
- Color coded chips by priority and status
- Click a task chip to view/edit/delete inline
- Hover a day to add a task with that date pre-filled

### Study Sessions (Pomodoro)
- Classic (25 min focus / 5 min break) and Flowtime (52/17) modes, configurable session count and skip-breaks toggle
- Animated progress ring and session-progress dots on the running timer; Pause/Resume and Skip-phase controls
- Optional task linking — attach a specific task to a session; starting it flips a `TODO` task to `IN_PROGRESS` automatically, and finishing the session offers a one-click "mark done"
- Multi-course sessions — select more than one course and split the session count across them, or accept an automatic "mix in [course]" suggestion after a few consecutive same-course sessions
- A floating countdown widget follows you to every other page while a session is active
- Session history with total focus time and sessions-completed-this-week stats
- Start/completion chimes synthesized with the Web Audio API — no audio files shipped

### Streak Tracking
- Complete at least one task per day to maintain a streak
- Server-side recalculation handles undo operations correctly
- Streak resets when a task is un-done and no other completed tasks exist for that day
- Live streak counter in the sidebar across all pages

### Gamification
- XP system — earn XP for adding courses (5 XP), adding tasks (5 XP), completing tasks (10 XP)
- Level progression — Freshman → Sophomore → Junior → Senior → Graduate
- 14 badge types across categories: getting started, consistency, volume, XP milestones, and comeback
- Badges earned once and kept permanently
- XP bar and badge grid displayed on profile page

### Profile
- View stats — total courses, tasks, completed count
- Edit academic info — school, program, year level, username
- Streak badge and XP level displayed on profile

### Login Page
- Two-panel layout with StudyHive branding
- Doodle background pattern on the right panel

---

## Database Schema

### users
| Column | Type | Notes |
|--------|------|-------|
| id | BIGINT | PK |
| name | VARCHAR | required |
| username | VARCHAR | unique |
| email | VARCHAR | unique |
| password | VARCHAR | bcrypt hashed |
| school | VARCHAR | |
| program | VARCHAR | |
| year_level | VARCHAR | |
| streak_count | INT | default 0 |
| last_active_date | DATE | streak tracking |
| xp_total | INT | default 0 |
| is_public | BOOLEAN | default true |

### courses
| Column | Type | Notes |
|--------|------|-------|
| id | BIGINT | PK |
| name | VARCHAR | required |
| code | VARCHAR | required |
| semester | VARCHAR | |
| year | INT | |
| color | VARCHAR | hex, default #6B4C3B |
| user_id | BIGINT | FK → users |

### tasks
| Column | Type | Notes |
|--------|------|-------|
| id | BIGINT | PK |
| task_name | VARCHAR | required |
| task_type | VARCHAR | |
| due_date | DATE | |
| status | ENUM | TODO, IN_PROGRESS, DONE |
| priority | ENUM | HIGH, MEDIUM, LOW |
| user_id | BIGINT | FK → users |

### course_tasks (join table)
| Column | Type |
|--------|------|
| course_id | BIGINT |
| task_id | BIGINT |

### grade_items
| Column | Type | Notes |
|--------|------|-------|
| id | BIGINT | PK |
| course_id | BIGINT | FK → courses |
| user_id | BIGINT | FK → users |
| name | VARCHAR | required |
| weight | DOUBLE | required |
| grade | DOUBLE | nullable — ungraded until entered |
| expected_grade | DOUBLE | nullable — personal projection, separate from `grade` |

### study_sessions
| Column | Type | Notes |
|--------|------|-------|
| id | BIGINT | PK |
| user_id | BIGINT | FK → users |
| task_id | BIGINT | nullable, denormalized snapshot |
| task_name | VARCHAR | nullable, denormalized snapshot |
| mode | ENUM | CLASSIC, FLOWTIME |
| planned_sessions | INT | |
| focus_minutes | INT | |
| break_minutes | INT | |
| skip_breaks | BOOLEAN | |
| started_at | TIMESTAMP | |
| ended_at | TIMESTAMP | nullable — null while a session is active |
| completed_sessions | INT | default 0 |

### session_courses (join table)
| Column | Type |
|--------|------|
| session_id | BIGINT |
| course_id | BIGINT |

### session_course_blocks
| Column | Type | Notes |
|--------|------|-------|
| session_id | BIGINT | FK → study_sessions |
| course_id | BIGINT | denormalized |
| course_code | VARCHAR | denormalized |
| session_count | INT | how many blocks of this session are this course's |

### activity_log
| Column | Type | Notes |
|--------|------|-------|
| id | BIGINT | PK |
| user_id | BIGINT | FK |
| action_type | ENUM | TASK_COMPLETED, TASK_ADDED, etc |
| description | VARCHAR | |
| xp_earned | INT | |
| created_at | TIMESTAMP | auto |

### badges
| Column | Type | Notes |
|--------|------|-------|
| id | BIGINT | PK |
| user_id | BIGINT | FK → users |
| badge_type | ENUM | 14 badge types |
| earned_at | DATE | |

---

## API Endpoints

### Auth (public)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/auth/register | Register new user |
| POST | /api/auth/login | Login, returns JWT |

### Courses (JWT required)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/courses | Get all courses |
| POST | /api/courses | Add a course |
| PUT | /api/courses/{id} | Edit a course |
| DELETE | /api/courses/{id} | Delete a course |
| GET | /api/courses/{id} | Get single course |
| GET | /api/courses/{id}/tasks | Get tasks for a course |

### Tasks (JWT required)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/tasks | Get all tasks |
| POST | /api/tasks | Add a task |
| PUT | /api/tasks/{id} | Edit a task |
| DELETE | /api/tasks/{id} | Delete a task |

### Grades (JWT required)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/courses/{courseId}/grades | Get grade components for a course |
| POST | /api/courses/{courseId}/grades | Add a grade component |
| PUT | /api/courses/{courseId}/grades/{itemId} | Edit a grade component |
| DELETE | /api/courses/{courseId}/grades/{itemId} | Delete a grade component |

### Study Sessions (JWT required)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/study-sessions | Start a session (rejects if one's already active) |
| GET | /api/study-sessions/active | Get the current in-progress session, if any |
| PUT | /api/study-sessions/{id}/progress | Increment completed session count |
| PUT | /api/study-sessions/{id}/end | End a session |
| GET | /api/study-sessions/history | Get past sessions |

### User (JWT required)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/user/me | Get current user profile |
| PUT | /api/user/me | Update profile |
| GET | /api/user/badges | Get earned badges |

### Activity (JWT required)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/activity/recent | Get last 10 activity logs |

---

## Setup

### Prerequisites
- Java 17
- Maven
- PostgreSQL 16
- VS Code with Live Server

### Database
```sql
CREATE DATABASE study_planner;
```

### application.properties (not committed — create manually)
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/study_planner
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
jwt.secret=YOUR_JWT_SECRET
```

### Run
```bash
# Backend (port 8080)
mvn clean spring-boot:run

# Frontend — open frontend/index.html with Live Server (port 5500)
```

---

## Design System

| Name | Hex | Usage |
|------|-----|-------|
| Espresso | #1C1410 | Sidebar |
| Cream | #F9F5EE | Page background |
| Warm Brown | #6B4C3B | Default course banner |
| Gold | #D2A050 | Accent, active state, XP bar |
| Muted | #8B7355 | Secondary text |

---

## Planned
- XP rewards and activity-log entries for completed study sessions
- Weekly per-course study-time goals (e.g. "aim for 10-15 hrs/week"), tracked against `study_sessions` history
- Study buddy connections — one-way, follow-style relationships between users
- Live "studying now" status and joinable group study sessions, built on top of study buddies
- Leaderboard within friend circles
- Social feed — auto-generated accomplishment posts from streaks, levels, badges, and study sessions
- AI study schedule generator

---

## Notes
- JWT tokens expire after 24 hours — re-login to refresh
- All endpoints except `/api/auth/**` require `Authorization: Bearer <token>`
