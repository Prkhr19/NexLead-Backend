# Adore Dezign Backend — Revision Handbook

Living notes for concepts and patterns introduced in this project.  
Updated each time we add something new worth revising.

---

## How to Use This File

- One section per topic
- Bullet points only — quick revision, not essays
- File path references point to where the concept lives in code
- New topics are appended at the bottom under **Revision Log**

---

## Layered Architecture

- **What:** Controller → Service → Repository → Database
- **Why:** Separates HTTP, business rules, and data access (Single Responsibility)
- **In this project:** Every module follows this (e.g. `businessprofile/`, `auth/`)
- **Rule:** Controllers never contain business logic; repositories never contain business rules

---

## DTO Pattern

- **What:** Data Transfer Objects — classes used only for API request/response, not DB
- **Why:** Hide entity internals; control what clients send/receive; enable validation
- **Examples:**
  - `UpdateBusinessProfileRequestDto` — incoming PUT body
  - `BusinessProfileResponseDto` — outgoing GET/PUT response
- **Location:** `{module}/dto/`

---

## Mapper Class

- **What:** Dedicated class that converts Entity ↔ DTO
- **Why:** Keeps mapping out of controllers and services; reusable and testable
- **Example:** `BusinessProfileMapper` — `toResponseDto()`, `updateEntity()`, `toDefaultEntity()`
- **Location:** `{module}/mapper/`
- **Alternative:** MapStruct (auto-generates mappers) — we use manual mappers for clarity while learning

---

## Constructor Injection

- **What:** Dependencies injected via `final` fields + `@RequiredArgsConstructor` (Lombok)
- **Why:** Immutable dependencies, easy testing, Spring-recommended over field injection
- **Example:** `BusinessProfileServiceImpl` receives `BusinessProfileRepository` and `BusinessProfileMapper`

---

## Singleton Entity Pattern (BusinessProfile)

- **What:** Exactly one row in DB for the whole business (Adore Dezign)
- **Why:** Single-company platform — no multi-tenant Company CRUD
- **How:**
  - Fixed ID: `BusinessProfile.SINGLETON_ID = 1L`
  - Table: `business_profile`
  - API: `GET/PUT /api/v1/business-profile` only (no list/create/delete)
  - Service auto-seeds default profile on first access

---

## ApiResponse & ErrorResponse

- **What:** Standard wrapper for all API responses (per ProjectGuide)
- **Success shape:** `{ success, message, timestamp, data }`
- **Error shape:** `{ success, message, timestamp, status, errors }`
- **Factory:** `ApiResponse.success(message, data)` for consistent success responses
- **Location:** `common/response/`

---

## Global Exception Handler

- **What:** `@RestControllerAdvice` class that catches exceptions globally
- **Why:** One place for error formatting — controllers stay clean
- **Handles:** `ResourceNotFoundException` (404), validation errors (400), generic (500)
- **Location:** `common/exception/GlobalExceptionHandler.java`

---

## ResourceNotFoundException

- **What:** Custom runtime exception for missing resources
- **Why:** Extends `RuntimeException` — no forced try/catch; caught by global handler
- **Usage:** `throw new ResourceNotFoundException("Lead not found")`

---

## Jakarta Validation

- **What:** Annotations on DTOs to validate incoming requests (`@NotBlank`, `@Email`, `@Pattern`)
- **Why:** Validation at API boundary before service layer runs
- **Trigger:** `@Valid` on controller `@RequestBody`
- **Errors:** Caught by `MethodArgumentNotValidException` → field-level error map

---

## Spring Data JPA Repository

- **What:** Interface extending `JpaRepository<Entity, Id>` — Spring generates implementation
- **Why:** No boilerplate SQL for basic CRUD
- **Example:** `BusinessProfileRepository extends JpaRepository<BusinessProfile, Long>`
- **Custom queries:** Method names like `findByEmail`, `existsByEmail` (Spring derives query from name)

---

## @Transactional

