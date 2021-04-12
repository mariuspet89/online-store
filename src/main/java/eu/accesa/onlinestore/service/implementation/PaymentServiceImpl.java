package eu.accesa.onlinestore.service.implementation;

import com.braintreepayments.http.HttpResponse;
import com.paypal.core.PayPalHttpClient;
import com.paypal.orders.*;
import eu.accesa.onlinestore.exceptionhandler.OnlineStoreException;
import eu.accesa.onlinestore.model.dto.PaymentDataDto;
import eu.accesa.onlinestore.model.dto.PaymentLinkDto;
import eu.accesa.onlinestore.service.PaymentService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PayPalHttpClient payPalClient;

    public PaymentServiceImpl(PayPalHttpClient payPalClient) {
        this.payPalClient = payPalClient;
    }

    @Override
    public PaymentLinkDto createPayment(PaymentDataDto paymentDataDto) {
        OrdersCreateRequest request = new OrdersCreateRequest();
        request.prefer("return=representation");
        request.requestBody(createPayPalOrderRequest(paymentDataDto));

        // call PayPal for transaction
        try {
            HttpResponse<Order> response = payPalClient.execute(request);
            LinkDescription approveLink = response.result().links().stream()
                    .filter(linkDescription -> linkDescription.rel().equals("approve"))
                    .findFirst()
                    .orElse(null);

            if (approveLink != null) {
                return new PaymentLinkDto(approveLink.href());
            } else {
                throw new OnlineStoreException("Payment Confirmation Link could not be found!");
            }
        } catch (IOException e) {
            throw new OnlineStoreException(e.getMessage());
        }
    }

    private OrderRequest createPayPalOrderRequest(PaymentDataDto paymentDataDto) {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.checkoutPaymentIntent("AUTHORIZE");

        ApplicationContext applicationContext = new ApplicationContext()
                .returnUrl("http://18.224.7.25:5000/#/success")
                .cancelUrl("http://18.224.7.25:5000/#/failure");
        orderRequest.applicationContext(applicationContext);

        List<PurchaseUnitRequest> purchaseUnitRequests = new ArrayList<>();
        PurchaseUnitRequest purchaseUnitRequest = new PurchaseUnitRequest()
                .amountWithBreakdown(new AmountWithBreakdown()
                        .currencyCode(paymentDataDto.getCurrency())
                        .value(paymentDataDto.getPrice().toString()));
        purchaseUnitRequests.add(purchaseUnitRequest);
        orderRequest.purchaseUnits(purchaseUnitRequests);
        return orderRequest;
    }
}
