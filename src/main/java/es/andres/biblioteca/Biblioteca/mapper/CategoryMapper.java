package es.andres.biblioteca.Biblioteca.mapper;

import es.andres.biblioteca.Biblioteca.dto.CategoryDto;
import es.andres.biblioteca.Biblioteca.entity.Category;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CategoryMapper {

    private final ModelMapper modelMapper;

    public CategoryDto toDto(Category category){
        return modelMapper.map(category,CategoryDto.class);
    }

    public Category toEntity(CategoryDto categoryDto){
        return modelMapper.map(categoryDto,Category.class);
    }

}
