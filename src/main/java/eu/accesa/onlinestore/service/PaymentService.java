package eu.accesa.onlinestore.service;

import eu.accesa.onlinestore.model.dto.PaymentDataDto;
import eu.accesa.onlinestore.model.dto.PaymentLinkDto;

public interface PaymentService {

    PaymentLinkDto createPayment(PaymentDataDto paymentDataDto);
}