- **What:** Spring wraps method in a DB transaction (commit on success, rollback on error)
- **Why:** Read-only for queries (`@Transactional(readOnly = true)`); write ops need atomicity
- **Example:** `BusinessProfileServiceImpl.updateProfile()` — save is atomic

---

## SecurityConfig (Baseline)

- **What:** Spring Security filter chain configuration
- **Why:** Controls which endpoints need authentication
- **Baseline (pre-JWT):** Stateless sessions, CSRF off, permitAll for dev
- **Location:** `security/SecurityConfig.java`

---

## OpenAPI / Swagger

- **What:** Auto-generated API documentation UI
- **URL:** `http://localhost:8080/swagger-ui.html`
- **Config:** `config/OpenApiConfig.java`
- **Why:** Test APIs without Postman during development

---

## JWT Authentication

- **What:** JSON Web Token — signed string that proves identity without server-side sessions
- **Why:** Stateless auth for REST APIs; works well with React SPA and mobile clients
- **Parts of a JWT:**
  - **Header** — algorithm (HS256)
  - **Payload** — claims (user id, email, role, expiry)
  - **Signature** — proves token wasn't tampered with (signed with secret key)
- **Flow in this project:**
  1. Client sends email + password → `POST /api/v1/auth/login`
  2. Server validates credentials → returns **access token** (short-lived) + **refresh token** (long-lived)
  3. Client sends `Authorization: Bearer <accessToken>` on protected requests
  4. `JwtAuthenticationFilter` intercepts request, validates token, sets SecurityContext
  5. When access token expires → `POST /api/v1/auth/refresh` with refresh token → new access token
  6. Logout → `POST /api/v1/auth/logout` → refresh token deleted from DB
- **Access vs Refresh token:**
  - Access: short life (~1 hour), sent on every request
  - Refresh: long life (~7 days), stored in DB, used only to get new access tokens
- **Why refresh tokens:** Short access token limits damage if stolen; refresh token can be revoked server-side
- **Alternatives:**
  - Session cookies (Spring Session + Redis) — simpler but less ideal for SPA
  - OAuth2 / Keycloak — enterprise SSO; overkill for single-company CRM
- **Location:**
  - `security/JwtService.java` — create/validate tokens
  - `security/JwtAuthenticationFilter.java` — filter chain integration
  - `auth/` module — login, register, refresh, logout

---

## Spring Security Filter Chain

- **What:** Ordered chain of filters that run before every HTTP request
- **Why:** Authentication happens before controller is reached
- **Our order:** `JwtAuthenticationFilter` → Spring Security authorization check
- **SecurityContext:** Thread-local storage of current authenticated user for the request

---

## UserDetailsService

- **What:** Spring Security interface to load user by username (email) from DB
- **Implementation:** `CustomUserDetailsService` in `security/`
- **Why:** Bridges our `User` entity to Spring Security's authentication model

---

## BCrypt Password Encoding

- **What:** One-way hash for passwords — never store plain text
- **Why:** Industry standard; salt built-in; slow by design (resists brute force)
- **Usage:** `passwordEncoder.encode(raw)` on register; `matches(raw, encoded)` on login
- **Bean:** `PasswordEncoder` in `SecurityConfig`

---

## Role-Based Access Control (RBAC)

- **What:** Users have roles that determine what they can access
- **Roles in this project:** `ADMIN`, `STAFF`
- **How:** `@PreAuthorize("hasRole('ADMIN')")` on methods (when needed)
- **Stored as:** `Role` enum on `User` entity → `GrantedAuthority` in JWT claims

---

## AdminDataInitializer (CommandLineRunner)

- **What:** Runs once on app startup to seed default data
- **Why:** Ensures an ADMIN account exists for first login without manual DB insert
- **Config:** Reads `app.admin.email`, `app.admin.password`, `app.admin.name` from properties
- **Production:** Set `ADMIN_EMAIL`, `ADMIN_PASSWORD`, `ADMIN_NAME` env vars — never hardcode secrets in Java
- **Location:** `config/AdminDataInitializer.java`

