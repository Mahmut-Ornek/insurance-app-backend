package com.company.insurance.collection_service.controller;

import com.company.insurance.collection_service.dto.CollectionCreateRequest;
import com.company.insurance.collection_service.dto.CollectionPaymentItemDto;
import com.company.insurance.collection_service.dto.CollectionPeriodSummaryDto;
import com.company.insurance.collection_service.dto.CollectionResponse;
import com.company.insurance.collection_service.service.CollectionService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/collections")
public class CollectionController {
    private final CollectionService collectionService;
    public CollectionController(CollectionService collectionService){
        this.collectionService = collectionService;
    }

    @GetMapping
    public List<CollectionResponse> getAll(){return collectionService.getAll();}

    @GetMapping("/{id}")
    public CollectionResponse getById(@PathVariable Long id){return collectionService.getById(id);}

    /**
     * DİKKAT: Bu endpoint iş mantığı gereği yalnızca Application-Service
     * tarafından otomatik tetiklenmelidir. Doğrudan çağrılmamalıdır.
     */
    @PostMapping
    public CollectionResponse create(@Valid @RequestBody CollectionCreateRequest request){return collectionService.create(request);}

    @PostMapping("/{id}/payments")
    public ResponseEntity<CollectionResponse> recordPayment(
            @PathVariable Long id,
            @Valid @RequestBody CollectionPaymentItemDto paymentDto) {
        return ResponseEntity.ok(collectionService.recordPayment(id, paymentDto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){collectionService.delete(id);}

    @GetMapping("/summary")
    public ResponseEntity<CollectionPeriodSummaryDto> getPaymentSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(collectionService.getPaymentSummary(from, to));
    }
}
