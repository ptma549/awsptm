package com.awsptm.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.awsptm.domain.AddressType} entity.
 */
@Schema(description = "The response from Google is made of individual components.\nThis describes the type of that component.")
public class AddressTypeDTO implements Serializable {

    private Long id;

    private String name;

    private Integer position;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AddressTypeDTO)) {
            return false;
        }

        AddressTypeDTO addressTypeDTO = (AddressTypeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, addressTypeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AddressTypeDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", position=" + getPosition() +
            "}";
    }
}
