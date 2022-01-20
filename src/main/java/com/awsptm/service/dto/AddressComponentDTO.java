package com.awsptm.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.awsptm.domain.AddressComponent} entity.
 */
@Schema(description = "The response from Google is made of individual components.\nThis describes the value of that component.")
public class AddressComponentDTO implements Serializable {

    private Long id;

    private String longName;

    private String shortName;

    private GoogleAddressDTO address;

    private AddressTypeDTO type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public GoogleAddressDTO getAddress() {
        return address;
    }

    public void setAddress(GoogleAddressDTO address) {
        this.address = address;
    }

    public AddressTypeDTO getType() {
        return type;
    }

    public void setType(AddressTypeDTO type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AddressComponentDTO)) {
            return false;
        }

        AddressComponentDTO addressComponentDTO = (AddressComponentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, addressComponentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AddressComponentDTO{" +
            "id=" + getId() +
            ", longName='" + getLongName() + "'" +
            ", shortName='" + getShortName() + "'" +
            ", address=" + getAddress() +
            ", type=" + getType() +
            "}";
    }
}
