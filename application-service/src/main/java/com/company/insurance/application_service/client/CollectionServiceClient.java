package com.company.insurance.application_service.client;

import com.company.insurance.application_service.dto.CollectionCreateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;



@Component
public class CollectionServiceClient {
    private static final Logger log = LoggerFactory.getLogger(CollectionServiceClient.class);
    private final RestClient restClient;

    public CollectionServiceClient(@Value("${services.collection-service.url}") String collectionServiceUrl){
        this.restClient = RestClient.builder()
                .baseUrl(collectionServiceUrl)
                .build();
    }

    public void createCollection(CollectionCreateRequest request){
        log.info("Collection-Service'te borc kaydi olusturuluyor. ApplicationId: {}, Tutar: {}",
                request.applicationId(), request.totalAmount());

        restClient.post()
                .uri("/collections")
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }
}
