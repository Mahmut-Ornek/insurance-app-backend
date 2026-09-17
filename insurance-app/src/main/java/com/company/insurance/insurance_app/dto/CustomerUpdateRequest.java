package com.company.insurance.insurance_app.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record CustomerUpdateRequest(@NotBlank String name,
                                    @NotBlank String surname,
                                    @NotBlank @Email(message = "Lütfen geçerli bir e-posta formatı giriniz (örn: ornek@alanadi.com)") String email,
                                    Long jobId,
                                    String motherName,
                                    String fatherName,
                                    @NotNull @Past LocalDate birthDate,
                                    @Size(max = 500) String address,
                                    String username) {
}
