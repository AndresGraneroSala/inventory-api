package es.andres.biblioteca.Biblioteca.service;

import es.andres.biblioteca.Biblioteca.dto.CategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(CategoryDto categoryDto);
    List<CategoryDto> findAllCategories();
    CategoryDto findCategoryById(Long categoryId);
    CategoryDto updateCategory(Long categoryId, CategoryDto categoryDto);
    void deleteCategory(Long categoryId);
}
