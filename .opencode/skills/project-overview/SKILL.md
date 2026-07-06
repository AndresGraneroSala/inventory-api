---
name: project-overview
description: Provide a comprehensive explanation of the Biblioteca API project.
---

# Skill: project-overview

Provide a comprehensive explanation of the Biblioteca API project to someone new to the codebase.

## When to use

When a new developer or interviewer wants to understand the project structure, architecture decisions, and conventions.

## What it does

Reads key project files and synthesizes a structured overview. It does NOT write to any file — it prints the explanation to the conversation.

## Overview structure

### 1. Project Identity
- **Name:** Biblioteca API
- **Purpose:** RESTful catalog management (products + categories)
- **Type:** Portfolio project demonstrating Spring Boot best practices
- **Repository root:** `C:\Users\Andriu\Documents\ProyectosSpringBoot\Biblioteca\Biblioteca_comandos`

### 2. Stack (read from `pom.xml`)
- Java 17, Spring Boot 4.1.0, MySQL 8.0, Maven, Lombok, ModelMapper 3.2.6, SpringDoc OpenAPI 2.6.0
- Testing: JUnit 5, MockMvc, Mockito
- Config: springboot4-dotenv for `.env` support

### 3. Architecture (read from source files)

Explain the 6-layer architecture:

| Layer | Package | Responsibility | Key file |
|---|---|---|---|
| Entity | `entity/` | JPA domain models | `Product.java`, `Category.java`, `ProductState.java` |
| Repository | `repository/` | Data access (Spring Data JPA) | `ProductRepository.java`, `CategoryRepository.java` |
| DTO | `dto/` | Data transfer objects with validation | `ProductDto.java`, `CategoryDto.java` |
| Mapper | `mapper/` | Entity ↔ DTO conversion (ModelMapper) | `ProductMapper.java`, `CategoryMapper.java` |
| Service | `service/` + `impl/` | Business logic | `ProductServiceImpl.java`, `CategoryServiceImpl.java` |
| Controller | `controller/` | REST endpoints | `ProductController.java`, `CategoryController.java` |

Cross-cutting:
- `exceptions/` — `GlobalExceptionHandler.java` (centralized error handling)
- `config/` — `ModelMapperConfig.java` (bean configuration)

### 4. Key Conventions
Read from actual source files to confirm these are followed:
- Constructor injection with `@RequiredArgsConstructor`
- `@Transactional(readOnly = true)` at service class level
- `@Data` on entities (Lombok)
- ModelMapper for all conversions
- `ResponseEntity<T>` from all controller methods
- `@RestControllerAdvice` for exception handling
- `@WebMvcTest` + `@MockitoBean` for controller tests

### 5. API Endpoints
List all endpoints from both controllers with method, path, and description.

### 6. Data Model
Describe the two entities and their relationship:
- `Category` (categoryId, categoryName)
- `Product` (productId, productName, productDescription, productPrice, productAmount, productState, productCategory → ManyToOne to Category)
- `ProductState` enum: `AVAILABLE`, `NOT_AVAILABLE`

### 7. Testing Strategy
- Controller tests with `@WebMvcTest` using MockMvc
- Service layer mocked with `@MockitoBean`
- Tests cover: happy paths, 400 validation errors, 404 not found, empty lists

### 8. Configuration
- `.env` file for environment variables (PORT, DB_USER, DB_PASSWORD)
- `example.env` as template (committed to repo)
- `application.properties` for Spring Boot config

## Rules

- Read actual source files — do not assume or invent conventions
- If the codebase deviates from what AGENTS.md says, note the deviation
- Present the information clearly but concisely — bullet points preferred
- No file modifications — this is a read-only informational skill
