package com.awsptm.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;

/**
 * The response from Google is made of individual components.\nThis describes the value of that component.
 */
@Entity
@Table(name = "address_component")
public class AddressComponent implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "long_name")
    private String longName;

    @Column(name = "short_name")
    private String shortName;

    @ManyToOne
    @JsonIgnoreProperties(value = { "addressComponents", "address" }, allowSetters = true)
    private GoogleAddress address;

    @ManyToOne
    @JsonIgnoreProperties(value = { "addressComponents" }, allowSetters = true)
    private AddressType type;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public AddressComponent id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLongName() {
        return this.longName;
    }

    public AddressComponent longName(String longName) {
        this.setLongName(longName);
        return this;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public String getShortName() {
        return this.shortName;
    }

    public AddressComponent shortName(String shortName) {
        this.setShortName(shortName);
        return this;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public GoogleAddress getAddress() {
        return this.address;
    }

    public void setAddress(GoogleAddress googleAddress) {
        this.address = googleAddress;
    }

    public AddressComponent address(GoogleAddress googleAddress) {
        this.setAddress(googleAddress);
        return this;
    }

    public AddressType getType() {
        return this.type;
    }

    public void setType(AddressType addressType) {
        this.type = addressType;
    }

    public AddressComponent type(AddressType addressType) {
        this.setType(addressType);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AddressComponent)) {
            return false;
        }
        return id != null && id.equals(((AddressComponent) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AddressComponent{" +
            "id=" + getId() +
            ", longName='" + getLongName() + "'" +
            ", shortName='" + getShortName() + "'" +
            "}";
    }
}
