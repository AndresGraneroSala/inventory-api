package es.andres.biblioteca.Biblioteca.mapper;

import es.andres.biblioteca.Biblioteca.dto.ProductDto;
import es.andres.biblioteca.Biblioteca.entity.Product;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ProductMapper {
    private final ModelMapper modelMapper;

    public Product toEntity(ProductDto productDto){
        return modelMapper.map(productDto,Product.class);
    }

    public void toEntity(ProductDto productDto,Product existsProduct){
        modelMapper.map(productDto,existsProduct);
    }

    public ProductDto toDto(Product product){
        return modelMapper.map(product,ProductDto.class);
    }


}
