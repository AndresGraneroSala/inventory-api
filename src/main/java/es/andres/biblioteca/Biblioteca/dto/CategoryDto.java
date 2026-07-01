package es.andres.biblioteca.Biblioteca.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {

    private Long categoryId;

    @NotBlank
    @Size(min = 3, max = 50)
    private String categoryName;





}
