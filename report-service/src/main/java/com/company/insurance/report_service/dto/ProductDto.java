package com.company.insurance.report_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ProductDto(@JsonProperty("productId")
                         Long productId,

                         @JsonProperty("name")
                         String name){
}
