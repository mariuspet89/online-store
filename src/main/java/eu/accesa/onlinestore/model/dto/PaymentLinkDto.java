package eu.accesa.onlinestore.model.dto;

public class PaymentLinkDto {

    private String link;

    public PaymentLinkDto(String link) {
        this.link = link;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
