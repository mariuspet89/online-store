package eu.accesa.onlinestore.model.dto;

public class PaymentDataDto {

    private double price;
    private String currency;
    private String method;
    private String intent;
    private String description;

    public PaymentDataDto(double price, String currency, String method, String intent, String description) {
        this.price = price;
        this.currency = currency;
        this.method = method;
        this.intent = intent;
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public String getCurrency() {
        return currency;
    }

    public String getMethod() {
        return method;
    }

    public String getIntent() {
        return intent;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "PaymentDataDto{" +
                "price=" + price +
                ", currency='" + currency + '\'' +
                ", method='" + method + '\'' +
                ", intent='" + intent + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
