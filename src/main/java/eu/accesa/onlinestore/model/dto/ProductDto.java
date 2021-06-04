package eu.accesa.onlinestore.model.dto;

import javax.validation.constraints.NotBlank;

public class ProductDto extends ProductDtoNoId {

    @NotBlank
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
