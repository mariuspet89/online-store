package eu.accesa.onlinestore.service;

import eu.accesa.onlinestore.model.dto.PaymentDataDto;

public interface PaymentService {

    String createTransaction(PaymentDataDto paymentDataDto);
}
