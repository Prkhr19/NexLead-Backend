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

- **What:** Read-only audit trail of everything that happened on a lead — who did what, when
- **Why:** CRM users need history without digging through notes, appointments, and follow-ups separately
- **Pattern:** Event log / activity feed — append-only records, never edited or deleted via API
- **Table:** `lead_activities`
- **Location:** `lead/entity/LeadActivity.java`, `LeadActivityService`, `LeadTimelineController`

### LeadActivity Entity

- **Fields:** `id`, `lead` (FK), `activityType` (enum), `description` (TEXT), `performedBy` (FK to User, nullable), `createdAt`
- **Timestamp:** `@CreationTimestamp` on `createdAt` — set automatically on insert, never updated
- **Relationship:** Many activities → one lead (`@ManyToOne` on `Lead`)

### LeadActivityType Enum

| Type | When recorded |
|------|---------------|
| `LEAD_CREATED` | New lead saved |
| `STATUS_CHANGED` | Lead status updated (e.g. NEW → CONTACTED) |
| `LEAD_ASSIGNED` | Lead assigned to a user |
| `LEAD_UNASSIGNED` | Assignee removed |
| `NOTE_ADDED` | New note created on lead |
| `APPOINTMENT_BOOKED` | Appointment created |
| `APPOINTMENT_ACCEPTED` | Appointment accepted |
| `APPOINTMENT_RESCHEDULED` | Appointment date/time changed |
| `APPOINTMENT_CANCELLED` | Appointment cancelled |
| `APPOINTMENT_COMPLETED` | Appointment marked done |
| `FOLLOW_UP_CREATED` | Follow-up task scheduled |
| `FOLLOW_UP_COMPLETED` | Follow-up marked complete |

### How Auto-Recording Works

- **Not** a background job, JPA listener, or AOP aspect — each service **explicitly calls** `LeadActivityService.recordActivity()` after the main action succeeds
- **Same transaction:** Activity is saved inside the same `@Transactional` method as the parent action (e.g. create lead + record activity commit together)
- **Core method:** `recordActivity(Lead lead, LeadActivityType type, String description, User performedBy)` → builds `LeadActivity` → `leadActivityRepository.save()`
- **Who performed it:** Resolved via `SecurityUtils.getCurrentUserEmail()` → `UserRepository`; nullable if no auth context (e.g. system actions)
- **No POST to timeline:** Clients never write activities directly — only other services do

### Which Service Records What

| Service | Triggers |
|---------|----------|
| `LeadServiceImpl` | create → `LEAD_CREATED` (+ `LEAD_ASSIGNED` if assignee set); update status → `STATUS_CHANGED`; assign → `LEAD_ASSIGNED` / `LEAD_UNASSIGNED` |
| `LeadNoteServiceImpl` | create note → `NOTE_ADDED` |
| `AppointmentServiceImpl` | book / accept / cancel / reschedule / complete → matching `APPOINTMENT_*` type |
| `FollowUpServiceImpl` | create → `FOLLOW_UP_CREATED`; complete → `FOLLOW_UP_COMPLETED` |

### What Is NOT Recorded (gaps to know for interviews)

- Lead delete — no activity logged
- Note update or delete — only create triggers `NOTE_ADDED`
- Follow-up update or delete — only create + complete
- Appointment full update (`PUT`) — only status-change endpoints log activity
- Generic lead field edits (name, phone, city) — only **status** change is tracked

### Lead Activity Timeline API

| Method | Path | Purpose |
|--------|------|---------|
| GET | `/api/v1/leads/{leadId}/timeline` | Paginated timeline, newest first |

- **Query params:** `page` (default 0), `size` (default 20)
- **Sort:** `createdAt DESC` (hardcoded in controller via `PageRequest`)
- **Auth:** JWT required (same as all protected endpoints)
- **404:** Lead ID does not exist → `ResourceNotFoundException`

### Response Shape

- **Wrapper:** `ApiResponse<LeadActivityPageResponseDto>`
- **Page fields:** `content`, `page`, `size`, `totalElements`, `totalPages`
- **Each item (`LeadActivityResponseDto`):** `id`, `leadId`, `activityType`, `description`, `performedById`, `performedByName`, `createdAt`

