---
name: skill-tester
description: Validate the output of other skills — verify correctness, completeness, and consistency with project conventions.
---

# Skill: skill-tester

Validate that the output produced by another skill is correct, complete, and consistent with project conventions.

## When to use

After running any skill (`add-entity`, `entity-mapper`, `api-concept-map`, `audit-review`, `generate-test`, `project-overview`) to verify the result is correct before committing.

## What it does

1. Determines which skill to validate (user specifies or detects last-run skill)
2. Reads relevant project files to establish baseline truth
3. Applies targeted checks for the specific skill
4. Reports results with ✅/❌ and actionable suggestions

## Verification checks by target skill

### `add-entity`

Verify that entity generation produced all required files and they compile.

| Check | What to verify |
|---|---|
| All files exist | `{Entity}.java`, `{Entity}Repository.java`, `{Entity}Dto.java`, `{Entity}Mapper.java`, `{Entity}Service.java`, `{Entity}ServiceImpl.java`, `{Entity}Controller.java`, `{Entity}ControllerTest.java` |
| Naming conventions | File names match PascalCase entity name, DTO → `{Entity}Dto`, Mapper → `{Entity}Mapper`, etc. |
| Entity annotations | `@Entity`, `@Data`, `@AllArgsConstructor`, `@NoArgsConstructor`, `@Id`, `@GeneratedValue(strategy = GenerationType.IDENTITY)`, `@Column(name = "snake_case")` |
| Repository pattern | Extends `JpaRepository<{Entity}, Long>`, has `@Repository` |
| DTO annotations | `@Data`, `@AllArgsConstructor`, `@NoArgsConstructor`, `@NotBlank`/`@NotNull`/`@Size`/`@Min` on fields |
| Mapper pattern | `@Component` with `@RequiredArgsConstructor`, uses `ModelMapper`, has `toDto()` and `toEntity()` |
| Service interface | Has `create`, `findAll`, `findById`, `update`, `delete` methods |
| Service impl | `@Service` + `@Transactional(readOnly = true)`, overrides with `@Transactional` on writes, `getByIdOrThrow()` pattern |
| Controller pattern | `@RestController` + `@RequestMapping("/api/{entities}")`, returns `ResponseEntity<T>`, uses `@Valid` |
| Test pattern | `@WebMvcTest`, `MockMvc`, `@MockitoBean`, `ObjectMapper.findAndRegisterModules()`, covers happy path + 400 + 404 |
| Compilation | Run `mvn compile` — must succeed |
| Tests | Run `mvn test -Dtest={Entity}ControllerTest` — must pass |

### `entity-mapper`

Verify README ER diagram is up-to-date.

| Check | What to verify |
|---|---|
| Marker comments exist | README contains `<!-- ENTITY_DIAGRAM_EN:START -->` and `<!-- ENTITY_DIAGRAM_ES:START -->` |
| All entities covered | Every `@Entity` class in `entity/` package appears in the diagram |
| Mermaid syntax | Content inside markers is valid Mermaid `erDiagram` syntax |
| Relationships correct | `@ManyToOne`/`@JoinColumn` relationships are reflected with correct cardinality |
| No stale entities | Removed entities (if any) are not in the diagram |

### `api-concept-map`

Verify README API endpoint map is up-to-date.

| Check | What to verify |
|---|---|
| Marker comments exist | README contains `<!-- API_CONCEPT_MAP_EN:START -->` and `<!-- API_CONCEPT_MAP_ES:START -->` |
| All controllers covered | Every `@RestController` has a subgraph |
| All endpoints covered | Each `@GetMapping`/`@PostMapping`/`@PutMapping`/`@DeleteMapping` appears |
| Layer flow present | The diagram includes Client → Controller → Service → Repository → Database flow |
| Mermaid syntax | Content inside markers is valid Mermaid syntax |

### `audit-review`

Verify audit output is accurate and comprehensive.

| Check | What to verify |
|---|---|
| All categories checked | Output mentions all 10 audit categories |
| No false positives | Spot-check 2-3 reported violations by reading actual source files |
| No false negatives | Verify a known violation (if any) was caught |
| Severity correct | Severity labels match the definitions in the skill |
| Summary present | Report ends with severity counts summary |

### `generate-test`

Verify generated tests follow project patterns.

| Check | What to verify |
|---|---|
| File exists | `{Entity}ControllerTest.java` exists |
| Imports correct | Uses `@WebMvcTest`, `MockMvc`, `@MockitoBean`, `ObjectMapper` |
| Setup correct | `@BeforeEach` with `objectMapper.findAndRegisterModules()` |
| Pattern correct | Method naming: `{method}_{scenario}_ShouldReturn{status}` |
| Coverage | Each controller endpoint has: happy path, 400 (if body), 404 (if ID) |
| Compilation | `mvn compile` passes |
| Tests pass | `mvn test -Dtest={Entity}ControllerTest` passes |

### `project-overview`

Verify the overview matches current codebase state.

| Check | What to verify |
|---|---|
| Stack accurate | Versions match `pom.xml` (Java, Spring Boot, etc.) |
| Architecture layers | All 6 layers + cross-cutting listed correctly |
| Entities | All `@Entity` classes listed with their fields |
| Endpoints | All controller endpoints listed with method + path |
| Conventions | Matches actual conventions in source files and AGENTS.md |

## Output format

```
## Skill: {name} — {PASS / FAIL / PARTIAL}

### Overall
- ✅ Files exist: all required files present
- ❌ Compilation: ...
- ✅ Tests: ...

### Files checked
- ✅ `file/path.java` — OK
- ❌ `file/path.java` — missing `@Transactional`
- ➖ `file/path.java` — not applicable

### Convention compliance
- ✅ Naming: ...
- ❌ Annotations: ...

### Details / Issues found
1. [HIGH] Missing `@Transactional` on `delete` method in `...ServiceImpl.java:42`
2. [LOW] Test file missing `@WebMvcTest` annotation

### Suggestions
- Add `@Transactional` override to delete method
- Add `@WebMvcTest(Controller.class)` to test file
```

## Rules

- Do NOT modify any files — only read and report
- Run `mvn compile` with a 120s timeout; if it fails, report the error
- If the target skill has not been run yet (no output to check), report "No changes to verify"
- Base all checks on actual file contents, not assumptions
- If a file can't be read, note it as a potential issue
- For `entity-mapper` and `api-concept-map`, read README markers directly, do not guess
- Prioritize issues by severity in the report
