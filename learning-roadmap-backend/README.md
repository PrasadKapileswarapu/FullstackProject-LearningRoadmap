# 🗺️ Learning Roadmap Generator — Backend

A Spring Boot REST API that generates personalized learning roadmaps based on a user's target job role and current skills.

---

## 🏗️ Architecture

```
React Frontend (localhost:3000)
        ↕ HTTP (JSON)
Spring Boot Backend (localhost:8080)
        ↕ JPA/Hibernate
MySQL Database
```

### Layer Pattern (MVC + Service Layer)
```
Controller  →  Service  →  Repository  →  Database
(HTTP)         (Logic)      (SQL)
```

---

## 📦 Tech Stack

| Technology     | Purpose                        |
|----------------|--------------------------------|
| Java 17        | Programming language           |
| Spring Boot 3  | Backend framework              |
| Spring Data JPA| Database ORM (no manual SQL)   |
| Hibernate      | JPA implementation             |
| MySQL 8        | Relational database            |
| Lombok         | Reduces boilerplate code       |
| Maven          | Build tool & dependency mgmt   |

---

## ⚙️ Setup Instructions

### Prerequisites
- Java 17 installed
- MySQL 8 running locally
- Maven installed (or use `./mvnw`)

### Step 1: Create MySQL Database
```sql
CREATE DATABASE roadmap_db;
```

### Step 2: Update application.properties
```properties
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
```

### Step 3: Run the Application
```bash
mvn spring-boot:run
```

✅ App starts on **http://localhost:8080**
✅ Database tables auto-created by Hibernate
✅ Seed data loaded from data.sql

---

## 📡 API Endpoints

### 1. Generate Roadmap
```
POST /api/roadmap/generate
Content-Type: application/json

Body:
{
  "userName": "John Doe",
  "userEmail": "john@example.com",
  "targetRoleId": 1,
  "currentSkills": ["Java", "HTML", "CSS", "MySQL"]
}

Response: 201 Created
{
  "roadmapId": 1,
  "userName": "John Doe",
  "targetRole": "Full Stack Java Developer",
  "completionPercentage": 30,
  "totalHoursToLearn": 195,
  "matchedSkills": [...],
  "skillsToLearn": [...]
}
```

### 2. Get All Roles
```
GET /api/roles
Response: 200 OK → List of all job roles
```

### 3. Get Skills by Role
```
GET /api/skills/role/{roleId}
Response: 200 OK → List of skills for that role
```

### 4. Get User History
```
GET /api/roadmap/history?email=john@example.com
Response: 200 OK → List of past roadmaps
```

---

## 🗄️ Database Schema

```
roles
  - id (PK)
  - name (UNIQUE)
  - category
  - description

skills
  - id (PK)
  - name
  - category
  - priority (1=Must, 2=Should, 3=Nice)
  - description
  - estimated_hours
  - resource_url
  - role_id (FK → roles.id)

user_roadmaps
  - id (PK)
  - user_name
  - user_email
  - target_role
  - current_skills (comma-separated)
  - skills_to_learn (comma-separated)
  - total_estimated_hours
  - completion_percentage
  - created_at
```

---

## 💡 Interview Talking Points

1. **Why Spring Boot?** → Convention over configuration, embedded server, auto-configuration
2. **Why DTOs?** → Separation of concerns, don't expose DB entities directly, control data shape
3. **Why JpaRepository?** → CRUD for free, type-safe queries, no SQL boilerplate
4. **What is CORS?** → Browser security policy; we configure it to allow React to call our API
5. **What is @Transactional?** → Ensures DB operations either all succeed or all fail (atomicity)
6. **Why Lombok?** → Reduces 70%+ boilerplate (getters/setters/constructors)
7. **Priority system?** → Skills ranked 1-3 so users tackle critical skills first
