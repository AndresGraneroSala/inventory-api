package es.andres.biblioteca.Biblioteca.mapper;

import es.andres.biblioteca.Biblioteca.dto.AuthorDto;
import es.andres.biblioteca.Biblioteca.entity.Author;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AuthorMapper {

    private final ModelMapper modelMapper;

    public AuthorDto toDto(Author author) {
        return modelMapper.map(author, AuthorDto.class);
    }

    public Author toEntity(AuthorDto authorDto) {
        return modelMapper.map(authorDto, Author.class);
    }
}
