package es.andres.biblioteca.Biblioteca.service;

import es.andres.biblioteca.Biblioteca.dto.ProductDto;
import es.andres.biblioteca.Biblioteca.entity.Category;
import es.andres.biblioteca.Biblioteca.entity.Product;
import es.andres.biblioteca.Biblioteca.entity.ProductState;

import java.util.List;

public interface ProductService {
    ProductDto registerProduct(Long categoryId, ProductDto product);


    List<ProductDto> findAllProducts();

    ProductDto findProductByName(String name);
    ProductDto findProductById(Long id);

    ProductDto updateProduct(Long id, ProductDto product);

    void deleteProduct(Long id);

    ProductDto changeProductState(Long id, ProductState productState);

    List<ProductDto> findProductsByState(ProductState productState);

    List<ProductDto> findProductsByCategory(String categoryName);

}
