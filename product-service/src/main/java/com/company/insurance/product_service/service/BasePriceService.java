package com.company.insurance.product_service.service;


import com.company.insurance.product_service.dto.CreateBasePriceRequest;
import com.company.insurance.product_service.dto.BasePriceResponse;
import com.company.insurance.product_service.dto.UpdateBasePriceRequest;
import com.company.insurance.product_service.entity.BasePrice;
import com.company.insurance.product_service.exception.BasePriceNotFoundException;
import com.company.insurance.product_service.repository.BasePriceRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BasePriceService {
    private final BasePriceRepository basePriceRepository;

    public BasePriceService(BasePriceRepository basePriceRepository){this.basePriceRepository = basePriceRepository;}

    private BasePriceResponse toResponse(BasePrice basePrice){
        return new BasePriceResponse(basePrice.getPriceId(), basePrice.getProductId(), basePrice.getBasePrice(),
                basePrice.getCurrencyCode(), basePrice.getMonth(), basePrice.getYear(), basePrice.isDeleted(),
                basePrice.getCreatedBy(), basePrice.getUpdatedBy());
    }

    public List<BasePriceResponse> getAll(){
        List<BasePrice> basePrices = basePriceRepository.findAllByIsDeletedFalse();
        List<BasePriceResponse> responses = new ArrayList<>();
        for (BasePrice basePrice : basePrices){
            responses.add(toResponse(basePrice));
        }
        return responses;
    }

    public BasePriceResponse getById(Long id){
        BasePrice basePrice = basePriceRepository.findByPriceIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new BasePriceNotFoundException(id));
        return toResponse(basePrice);
    }

    public BasePriceResponse getByProductAndPeriod(Long productId, Integer month, Integer year) {
        BasePrice basePrice = basePriceRepository
                .findByProductIdAndMonthAndYearAndIsDeletedFalse(productId, month, year)
                .orElseThrow(() -> new IllegalArgumentException("Belirtilen ürün ve döneme ait baz fiyat bulunamadı."));
        return toResponse(basePrice);
    }

    public BasePriceResponse create(CreateBasePriceRequest request){
        BasePrice basePrice = new BasePrice();

        basePrice.setProductId(request.productId());
        basePrice.setBasePrice(request.basePrice());
        basePrice.setCurrencyCode(request.currencyCode());
        basePrice.setMonth(request.month());
        basePrice.setYear(request.year());
        basePrice.setCreatedBy(request.createdBy());

        BasePrice saved = basePriceRepository.save(basePrice);

        return toResponse(saved);
    }

    public BasePriceResponse update(Long id, UpdateBasePriceRequest request){
        BasePrice basePrice = basePriceRepository.findByPriceIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new BasePriceNotFoundException(id));

        basePrice.setBasePrice(request.basePrice());
        basePrice.setCurrencyCode(request.currencyCode());
        basePrice.setMonth(request.month());
        basePrice.setYear(request.year());
        basePrice.setUpdatedBy(request.updatedBy());

        BasePrice saved = basePriceRepository.save(basePrice);

        return toResponse(saved);
    }

    public void delete(Long id){
        BasePrice basePrice = basePriceRepository.findByPriceIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new BasePriceNotFoundException(id));
        basePrice.setDeleted(true);
        basePriceRepository.save(basePrice);
    }
}
