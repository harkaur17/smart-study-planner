# StudyHive 🐝

A full-stack academic productivity app built for university students. StudyHive helps you manage courses, track tasks, monitor grades, maintain study streaks, and earn achievements — all in one warm, focused space.

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

### Calendar
- Monthly calendar view with tasks rendered by due date
- Color coded chips by priority and status
- Click a task chip to view/edit/delete inline
- Hover a day to add a task with that date pre-filled

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
- Grade tracker with weighted average and target grade calculator
- Leaderboard within friend circles
- Pomodoro timer with XP rewards
- Social feed and study groups
- AI study schedule generator

---

## Notes
- JWT tokens expire after 24 hours — re-login to refresh
- All endpoints except `/api/auth/**` require `Authorization: Bearer <token>`
