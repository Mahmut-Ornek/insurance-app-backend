package com.company.insurance.payment_service.gateway;

import com.iyzipay.Options;
import com.iyzipay.model.CheckoutForm;
import com.iyzipay.model.CheckoutFormInitialize;
import com.iyzipay.request.CreateCheckoutFormInitializeRequest;
import com.iyzipay.request.RetrieveCheckoutFormRequest;
import org.springframework.stereotype.Component;

@Component
public class IyzicoGatewayImpl implements IyzicoGateway{
    private final Options iyzicoOptions;

    public IyzicoGatewayImpl(Options iyzicoOptions){this.iyzicoOptions = iyzicoOptions;}
    @Override
    public CheckoutFormInitialize initializeCheckoutForm(CreateCheckoutFormInitializeRequest request) {
        return CheckoutFormInitialize.create(request, iyzicoOptions);
    }

    @Override
    public CheckoutForm retrieveCheckoutForm(RetrieveCheckoutFormRequest request) {
        return CheckoutForm.retrieve(request, iyzicoOptions);
    }
}
