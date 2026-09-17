package com.company.insurance.payment_service.gateway;

import com.iyzipay.model.CheckoutForm;
import com.iyzipay.model.CheckoutFormInitialize;
import com.iyzipay.request.CreateCheckoutFormInitializeRequest;
import com.iyzipay.request.RetrieveCheckoutFormRequest;

public interface IyzicoGateway {
    CheckoutFormInitialize initializeCheckoutForm(CreateCheckoutFormInitializeRequest request);
    CheckoutForm retrieveCheckoutForm(RetrieveCheckoutFormRequest request);
}
