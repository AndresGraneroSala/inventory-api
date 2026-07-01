package es.andres.biblioteca.Biblioteca.service.impl;

import es.andres.biblioteca.Biblioteca.dto.ProductDto;
import es.andres.biblioteca.Biblioteca.entity.Category;
import es.andres.biblioteca.Biblioteca.entity.Product;
import es.andres.biblioteca.Biblioteca.entity.ProductState;
import es.andres.biblioteca.Biblioteca.exceptions.BadRequestException;
import es.andres.biblioteca.Biblioteca.exceptions.ResourceNotFoundException;
import es.andres.biblioteca.Biblioteca.mapper.CategoryMapper;
import es.andres.biblioteca.Biblioteca.mapper.ProductMapper;
import es.andres.biblioteca.Biblioteca.repository.ProductRepository;
import es.andres.biblioteca.Biblioteca.service.CategoryService;
import es.andres.biblioteca.Biblioteca.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    private final CategoryService categoryService;

    private final CategoryMapper categoryMapper;

    @Override
    public ProductDto registerProduct(Long categoryId, ProductDto productDto) {

        if (productDto.getProductPrice() == null || productDto.getProductPrice() <= 0) {
            throw new BadRequestException("Precio tiene que ser mayor que 0");
        }


        Category category = categoryMapper.toEntity(categoryService.findCategoryById(categoryId));

        Product product = productMapper.toEntity(productDto);

        product.setProductCategory(category);

        Product savedProduct = productRepository.save(product);
        return productMapper.toDTO(savedProduct);
    }

    @Override
    public List<ProductDto> findAllProducts() {
        List<Product> products = productRepository.findAll();

        return products.stream().map(productMapper::toDTO).toList();

    }

    @Override
    public ProductDto findProductByName(String name) {

        Product product = productRepository.findByProductName(name)
                .orElseThrow(() -> new ResourceNotFoundException("ProductDto with name " + name + " not found"));

        return productMapper.toDTO(product);

    }

    @Override
    public ProductDto findProductById(Long id) {
        Product product = productRepository.findByProductId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " not found"));
        return productMapper.toDTO(product);

    }


    @Override
    public ProductDto updateProduct(Long id, ProductDto productDto) {
        Product existProduct = productMapper.toEntity(findProductById(id));

        existProduct.setProductName(productDto.getProductName());
        existProduct.setProductDescription(productDto.getProductDescription());
        existProduct.setProductPrice(productDto.getProductPrice());
        existProduct.setProductAmount(productDto.getProductAmount());
        existProduct.setProductState(productDto.getProductState());

        if (productDto.getProductCategory() != null && productDto.getProductCategory().getCategoryId() != null) {
            Category category = categoryMapper.toEntity(categoryService.findCategoryById(productDto.getProductCategory().getCategoryId()));

            existProduct.setProductCategory(category);
        }


        return productMapper.toDTO(productRepository.save(existProduct));
    }

    @Override
    public void deleteProduct(Long id) {
        ProductDto existProductDto = findProductById(id);

        productRepository.deleteById(id);
    }

    @Override
    public ProductDto changeProductState(Long id, ProductState productState) {
        Product existProduct = productMapper.toEntity(findProductById(id));

        existProduct.setProductState(productState);

        return productMapper.toDTO(productRepository.save(existProduct));
    }

    @Override
    public List<ProductDto> findProductsByState(ProductState productState) {
        List<Product> products = productRepository.findByProductState(productState);

        return products.stream().map(productMapper::toDTO).toList();

    }

    @Override
    public List<ProductDto> findProductsByCategory(String categoryName) {
        List<Product> products = productRepository.findByProductCategoryCategoryName(categoryName);
        return products.stream().map(productMapper::toDTO).toList();

    }
}
