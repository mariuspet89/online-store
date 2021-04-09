package eu.accesa.onlinestore.model.dto;

public class PaymentDataDto {

    private double price;
    private String currency;

    public PaymentDataDto(double price, String currency) {
        this.price = price;
        this.currency = currency;
    }

    public Double getPrice() {
        return price;
    }

    public String getCurrency() {
        return currency;
    }
}
