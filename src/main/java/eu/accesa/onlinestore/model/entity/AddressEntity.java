package eu.accesa.onlinestore.model.entity;

import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Document(collection = "address")
public class AddressEntity {

    @Id
    private String address;
    private String city;
    private String county;
    private String postalCode;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddressEntity addressEntity1 = (AddressEntity) o;
        return Objects.equals(address, addressEntity1.address) && Objects.equals(city, addressEntity1.city) && Objects.equals(county, addressEntity1.county) && Objects.equals(postalCode, addressEntity1.postalCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, city, county, postalCode);
    }
}
