package es.andres.biblioteca.Biblioteca.service.impl;

import es.andres.biblioteca.Biblioteca.dto.CategoryDto;
import es.andres.biblioteca.Biblioteca.entity.Category;
import es.andres.biblioteca.Biblioteca.exceptions.BadRequestException;
import es.andres.biblioteca.Biblioteca.exceptions.ResourceNotFoundException;
import es.andres.biblioteca.Biblioteca.mapper.CategoryMapper;
import es.andres.biblioteca.Biblioteca.repository.CategoryRepository;
import es.andres.biblioteca.Biblioteca.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) {
        if (categoryRepository.existsByCategoryName(categoryDto.getCategoryName())) {
            throw new BadRequestException("There is already a category with that name");
        }

        Category category = categoryMapper.toEntity(categoryDto);
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public List<CategoryDto> findAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    @Override
    public CategoryDto findCategoryById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category with id " + categoryId + " not found"));
        return categoryMapper.toDto(category);
    }

    @Override
    public CategoryDto updateCategory(Long categoryId, CategoryDto categoryDto) {
        Category categoryExists = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category with id " + categoryId + " not found"));

        categoryExists.setCategoryName(categoryDto.getCategoryName());

        return categoryMapper.toDto(categoryRepository.save(categoryExists));
    }

    @Override
    public void deleteCategory(Long categoryId) {
        Category categoryExists = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category with id " + categoryId + " not found"));

        categoryRepository.deleteById(categoryId);
    }
}