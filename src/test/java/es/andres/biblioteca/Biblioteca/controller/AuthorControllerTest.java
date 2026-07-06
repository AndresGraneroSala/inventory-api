package es.andres.biblioteca.Biblioteca.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.andres.biblioteca.Biblioteca.dto.AuthorDto;
import es.andres.biblioteca.Biblioteca.exceptions.BadRequestException;
import es.andres.biblioteca.Biblioteca.exceptions.ResourceNotFoundException;
import es.andres.biblioteca.Biblioteca.service.AuthorService;
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

@WebMvcTest(AuthorController.class)
class AuthorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    @MockitoBean
    private AuthorService authorService;

    @Test
    void createAuthor_ShouldReturn201() throws Exception {
        AuthorDto request = new AuthorDto(null, "Gabriel García Márquez", "Colombiana", 1927);
        AuthorDto response = new AuthorDto(1L, "Gabriel García Márquez", "Colombiana", 1927);

        when(authorService.createAuthor(any(AuthorDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.authorId").value(1))
                .andExpect(jsonPath("$.authorName").value("Gabriel García Márquez"))
                .andExpect(jsonPath("$.authorNationality").value("Colombiana"))
                .andExpect(jsonPath("$.birthYear").value(1927));
    }

    @Test
    void createAuthor_WithBlankName_ShouldReturn400() throws Exception {
        AuthorDto request = new AuthorDto(null, "", "Colombiana", 1927);

        mockMvc.perform(post("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.errorDetails").value("Incorrect request"));
    }

    @Test
    void createAuthor_WithShortName_ShouldReturn400() throws Exception {
        AuthorDto request = new AuthorDto(null, "AB", "Colombiana", 1927);

        mockMvc.perform(post("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.errorDetails").value("Incorrect request"));
    }

    @Test
    void createAuthor_DuplicateName_ShouldReturn400() throws Exception {
        AuthorDto request = new AuthorDto(null, "Gabriel García Márquez", "Colombiana", 1927);

        when(authorService.createAuthor(any(AuthorDto.class)))
                .thenThrow(new BadRequestException("There is already an author with that name"));

        mockMvc.perform(post("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("There is already an author with that name"))
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.errorDetails").value("Incorrect request"));
    }

    @Test
    void findAllAuthors_ShouldReturn200() throws Exception {
        List<AuthorDto> authors = List.of(
                new AuthorDto(1L, "Gabriel García Márquez", "Colombiana", 1927),
                new AuthorDto(2L, "Julio Cortázar", "Argentina", 1914)
        );

        when(authorService.findAllAuthors()).thenReturn(authors);

        mockMvc.perform(get("/api/authors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].authorName").value("Gabriel García Márquez"))
                .andExpect(jsonPath("$[1].authorName").value("Julio Cortázar"));
    }

    @Test
    void findAllAuthors_EmptyList_ShouldReturn200() throws Exception {
        when(authorService.findAllAuthors()).thenReturn(List.of());

        mockMvc.perform(get("/api/authors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }

    @Test
    void findAuthorById_ShouldReturn200() throws Exception {
        AuthorDto author = new AuthorDto(1L, "Gabriel García Márquez", "Colombiana", 1927);

        when(authorService.findAuthorById(1L)).thenReturn(author);

        mockMvc.perform(get("/api/authors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authorId").value(1))
                .andExpect(jsonPath("$.authorName").value("Gabriel García Márquez"));
    }

    @Test
    void findAuthorById_NotFound_ShouldReturn404() throws Exception {
        when(authorService.findAuthorById(999L))
                .thenThrow(new ResourceNotFoundException("Author with id 999 not found"));

        mockMvc.perform(get("/api/authors/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Author with id 999 not found"))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.errorDetails").value("Resource not found"));
    }

    @Test
    void updateAuthor_ShouldReturn200() throws Exception {
        AuthorDto request = new AuthorDto(null, "Gabriel García Márquez", "Mexicana", 1927);
        AuthorDto response = new AuthorDto(1L, "Gabriel García Márquez", "Mexicana", 1927);

        when(authorService.updateAuthor(anyLong(), any(AuthorDto.class))).thenReturn(response);

        mockMvc.perform(put("/api/authors/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authorId").value(1))
                .andExpect(jsonPath("$.authorNationality").value("Mexicana"));
    }

    @Test
    void updateAuthor_WithBlankName_ShouldReturn400() throws Exception {
        AuthorDto request = new AuthorDto(null, "", "Colombiana", 1927);

        mockMvc.perform(put("/api/authors/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.errorDetails").value("Incorrect request"));
    }

    @Test
    void updateAuthor_NotFound_ShouldReturn404() throws Exception {
        AuthorDto request = new AuthorDto(null, "Inexistente", null, null);

        when(authorService.updateAuthor(anyLong(), any(AuthorDto.class)))
                .thenThrow(new ResourceNotFoundException("Author with id 999 not found"));

        mockMvc.perform(put("/api/authors/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Author with id 999 not found"))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.errorDetails").value("Resource not found"));
    }

    @Test
    void deleteAuthor_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/authors/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteAuthor_NotFound_ShouldReturn404() throws Exception {
        doThrow(new ResourceNotFoundException("Author with id 999 not found"))
                .when(authorService).deleteAuthor(999L);

        mockMvc.perform(delete("/api/authors/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Author with id 999 not found"))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.errorDetails").value("Resource not found"));
    }
}
