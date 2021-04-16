package eu.accesa.onlinestore.model.dto;

public class ResetLinkDto {
    private String link;

    public ResetLinkDto(String link) {
        this.link = link;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
