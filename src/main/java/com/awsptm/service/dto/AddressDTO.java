package com.awsptm.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.awsptm.domain.Address} entity.
 */
@Schema(
    description = "This represents how an address will be respresented internally.\nIt will normally be created based upon the response from Google address lookup"
)
public class AddressDTO implements Serializable {

    private Long id;

    private String postcode;

    private String number;

    private String position;

    private String addressLine1;

    private String addressLine2;

    private String town;

    private String county;

    private GoogleAddressDTO googleAddress;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public GoogleAddressDTO getGoogleAddress() {
        return googleAddress;
    }

    public void setGoogleAddress(GoogleAddressDTO googleAddress) {
        this.googleAddress = googleAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AddressDTO)) {
            return false;
        }

        AddressDTO addressDTO = (AddressDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, addressDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AddressDTO{" +
            "id=" + getId() +
            ", postcode='" + getPostcode() + "'" +
            ", number='" + getNumber() + "'" +
            ", position='" + getPosition() + "'" +
            ", addressLine1='" + getAddressLine1() + "'" +
            ", addressLine2='" + getAddressLine2() + "'" +
            ", town='" + getTown() + "'" +
            ", county='" + getCounty() + "'" +
            ", googleAddress=" + getGoogleAddress() +
            "}";
    }
}
