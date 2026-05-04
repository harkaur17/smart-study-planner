# StudyHive 🐝

A full-stack social study planner built for students. StudyHive helps you manage your courses, track tasks, monitor progress, and (soon) connect with other students in a social academic community.

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Frontend | HTML, CSS, JavaScript (Vanilla) |
| Backend | Java 17, Spring Boot 3.2 |
| Database | PostgreSQL 16 |
| Auth | JWT (JSON Web Tokens) |
| ORM | Spring Data JPA / Hibernate |
| Security | Spring Security |

---

## Project Structure

```
smart-study-planner/
├── frontend/
│   ├── index.html              # Dashboard
│   ├── courses.html            # Courses list
│   ├── course-detail.html      # Course detail with tasks
│   ├── tasks.html              # All tasks
│   ├── login.html              # Login / Register
│   ├── css/
│   │   └── style.css           # All styles
│   └── js/
│       ├── api.js              # Fetch helpers, auth utils, sidebar loader
│       ├── auth.js             # Login / register logic
│       ├── script.js           # Dashboard logic
│       ├── courses.js          # Courses page logic
│       ├── course-detail.js    # Course detail page logic
│       └── tasks.js            # Tasks page logic
│
└── src/main/java/studyPlanner/
    ├── StudyPlannerApplication.java
    ├── auth/
    │   ├── AuthController.java
    │   └── AuthService.java
    ├── controller/
    │   ├── CourseController.java
    │   ├── TaskController.java
    │   ├── UserController.java
    │   └── ActivityController.java
    ├── dto/
    │   └── TaskDTO.java
    ├── model/
    │   ├── User.java
    │   ├── Course.java
    │   ├── Task.java
    │   └── ActivityLog.java
    ├── repository/
    │   ├── UserRepository.java
    │   ├── CourseRepository.java
    │   ├── TaskRepository.java
    │   └── ActivityLogRepository.java
    ├── security/
    │   ├── JwtUtil.java
    │   ├── JwtFilter.java
    │   └── SecurityConfig.java
    └── service/
        ├── CourseService.java
        └── TaskService.java
```

---

## Database Schema

### users
| Column | Type | Notes |
|--------|------|-------|
| id | BIGINT | PK, auto |
| name | VARCHAR | required |
| username | VARCHAR | unique |
| email | VARCHAR | unique |
| password | VARCHAR | bcrypt hashed |
| profile_picture | VARCHAR | URL |
| school | VARCHAR | |
| program | VARCHAR | |
| year_level | VARCHAR | |
| streak_count | INT | default 0 |
| last_active_date | DATE | for streak tracking |
| is_public | BOOLEAN | default true |

### courses
| Column | Type | Notes |
|--------|------|-------|
| id | BIGINT | PK, auto |
| name | VARCHAR | required |
| code | VARCHAR | required |
| semester | VARCHAR | Fall/Winter/Summer |
| year | INT | e.g. 2026 |
| is_completed | BOOLEAN | default false |
| color | VARCHAR | hex color for banner |
| user_id | BIGINT | FK → users |

### tasks
| Column | Type | Notes |
|--------|------|-------|
| id | BIGINT | PK, auto |
| task_name | VARCHAR | required |
| task_type | VARCHAR | Assignment/Quiz etc |
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
| id | BIGINT | PK, auto |
| user_id | BIGINT | FK → users |
| action_type | ENUM | TASK_COMPLETED, TASK_ADDED, COURSE_ADDED, etc |
| description | VARCHAR | human readable |
| xp_earned | INT | for gamification |
| created_at | TIMESTAMP | auto |

---

## API Endpoints

### Auth (public)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/auth/register | Register new user |
| POST | /api/auth/login | Login, returns JWT |

### Courses (requires JWT)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/courses | Get all courses for current user |
| POST | /api/courses | Add a course |
| PUT | /api/courses/{id} | Edit a course |
| DELETE | /api/courses/{id} | Delete a course |
| GET | /api/courses/{id} | Get single course |
| GET | /api/courses/{id}/tasks | Get all tasks for a course |

### Tasks (requires JWT)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/tasks | Get all tasks for current user |
| POST | /api/tasks | Add a task |
| PUT | /api/tasks/{id} | Edit a task |
| DELETE | /api/tasks/{id} | Delete a task |

### User (requires JWT)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/user/me | Get current user profile |

### Activity (requires JWT)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/activity/recent | Get last 10 activity logs |

---

## Setup & Running

### Prerequisites
- Java 17
- Maven
- PostgreSQL 16
- VS Code with Live Server extension

### Database Setup
```sql
CREATE DATABASE study_planner;
```

### application.properties (NOT committed — create manually)
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/study_planner
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
jwt.secret=YOUR_JWT_SECRET
```

### Run Backend
```bash
mvn clean spring-boot:run
```
Backend runs on: `http://127.0.0.1:8080`

### Run Frontend
Open `frontend/index.html` with Live Server.
Frontend runs on: `http://127.0.0.1:5500`

---

## Completed Features

- [x] User registration and login with JWT authentication
- [x] Protected routes — redirect to login if not authenticated
- [x] Sidebar navigation (dark espresso + warm cream theme)
- [x] Dashboard with real stats (courses, tasks, upcoming)
- [x] Recent activity feed on dashboard
- [x] Courses page — add, edit, delete courses
- [x] Course cards with colored banners
- [x] Course detail page — view all tasks for a course
- [x] Task status toggle (TODO → IN PROGRESS → DONE) on course detail
- [x] Progress bar per course
- [x] Tasks page — add, edit, delete tasks
- [x] Link tasks to multiple courses
- [x] Activity logging (XP system foundation)
- [x] User ownership — each user sees only their own data

---

## Planned Features

### Phase 7 — Calendar View
- Monthly calendar showing tasks by due date
- Click a day to see tasks due that day
- Color coded by priority

### Phase 8 — Polish
- Login page redesign with StudyHive branding
- Profile page (edit school, program, year)
- Color picker for course banners

### Phase 9 — Gamification
- XP points for completing tasks
- Streak tracking (complete at least 1 task per day)
- Badges (7 day streak, first A grade, etc.)
- Leaderboard

### Phase 10 — Social Features
- Social feed (user chooses what to share)
- Study groups (open or invite only)
- Follow other students
- Share accomplishments

### Phase 11 — Study Tools
- Pomodoro timer
- Other study techniques
- Grade tracker
- Assignment weight calculator

---

## Design

### Color Palette
| Name | Hex | Usage |
|------|-----|-------|
| Espresso | #1C1410 | Sidebar background |
| Cream | #F9F5EE | Page background |
| Warm Brown | #6B4C3B | Course banner default, accents |
| Gold | #D2A050 | IN PROGRESS status |
| Muted | #8B7355 | Secondary text |

### Design Principles
- Warm minimal — no clutter
- Visual mockup before coding every feature
- User sees only their own data by default
- Everything private, user chooses what to share

---

## Git Branches

| Branch | Purpose |
|--------|---------|
| main | Stable, merged code |
| spring-boot | Phase 1-5 work |
| redesign | Phase 6+ (current) |

---

## Important Notes

- `application.properties` is gitignored
- JWT tokens expire after 24 hours
- All API endpoints except `/api/auth/**` require `Authorization: Bearer <token>` header
- Frontend and backend must both be running for the app to work
