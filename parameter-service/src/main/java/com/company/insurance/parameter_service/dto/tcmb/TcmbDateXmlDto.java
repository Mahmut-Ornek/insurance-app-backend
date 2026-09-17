package com.company.insurance.parameter_service.dto.tcmb;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

@JacksonXmlRootElement(localName = "Tarih_Date")
@JsonIgnoreProperties(ignoreUnknown = true)
public record TcmbDateXmlDto(@JacksonXmlProperty(isAttribute = true, localName = "Tarih") String tarih,
                             @JacksonXmlElementWrapper(useWrapping = false)
                             @JacksonXmlProperty(localName = "Currency")
                             List<TcmbCurrencyXmlDto> currencies) {
}
