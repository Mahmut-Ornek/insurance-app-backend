package com.company.insurance.parameter_service.dto.tcmb;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TcmbCurrencyXmlDto(@JacksonXmlProperty(isAttribute = true, localName = "Kod") String kod,
                                 @JacksonXmlProperty(localName = "Unit") Integer unit,
                                 @JacksonXmlProperty(localName = "ForexSelling") BigDecimal forexSelling) {
}
