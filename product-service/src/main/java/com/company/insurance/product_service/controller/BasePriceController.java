package com.company.insurance.product_service.controller;


import com.company.insurance.product_service.dto.CreateBasePriceRequest;
import com.company.insurance.product_service.dto.BasePriceResponse;
import com.company.insurance.product_service.dto.UpdateBasePriceRequest;
import com.company.insurance.product_service.service.BasePriceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/baseprices")
public class BasePriceController {
    private final BasePriceService basePriceService;

    public BasePriceController(BasePriceService basePriceService){this.basePriceService = basePriceService;}

    @GetMapping
    public List<BasePriceResponse> getAll() {return basePriceService.getAll();}

    @GetMapping("/{id}")
    public BasePriceResponse getById(@PathVariable Long id){return basePriceService.getById(id);}

    @GetMapping("/search")
    public BasePriceResponse getByProductAndPeriod(
            @RequestParam Long productId,
            @RequestParam Integer month,
            @RequestParam Integer year) {
        return basePriceService.getByProductAndPeriod(productId, month, year);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BasePriceResponse create(@Valid @RequestBody CreateBasePriceRequest request){
        return basePriceService.create(request);
    }

    @PutMapping("/{id}")
    public BasePriceResponse update(@PathVariable Long id, @Valid @RequestBody UpdateBasePriceRequest request){
        return basePriceService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){basePriceService.delete(id);}
}
