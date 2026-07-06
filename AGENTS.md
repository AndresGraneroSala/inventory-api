# Biblioteca API — AGENTS.md

RESTful API for product and category catalog management. Spring Boot portfolio project focused on clean architecture and software engineering best practices.

## Stack

- **Language:** Java 17
- **Framework:** Spring Boot 4.1.0
- **Persistence:** Spring Data JPA + Hibernate
- **Database:** MySQL 8.0
- **Build:** Maven
- **DTO Mapping:** ModelMapper 3.2.6
- **Validation:** Jakarta Validation (`@Valid`)
- **Documentation:** SpringDoc OpenAPI 2.6.0 (Swagger UI)
- **Testing:** JUnit 5 + MockMvc + Mockito
- **Boilerplate:** Lombok
- **Config:** `.env` via springboot4-dotenv
- **Monitoring:** Spring Boot Actuator
- **Template engine:** Thymeleaf (available, not used in API)

## Commands

| Command | Description |
|---|---|
| `mvn spring-boot:run` | Start local server |
| `mvn test` | Run all tests (must pass before every commit) |
| `mvn verify` | Run full verification |
| `mvn clean` | Clean build artifacts |
| `mvn dependency:tree` | Inspect dependency tree |

## Project Structure

```
src/main/java/es/andres/biblioteca/Biblioteca/
├── BibliotecaApplication.java       # Entry point (prints Swagger URLs on startup)
├── config/
│   └── ModelMapperConfig.java       # ModelMapper bean definition
├── controller/
│   ├── ProductController.java       # /api/products
│   └── CategoryController.java      # /api/categories
├── dto/
│   ├── ProductDto.java              # Product data transfer object
│   └── CategoryDto.java             # Category data transfer object
├── entity/
│   ├── Product.java                 # Product JPA entity
│   ├── Category.java                # Category JPA entity
│   └── ProductState.java            # Enum: AVAILABLE, NOT_AVAILABLE
├── exceptions/
│   ├── GlobalExceptionHandler.java  # @RestControllerAdvice central handler
│   ├── ErrorResponse.java           # Standardized error response body
│   ├── BadRequestException.java     # 400 exception
│   └── ResourceNotFoundException.java  # 404 exception
├── mapper/
│   ├── ProductMapper.java           # Product <-> ProductDto (ModelMapper)
│   └── CategoryMapper.java          # Category <-> CategoryDto (ModelMapper)
├── repository/
│   ├── ProductRepository.java       # Spring Data JPA repository
│   └── CategoryRepository.java      # Spring Data JPA repository
└── service/
    ├── ProductService.java          # Product business logic interface
    ├── CategoryService.java         # Category business logic interface
    └── impl/
        ├── ProductServiceImpl.java  # Product service implementation
        └── CategoryServiceImpl.java # Category service implementation

src/test/java/es/andres/biblioteca/Biblioteca/
├── BibliotecaApplicationTests.java
└── controller/
    ├── ProductControllerTest.java   # @WebMvcTest for Product endpoints
    └── CategoryControllerTest.java  # @WebMvcTest for Category endpoints
```

## Conventions

### Dependency Injection
- Always constructor injection with `@RequiredArgsConstructor`
- Never `@Autowired` on fields

### Entities
- Use `@Data`, `@AllArgsConstructor`, `@NoArgsConstructor` from Lombok
- Always use `@GeneratedValue(strategy = GenerationType.IDENTITY)` for PKs
- Use `@Enumerated(EnumType.STRING)` for enum fields
- Use `@Column(name = "snake_case")` with explicit column names
- `@ManyToOne` for relationships, never `@OneToMany` (no bidirectional mapping)

### DTOs
- Use `@Data`, `@AllArgsConstructor`, `@NoArgsConstructor` from Lombok
- Apply `@NotBlank`, `@NotNull`, `@Size`, `@Min` validation annotations
- Never expose JPA entities in HTTP responses
- DTOs reference other DTOs (not entity IDs) for nested objects

### Services
- `@Transactional(readOnly = true)` at class level
- Override with `@Transactional` on write methods
- Throw `ResourceNotFoundException` for missing entities
- Throw `BadRequestException` for invalid business logic
- Use a private helper method for `getByIdOrThrow()` pattern

### Mappers
- Use ModelMapper for Entity <-> DTO conversion
- Mappers are `@Component` beans with `@RequiredArgsConstructor`
- Keep conversion logic in mapper classes, never in services

### Controllers
- `@RequiredArgsConstructor` + `@RestController` + `@RequestMapping("/api/resource")`
- Return `ResponseEntity<T>` with appropriate HTTP status
- Use `@Valid` on request bodies for validation
- Keep controllers thin — no business logic, only delegation to service

### Exception Handling
- All exceptions handled in `GlobalExceptionHandler` via `@RestControllerAdvice`
- Return `ErrorResponse` with: message, statusCode, timestamp, errorDetails
- `ResourceNotFoundException` → 404
- `BadRequestException` → 400
- `MethodArgumentNotValidException` → 400

### API Design
- RESTful: resources in plural (`/api/products`, `/api/categories`)
- Path variables for identifiers, query params for filtering
- Standard HTTP methods: GET (read), POST (create), PUT (update), DELETE (delete)

### Testing
- Controller tests: `@WebMvcTest(ControllerClass.class)` + `MockMvc` + `@MockitoBean`
- Use `ObjectMapper.findAndRegisterModules()` in `@BeforeEach setUp()`
- Test happy path and error scenarios (404, 400) for each endpoint
- Verify response status, jsonPath assertions, and service interaction via Mockito

## Do Not

- Do NOT use `@Autowired` on fields — always constructor injection
- Do NOT expose JPA entities in HTTP responses — always use DTOs
- Do NOT hardcode credentials — use `.env` with `example.env` as template
- Do NOT duplicate validation — DTO `@Valid` at controller, business rules at service
- Do NOT do Entity -> DTO -> Entity conversions in write operations (use mapper directly)
- Do NOT create bidirectional relationships (`@OneToMany` in both directions)
- Do NOT add business logic in controllers — delegate to services
- Do NOT commit `.env` files — only commit `example.env`

## Available Skills

Skills are in `.opencode/skills/<name>/SKILL.md`. Load with the skill tool.

| Skill | When to use |
|---|---|
| `entity-mapper` | Auto-generate ER diagram from `@Entity` classes and insert into README |
| `api-concept-map` | Auto-generate API endpoint map from `@RestController` classes and insert into README |
| `add-entity` | Generate a complete new entity with all layers (Entity → Repository → Service → DTO → Mapper → Controller → Tests) |
| `project-overview` | Get a full explanation of the project (stack, structure, conventions, endpoints) |
| `audit-review` | Scan the codebase for anti-patterns and convention violations |
| `generate-test` | Generate MockMvc tests for new or existing controllers following existing patterns |

## Workflow

1. Before non-trivial tasks, propose a plan and wait for approval
2. One task at a time; when done, summarize what changed
3. If uncertain (<80% confidence), ask — do not invent
4. Verify changes with `mvn test` before signaling completion
5. When in Plan mode: design first, then execute (no direct code changes during planning)
6. When in Build mode: execute atomic changes directly, but still verify with tests

## Environment Reference

| Variable | Example | Description |
|---|---|---|
| `PORT` | `8080` | Server port |
| `DB_USER` | `root` | Database username |
| `DB_PASSWORD` | `secret` | Database password |

See `example.env` for the template.
