package es.andres.biblioteca.Biblioteca.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.andres.biblioteca.Biblioteca.dto.ProductDto;
import es.andres.biblioteca.Biblioteca.entity.Category;
import es.andres.biblioteca.Biblioteca.entity.ProductState;
import es.andres.biblioteca.Biblioteca.exceptions.BadRequestException;
import es.andres.biblioteca.Biblioteca.exceptions.ResourceNotFoundException;
import es.andres.biblioteca.Biblioteca.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    @MockitoBean
    private ProductService productService;

    private final Category category = new Category(1L, "Ficción");

    private ProductDto buildValidProduct() {
        return new ProductDto(null, "Cien años de soledad", "Novela de García Márquez",
                19.99, 5, ProductState.AVAILABLE, category);
    }

    // ==================== GET /api/products ====================

    @Test
    void listProducts_ShouldReturn200() throws Exception {
        ProductDto product = buildValidProduct();
        product.setProductId(1L);
        when(productService.findAllProducts()).thenReturn(List.of(product));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].productName").value("Cien años de soledad"));
    }

    @Test
    void listProducts_Empty_ShouldReturn200() throws Exception {
        when(productService.findAllProducts()).thenReturn(List.of());

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }

    // ==================== POST /api/products/{categoryId} ====================

    @Test
    void registerProduct_ShouldReturn201() throws Exception {
        ProductDto request = buildValidProduct();
        ProductDto response = buildValidProduct();
        response.setProductId(1L);

        when(productService.registerProduct(anyLong(), any(ProductDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.productName").value("Cien años de soledad"));
    }

    @Test
    void registerProduct_WithBlankName_ShouldReturn400() throws Exception {
        ProductDto request = buildValidProduct();
        request.setProductName("");

        mockMvc.perform(post("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.errorDetails").value("Incorrect request"));
    }

    @Test
    void registerProduct_WithNullPrice_ShouldReturn400() throws Exception {
        ProductDto request = buildValidProduct();
        request.setProductPrice(null);

        mockMvc.perform(post("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.errorDetails").value("Incorrect request"));
    }

    @Test
    void registerProduct_WithNullState_ShouldReturn400() throws Exception {
        ProductDto request = buildValidProduct();
        request.setProductState(null);

        mockMvc.perform(post("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.errorDetails").value("Incorrect request"));
    }

    @Test
    void registerProduct_WithNullCategory_ShouldReturn400() throws Exception {
        ProductDto request = buildValidProduct();
        request.setProductCategory(null);

        mockMvc.perform(post("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.errorDetails").value("Incorrect request"));
    }

    @Test
    void registerProduct_PriceZero_ServiceThrowsBadRequest_ShouldReturn400() throws Exception {
        ProductDto request = buildValidProduct();

        when(productService.registerProduct(anyLong(), any(ProductDto.class)))
                .thenThrow(new BadRequestException("Precio tiene que ser mayor que 0"));

        mockMvc.perform(post("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Precio tiene que ser mayor que 0"))
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.errorDetails").value("Incorrect request"));
    }

    @Test
    void registerProduct_CategoryNotFound_ShouldReturn404() throws Exception {
        ProductDto request = buildValidProduct();

        when(productService.registerProduct(anyLong(), any(ProductDto.class)))
                .thenThrow(new ResourceNotFoundException("Category with id 999 not found"));

        mockMvc.perform(post("/api/products/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Category with id 999 not found"))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.errorDetails").value("Resource not found"));
    }

    // ==================== GET /api/products/name/{name} ====================

    @Test
    void findByName_ShouldReturn200() throws Exception {
        ProductDto product = buildValidProduct();
        product.setProductId(1L);

        when(productService.findProductByName("Cien años de soledad")).thenReturn(product);

        mockMvc.perform(get("/api/products/name/Cien años de soledad"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName").value("Cien años de soledad"));
    }

    @Test
    void findByName_NotFound_ShouldReturn404() throws Exception {
        when(productService.findProductByName("Inexistente"))
                .thenThrow(new ResourceNotFoundException("ProductDto with name Inexistente not found"));

        mockMvc.perform(get("/api/products/name/Inexistente"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("ProductDto with name Inexistente not found"))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.errorDetails").value("Resource not found"));
    }

    // ==================== GET /api/products/id/{id} ====================

    @Test
    void findProductById_ShouldReturn200() throws Exception {
        ProductDto product = buildValidProduct();
        product.setProductId(1L);

        when(productService.findProductById(1L)).thenReturn(product);

        mockMvc.perform(get("/api/products/id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(1));
    }

    @Test
    void findProductById_NotFound_ShouldReturn404() throws Exception {
        when(productService.findProductById(999L))
                .thenThrow(new ResourceNotFoundException("Product with id 999 not found"));

        mockMvc.perform(get("/api/products/id/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product with id 999 not found"))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.errorDetails").value("Resource not found"));
    }

    // ==================== PUT /api/products/{id} ====================

    @Test
    void updateProduct_ShouldReturn200() throws Exception {
        ProductDto request = buildValidProduct();
        ProductDto response = buildValidProduct();
        response.setProductId(1L);
        response.setProductName("Actualizado");

        when(productService.updateProduct(anyLong(), any(ProductDto.class))).thenReturn(response);

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.productName").value("Actualizado"));
    }

    @Test
    void updateProduct_WithBlankName_ShouldReturn400() throws Exception {
        ProductDto request = buildValidProduct();
        request.setProductName("");

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.errorDetails").value("Incorrect request"));
    }

    @Test
    void updateProduct_NotFound_ShouldReturn404() throws Exception {
        ProductDto request = buildValidProduct();

        when(productService.updateProduct(anyLong(), any(ProductDto.class)))
                .thenThrow(new ResourceNotFoundException("Product with id 999 not found"));

        mockMvc.perform(put("/api/products/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product with id 999 not found"))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.errorDetails").value("Resource not found"));
    }

    // ==================== DELETE /api/products/{id} ====================

    @Test
    void deleteProduct_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteProduct_NotFound_ShouldReturn404() throws Exception {
        doThrow(new ResourceNotFoundException("Product with id 999 not found"))
                .when(productService).deleteProduct(999L);

        mockMvc.perform(delete("/api/products/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product with id 999 not found"))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.errorDetails").value("Resource not found"));
    }

    // ==================== PUT /api/products/state/{id} ====================

    @Test
    void changeProductState_ShouldReturn200() throws Exception {
        ProductDto response = buildValidProduct();
        response.setProductId(1L);
        response.setProductState(ProductState.NOT_AVAILABLE);

        when(productService.changeProductState(anyLong(), any(ProductState.class))).thenReturn(response);

        mockMvc.perform(put("/api/products/state/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ProductState.NOT_AVAILABLE)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productState").value("NOT_AVAILABLE"));
    }

    @Test
    void changeProductState_NotFound_ShouldReturn404() throws Exception {
        when(productService.changeProductState(anyLong(), any(ProductState.class)))
                .thenThrow(new ResourceNotFoundException("Product with id 999 not found"));

        mockMvc.perform(put("/api/products/state/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ProductState.AVAILABLE)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product with id 999 not found"))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.errorDetails").value("Resource not found"));
    }

    // ==================== GET /api/products/state/{state} ====================

    @Test
    void findProductsByState_ShouldReturn200() throws Exception {
        ProductDto product = buildValidProduct();
        product.setProductId(1L);

        when(productService.findProductsByState(ProductState.AVAILABLE)).thenReturn(List.of(product));

        mockMvc.perform(get("/api/products/state/AVAILABLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].productState").value("AVAILABLE"));
    }

    @Test
    void findProductsByState_Empty_ShouldReturn200() throws Exception {
        when(productService.findProductsByState(ProductState.NOT_AVAILABLE)).thenReturn(List.of());

        mockMvc.perform(get("/api/products/state/NOT_AVAILABLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }

    // ==================== GET /api/products/category/{categoryName} ====================

    @Test
    void findProductsByCategory_ShouldReturn200() throws Exception {
        ProductDto product = buildValidProduct();
        product.setProductId(1L);

        when(productService.findProductsByCategory("Ficción")).thenReturn(List.of(product));

        mockMvc.perform(get("/api/products/category/Ficción"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].productCategory.categoryName").value("Ficción"));
    }

    @Test
    void findProductsByCategory_Empty_ShouldReturn200() throws Exception {
        when(productService.findProductsByCategory("Inexistente")).thenReturn(List.of());

        mockMvc.perform(get("/api/products/category/Inexistente"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }
}
