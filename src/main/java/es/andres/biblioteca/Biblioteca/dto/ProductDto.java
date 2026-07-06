package es.andres.biblioteca.Biblioteca.dto;

import es.andres.biblioteca.Biblioteca.entity.ProductState;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {

    private Long productId;

    @NotBlank
    @Size(max = 100)
    private String productName;

    @Size(max = 255)
    private String productDescription;

    @NotNull
    @Min(value = 0)
    private Double productPrice;

    @NotNull
    @Min(value = 1)
    private int productAmount;

    @NotNull
    private ProductState productState;

    @NotNull
    private CategoryDto productCategory;

    private AuthorDto productAuthor;
}