### Load & “Self-Update” Flow

1. Frontend opens lead detail → `GET /api/v1/leads/{leadId}/timeline`
2. User performs action (e.g. adds note) → `POST /api/v1/leads/{leadId}/notes`
3. Backend saves note **and** writes `NOTE_ADDED` row in same transaction
4. Frontend **re-fetches** timeline → new entry appears
- **No real-time push** in this backend — no WebSocket, SSE, or polling endpoint; UI must refetch or poll on its own

### Interview Talking Points

- **Why separate table instead of querying notes/appointments?** Single chronological feed; uniform shape; easy pagination; decoupled from source entities
- **Why enum for type?** Type-safe, filterable, self-documenting; avoids magic strings in DB
- **Why description as free text?** Human-readable in UI (e.g. "Status changed from NEW to CONTACTED"); enum alone is not enough
- **Cross-module recording:** `appointment/` and `followup/` depend on `LeadActivityService` in `lead/` — acceptable for shared audit concern; alternative would be Spring ApplicationEvents for loose coupling
- **Extension idea:** Add `@EventListener` or domain events so services publish `LeadStatusChangedEvent` and one listener writes activity — reduces scattered `recordActivity()` calls

### Key Files

| File | Role |
|------|------|
| `lead/entity/LeadActivity.java` | JPA entity |
| `lead/entity/LeadActivityType.java` | Activity enum |
| `lead/repository/LeadActivityRepository.java` | `findByLeadIdOrderByCreatedAtDesc` |
| `lead/service/LeadActivityService.java` | Interface: `recordActivity`, `getTimeline` |
| `lead/service/impl/LeadActivityServiceImpl.java` | Write + read logic |
| `lead/controller/LeadTimelineController.java` | GET endpoint |
| `lead/mapper/LeadActivityMapper.java` | Entity → `LeadActivityResponseDto` |
| `lead/dto/LeadActivityResponseDto.java` | Single activity response |
| `lead/dto/LeadActivityPageResponseDto.java` | Paginated wrapper |

---

## Follow-up Tasks (CRM)

- **What:** Scheduled reminders for staff to call, meet, or visit a lead — task management inside the CRM
- **Why:** Sales teams need due dates and assignees; separate from WhatsApp automation (Sprint 4)
- **Pattern:** Standalone REST resource (`/api/v1/follow-ups`), not nested under leads — but each follow-up belongs to one lead
- **Table:** `follow_ups`
- **Location:** `followup/` module (entity, dto, mapper, repository, specification, service, controller)

### FollowUp Entity

- **Fields:** `id`, `lead` (FK), `assignedTo` (FK), `createdBy` (FK), `followUpType` (enum), `dueDate` (`LocalDate`), `notes` (TEXT, optional), `completed` (boolean, default `false`), `createdAt`, `updatedAt`
- **Three user roles on one record:**
  - `assignedTo` — who must do the task
  - `createdBy` — who scheduled it (from JWT via `SecurityUtils`)
- **Timestamps:** `@CreationTimestamp` + `@UpdateTimestamp`
- **No status enum:** Only `completed` true/false — simpler than appointment status flow

### FollowUpType Enum

| Type | Typical use |
|------|-------------|
| `CALL` | Phone follow-up |
| `MEETING` | In-office or virtual meeting |
| `SITE_VISIT` | On-site consultation |
| `WHATSAPP` | Manual WhatsApp outreach (not Sprint 4 automation) |
| `OTHER` | Catch-all |

### Follow-up API

| Method | Path | Purpose |
|--------|------|---------|
| POST | `/api/v1/follow-ups` | Create follow-up |
| GET | `/api/v1/follow-ups/{id}` | Get by ID |
| GET | `/api/v1/follow-ups` | List with filters (paginated) |
| PUT | `/api/v1/follow-ups/{id}` | Update (blocked if already completed) |
| PATCH | `/api/v1/follow-ups/{id}/complete` | Mark completed |
| DELETE | `/api/v1/follow-ups/{id}` | Delete |

