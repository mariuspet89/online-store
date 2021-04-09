package eu.accesa.onlinestore.controller;

import com.braintreepayments.http.HttpResponse;
import com.paypal.orders.Order;
import com.paypal.orders.OrdersGetRequest;
import eu.accesa.onlinestore.model.dto.OrderDtoNoId;
import eu.accesa.onlinestore.model.dto.PaymentDataDto;
import eu.accesa.onlinestore.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/pay")
    public ResponseEntity<String> createPayment(@Valid @RequestBody PaymentDataDto paymentDataDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.createTransaction(paymentDataDto));
    }

    @PostMapping("/approve/{payPalOrderId}")
    public void approvePayment(@PathVariable String payPalOrderId, @Valid @RequestBody OrderDtoNoId orderDtoNoId) {

    }
}
