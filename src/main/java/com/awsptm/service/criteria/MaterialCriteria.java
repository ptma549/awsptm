package com.awsptm.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BigDecimalFilter;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.awsptm.domain.Material} entity. This class is used
 * in {@link com.awsptm.web.rest.MaterialResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /materials?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class MaterialCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private IntegerFilter quantity;

    private BigDecimalFilter unitCost;

    private LongFilter visitId;

    private Boolean distinct;

    public MaterialCriteria() {}

    public MaterialCriteria(MaterialCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.quantity = other.quantity == null ? null : other.quantity.copy();
        this.unitCost = other.unitCost == null ? null : other.unitCost.copy();
        this.visitId = other.visitId == null ? null : other.visitId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public MaterialCriteria copy() {
        return new MaterialCriteria(this);
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

    public StringFilter getName() {
        return name;
    }

    public StringFilter name() {
        if (name == null) {
            name = new StringFilter();
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public IntegerFilter getQuantity() {
        return quantity;
    }

    public IntegerFilter quantity() {
        if (quantity == null) {
            quantity = new IntegerFilter();
        }
        return quantity;
    }

    public void setQuantity(IntegerFilter quantity) {
        this.quantity = quantity;
    }

    public BigDecimalFilter getUnitCost() {
        return unitCost;
    }

    public BigDecimalFilter unitCost() {
        if (unitCost == null) {
            unitCost = new BigDecimalFilter();
        }
        return unitCost;
    }

    public void setUnitCost(BigDecimalFilter unitCost) {
        this.unitCost = unitCost;
    }

    public LongFilter getVisitId() {
        return visitId;
    }

    public LongFilter visitId() {
        if (visitId == null) {
            visitId = new LongFilter();
        }
        return visitId;
    }

    public void setVisitId(LongFilter visitId) {
        this.visitId = visitId;
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
        final MaterialCriteria that = (MaterialCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(quantity, that.quantity) &&
            Objects.equals(unitCost, that.unitCost) &&
            Objects.equals(visitId, that.visitId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, quantity, unitCost, visitId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MaterialCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (quantity != null ? "quantity=" + quantity + ", " : "") +
            (unitCost != null ? "unitCost=" + unitCost + ", " : "") +
            (visitId != null ? "visitId=" + visitId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
