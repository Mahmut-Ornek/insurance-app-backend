package com.company.insurance.parameter_service.service;


import com.company.insurance.parameter_service.client.TcmbClient;
import com.company.insurance.parameter_service.dto.ExchangeRateResponse;
import com.company.insurance.parameter_service.dto.tcmb.TcmbCurrencyXmlDto;
import com.company.insurance.parameter_service.dto.tcmb.TcmbDateXmlDto;
import com.company.insurance.parameter_service.entity.Currency;
import com.company.insurance.parameter_service.entity.ExchangeRate;
import com.company.insurance.parameter_service.repository.CurrencyRepository;
import com.company.insurance.parameter_service.repository.ExchangeRateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ExchangeRateService {
    private static final Logger log = LoggerFactory.getLogger(ExchangeRateService.class);

    private final TcmbClient tcmbClient;
    private final ExchangeRateRepository exchangeRateRepository;
    private final CurrencyRepository currencyRepository;

    public ExchangeRateService(TcmbClient tcmbClient, ExchangeRateRepository exchangeRateRepository,
                               CurrencyRepository currencyRepository){
        this.tcmbClient = tcmbClient;
        this.exchangeRateRepository = exchangeRateRepository;
        this.currencyRepository = currencyRepository;
    }

    @Transactional
    public void syncRatesFromTcmb(){
        TcmbDateXmlDto xmlData = tcmbClient.fetchTodayRates();
        if(xmlData == null || xmlData.currencies() == null){
            log.warn("TCMB'den kur verisi alınamadı!");
            return;
        }

        Set<String> supportedCurrencies = currencyRepository.findAll().stream()
                .map(Currency::getCode)
                .collect(Collectors.toSet());

        LocalDate today = LocalDate.now();

        for(TcmbCurrencyXmlDto item : xmlData.currencies()){
            if(item.kod() != null && supportedCurrencies.contains(item.kod()) && item.forexSelling() != null){
                int unit = (item.unit() != null && item.unit() > 0) ? item.unit() : 1;
                BigDecimal unitPrice = item.forexSelling().divide(BigDecimal.valueOf(unit), 4, RoundingMode.HALF_UP);

                ExchangeRate rate = exchangeRateRepository.findByCurrencyCodeAndRateDate(item.kod(), today)
                        .orElseGet(() -> new ExchangeRate(item.kod(), today, unitPrice));

                rate.setForexSelling(unitPrice);
                exchangeRateRepository.save(rate);
            }
        }
        log.info("TCMB döviz kurları başarıyla veritabanına işlendi. Tarih: {}", today);
    }

    public ExchangeRateResponse getRate(String currencyCode){
        if("TRY".equalsIgnoreCase(currencyCode)){
            return new ExchangeRateResponse("TRY", LocalDate.now(), BigDecimal.ONE);
        }
        LocalDate today = LocalDate.now();

        ExchangeRate rate = exchangeRateRepository.findByCurrencyCodeAndRateDate(currencyCode, today)
                .or(() -> exchangeRateRepository.findFirstByCurrencyCodeOrderByRateDateDesc(currencyCode))
                .orElseThrow(() -> new IllegalArgumentException("Kur verisi bulunamadı: " + currencyCode));

        return new ExchangeRateResponse(rate.getCurrencyCode(), rate.getRateDate(), rate.getForexSelling());
    }
}
