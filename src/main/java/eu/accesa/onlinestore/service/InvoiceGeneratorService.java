package eu.accesa.onlinestore.service;

import eu.accesa.onlinestore.model.entity.OrderEntity;

public interface InvoiceGeneratorService {
    void createPDF (OrderEntity order);
}