### Create Request Validation

- `leadId` — required
- `assignedToId` — required
- `followUpType` — required
- `dueDate` — required, `@FutureOrPresent` (cannot schedule in the past)
- `notes` — optional

### List Filters & Sort

| Query param | Behavior |
|-------------|----------|
| `leadId` | Follow-ups for one lead |
| `assignedToId` | Follow-ups for a specific user |
| `assignedToMe=true` | Overrides `assignedToId` with current JWT user |
| `completed=true/false` | Filter by completion |
| `overdue=true` | `dueDate < today` AND `completed = false` |
| `dueToday=true` | `dueDate = today` |
| `page`, `size` | Pagination (default 0, 20) |

- **Sort:** `dueDate ASC` — soonest / most urgent first
- **Implementation:** JPA `Specification` via `FollowUpSpecification.build()` — same pattern as `LeadSpecification`

### Overdue Logic (important for interviews)

```text
overdue = dueDate < today AND completed = false
```

- A completed task is **never** overdue, even if its due date was in the past
- `dueToday` and `overdue` can be combined in one query if both params are `true`

### Business Rules (BusinessRuleException → 400)

| Rule | When |
|------|------|
| Completed follow-ups cannot be updated | `PUT` on a `completed=true` record |
| Follow-up is already completed | `PATCH .../complete` called twice |

- **Delete:** Allowed even on completed follow-ups — no guard
- **Update due date to past:** Allowed on update DTO (no `@FutureOrPresent` on `UpdateFollowUpRequestDto`) — only create enforces future dates

### Lead Timeline Integration

- **Create** → `FOLLOW_UP_CREATED` activity ("Follow-up scheduled for {date} ({type})")
- **Complete** → `FOLLOW_UP_COMPLETED` activity ("Follow-up completed ({type})")
- **Update / delete** → no timeline entry

### Dashboard Integration

- `DashboardServiceImpl` uses `FollowUpRepository` count methods:
  - `countByDueDateAndCompletedFalse(today)` → today's open follow-ups
  - `countByDueDateBeforeAndCompletedFalse(today)` → overdue count
- Exposed in `GET /api/v1/dashboard/stats` as `todaysFollowUps`, `overdueFollowUps`

### Response Shape (FollowUpResponseDto)

- Includes denormalized names for UI: `leadName`, `assignedToName`, `createdByName`
- Plus IDs, type, dueDate, notes, completed, timestamps

### Interview Talking Points

- **Follow-up vs Appointment:** Appointment = customer-facing event with status workflow; Follow-up = internal staff task with simple done/not-done
- **Follow-up vs Lead Note:** Note = free-form log entry; Follow-up = actionable task with due date and assignee
- **Why top-level `/follow-ups` not nested?** Easier global views ("my tasks today", "all overdue") without requiring a leadId; still filterable by `leadId`
- **Why Specification over @Query?** Composable filters (`leadId` + `overdue` + `assignedToMe`) — same reusable pattern as leads
- **assignedToMe pattern:** Controller passes flag → service resolves current user ID → spec filters — avoids client sending their own user ID
- **Extension ideas:** Snooze/reschedule endpoint; auto-create follow-up when appointment completes; email reminder job for overdue tasks

### Key Files

| File | Role |
|------|------|
| `followup/entity/FollowUp.java` | JPA entity |
| `followup/entity/FollowUpType.java` | Type enum |
| `followup/repository/FollowUpRepository.java` | CRUD + dashboard count queries + `JpaSpecificationExecutor` |
| `followup/specification/FollowUpSpecification.java` | Dynamic filters (lead, assignee, completed, overdue, dueToday) |
| `followup/service/impl/FollowUpServiceImpl.java` | Business logic + timeline recording |
| `followup/controller/FollowUpController.java` | REST endpoints |
| `followup/mapper/FollowUpMapper.java` | Entity ↔ DTO |
| `followup/dto/CreateFollowUpRequestDto.java` | Create body + validation |
| `followup/dto/UpdateFollowUpRequestDto.java` | Update body |
| `followup/dto/FollowUpResponseDto.java` | Single item response |
| `followup/dto/FollowUpPageResponseDto.java` | Paginated wrapper |

