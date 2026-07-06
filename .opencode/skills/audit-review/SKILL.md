---
name: audit-review
description: Scan the codebase for anti-patterns and convention violations.
---

# Skill: audit-review

Scan the codebase for anti-patterns, convention violations, and potential issues specific to this project.

## When to use

Before a release, after significant changes, or when asked for a code quality review.

## What it does

Reads every `.java` file in the project and checks for violations across these categories:

### 1. Dependency Injection

**Check:** `@Autowired` annotation on fields.
- **Violation:** `@Autowired private SomeService service;`
- **Expected:** `private final SomeService service;` with `@RequiredArgsConstructor` on the class.
- **Severity:** High

### 2. Entity Exposure in Responses

**Check:** Controller or DTO methods returning JPA entity types directly.
- **Violation:** `ResponseEntity<Product>` instead of `ResponseEntity<ProductDto>`
- **Expected:** Always use DTOs for HTTP responses.
- **Severity:** High

### 3. Transactional Correctness

**Check:** Service implementation classes.
- **Violation:** Missing `@Transactional(readOnly = true)` at class level.
- **Violation:** Write methods (save, update, delete) without `@Transactional`.
- **Severity:** Medium

### 4. Entity Relationship Design

**Check:** Bidirectional `@OneToMany` or `@ManyToMany` without explicit cascade handling.
- **Note:** This project uses unidirectional `@ManyToOne` only — flag any bidirectional mapping.
- **Severity:** Medium

### 5. Validation Duplication

**Check:** Business validation in service that duplicates DTO `@Valid` annotations.
- **Example:** Checking `@NotNull` fields manually in service when the DTO already has `@NotNull`.
- **Note:** It's acceptable to have business rules (e.g., "price must be > 0") in the service even if DTO has `@Min(0)`, but flag exact duplicates.
- **Severity:** Low

### 6. Hardcoded Configuration

**Check:** `application.properties` or any `.java` file for hardcoded credentials, URLs, or database names.
- **Expected:** Values that vary by environment should be in `.env` with a corresponding `example.env`.
- **Severity:** High

### 7. Naming Conventions

**Check:** Java classes, methods, and fields follow project conventions.
- Entities: PascalCase (e.g., `Product`)
- DTOs: `{EntityName}Dto`
- Mappers: `{EntityName}Mapper`
- Repositories: `{EntityName}Repository`
- Services: `{EntityName}Service` (interface), `{EntityName}ServiceImpl` (implementation)
- Controllers: `{EntityName}Controller`
- **Severity:** Low

### 8. Exception Handling Coverage

**Check:** `GlobalExceptionHandler` covers all custom exceptions.
- All exception classes in `exceptions/` package should have a corresponding `@ExceptionHandler` method.
- **Severity:** Medium

### 9. Test Coverage Gaps

**Check:** For each controller endpoint, verify there's a corresponding test.
- Read `ProductController.java` and `CategoryController.java` methods.
- Check tests in `ProductControllerTest.java` and `CategoryControllerTest.java`.
- List any endpoints without test coverage.
- **Severity:** Medium

### 10. Lombok on Entities

**Note:** The project uses `@Data` on JPA entities. While this is the project convention, be aware:
- `@Data` includes `@EqualsAndHashCode` which can cause issues with lazy-loading proxies
- `@Data` includes `@ToString` which can trigger lazy-load exceptions
- Document this as a known convention (not a violation since it's consistent across the project)
- **Severity:** Info

## Output format

For each violation found, report:

```
## [Severity] Category: description

**File:** `path/to/file.java:line`
**Current:** ...
**Expected:** ...
**Suggestion:** ...
```

If no violations are found in a category, report `✅ No issues found.`

End with a summary:

```
## Summary

- High severity: X
- Medium severity: Y
- Low severity: Z
- Info items: W
```

## Rules

- Do NOT modify any files — this is a read-only audit
- Base all checks on actual file contents, not assumptions
- If a file can't be read, note it as a potential issue
- Prioritize findings by severity in the output
