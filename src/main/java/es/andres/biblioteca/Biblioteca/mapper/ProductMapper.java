package es.andres.biblioteca.Biblioteca.mapper;

import es.andres.biblioteca.Biblioteca.dto.ProductDto;
import es.andres.biblioteca.Biblioteca.entity.Product;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    @Autowired
    private ModelMapper modelMapper;

    public Product toEntity(ProductDto productDto){
        return modelMapper.map(productDto,Product.class);
    }

    public void toEntity(ProductDto productDto,Product existsProduct){
        modelMapper.map(productDto,existsProduct);
    }

    public ProductDto toDTO(Product product){
        return modelMapper.map(product,ProductDto.class);
    }


}
