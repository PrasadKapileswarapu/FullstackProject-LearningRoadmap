# 🗺️ Learning Roadmap Generator — Backend

A Spring Boot REST API that generates personalized learning roadmaps based on a user's target job role and current skills. Users register and log in, and every protected request is secured with **Spring Security + JWT**.

---

## 🏗️ Architecture

```
React Frontend (localhost:3000)
        ↕ HTTP (JSON + Authorization: Bearer <token>)
Spring Boot Backend (localhost:8080)
        ↕ JPA/Hibernate
MySQL Database
```

### Layer Pattern (MVC + Service Layer)
```
Request → JwtAuthenticationFilter → Controller → Service → Repository → Database
          (checks the JWT)          (HTTP)       (Logic)    (SQL)
```

### Project Structure
```
com.roadmap
├── config       SecurityConfig, ApplicationConfig
├── security     JwtService, JwtAuthenticationFilter, CustomUserDetailsService, RestSecurityExceptionHandler
├── controller   AuthController, UserController, RoadmapController, SkillController
├── service      AuthService, RoadmapService, CatalogService, SkillMatcher
├── repository   RoleRepository, SkillRepository, UserRepository, UserRoadmapRepository
├── model        AppUser, Role, Skill, UserRoadmap
├── dto          Request/response objects
└── exception    Custom exceptions, GlobalExceptionHandler, ApiError
```

### How JWT Authentication Works
1. User calls `POST /api/auth/register` or `/api/auth/login` and receives a signed token.
2. The frontend sends `Authorization: Bearer <token>` with every later request.
3. `JwtAuthenticationFilter` checks the token's signature and expiry, then loads the user.
4. Protected endpoints know who is calling from the token. A missing, invalid or expired token returns `401`.

---

## 📦 Tech Stack

| Technology       | Purpose                                  |
|------------------|------------------------------------------|
| Java 17          | Programming language                     |
| Spring Boot 3    | Backend framework                        |
| Spring Security  | Authentication, password hashing, CORS   |
| JWT (jjwt)       | Creating and verifying tokens            |
| Spring Data JPA  | Database ORM (no manual SQL)             |
| Hibernate        | JPA implementation                       |
| MySQL 8          | Relational database                      |
| Bean Validation  | Request validation                       |
| Lombok           | Reduces boilerplate code                 |
| JUnit 5, Mockito | Unit and integration tests               |
| Maven            | Build tool & dependency mgmt             |

---

## ⚙️ Setup Instructions

### Prerequisites
- Java 17 installed
- MySQL 8 running locally
- Maven installed

### Step 1: Create the secrets file
Copy `.env.example` to `.env` (this file is gitignored, so your secrets never reach GitHub):
```bash
cp .env.example .env
```
Then edit `.env`:
```properties
DB_PASSWORD=YOUR_MYSQL_PASSWORD
JWT_SECRET=a-long-random-string-of-at-least-32-characters
```
Generate a good secret with `openssl rand -base64 48`.

