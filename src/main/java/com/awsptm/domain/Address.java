package com.awsptm.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * This represents how an address will be respresented internally.\nIt will normally be created based upon the response from Google address lookup
 */
@Entity
@Table(name = "address")
public class Address implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "postcode")
    private String postcode;

    @Column(name = "number")
    private String number;

    @Column(name = "position")
    private String position;

    @Column(name = "address_line_1")
    private String addressLine1;

    @Column(name = "address_line_2")
    private String addressLine2;

    @Column(name = "town")
    private String town;

    @Column(name = "county")
    private String county;

    @JsonIgnoreProperties(value = { "addressComponents", "address" }, allowSetters = true)
    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private GoogleAddress googleAddress;

    @OneToMany(mappedBy = "address")
    @JsonIgnoreProperties(value = { "inspections", "visits", "createdBy", "assignedTo", "address" }, allowSetters = true)
    private Set<Job> jobs = new HashSet<>();

    @OneToMany(mappedBy = "address")
    @JsonIgnoreProperties(value = { "jobs", "createdBy", "address" }, allowSetters = true)
    private Set<Inspection> inspections = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Address id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPostcode() {
        return this.postcode;
    }

    public Address postcode(String postcode) {
        this.setPostcode(postcode);
        return this;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getNumber() {
        return this.number;
    }

    public Address number(String number) {
        this.setNumber(number);
        return this;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPosition() {
        return this.position;
    }

    public Address position(String position) {
        this.setPosition(position);
        return this;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getAddressLine1() {
        return this.addressLine1;
    }

    public Address addressLine1(String addressLine1) {
        this.setAddressLine1(addressLine1);
        return this;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return this.addressLine2;
    }

    public Address addressLine2(String addressLine2) {
        this.setAddressLine2(addressLine2);
        return this;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getTown() {
        return this.town;
    }

    public Address town(String town) {
        this.setTown(town);
        return this;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getCounty() {
        return this.county;
    }

    public Address county(String county) {
        this.setCounty(county);
        return this;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public GoogleAddress getGoogleAddress() {
        return this.googleAddress;
    }

    public void setGoogleAddress(GoogleAddress googleAddress) {
        this.googleAddress = googleAddress;
    }

    public Address googleAddress(GoogleAddress googleAddress) {
        this.setGoogleAddress(googleAddress);
        return this;
    }

    public Set<Job> getJobs() {
        return this.jobs;
    }

    public void setJobs(Set<Job> jobs) {
        if (this.jobs != null) {
            this.jobs.forEach(i -> i.setAddress(null));
        }
        if (jobs != null) {
            jobs.forEach(i -> i.setAddress(this));
        }
        this.jobs = jobs;
    }

    public Address jobs(Set<Job> jobs) {
        this.setJobs(jobs);
        return this;
    }

    public Address addJobs(Job job) {
        this.jobs.add(job);
        job.setAddress(this);
        return this;
    }

    public Address removeJobs(Job job) {
        this.jobs.remove(job);
        job.setAddress(null);
        return this;
    }

    public Set<Inspection> getInspections() {
        return this.inspections;
    }

    public void setInspections(Set<Inspection> inspections) {
        if (this.inspections != null) {
            this.inspections.forEach(i -> i.setAddress(null));
        }
        if (inspections != null) {
            inspections.forEach(i -> i.setAddress(this));
        }
        this.inspections = inspections;
    }

    public Address inspections(Set<Inspection> inspections) {
        this.setInspections(inspections);
        return this;
    }

    public Address addInspections(Inspection inspection) {
        this.inspections.add(inspection);
        inspection.setAddress(this);
        return this;
    }

    public Address removeInspections(Inspection inspection) {
        this.inspections.remove(inspection);
        inspection.setAddress(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Address)) {
            return false;
        }
        return id != null && id.equals(((Address) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Address{" +
            "id=" + getId() +
            ", postcode='" + getPostcode() + "'" +
            ", number='" + getNumber() + "'" +
            ", position='" + getPosition() + "'" +
            ", addressLine1='" + getAddressLine1() + "'" +
            ", addressLine2='" + getAddressLine2() + "'" +
            ", town='" + getTown() + "'" +
            ", county='" + getCounty() + "'" +
            "}";
    }
}
