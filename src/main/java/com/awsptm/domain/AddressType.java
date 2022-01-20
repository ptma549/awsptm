package com.awsptm.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * The response from Google is made of individual components.\nThis describes the type of that component.
 */
@Entity
@Table(name = "address_type")
public class AddressType implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "position")
    private Integer position;

    @OneToMany(mappedBy = "type")
    @JsonIgnoreProperties(value = { "address", "type" }, allowSetters = true)
    private Set<AddressComponent> addressComponents = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public AddressType id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public AddressType name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPosition() {
        return this.position;
    }

    public AddressType position(Integer position) {
        this.setPosition(position);
        return this;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Set<AddressComponent> getAddressComponents() {
        return this.addressComponents;
    }

    public void setAddressComponents(Set<AddressComponent> addressComponents) {
        if (this.addressComponents != null) {
            this.addressComponents.forEach(i -> i.setType(null));
        }
        if (addressComponents != null) {
            addressComponents.forEach(i -> i.setType(this));
        }
        this.addressComponents = addressComponents;
    }

    public AddressType addressComponents(Set<AddressComponent> addressComponents) {
        this.setAddressComponents(addressComponents);
        return this;
    }

    public AddressType addAddressComponents(AddressComponent addressComponent) {
        this.addressComponents.add(addressComponent);
        addressComponent.setType(this);
        return this;
    }

    public AddressType removeAddressComponents(AddressComponent addressComponent) {
        this.addressComponents.remove(addressComponent);
        addressComponent.setType(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AddressType)) {
            return false;
        }
        return id != null && id.equals(((AddressType) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AddressType{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", position=" + getPosition() +
            "}";
    }
}
