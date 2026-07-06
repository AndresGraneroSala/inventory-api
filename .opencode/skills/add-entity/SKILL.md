---
name: add-entity
description: Generate a complete new entity with all layers following project patterns.
---

# Skill: add-entity

Generate a complete new entity with all layers, following the exact patterns already established in the project.

## When to use

When you need to add a new domain entity (e.g., `Author`, `Publisher`, `Review`) with full CRUD and all architectural layers.

## What it does

Generates the following files (one at a time, asking for confirmation before each):

### 1. Entity class
`src/main/java/es/andres/biblioteca/Biblioteca/entity/{EntityName}.java`

Pattern (from `Category.java`):
```java
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class {EntityName} {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "{snake_case_id}")
    private Long {entityId};

    // fields with @Column(name = "snake_case", nullable = false, length = ...)
    // relationships with @ManyToOne / @JoinColumn
}
```

### 2. Repository interface
`src/main/java/es/andres/biblioteca/Biblioteca/repository/{EntityName}Repository.java`

Pattern (from `CategoryRepository.java`):
```java
@Repository
public interface {EntityName}Repository extends JpaRepository<{EntityName}, Long> {
    // custom query methods as needed
}
```

### 3. DTO class
`src/main/java/es/andres/biblioteca/Biblioteca/dto/{EntityName}Dto.java`

Pattern (from `CategoryDto.java`):
```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class {EntityName}Dto {
    private Long {entityId};
    // fields with @NotBlank, @NotNull, @Size, @Min validation annotations
}
```

### 4. Mapper class
`src/main/java/es/andres/biblioteca/Biblioteca/mapper/{EntityName}Mapper.java`

Pattern (from `CategoryMapper.java`):
```java
@RequiredArgsConstructor
@Component
public class {EntityName}Mapper {
    private final ModelMapper modelMapper;

    public {EntityName}Dto toDto({EntityName} entity) {
        return modelMapper.map(entity, {EntityName}Dto.class);
    }

    public {EntityName} toEntity({EntityName}Dto dto) {
        return modelMapper.map(dto, {EntityName}.class);
    }
}
```

### 5. Service interface
`src/main/java/es/andres/biblioteca/Biblioteca/service/{EntityName}Service.java`

Pattern (from `CategoryService.java`):
```java
public interface {EntityName}Service {
    {EntityName}Dto create{EntityName}({EntityName}Dto dto);
    List<{EntityName}Dto> findAll();
    {EntityName}Dto findById(Long id);
    {EntityName}Dto update(Long id, {EntityName}Dto dto);
    void delete(Long id);
}
```

### 6. Service implementation
`src/main/java/es/andres/biblioteca/Biblioteca/service/impl/{EntityName}ServiceImpl.java`

Pattern (from `CategoryServiceImpl.java`):
```java
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class {EntityName}ServiceImpl implements {EntityName}Service {
    private final {EntityName}Repository repository;
    private final {EntityName}Mapper mapper;

    @Override
    @Transactional
    public {EntityName}Dto create{EntityName}({EntityName}Dto dto) {
        // duplicate validation if applicable
        {EntityName} entity = mapper.toEntity(dto);
        return mapper.toDto(repository.save(entity));
    }

    // ... other CRUD methods following CategoryServiceImpl patterns
}
```

### 7. Controller class
`src/main/java/es/andres/biblioteca/Biblioteca/controller/{EntityName}Controller.java`

Pattern (from `CategoryController.java`):
```java
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/{entities}")
public class {EntityName}Controller {
    private final {EntityName}Service service;

    @PostMapping
    public ResponseEntity<{EntityName}Dto> create(@Valid @RequestBody {EntityName}Dto dto) { ... }

    @GetMapping
    public ResponseEntity<List<{EntityName}Dto>> findAll() { ... }

    @GetMapping("/{id}")
    public ResponseEntity<{EntityName}Dto> findById(@PathVariable Long id) { ... }

    @PutMapping("/{id}")
    public ResponseEntity<{EntityName}Dto> update(@PathVariable Long id, @Valid @RequestBody {EntityName}Dto dto) { ... }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) { ... }
}
```

### 8. Controller test class
`src/test/java/es/andres/biblioteca/Biblioteca/controller/{EntityName}ControllerTest.java`

Pattern (from `CategoryControllerTest.java`):
```java
@WebMvcTest({EntityName}Controller.class)
class {EntityName}ControllerTest {
    @Autowired private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    @MockitoBean
    private {EntityName}Service service;

    // Tests for each endpoint: happy path + 400 + 404 scenarios
}
```

## Workflow

1. Ask the user for the entity name (singular, PascalCase, e.g. `Publisher`)
2. Ask for the fields and their types
3. Ask about relationships to existing entities
4. Generate files one by one, confirming before writing each
5. After all files are created, run `mvn test` to verify
6. Suggest running `entity-mapper` skill to update the ER diagram in README
7. Suggest running `api-concept-map` skill to update the API map

## Rules

- Never modify existing files (except if a relationship requires adding a `@ManyToOne` field in an existing entity — ask first)
- Always follow the exact patterns from the codebase (not generic Spring Boot patterns)
- Use `@Data` on entities (matching existing convention) — note `@Data` is used despite known `equals()/hashCode()` concerns with proxies, to stay consistent with the project
- Add validation annotations to DTOs matching the field types
- Always create the mapper even if it seems trivial — consistency matters
- Tests must cover at least: successful create (201), successful list (200), successful find by id (200), successful update (200), successful delete (204), 404 on not found, 400 on validation error
