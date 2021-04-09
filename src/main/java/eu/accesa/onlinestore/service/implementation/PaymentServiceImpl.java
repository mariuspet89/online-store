package eu.accesa.onlinestore.service.implementation;

import com.braintreepayments.http.HttpResponse;
import com.paypal.core.PayPalHttpClient;
import com.paypal.orders.*;
import eu.accesa.onlinestore.exceptionhandler.OnlineStoreException;
import eu.accesa.onlinestore.model.dto.PaymentDataDto;
import eu.accesa.onlinestore.service.PaymentService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final String SUCCESS_URL = "pay/success";
    private static final String CANCEL_URL = "pay/cancel";

    private final PayPalHttpClient payPalClient;

    public PaymentServiceImpl(PayPalHttpClient payPalClient) {
        this.payPalClient = payPalClient;
    }

    @Override
    public String createTransaction(PaymentDataDto paymentDataDto) {
        OrdersCreateRequest request = new OrdersCreateRequest();
        request.prefer("return=representation");
        request.requestBody(createPayPalOrderRequest(paymentDataDto));

        // call PayPal for transaction
        try {
            HttpResponse<Order> response = payPalClient.execute(request);
            return response.result().id();
        } catch (IOException e) {
            throw new OnlineStoreException(e.getMessage());
        }
    }

    private OrderRequest createPayPalOrderRequest(PaymentDataDto paymentDataDto) {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.checkoutPaymentIntent("AUTHORIZE");

        ApplicationContext applicationContext = new ApplicationContext()
                .returnUrl("http://localhost:8080/" + SUCCESS_URL)
                .cancelUrl("http://localhost:8080/" + CANCEL_URL);
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
