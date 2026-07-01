package es.andres.biblioteca.Biblioteca.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.andres.biblioteca.Biblioteca.dto.CategoryDto;
import es.andres.biblioteca.Biblioteca.exceptions.BadRequestException;
import es.andres.biblioteca.Biblioteca.exceptions.ResourceNotFoundException;
import es.andres.biblioteca.Biblioteca.service.CategoryService;
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

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    @MockitoBean
    private CategoryService categoryService;

    @Test
    void createCategory_ShouldReturn201() throws Exception {
        CategoryDto request = new CategoryDto(null, "Ficción");
        CategoryDto response = new CategoryDto(1L, "Ficción");

        when(categoryService.createCategory(any(CategoryDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.categoryId").value(1))
                .andExpect(jsonPath("$.categoryName").value("Ficción"));
    }

    @Test
    void createCategory_WithBlankName_ShouldReturn400() throws Exception {
        CategoryDto request = new CategoryDto(null, "");

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.errorDetails").value("Incorrect request"));
    }

    @Test
    void createCategory_WithShortName_ShouldReturn400() throws Exception {
        CategoryDto request = new CategoryDto(null, "AB");

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.errorDetails").value("Incorrect request"));
    }

    @Test
    void createCategory_DuplicateName_ShouldReturn400() throws Exception {
        CategoryDto request = new CategoryDto(null, "Ficción");

        when(categoryService.createCategory(any(CategoryDto.class)))
                .thenThrow(new BadRequestException("There is already a category with that name"));

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("There is already a category with that name"))
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.errorDetails").value("Incorrect request"));
    }

    @Test
    void findAllCategories_ShouldReturn200() throws Exception {
        List<CategoryDto> categories = List.of(
                new CategoryDto(1L, "Ficción"),
                new CategoryDto(2L, "Ciencia")
        );

        when(categoryService.findAllCategories()).thenReturn(categories);

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].categoryName").value("Ficción"))
                .andExpect(jsonPath("$[1].categoryName").value("Ciencia"));
    }

    @Test
    void findAllCategories_EmptyList_ShouldReturn200() throws Exception {
        when(categoryService.findAllCategories()).thenReturn(List.of());

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }

    @Test
    void findCategoryById_ShouldReturn200() throws Exception {
        CategoryDto category = new CategoryDto(1L, "Ficción");

        when(categoryService.findCategoryById(1L)).thenReturn(category);

        mockMvc.perform(get("/api/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryId").value(1))
                .andExpect(jsonPath("$.categoryName").value("Ficción"));
    }

    @Test
    void findCategoryById_NotFound_ShouldReturn404() throws Exception {
        when(categoryService.findCategoryById(999L))
                .thenThrow(new ResourceNotFoundException("Category with id 999 not found"));

        mockMvc.perform(get("/api/categories/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Category with id 999 not found"))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.errorDetails").value("Resource not found"));
    }

    @Test
    void updateCategory_ShouldReturn200() throws Exception {
        CategoryDto request = new CategoryDto(null, "Ficción actualizada");
        CategoryDto response = new CategoryDto(1L, "Ficción actualizada");

        when(categoryService.updateCategory(anyLong(), any(CategoryDto.class))).thenReturn(response);

        mockMvc.perform(put("/api/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryId").value(1))
                .andExpect(jsonPath("$.categoryName").value("Ficción actualizada"));
    }

    @Test
    void updateCategory_WithBlankName_ShouldReturn400() throws Exception {
        CategoryDto request = new CategoryDto(null, "");

        mockMvc.perform(put("/api/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.errorDetails").value("Incorrect request"));
    }

    @Test
    void updateCategory_NotFound_ShouldReturn404() throws Exception {
        CategoryDto request = new CategoryDto(null, "Inexistente");

        when(categoryService.updateCategory(anyLong(), any(CategoryDto.class)))
                .thenThrow(new ResourceNotFoundException("Category with id 999 not found"));

        mockMvc.perform(put("/api/categories/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Category with id 999 not found"))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.errorDetails").value("Resource not found"));
    }

    @Test
    void deleteCategory_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/categories/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteCategory_NotFound_ShouldReturn404() throws Exception {
        doThrow(new ResourceNotFoundException("Category with id 999 not found"))
                .when(categoryService).deleteCategory(999L);

        mockMvc.perform(delete("/api/categories/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Category with id 999 not found"))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.errorDetails").value("Resource not found"));
    }
}
