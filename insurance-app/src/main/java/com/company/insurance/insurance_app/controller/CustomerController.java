package com.company.insurance.insurance_app.controller;

import com.company.insurance.insurance_app.dto.CustomerCreateRequest;
import com.company.insurance.insurance_app.dto.CustomerResponse;
import com.company.insurance.insurance_app.dto.CustomerUpdateRequest;
import com.company.insurance.insurance_app.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService){
        this.customerService = customerService;
    }

    @GetMapping
    public List<CustomerResponse> getAll(){
        return customerService.getAll();
    }

    @GetMapping("/{id}")
    public CustomerResponse getById(@PathVariable Long id) {
        return customerService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponse create(@Valid @RequestBody CustomerCreateRequest request){
        return customerService.create(request);
    }

    @PutMapping("/{id}")
    public CustomerResponse update(@PathVariable Long id, @Valid @RequestBody CustomerUpdateRequest request) {
        return customerService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){
        customerService.delete(id);
    }
}
