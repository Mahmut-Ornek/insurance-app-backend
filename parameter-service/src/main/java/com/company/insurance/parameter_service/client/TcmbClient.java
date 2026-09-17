package com.company.insurance.parameter_service.client;

import com.company.insurance.parameter_service.dto.tcmb.TcmbDateXmlDto;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.http.HttpHeaders;

@Component
public class TcmbClient {

    private final RestClient restClient;
    private final XmlMapper xmlMapper;

    public TcmbClient() {
        this.restClient = RestClient.builder()
                .baseUrl("https://www.tcmb.gov.tr")
                .defaultHeader("Accept-Encoding", "identity")
                .defaultHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                .build();
        this.xmlMapper = new XmlMapper();
    }

    public TcmbDateXmlDto fetchTodayRates() {
        byte[] xmlBytes = restClient.get()
                .uri("/kurlar/today.xml")
                .retrieve()
                .body(byte[].class);

        try {
            return xmlMapper.readValue(xmlBytes, TcmbDateXmlDto.class);
        } catch (Exception e) {
            throw new RuntimeException("TCMB XML verisi çözümlenemedi: " + e.getMessage(), e);
        }
    }
}