package es.andres.biblioteca.Biblioteca.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorDto {

    private Long authorId;

    @NotBlank
    @Size(min = 3, max = 100)
    private String authorName;

    @Size(max = 50)
    private String authorNationality;

    @Min(value = 1)
    private Integer birthYear;
}
