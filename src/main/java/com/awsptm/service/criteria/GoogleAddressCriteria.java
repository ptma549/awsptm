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
 * Criteria class for the {@link com.awsptm.domain.GoogleAddress} entity. This class is used
 * in {@link com.awsptm.web.rest.GoogleAddressResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /google-addresses?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class GoogleAddressCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter position;

    private StringFilter url;

    private StringFilter html;

    private StringFilter formatted;

    private StringFilter types;

    private LongFilter addressComponentsId;

    private LongFilter addressId;

    private Boolean distinct;

    public GoogleAddressCriteria() {}

    public GoogleAddressCriteria(GoogleAddressCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.position = other.position == null ? null : other.position.copy();
        this.url = other.url == null ? null : other.url.copy();
        this.html = other.html == null ? null : other.html.copy();
        this.formatted = other.formatted == null ? null : other.formatted.copy();
        this.types = other.types == null ? null : other.types.copy();
        this.addressComponentsId = other.addressComponentsId == null ? null : other.addressComponentsId.copy();
        this.addressId = other.addressId == null ? null : other.addressId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public GoogleAddressCriteria copy() {
        return new GoogleAddressCriteria(this);
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

    public StringFilter getUrl() {
        return url;
    }

    public StringFilter url() {
        if (url == null) {
            url = new StringFilter();
        }
        return url;
    }

    public void setUrl(StringFilter url) {
        this.url = url;
    }

    public StringFilter getHtml() {
        return html;
    }

    public StringFilter html() {
        if (html == null) {
            html = new StringFilter();
        }
        return html;
    }

    public void setHtml(StringFilter html) {
        this.html = html;
    }

    public StringFilter getFormatted() {
        return formatted;
    }

    public StringFilter formatted() {
        if (formatted == null) {
            formatted = new StringFilter();
        }
        return formatted;
    }

    public void setFormatted(StringFilter formatted) {
        this.formatted = formatted;
    }

    public StringFilter getTypes() {
        return types;
    }

    public StringFilter types() {
        if (types == null) {
            types = new StringFilter();
        }
        return types;
    }

    public void setTypes(StringFilter types) {
        this.types = types;
    }

    public LongFilter getAddressComponentsId() {
        return addressComponentsId;
    }

    public LongFilter addressComponentsId() {
        if (addressComponentsId == null) {
            addressComponentsId = new LongFilter();
        }
        return addressComponentsId;
    }

    public void setAddressComponentsId(LongFilter addressComponentsId) {
        this.addressComponentsId = addressComponentsId;
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
        final GoogleAddressCriteria that = (GoogleAddressCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(position, that.position) &&
            Objects.equals(url, that.url) &&
            Objects.equals(html, that.html) &&
            Objects.equals(formatted, that.formatted) &&
            Objects.equals(types, that.types) &&
            Objects.equals(addressComponentsId, that.addressComponentsId) &&
            Objects.equals(addressId, that.addressId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, position, url, html, formatted, types, addressComponentsId, addressId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "GoogleAddressCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (position != null ? "position=" + position + ", " : "") +
            (url != null ? "url=" + url + ", " : "") +
            (html != null ? "html=" + html + ", " : "") +
            (formatted != null ? "formatted=" + formatted + ", " : "") +
            (types != null ? "types=" + types + ", " : "") +
            (addressComponentsId != null ? "addressComponentsId=" + addressComponentsId + ", " : "") +
            (addressId != null ? "addressId=" + addressId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
