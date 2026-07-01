package es.andres.biblioteca.Biblioteca.repository;


import es.andres.biblioteca.Biblioteca.entity.Category;
import es.andres.biblioteca.Biblioteca.entity.Product;
import es.andres.biblioteca.Biblioteca.entity.ProductState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
    Optional<Product> findByProductName(String productName);
    Optional<Product> findByProductId(Long id);
    List<Product> findByProductState(ProductState productState);
    List<Product> findByProductCategoryCategoryName(String categoryName);
}

