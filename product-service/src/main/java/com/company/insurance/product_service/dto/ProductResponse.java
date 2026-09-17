package com.company.insurance.product_service.dto;


import java.time.LocalDate;

public record ProductResponse(Long productId,
                              String name,
                              boolean isDeleted,
                              String description,
                              String createdBy,
                              LocalDate createDate,
                              String updatedBy) {
}
