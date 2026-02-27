eamFlow – Enterprise Team Management Platform
Overview

TeamFlow is a full-stack Team Management Platform designed to manage organizational structure, users, and meetings using a secure and scalable architecture.

The system was developed with strong emphasis on clean architecture, security best practices, and modular design. It demonstrates backend engineering skills, secure authentication implementation, structured frontend development, and automation readiness.

Core Capabilities
Role-Based Access Control (RBAC)

The platform enforces strict role separation:

Admin

Full user lifecycle management

Role assignment and permission control

System-wide visibility

Team Leader

Team member management

Meeting creation and tracking

Operational oversight

Employee

Profile management

Meeting participation

Controlled access to team resources

All permissions are enforced at backend level using Spring Security.

Architecture

TeamFlow follows a layered enterprise architecture:

Controller → Service → Repository → Database

Backend Design

RESTful API

DTO pattern

Service-layer business logic isolation

Repository abstraction via Spring Data JPA

Centralized exception handling

JWT-based authentication

BCrypt password encryption

In-memory database configuration for fast development and testing

Frontend Design

Angular component-based architecture

Route guards for role-based navigation

Modular service communication

Strong TypeScript typing

Clear separation of concerns

Technology Stack
Backend

Java

Spring Boot

Spring Security (JWT)

Spring Data JPA

Hibernate

H2 Database (In-Memory)

Frontend

Angular

TypeScript

HTML5

CSS3

DevOps & Tooling

Git / GitHub

Postman

Jenkins

Automated API Testing

Automated UI Testing

Database Implementation

The system uses H2 in-memory database for development and testing purposes.

Why H2:

Fast setup and zero external dependencies

Ideal for local development and CI environments

Easy integration with Spring Boot

Supports automatic schema generation via Hibernate

Main Entities:

User

Role

Team

Meeting

Task

The database schema is normalized and managed via JPA annotations.

The system can be easily migrated to MySQL or PostgreSQL by updating the datasource configuration.

Security Implementation

Stateless JWT authentication

Role-based authorization

BCrypt password hashing

Protected REST endpoints

Angular route guards

Unauthorized access is blocked at both backend and frontend levels.

Installation & Setup
Clone Repository
git clone https://github.com/saraMauda/Team-management
Backend

Run the application:

mvn spring-boot:run

Backend default:
http://localhost:8080

H2 Console:
http://localhost:8080/h2-console

Frontend
npm install
ng serve

Frontend default:
http://localhost:4200

Engineering Highlights

Clean layered architecture

Secure authentication flow

Strict role-based authorization

In-memory database optimized for development speed

Automation-ready infrastructure

CI integration compatible

Production migration-ready design

Future Enhancements

Migration to production-grade SQL database

Docker containerization

Cloud deployment

Real-time notifications

Advanced dashboard analytics

Developed by
Sara
Software Engineering Student
