package eu.accesa.onlinestore.model.invoice;

public class ProductLine {

    private String description;
    private int quantity;
    private double unitPrice;

    public ProductLine(String description, int quantity, double unitPrice) {
        this.description = description;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public String getDescription() {
        return description;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }
}
