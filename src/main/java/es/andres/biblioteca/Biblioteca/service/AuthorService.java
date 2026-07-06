package es.andres.biblioteca.Biblioteca.service;

import es.andres.biblioteca.Biblioteca.dto.AuthorDto;

import java.util.List;

public interface AuthorService {
    AuthorDto createAuthor(AuthorDto authorDto);
    List<AuthorDto> findAllAuthors();
    AuthorDto findAuthorById(Long authorId);
    AuthorDto updateAuthor(Long authorId, AuthorDto authorDto);
    void deleteAuthor(Long authorId);
}
