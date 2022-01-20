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
 * Criteria class for the {@link com.awsptm.domain.Address} entity. This class is used
 * in {@link com.awsptm.web.rest.AddressResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /addresses?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class AddressCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter postcode;

    private StringFilter number;

    private StringFilter position;

    private StringFilter addressLine1;

    private StringFilter addressLine2;

    private StringFilter town;

    private StringFilter county;

    private LongFilter googleAddressId;

    private LongFilter jobsId;

    private LongFilter inspectionsId;

    private Boolean distinct;

    public AddressCriteria() {}

    public AddressCriteria(AddressCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.postcode = other.postcode == null ? null : other.postcode.copy();
        this.number = other.number == null ? null : other.number.copy();
        this.position = other.position == null ? null : other.position.copy();
        this.addressLine1 = other.addressLine1 == null ? null : other.addressLine1.copy();
        this.addressLine2 = other.addressLine2 == null ? null : other.addressLine2.copy();
        this.town = other.town == null ? null : other.town.copy();
        this.county = other.county == null ? null : other.county.copy();
        this.googleAddressId = other.googleAddressId == null ? null : other.googleAddressId.copy();
        this.jobsId = other.jobsId == null ? null : other.jobsId.copy();
        this.inspectionsId = other.inspectionsId == null ? null : other.inspectionsId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public AddressCriteria copy() {
        return new AddressCriteria(this);
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

    public StringFilter getPostcode() {
        return postcode;
    }

    public StringFilter postcode() {
        if (postcode == null) {
            postcode = new StringFilter();
        }
        return postcode;
    }

    public void setPostcode(StringFilter postcode) {
        this.postcode = postcode;
    }

    public StringFilter getNumber() {
        return number;
    }

    public StringFilter number() {
        if (number == null) {
            number = new StringFilter();
        }
        return number;
    }

    public void setNumber(StringFilter number) {
        this.number = number;
    }

    public StringFilter getPosition() {
        return position;
    }

    public StringFilter position() {
        if (position == null) {
            position = new StringFilter();
        }
        return position;
    }

    public void setPosition(StringFilter position) {
        this.position = position;
    }

    public StringFilter getAddressLine1() {
        return addressLine1;
    }

    public StringFilter addressLine1() {
        if (addressLine1 == null) {
            addressLine1 = new StringFilter();
        }
        return addressLine1;
    }

    public void setAddressLine1(StringFilter addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public StringFilter getAddressLine2() {
        return addressLine2;
    }

    public StringFilter addressLine2() {
        if (addressLine2 == null) {
            addressLine2 = new StringFilter();
        }
        return addressLine2;
    }

    public void setAddressLine2(StringFilter addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public StringFilter getTown() {
        return town;
    }

    public StringFilter town() {
        if (town == null) {
            town = new StringFilter();
        }
        return town;
    }

    public void setTown(StringFilter town) {
        this.town = town;
    }

    public StringFilter getCounty() {
        return county;
    }

    public StringFilter county() {
        if (county == null) {
            county = new StringFilter();
        }
        return county;
    }

    public void setCounty(StringFilter county) {
        this.county = county;
    }

    public LongFilter getGoogleAddressId() {
        return googleAddressId;
    }

    public LongFilter googleAddressId() {
        if (googleAddressId == null) {
            googleAddressId = new LongFilter();
        }
        return googleAddressId;
    }

    public void setGoogleAddressId(LongFilter googleAddressId) {
        this.googleAddressId = googleAddressId;
    }

    public LongFilter getJobsId() {
        return jobsId;
    }

    public LongFilter jobsId() {
        if (jobsId == null) {
            jobsId = new LongFilter();
        }
        return jobsId;
    }

    public void setJobsId(LongFilter jobsId) {
        this.jobsId = jobsId;
    }

    public LongFilter getInspectionsId() {
        return inspectionsId;
    }

    public LongFilter inspectionsId() {
        if (inspectionsId == null) {
            inspectionsId = new LongFilter();
        }
        return inspectionsId;
    }

    public void setInspectionsId(LongFilter inspectionsId) {
        this.inspectionsId = inspectionsId;
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
        final AddressCriteria that = (AddressCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(postcode, that.postcode) &&
            Objects.equals(number, that.number) &&
            Objects.equals(position, that.position) &&
            Objects.equals(addressLine1, that.addressLine1) &&
            Objects.equals(addressLine2, that.addressLine2) &&
            Objects.equals(town, that.town) &&
            Objects.equals(county, that.county) &&
            Objects.equals(googleAddressId, that.googleAddressId) &&
            Objects.equals(jobsId, that.jobsId) &&
            Objects.equals(inspectionsId, that.inspectionsId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            postcode,
            number,
            position,
            addressLine1,
            addressLine2,
            town,
            county,
            googleAddressId,
            jobsId,
            inspectionsId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AddressCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (postcode != null ? "postcode=" + postcode + ", " : "") +
            (number != null ? "number=" + number + ", " : "") +
            (position != null ? "position=" + position + ", " : "") +
            (addressLine1 != null ? "addressLine1=" + addressLine1 + ", " : "") +
            (addressLine2 != null ? "addressLine2=" + addressLine2 + ", " : "") +
            (town != null ? "town=" + town + ", " : "") +
            (county != null ? "county=" + county + ", " : "") +
            (googleAddressId != null ? "googleAddressId=" + googleAddressId + ", " : "") +
            (jobsId != null ? "jobsId=" + jobsId + ", " : "") +
            (inspectionsId != null ? "inspectionsId=" + inspectionsId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
