package com.company.insurance.product_service.service;


import com.company.insurance.product_service.dto.ProductRequest;
import com.company.insurance.product_service.dto.ProductResponse;
import com.company.insurance.product_service.entity.Product;
import com.company.insurance.product_service.exception.ProductNotFoundException;
import com.company.insurance.product_service.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository){this.productRepository = productRepository;}

    private ProductResponse toResponse(Product product){
        return new ProductResponse(product.getProductId(),
                product.getName(), product.isDeleted(), product.getDescription(), product.getCreatedBy(),
                product.getCreateDate(), product.getUpdatedBy());
    }

    public List<ProductResponse> getAll(){
        List<Product> products = productRepository.findAllByIsDeletedFalse();
        List<ProductResponse> responses = new ArrayList<>();
        for(Product product : products){
            responses.add(toResponse(product));
        }

        return responses;
    }

    public ProductResponse getById(Long id){
        Product product = productRepository.findByProductIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        return toResponse(product);
    }

    public ProductResponse create(ProductRequest request){
        Product product = new Product();
        product.setName(request.name());
        product.setDescription(request.description());
        product.setCreatedBy(request.createdBy());
        product.setCreateDate(request.createDate());
        product.setUpdatedBy(request.updatedBy());

        Product saved = productRepository.save(product);

        return toResponse(saved);
    }

    public ProductResponse update(Long id, ProductRequest request){
        Product product = productRepository.findByProductIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        product.setName(request.name());
        product.setDescription(request.description());
        product.setCreatedBy(request.createdBy());
        product.setCreateDate(request.createDate());
        product.setUpdatedBy(request.updatedBy());

        Product saved = productRepository.save(product);
        return toResponse(saved);
    }

    public void delete(Long id){
        Product product = productRepository.findByProductIdAndIsDeletedFalse(id)
                        .orElseThrow(() -> new ProductNotFoundException(id));
        product.setDeleted(true);
        productRepository.save(product);
    }
}
