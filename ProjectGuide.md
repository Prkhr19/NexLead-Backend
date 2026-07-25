# Adore Dezign CRM + Website Project Guide

## HANDBOOK RULE

Maintain `handbook.md` throughout this project.

Whenever we introduce a new concept, pattern, or tool (e.g. Mapper class, JWT, Transactions):

- Add a revision entry in `handbook.md`
- Use bullet points — short, scannable notes
- Focus on what it is, why we use it, and where it lives in this codebase
- Update the handbook in the same phase/commit as the feature when possible

---

## PROJECT VISION

This project is not just a company website.

It is a complete Business Management Platform for Adore Dezign that combines:

- Modern React Website
- Spring Boot Backend
- CRM
- WhatsApp Automation
- Appointment Scheduling
- Quotation Management
- Project Tracking
- Analytics Dashboard
- AI Features

The objective is to build software that the business can use every day while creating an enterprise-level portfolio project.

The architecture should always prioritize solving real business problems rather than implementing unnecessary CRUD operations.

---

# BUSINESS OVERVIEW

Company Name:
Adore Dezign

Industry:

- Interior Designing
- Modular Kitchen
- Layout Planning
- Technical Project
- Technical Project + Concept

Primary Goal:

Convert website visitors into customers and manage the complete lifecycle from inquiry to project completion.

---

# PRODUCT ARCHITECTURE

Website
↓

Lead Capture

↓

CRM Backend

↓

WhatsApp Automation

↓

Appointment Management

↓

Quotation Generator

↓

Project Management

↓

Analytics Dashboard

↓

AI Assistant

---

# PHASE 1 — BACKEND FOUNDATION

Goal:

Build a production-ready Spring Boot backend.

Modules

- Authentication
- Authorization
- JWT Authentication
- Refresh Tokens
- Validation
- Global Exception Handling
- Swagger Documentation
- Business Profile

Business Profile replaces Company CRUD because there will always be only ONE company.

Business Profile contains:

- Company Name
- Logo
- Phone
- Email
- WhatsApp Number
- Address
- Google Maps
- Working Hours
- Social Links

---

# PHASE 2 — WEBSITE

Pages

- Home
- About
- Services
- Portfolio
- Testimonials
- Contact
- Book Consultation

Current Services

- Layout Planning
- Technical Project
- Technical Project + Concept
- Interior Designing
- Modular Kitchen

Each service page contains

- Overview
- Benefits
- Gallery
- Pricing
- FAQs
- Consultation CTA

---

# PHASE 3 — LEAD MANAGEMENT

This is the first major backend module.

Lead Fields

- Name
- Phone
- Email
- City
- Property Type
- Service Interested
- Budget
- Project Stage
- Notes
- Created Date

Lead Status

- New
- Contacted
- Meeting Scheduled
- Site Visit
- Quotation Sent
- Negotiation
- Won
- Lost

Features

- Create Lead
- Update Lead
- Delete Lead
- Search Leads
- Filter Leads
- Lead Timeline
- Lead Notes

---

# PHASE 4 — APPOINTMENT MANAGEMENT

Appointment contains

- Customer
- Lead
- Date
- Time
- Address
- Assigned Designer
- Status

Status

- Pending
- Accepted
- Rescheduled
- Cancelled
- Completed

Features

- Book Appointment
- Accept Appointment
- Reject Appointment
- Reschedule Appointment
- Reminder Notifications

---

# PHASE 5 — WHATSAPP AUTOMATION

Automatically send messages.

Customer receives

- Thank You Message
- Appointment Reminder
- Quotation Ready
- Payment Reminder
- Project Completion Message

Owner receives

- New Lead
- Appointment Notification
- Follow-up Reminder
- Pending Payment Alert

Store reusable WhatsApp Templates.

---

# PHASE 6 — QUOTATION GENERATOR

Quotation contains

- Customer
- Project
- Services
- Area
- Materials
- GST
- Discount
- Final Amount

Export

- PDF
- WhatsApp

---

# PHASE 7 — PROJECT MANAGEMENT

Workflow

Lead

↓

Project Created

↓

Measurement

↓

Design

↓

Execution

↓

Completed

Track

- Progress
- Timeline
- Assigned Team
- Photos
- Documents
- Payments
- Notes

---

# PHASE 8 — PORTFOLIO CMS

Admin uploads

- Images
- Videos
- Description
- Category

Website updates automatically.

---

# PHASE 9 — DASHBOARD

Cards

- Today's Leads
- Monthly Leads
- Appointments
- Projects Running
- Pending Quotations
- Revenue

Charts

- Monthly Leads
- Conversion Rate
- Revenue
- Popular Services
- Lead Sources

---

# PHASE 10 — AI FEATURES

AI Lead Qualification

Extract

