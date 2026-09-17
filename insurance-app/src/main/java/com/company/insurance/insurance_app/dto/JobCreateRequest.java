package com.company.insurance.insurance_app.dto;

import jakarta.validation.constraints.*;

public record JobCreateRequest(@NotBlank String name,
                               @NotNull @Min(1) @Max(5) Short risk) {
}
