---
name: api-concept-map
description: Generate API endpoint map from @RestController classes and update the README.
---

# Skill: api-concept-map

Auto-generate a conceptual map of all API endpoints from `@RestController` classes and insert it into the README.

## When to use

After adding, removing, or modifying REST controllers — or when the README concept map section is stale.

## What it does

1. Scans all files under `src/main/java/**/controller/` for `@RestController` annotations
2. For each controller, extracts:
   - `@RequestMapping` base path
   - Each endpoint method with `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`, `@PatchMapping`
   - Full path (base + method-level mapping)
   - HTTP method
   - Parameters (`@PathVariable`, `@RequestParam`, `@RequestBody`)
   - Return type
   - A brief description derived from the method name or Javadoc
3. Generates a Mermaid `flowchart LR` or `graph TD` diagram showing the request flow: Client → Controller → Service → Repository
4. Inserts the diagram into `README.md` between the marker comments:

   - English section: `<!-- API_CONCEPT_MAP_EN:START --> ... <!-- API_CONCEPT_MAP_EN:END -->`
   - Spanish section: `<!-- API_CONCEPT_MAP_ES:START --> ... <!-- API_CONCEPT_MAP_ES:END -->`

## Output format

### English
```markdown
<!-- API_CONCEPT_MAP_EN:START -->
```mermaid
graph TD
    subgraph "API /api/products"
        GET_products["GET /api/products"]
        POST_products["POST /api/products/{categoryId}"]
        GET_product_by_id["GET /api/products/id/{id}"]
        GET_product_by_name["GET /api/products/name/{name}"]
        PUT_product["PUT /api/products/{id}"]
        DELETE_product["DELETE /api/products/{id}"]
        PUT_product_state["PUT /api/products/state/{id}"]
        GET_products_by_state["GET /api/products/state/{state}"]
        GET_products_by_category["GET /api/products/category/{categoryName}"]
    end
    subgraph "API /api/categories"
        GET_categories["GET /api/categories"]
        POST_category["POST /api/categories"]
        GET_category_by_id["GET /api/categories/{id}"]
        PUT_category["PUT /api/categories/{id}"]
        DELETE_category["DELETE /api/categories/{id}"]
    end
    subgraph "Layers"
        Client --> Controller
        Controller --> Service
        Service --> Repository
        Repository --> Database[(MySQL)]
    end
```
<!-- API_CONCEPT_MAP_EN:END -->
```

### Spanish
Same diagram, but between `<!-- API_CONCEPT_MAP_ES:START -->` and `<!-- API_CONCEPT_MAP_ES:END -->` with labels in Spanish.

## Rules

- Do NOT modify any content outside the marker comments
- Do NOT create additional files — only update `README.md`
- Organize endpoints by controller/resource group (subgraphs)
- Include the layer flow diagram (Client → Controller → Service → Repository → Database)
- Verify the Mermaid syntax is valid before writing
- Run `mvn test` after updating to ensure nothing is broken