---

## CRM Dashboard Statistics (Sprint 3)

- **What:** Summary cards for CRM home screen — not full Phase 9 analytics
- **API:** `GET /api/v1/dashboard/stats`
- **Returns:** today's leads, monthly leads, leads by status, upcoming appointments, follow-ups, leads per assignee
- **Deferred:** revenue, conversion charts, projects running (Sprint 5+)
- **Location:** `dashboard/` module

---

## JPA @ManyToOne Relations

- **What:** Maps a child entity to one parent entity; Hibernate creates/maintains the FK column
- **Why use it over plain `Long parentId`:**
  - Service validates parent exists before save (same pattern as `FollowUpServiceImpl`)
  - JPQL joins work (`p.lead.name`, `payment.project.contractAmount`)
  - Specifications can filter on related entity fields
  - Consistent with every existing module in this codebase
- **Pattern used everywhere:** Unidirectional — child holds `@ManyToOne`; parent does **not** have `@OneToMany`
- **Standard annotations:**
  - `@ManyToOne(fetch = FetchType.LAZY)` — load parent only when accessed (inside a transaction)
  - `@JoinColumn(name = "lead_id")` — DB column name for the FK
  - `optional = false` + `nullable = false` — relation is required
  - Omit `optional = false` — relation is optional (nullable FK)
- **What to avoid:**
  - `cascade = CascadeType.ALL` on Lead/Project relations — deleting a lead must not auto-delete projects
  - `FetchType.EAGER` — causes N+1 query problems
  - `@OneToMany` on parent unless you frequently navigate parent → children in Java
- **Service pattern:** DTO accepts `leadId` / `projectId` → service loads entity → sets object reference on child entity
- **Interview point:** FK is the DB constraint; `@ManyToOne` is the ORM mapping of that FK to a Java object

### Relation Map (Project & Payment Phase)

```text
Lead (1) ──────< Project (many)
User (1) ──────< Project (many)     via assignedTo
Project (1) ───< Payment (many)
```

- Lead reachable from payment via: `payment.getProject().getLead()` — no direct `Payment → Lead` FK (by design)

---

## Project Management (Phase 7 — Entity Layer)

- **What:** Tracks active interior-design jobs after lead conversion — progress, timeline, contract value, assigned team
- **Phase:** ProjectGuide Phase 7 — Sprint 5 (entity layer first; CRUD API deferred)
- **Table:** `projects`
- **Location:** `project/entity/`

### Project Entity

- **Fields:** `id`, `name`, `lead` (FK), customer snapshot (`customerName`, `customerPhone`, `customerEmail`, `city`, `address`), `serviceInterested`, `status`, `contractAmount`, `progress` (0–100), `startDate`, `expectedCompletionDate`, `actualCompletionDate`, `assignedTo` (FK), `notes`, `createdAt`, `updatedAt`
- **Customer snapshot:** Denormalized from lead at project creation — renames on lead don't break project records (same idea as `Appointment.customerName`)

### Project JPA Relations

| Field | Type | Column | Required? |
|-------|------|--------|-----------|
| `lead` | `@ManyToOne Lead` | `lead_id` | Yes |
| `assignedTo` | `@ManyToOne User` | `assigned_to_id` | No |

- Both use `fetch = FetchType.LAZY`
- Mirrors `Appointment.lead` (required) + `Lead.assignedTo` (optional)

### ProjectStatus Enum

| Status | Meaning |
|--------|---------|
| `CREATED` | Project record created (default) |
| `MEASUREMENT` | Site measurement phase |
| `DESIGN` | Design / planning phase |
| `EXECUTION` | Work in progress on site |
| `COMPLETED` | Project finished |
| `ON_HOLD` | Paused |
| `CANCELLED` | Cancelled |

- **Workflow (Phase 7):** Lead → Project Created → Measurement → Design → Execution → Completed

