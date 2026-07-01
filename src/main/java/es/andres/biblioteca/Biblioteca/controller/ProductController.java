package es.andres.biblioteca.Biblioteca.controller;

import es.andres.biblioteca.Biblioteca.dto.ProductDto;
import es.andres.biblioteca.Biblioteca.entity.ProductState;
import es.andres.biblioteca.Biblioteca.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductDto>> listProducts() {
        List<ProductDto> products = productService.findAllProducts();
        return ResponseEntity.ok(products);
    }

    @PostMapping("/{categoryId}")
    public ResponseEntity<ProductDto> registerProduct(
            @PathVariable Long categoryId,
            @Valid @RequestBody ProductDto productDto
    ) {
        ProductDto productSaved = productService.registerProduct(categoryId, productDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(productSaved);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<ProductDto> findByName(@PathVariable String name) {
        ProductDto product = productService.findProductByName(name);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<ProductDto> findProductById(@PathVariable Long id) {
        ProductDto product = productService.findProductById(id);
        return ResponseEntity.ok(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDto productDto) {
        ProductDto savedProduct = productService.updateProduct(id, productDto);
        return ResponseEntity.ok(savedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/state/{id}")
    public ResponseEntity<ProductDto> changeProductState(@PathVariable Long id, @RequestBody ProductState productState) {
        ProductDto productUpdated = productService.changeProductState(id, productState);
        return ResponseEntity.ok(productUpdated);
    }

    @GetMapping("/state/{state}")
    public ResponseEntity<List<ProductDto>> findProductsByState(@PathVariable ProductState state) {
        List<ProductDto> products = productService.findProductsByState(state);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/category/{categoryName}")
    public ResponseEntity<List<ProductDto>> findProductsByCategory(@PathVariable String categoryName) {
        List<ProductDto> products = productService.findProductsByCategory(categoryName);
        return ResponseEntity.ok(products);
    }
}