---

## Custom Auth Exceptions

- **DuplicateResourceException** — email already registered → HTTP 409
- **InvalidTokenException** — bad/expired refresh token → HTTP 401
- **BadCredentialsException** — wrong login credentials → HTTP 401 (Spring Security)
- **Location:** `common/exception/`

---

## Auth API Endpoints

| Method | Path | Auth | Purpose |
|--------|------|------|---------|
| POST | `/api/v1/auth/register` | Public | Register staff user |
| POST | `/api/v1/auth/login` | Public | Login → access + refresh tokens |
| POST | `/api/v1/auth/refresh` | Public | Get new access token |
| POST | `/api/v1/auth/logout` | Public | Revoke refresh token |

- **Protected endpoints** (e.g. `/api/v1/business-profile`) require header: `Authorization: Bearer <accessToken>`

---

## Lead Management Module

- **What:** First business module — tracks potential customers from inquiry to conversion
- **Entity:** `Lead` — name, phone, email, city, propertyType, serviceInterested, budget, projectStage, notes, status
- **Status pipeline:** NEW → CONTACTED → MEETING_SCHEDULED → SITE_VISIT → QUOTATION_SENT → NEGOTIATION → WON / LOST
- **Services enum:** Matches Adore Dezign offerings (Layout Planning, Modular Kitchen, etc.)
- **Location:** `lead/` module

---

## JPA Specification (Dynamic Queries)

- **What:** Builds database queries dynamically from optional filter parameters
- **Why:** One method handles many filter combinations without writing 10+ repository methods
- **Example:** `LeadSpecification.build(status, city, serviceInterested, search)`
- **Used with:** `JpaSpecificationExecutor<Lead>` on repository
- **Location:** `lead/specification/LeadSpecification.java`

---

## Pagination

- **What:** Split large result sets into pages (`page`, `size`, `totalElements`)
- **Why:** CRM lead lists grow fast — never return unbounded lists
- **Default:** page=0, size=20, sorted by `createdAt` DESC
- **Response wrapper:** `LeadPageResponseDto` inside standard `ApiResponse`

---

## Lead API Endpoints

| Method | Path | Auth | Purpose |
|--------|------|------|---------|
| POST | `/api/v1/leads` | Required | Create lead (status defaults to NEW) |
| GET | `/api/v1/leads/{id}` | Required | Get lead by ID |
| GET | `/api/v1/leads` | Required | List with filters: `status`, `city`, `serviceInterested`, `search`, `page`, `size` |
| PUT | `/api/v1/leads/{id}` | Required | Update lead (including status) |
| DELETE | `/api/v1/leads/{id}` | Required | Delete lead |

- **Search:** `search` matches name, phone, email, or city (partial, case-insensitive)

---

## Lead Notes

- **What:** Timestamped notes attached to a lead — tracks follow-ups, call summaries, site visit details
- **Entity:** `LeadNote` — content, lead (FK), createdBy (FK to User), timestamps
- **Nested REST:** Notes live under their parent lead (`/api/v1/leads/{leadId}/notes`)
- **Auth context:** `SecurityUtils.getCurrentUserEmail()` resolves who created the note from JWT
- **Location:** `lead/` module (entity, service, controller)

### Lead Notes API

| Method | Path | Purpose |
|--------|------|---------|
| POST | `/api/v1/leads/{leadId}/notes` | Add note |
| GET | `/api/v1/leads/{leadId}/notes` | List notes (paginated, newest first) |
| PUT | `/api/v1/leads/{leadId}/notes/{noteId}` | Update note |
| DELETE | `/api/v1/leads/{leadId}/notes/{noteId}` | Delete note |

---

## SecurityUtils

- **What:** Helper to read the currently authenticated user from Spring Security context
- **Why:** Services need the logged-in user (e.g. note author) without passing it from controller
- **Method:** `SecurityUtils.getCurrentUserEmail()` → email from JWT
- **Location:** `common/util/SecurityUtils.java`

---

