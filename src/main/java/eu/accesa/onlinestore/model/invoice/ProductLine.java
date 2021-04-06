package eu.accesa.onlinestore.model.invoice;

public class ProductLine {

    private String id;
    private String description;
    private int quantity;
    private double unitPrice;

    public ProductLine(String id, String description, int quantity, double unitPrice) {
        this.id = id;
        this.description = description;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public String getId() {
        return id;
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
