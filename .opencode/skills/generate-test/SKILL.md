---
name: generate-test
description: Generate MockMvc controller tests following existing patterns.
---

# Skill: generate-test

Generate MockMvc controller tests following the existing patterns in the project.

## When to use

When a new controller is added, a new endpoint is created in an existing controller, or existing test coverage is insufficient.

## What it does

1. Reads the target controller file to extract all endpoints
2. Reads an existing test file (e.g., `CategoryControllerTest.java`) as a reference template
3. Generates test methods for each endpoint

## Template (from `CategoryControllerTest.java`)

```java
package es.andres.biblioteca.Biblioteca.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.andres.biblioteca.Biblioteca.dto.{EntityName}Dto;
import es.andres.biblioteca.Biblioteca.exceptions.BadRequestException;
import es.andres.biblioteca.Biblioteca.exceptions.ResourceNotFoundException;
import es.andres.biblioteca.Biblioteca.service.{EntityName}Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({EntityName}Controller.class)
class {EntityName}ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    @MockitoBean
    private {EntityName}Service {entityName}Service;

    // --- Tests ---
}
```

## Test patterns to generate

### For each CREATE endpoint (`@PostMapping`):
```java
@Test
void create{Entity}_ShouldReturn201() throws Exception {
    // Arrange: build valid request and expected response
    // Mock: service returns response
    // Act & Assert: POST, verify 201 + jsonPath
}

@Test
void create{Entity}_WithInvalidField_ShouldReturn400() throws Exception {
    // Arrange: build invalid request (blank name, null field, etc.)
    // Act & Assert: POST, verify 400 + statusCode + errorDetails
}

@Test
void create{Entity}_DuplicateName_ShouldReturn400() throws Exception {
    // Arrange: build valid request
    // Mock: service throws BadRequestException
    // Act & Assert: POST, verify 400 + message
}
```

### For each LIST endpoint (`@GetMapping` without `{id}`):
```java
@Test
void findAll_ShouldReturn200() throws Exception {
    // Arrange: service returns list of DTOs
    // Act & Assert: GET, verify 200 + jsonPath size + field values
}

@Test
void findAll_Empty_ShouldReturn200() throws Exception {
    // Arrange: service returns empty list
    // Act & Assert: GET, verify 200 + size 0
}
```

### For each GET BY ID endpoint (`@GetMapping("/{id}")`):
```java
@Test
void findById_ShouldReturn200() throws Exception {
    // Arrange: service returns DTO
    // Act & Assert: GET, verify 200 + jsonPath
}

@Test
void findById_NotFound_ShouldReturn404() throws Exception {
    // Mock: service throws ResourceNotFoundException
    // Act & Assert: GET, verify 404
}
```

### For each UPDATE endpoint (`@PutMapping`):
```java
@Test
void update_ShouldReturn200() throws Exception {
    // Arrange: build request and response
    // Mock: service returns updated DTO
    // Act & Assert: PUT, verify 200
}

@Test
void update_WithBlankName_ShouldReturn400() throws Exception {
    // Arrange: invalid request
    // Act & Assert: PUT, verify 400
}

@Test
void update_NotFound_ShouldReturn404() throws Exception {
    // Mock: service throws ResourceNotFoundException
    // Act & Assert: PUT, verify 404
}
```

### For each DELETE endpoint (`@DeleteMapping`):
```java
@Test
void delete_ShouldReturn204() throws Exception {
    // Act & Assert: DELETE, verify 204
}

@Test
void delete_NotFound_ShouldReturn404() throws Exception {
    // Mock: service throws ResourceNotFoundException
    // Act & Assert: DELETE, verify 404 + message
}
```

### For custom endpoints (e.g., change state, find by state):
Generate tests matching the specific endpoint semantics, always covering:
- Happy path (200 or 201)
- Validation error (400) if the request has a body
- Not found (404) if the endpoint uses an ID

## Rules

- Write the test to an existing test file if it exists, or create a new `{EntityName}ControllerTest.java`
- Follow naming convention: `{method}_{scenario}_ShouldReturn{status}`
- Use descriptive test data (avoid "test1", "test2")
- Include `ObjectMapper.findAndRegisterModules()` in `@BeforeEach` setup
- Use `@WebMvcTest(ControllerClass.class)` (only the specific controller)
- Mock the service layer with `@MockitoBean`
- Always verify jsonPath assertions on the response body when applicable
- Run the specific test file with `mvn test -Dtest={TestClassName}` to verify after generating
