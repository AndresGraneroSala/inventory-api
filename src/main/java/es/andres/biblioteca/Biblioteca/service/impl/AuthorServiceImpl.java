package es.andres.biblioteca.Biblioteca.service.impl;

import es.andres.biblioteca.Biblioteca.dto.AuthorDto;
import es.andres.biblioteca.Biblioteca.entity.Author;
import es.andres.biblioteca.Biblioteca.exceptions.BadRequestException;
import es.andres.biblioteca.Biblioteca.exceptions.ResourceNotFoundException;
import es.andres.biblioteca.Biblioteca.mapper.AuthorMapper;
import es.andres.biblioteca.Biblioteca.repository.AuthorRepository;
import es.andres.biblioteca.Biblioteca.service.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    private final AuthorMapper authorMapper;

    @Override
    @Transactional
    public AuthorDto createAuthor(AuthorDto authorDto) {
        if (authorRepository.existsByAuthorName(authorDto.getAuthorName())) {
            throw new BadRequestException("There is already an author with that name");
        }

        Author author = authorMapper.toEntity(authorDto);
        return authorMapper.toDto(authorRepository.save(author));
    }

    @Override
    public List<AuthorDto> findAllAuthors() {
        return authorRepository.findAll().stream()
                .map(authorMapper::toDto)
                .toList();
    }

    @Override
    public AuthorDto findAuthorById(Long authorId) {
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Author with id " + authorId + " not found"));
        return authorMapper.toDto(author);
    }

    @Override
    @Transactional
    public AuthorDto updateAuthor(Long authorId, AuthorDto authorDto) {
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Author with id " + authorId + " not found"));

        author.setAuthorName(authorDto.getAuthorName());
        author.setAuthorNationality(authorDto.getAuthorNationality());
        author.setBirthYear(authorDto.getBirthYear());

        return authorMapper.toDto(authorRepository.save(author));
    }

    @Override
    @Transactional
    public void deleteAuthor(Long authorId) {
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Author with id " + authorId + " not found"));

        authorRepository.deleteById(authorId);
    }
}