- Budget
- Location
- Service
- Timeline

AI Reply Suggestions

Generate WhatsApp replies for approval.

AI Search

Example

"Show Bangalore modular kitchen leads above ₹10 lakh."

AI Dashboard Insights

- Revenue Prediction
- Conversion Trends
- Follow-up Suggestions
- Best Performing Services

---

# DATABASE MODULES

Authentication

BusinessProfile

Services

Portfolio

Testimonials

Leads

LeadNotes

Appointments

Projects

Quotations

WhatsAppTemplates

Notifications

Dashboard

---

# DEVELOPMENT SPRINTS

Sprint 1

- Backend Foundation
- Authentication
- JWT
- Refresh Tokens
- Business Profile
- Lead CRUD
- Swagger

Sprint 2

- React Website
- Portfolio
- Contact Form
- Lead Integration

Sprint 3

- CRM Dashboard
- Lead Search
- Filters
- Lead Notes
- Appointment Management

Sprint 4

- WhatsApp Integration
- Notifications
- Scheduler
- Template Management

Sprint 5

- Quotation Generator
- Project Tracking
- Portfolio CMS

Sprint 6

- Analytics
- AI Features
- Deployment
- Performance Optimization

---

# BACKEND LAYER ARCHITECTURE

Every feature MUST follow this architecture.

Client

↓

Controller

↓

Service

↓

Repository

↓

Database

Responsibilities

Controller

- Receive Request
- Validate DTO
- Return Response
- Never contain Business Logic

Service

- Business Rules
- DTO ↔ Entity Mapping
- Transactions
- Calls Repository

Repository

- Database Queries
- Query Methods
- No Business Logic

Entity

- Database Representation

DTO

- Request / Response Objects

Mapper

- DTO ↔ Entity Conversion

Global Exception Handler

- Handles Exceptions
- Returns Standard Response

---

# STANDARD API RESPONSE

Success Response

{
"success": true,
"message": "Operation Successful",
"timestamp": "...",
"data": {}
}

Error Response

{
"success": false,
"message": "...",
"timestamp": "...",
"errors": {}
}

---

# PROJECT DEVELOPMENT RULES

These rules are mandatory and must never be ignored.

## Rule 1 — Learn New Concepts Deeply

Whenever a completely new concept appears:

- Stop coding.
- Explain the concept.
- Explain why it exists.
- Explain alternatives.
- Explain best practices.
- Then implement it.

Examples

- Spring Security
- JWT
- Transactions
- Redis
- Kafka
- WebSockets
- WhatsApp APIs
- AI Integration
- System Design

---

## Rule 2 — AI Generates Boilerplate

Once a concept has been learned once,

AI should generate repetitive code.

Examples

- CRUD
- DTOs
- Repository Methods
- Controllers
- Mapping
- Standard Services
- Swagger
- Unit Test Skeletons

Do not waste time rewriting identical code.

---

## Rule 3 — Business Logic Over CRUD

Never build CRUD simply because it is common.

Every feature must solve an actual business problem.

---

## Rule 4 — Architecture First

Before implementation always

1. Design API
2. Design Flow
3. Design Entities
4. Design Relationships
5. Then write code

Never code first.

---

## Rule 5 — Explain Every Architectural Decision

Whenever an architectural decision is made, explain

- Why this approach?
- Alternatives
- Trade-offs
- Industry Practice

---

## Rule 6 — Code Reviews

After every completed feature

- Review code quality
- Suggest improvements
- Explain optimizations
- Point out bad practices

---

## Rule 7 — Learn Once, Move Fast

Once a concept has already been understood,

Do not repeatedly explain it.

Move quickly.

Generate boilerplate.

Focus on finishing the product.

---

## Rule 8 — Enterprise Standards

Always follow enterprise-level Spring Boot practices.

- Layered Architecture
- SOLID Principles
- Constructor Injection
- DTO Pattern
- Global Exception Handling
- Validation
- REST Standards
- Clean Code
- Proper Package Structure
- Reusable Components
- Meaningful Naming

---

## Rule 9 — AI Collaboration Model

When working on this project:

For NEW concepts

- Slow down
- Teach thoroughly
- Ask conceptual questions
- Ensure understanding before implementation

For ALREADY LEARNED concepts

- Generate code directly
- Explain only implementation-specific decisions
- Skip repetitive explanations
- Keep development moving

The objective is to maximize both learning and development speed.

---

# FINAL GOAL

Deliver a production-ready business platform that Adore Dezign can use daily.

The completed project should demonstrate:

- Enterprise Spring Boot
- React
- REST APIs
- JWT Security
- CRM Architecture
- WhatsApp Automation
- Scheduling
- PDF Generation
- Dashboard Analytics
- AI Integration

while remaining clean, scalable, maintainable, and portfolio-worthy.