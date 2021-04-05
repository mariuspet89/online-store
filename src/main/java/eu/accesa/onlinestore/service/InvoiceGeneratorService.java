package eu.accesa.onlinestore.service;

import eu.accesa.onlinestore.model.entity.OrderEntity;

import java.io.ByteArrayOutputStream;

public interface InvoiceGeneratorService {

    /**
     * Generates a invoice based on the order data. The file format is PDF.
     *
     * @param order the order's data
     * @return a byte array containing the invoice as a PDF
     */
    ByteArrayOutputStream createPDF(OrderEntity order);
}
