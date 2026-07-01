package es.andres.biblioteca.Biblioteca.mapper;

import es.andres.biblioteca.Biblioteca.dto.CategoryDto;
import es.andres.biblioteca.Biblioteca.entity.Category;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    @Autowired
    private ModelMapper modelMapper;

    public CategoryDto toDto(Category category){
        return modelMapper.map(category,CategoryDto.class);
    }

    public Category toEntity(CategoryDto categoryDto){
        return modelMapper.map(categoryDto,Category.class);
    }

}
