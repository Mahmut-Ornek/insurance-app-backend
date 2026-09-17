package com.company.insurance.parameter_service.exception;

public class DistrictNotFoundException extends RuntimeException {
  public DistrictNotFoundException(Long id) {
    super("İlçe bulunamadı: " + id);
  }
}