You can also set `DB_PASSWORD` and `JWT_SECRET` as normal environment variables (for example in your IDE's run configuration) instead of using `.env`.

Optional settings: `DB_USERNAME` (default `root`), `DB_URL`, `JWT_EXPIRATION_MS` (default 24 hours), `CORS_ALLOWED_ORIGINS` (default `http://localhost:3000`).

### Step 2: Run the Application
```bash
mvn spring-boot:run
```

✅ App starts on **http://localhost:8080**
✅ Database `learning_roadmap` and tables are auto-created by Hibernate
✅ Seed data (roles and skills) loaded from data.sql

### Step 3: Run the Tests (optional)
```bash
mvn verify
```
Tests use an in-memory H2 database, so MySQL is not needed for them.

---

## 📡 API Endpoints

Errors always return JSON in this shape:
```json
{
  "timestamp": "2026-11-02T06:30:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Role not found with ID: 99",
  "path": "/api/roadmap/generate"
}
```
Validation errors (`400`) also include `"fieldErrors": { "email": "Please provide a valid email address" }`.

### 1. Register
```
POST /api/auth/register
Content-Type: application/json

Body:
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "Password123"
}

Response: 201 Created
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresInMs": 86400000,
  "name": "John Doe",
  "email": "john@example.com"
}
```
Errors: `400` invalid input, `409` email already registered.

### 2. Login
```
POST /api/auth/login
Content-Type: application/json

Body:
{
  "email": "john@example.com",
  "password": "Password123"
}

Response: 200 OK → same shape as register
```
Errors: `401` wrong email or password.

### 3. Get All Roles (public)
```
GET /api/roles
Response: 200 OK → List of all job roles
```
Also public: `GET /api/roles/{id}` and `GET /api/roles/category/{category}`.

### 4. Get Skills by Role (public)
```
GET /api/skills/role/{roleId}
Response: 200 OK → List of skills for that role (by priority)
```

### 5. Generate Roadmap 🔒
```
POST /api/roadmap/generate
Authorization: Bearer <token>
Content-Type: application/json

Body:
{
  "targetRoleId": 1,
  "currentSkills": ["Java", "HTML", "CSS", "MySQL"]
}

Response: 201 Created
{
  "roadmapId": 1,
  "userName": "John Doe",
  "targetRole": "Full Stack Java Developer",
  "completionPercentage": 23,
  "totalHoursToLearn": 298,
  "matchedSkills": [...],
  "skillsToLearn": [...]
}
```
The user's name and email come from the token, not from the request body. Errors: `401` no/invalid token, `404` role not found.

### 6. Get My History 🔒
```
GET /api/roadmap/history
Authorization: Bearer <token>
Response: 200 OK → List of the logged-in user's past roadmaps
```

### 7. Get My Profile 🔒
```
GET /api/users/me
Authorization: Bearer <token>
Response: 200 OK → { "id": 1, "name": "John Doe", "email": "john@example.com" }
```

🔒 = requires a valid JWT.

> Skill matching ignores case and extra spaces. A combined skill like `HTML & CSS` counts as known if you list the full name or both `HTML` and `CSS`.

---

## 🗄️ Database Schema

```
app_users
  - id (PK)
  - full_name
  - email (UNIQUE)
  - password (BCrypt hash)
  - created_at

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

## 🔒 Security Notes

- Passwords are stored as BCrypt hashes and are never returned by any endpoint.
- Login gives the same error for an unknown email and a wrong password.
- The DB password and JWT secret come from environment variables. The app won't start if the secret is shorter than 32 characters.
- CSRF protection is off on purpose: the API is stateless and uses Bearer tokens instead of cookies.
- CORS only allows the origins listed in `CORS_ALLOWED_ORIGINS`.

**Known limitations:** tokens can't be revoked before they expire, there are no refresh tokens, there is no rate limiting on login, and all users share a single `USER` role.

---

## 💡 Interview Talking Points

1. **How does your JWT authentication work?** → A filter reads the Bearer token, verifies its signature and expiry, loads the user and sets the SecurityContext. No session is kept on the server, so it is stateless.
2. **Why disable CSRF?** → CSRF attacks depend on browsers sending cookies automatically. We send the token in a header ourselves, so the attack doesn't apply.
3. **How are passwords stored?** → BCrypt hashes with a built-in salt, never plain text.
4. **Why take the user from the token, not a request parameter?** → The old history endpoint took `?email=`, so anyone could read anyone's data. Using the token fixes that.
5. **Why DTOs?** → Separation of concerns, don't expose DB entities (or password hashes), control data shape.
6. **What is @Transactional?** → Ensures DB operations either all succeed or all fail (atomicity). Roadmap generation uses it.
7. **How do you handle errors?** → `@RestControllerAdvice` maps exceptions to status codes (404, 400, 409...). Security errors happen before controllers run, so a custom `AuthenticationEntryPoint` returns the same JSON format.
8. **What is CORS?** → Browser security policy; we configure it to allow only our React app to call the API.
9. **Why JpaRepository?** → CRUD for free, type-safe queries, no SQL boilerplate.
10. **How did you test it?** → Unit tests (JUnit, Mockito) for the matching logic, JWT service and roadmap service, plus MockMvc integration tests through the real security filter.
