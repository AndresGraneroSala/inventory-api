package es.andres.biblioteca.Biblioteca.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(name = "product_name",nullable = false)
    private String productName;

    @Column(name = "product_description",nullable = false)
    private String productDescription;

    @Column(name = "product_price",nullable = false)
    private Double productPrice;

    @Column(name = "product_amount",nullable = false)
    private int productAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_state",nullable = false)
    private ProductState productState;


    @ManyToOne
    @JoinColumn(name = "category_id",referencedColumnName = "category_id")
    private Category productCategory;
}