### What's Deferred (not built yet)

- Repository, Service, Controller, DTOs, Mapper
- `GET/POST/PUT /api/v1/projects` endpoints
- Filters (by status, assignee, lead)
- Lead timeline activity types for project events
- Dashboard `projectsRunning` count
- Photos, documents, team assignment beyond single `assignedTo`

### Interview Talking Points

- **Lead vs Project:** Lead = sales pipeline; Project = delivery after conversion (WON lead)
- **Why required `lead` FK?** Every project originates from a lead — traceability and CRM continuity
- **Why snapshot customer fields?** Historical accuracy; lead contact info may change over time
- **Why `progress` integer not status only?** Status = phase; progress = % completion within/between phases
- **Extension ideas:** Multi-user team (`ProjectMember` join table); link to quotation; milestone-based progress auto-update

### Key Files

| File | Role |
|------|------|
| `project/entity/Project.java` | JPA entity |
| `project/entity/ProjectStatus.java` | Status enum |

---

## Payment Tracking (Phase 7 — Entity Layer)

- **What:** Tracks payment installments and milestones against a project — amount due, paid, method, dates
- **Phase:** ProjectGuide Phase 7 — Sprint 5 (entity layer first; CRUD API deferred)
- **Table:** `payments`
- **Location:** `payment/entity/`

### Payment Entity

- **Fields:** `id`, `project` (FK), `amount`, `paidAmount`, `status`, `paymentMethod`, `dueDate`, `paidDate`, `transactionReference`, `notes`, `createdAt`, `updatedAt`
- **`amount`:** Total due for this payment record (installment or milestone)
- **`paidAmount`:** How much has been received so far — supports partial payments without splitting records

### Payment JPA Relations

| Field | Type | Column | Required? |
|-------|------|--------|-----------|
| `project` | `@ManyToOne Project` | `project_id` | Yes |

- No direct `Lead` FK — customer/lead reached via `payment.getProject().getLead()`
- **Why no `lead_id` on Payment?** Payments belong to projects; avoids redundant FK. Add optional `lead` later only if token/advance payments before project creation are needed

### PaymentStatus Enum

| Status | Meaning |
|--------|---------|
| `PENDING` | Not yet paid (default) |
| `PARTIAL` | Some amount received |
| `PAID` | Fully paid |
| `OVERDUE` | Past due date, not fully paid |
| `CANCELLED` | Payment record voided |

### PaymentMethod Enum

| Method | Typical use |
|--------|-------------|
| `CASH` | Cash payment |
| `UPI` | UPI transfer |
| `BANK_TRANSFER` | NEFT/IMPS/RTGS |
| `CHEQUE` | Cheque |
| `CARD` | Debit/credit card |
| `OTHER` | Catch-all |

### What's Deferred (not built yet)

- Repository, Service, Controller, DTOs, Mapper
- `GET/POST/PUT /api/v1/payments` endpoints
- Auto-set `OVERDUE` status (scheduled job or query-time logic)
- Payment reminder WhatsApp (Phase 5 / Sprint 4)
- Dashboard revenue / pending payment stats
- Lead timeline integration for payment events

### Interview Talking Points

- **Payment vs Quotation:** Quotation = proposed price; Payment = actual money received against a live project
- **Why `amount` + `paidAmount`?** One record tracks partial payments — status becomes `PARTIAL` until `paidAmount >= amount`
- **Why `@ManyToOne Project` not Lead?** Financial tracking is per project; one lead may eventually have multiple projects
- **Extension ideas:** Payment schedule generator from `contractAmount`; Razorpay/webhook integration; aggregate paid total per project via repository query

### Key Files

| File | Role |
|------|------|
| `payment/entity/Payment.java` | JPA entity |
| `payment/entity/PaymentStatus.java` | Status enum |
| `payment/entity/PaymentMethod.java` | Method enum |

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
| Sprint 5 | JPA @ManyToOne Relations (pattern) | common |
| Sprint 5 | Project entity + ProjectStatus | project |
| Sprint 5 | Payment entity + PaymentStatus + PaymentMethod | payment |
