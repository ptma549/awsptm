package com.awsptm.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.awsptm.domain.AddressComponent} entity. This class is used
 * in {@link com.awsptm.web.rest.AddressComponentResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /address-components?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class AddressComponentCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter longName;

    private StringFilter shortName;

    private LongFilter addressId;

    private LongFilter typeId;

    private Boolean distinct;

    public AddressComponentCriteria() {}

    public AddressComponentCriteria(AddressComponentCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.longName = other.longName == null ? null : other.longName.copy();
        this.shortName = other.shortName == null ? null : other.shortName.copy();
        this.addressId = other.addressId == null ? null : other.addressId.copy();
        this.typeId = other.typeId == null ? null : other.typeId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public AddressComponentCriteria copy() {
        return new AddressComponentCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getLongName() {
        return longName;
    }

    public StringFilter longName() {
        if (longName == null) {
            longName = new StringFilter();
        }
        return longName;
    }

    public void setLongName(StringFilter longName) {
        this.longName = longName;
    }

    public StringFilter getShortName() {
        return shortName;
    }

    public StringFilter shortName() {
        if (shortName == null) {
            shortName = new StringFilter();
        }
        return shortName;
    }

    public void setShortName(StringFilter shortName) {
        this.shortName = shortName;
    }

    public LongFilter getAddressId() {
        return addressId;
    }

    public LongFilter addressId() {
        if (addressId == null) {
            addressId = new LongFilter();
        }
        return addressId;
    }

    public void setAddressId(LongFilter addressId) {
        this.addressId = addressId;
    }

    public LongFilter getTypeId() {
        return typeId;
    }

    public LongFilter typeId() {
        if (typeId == null) {
            typeId = new LongFilter();
        }
        return typeId;
    }

    public void setTypeId(LongFilter typeId) {
        this.typeId = typeId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AddressComponentCriteria that = (AddressComponentCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(longName, that.longName) &&
            Objects.equals(shortName, that.shortName) &&
            Objects.equals(addressId, that.addressId) &&
            Objects.equals(typeId, that.typeId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, longName, shortName, addressId, typeId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AddressComponentCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (longName != null ? "longName=" + longName + ", " : "") +
            (shortName != null ? "shortName=" + shortName + ", " : "") +
            (addressId != null ? "addressId=" + addressId + ", " : "") +
            (typeId != null ? "typeId=" + typeId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