## Appointment Management

- **What:** Schedule and track customer consultations / site visits
- **Entity:** `Appointment` — lead, customerName, date, time, address, assignedDesigner, status
- **Status flow:** PENDING → ACCEPTED → COMPLETED; or RESCHEDULED / CANCELLED
- **Business rule:** Booking an appointment auto-updates lead status to `MEETING_SCHEDULED`
- **Location:** `appointment/` module

### Appointment API

| Method | Path | Purpose |
|--------|------|---------|
| POST | `/api/v1/appointments` | Book appointment |
| GET | `/api/v1/appointments/{id}` | Get by ID |
| GET | `/api/v1/appointments` | List with filters: `status`, `leadId`, `assignedDesignerId`, `appointmentDate` |
| PUT | `/api/v1/appointments/{id}` | Full update |
| PATCH | `/api/v1/appointments/{id}/accept` | Accept |
| PATCH | `/api/v1/appointments/{id}/cancel` | Cancel |
| PATCH | `/api/v1/appointments/{id}/reschedule` | Reschedule date/time |
| PATCH | `/api/v1/appointments/{id}/complete` | Mark completed |

---

## BusinessRuleException

- **What:** Custom exception for invalid business state transitions
- **Example:** Accepting a cancelled appointment, completing a pending one
- **HTTP:** 400 Bad Request via `GlobalExceptionHandler`
- **Location:** `common/exception/BusinessRuleException.java`

---

## Lead Assignment

- **What:** Assign leads to staff members (`assignedTo` FK on `Lead`)
- **API:** `PATCH /api/v1/leads/{id}/assign` with `{ "assignedToId": 1 }` or `null` to unassign
- **Filters:** `GET /api/v1/leads?assignedToId=1` or `?assignedToMe=true`
- **Timeline:** Assignment/unassignment recorded as activity events

---

## Lead Activity Timeline

- **What:** Audit trail of everything that happened on a lead
- **Entity:** `LeadActivity` — type, description, performedBy, timestamp
- **Auto-recorded from:** lead create/update, notes, appointments, assignment, follow-ups
- **API:** `GET /api/v1/leads/{leadId}/timeline`
- **Location:** `lead/entity/LeadActivity.java`, `LeadTimelineController`

---

## Follow-up Tasks (CRM)

- **What:** Scheduled tasks to call/meet/visit a lead — separate from WhatsApp automation (Sprint 4)
- **Entity:** `FollowUp` — lead, assignedTo, dueDate, type, notes, completed
- **Types:** CALL, MEETING, SITE_VISIT, WHATSAPP, OTHER
- **API base:** `/api/v1/follow-ups`
- **Key filters:** `overdue=true`, `dueToday=true`, `assignedToMe=true`
- **Location:** `followup/` module

---

## CRM Dashboard Statistics (Sprint 3)

- **What:** Summary cards for CRM home screen — not full Phase 9 analytics
- **API:** `GET /api/v1/dashboard/stats`
- **Returns:** today's leads, monthly leads, leads by status, upcoming appointments, follow-ups, leads per assignee
- **Deferred:** revenue, conversion charts (Sprint 6)
- **Location:** `dashboard/` module

---

## Revision Log

| Date | Topic | Module |
|------|-------|--------|
| Sprint 1 | Layered Architecture, DTO, Mapper, ApiResponse | Foundation |
| Sprint 1 | BusinessProfile singleton | businessprofile |
| Sprint 1 | Global Exception Handler, Validation | common |
| Sprint 1 | JWT, Refresh Tokens, Filter Chain, RBAC | auth, security |
| Sprint 1 | Lead CRUD, Specification, Pagination | lead |
| Sprint 3 | Lead Notes, SecurityUtils | lead |
| Sprint 3 | Appointment Management, BusinessRuleException | appointment |
| Sprint 3 | Lead Assignment, Activity Timeline | lead |
| Sprint 3 | Follow-up Tasks | followup |
| Sprint 3 | CRM Dashboard Statistics | dashboard |
