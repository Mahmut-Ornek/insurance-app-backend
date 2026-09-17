package com.company.insurance.insurance_app.dto;

import jakarta.validation.constraints.*;

public record JobUpdateRequest(@NotNull @Min(1) @Max(5) Short risk) {
}
