package com.awsptm.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * A proxy object to the User object created by JHipster\nto allow associations to it.
 */
@Entity
@Table(name = "client_user")
public class ClientUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "landline")
    private String landline;

    @Column(name = "mobile")
    private String mobile;

    @OneToOne
    @JoinColumn(unique = true)
    private User user;

    @OneToMany(mappedBy = "createdBy")
    @JsonIgnoreProperties(value = { "inspections", "visits", "createdBy", "assignedTo", "address" }, allowSetters = true)
    private Set<Job> jobs = new HashSet<>();

    @OneToMany(mappedBy = "createdBy")
    @JsonIgnoreProperties(value = { "jobs", "createdBy", "address" }, allowSetters = true)
    private Set<Inspection> inspections = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "clientUsers" }, allowSetters = true)
    private ClientOrganisation client;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ClientUser id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLandline() {
        return this.landline;
    }

    public ClientUser landline(String landline) {
        this.setLandline(landline);
        return this;
    }

    public void setLandline(String landline) {
        this.landline = landline;
    }

    public String getMobile() {
        return this.mobile;
    }

    public ClientUser mobile(String mobile) {
        this.setMobile(mobile);
        return this;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ClientUser user(User user) {
        this.setUser(user);
        return this;
    }

    public Set<Job> getJobs() {
        return this.jobs;
    }

    public void setJobs(Set<Job> jobs) {
        if (this.jobs != null) {
            this.jobs.forEach(i -> i.setCreatedBy(null));
        }
        if (jobs != null) {
            jobs.forEach(i -> i.setCreatedBy(this));
        }
        this.jobs = jobs;
    }

    public ClientUser jobs(Set<Job> jobs) {
        this.setJobs(jobs);
        return this;
    }

    public ClientUser addJobs(Job job) {
        this.jobs.add(job);
        job.setCreatedBy(this);
        return this;
    }

    public ClientUser removeJobs(Job job) {
        this.jobs.remove(job);
        job.setCreatedBy(null);
        return this;
    }

    public Set<Inspection> getInspections() {
        return this.inspections;
    }

    public void setInspections(Set<Inspection> inspections) {
        if (this.inspections != null) {
            this.inspections.forEach(i -> i.setCreatedBy(null));
        }
        if (inspections != null) {
            inspections.forEach(i -> i.setCreatedBy(this));
        }
        this.inspections = inspections;
    }

    public ClientUser inspections(Set<Inspection> inspections) {
        this.setInspections(inspections);
        return this;
    }

    public ClientUser addInspections(Inspection inspection) {
        this.inspections.add(inspection);
        inspection.setCreatedBy(this);
        return this;
    }

    public ClientUser removeInspections(Inspection inspection) {
        this.inspections.remove(inspection);
        inspection.setCreatedBy(null);
        return this;
    }

    public ClientOrganisation getClient() {
        return this.client;
    }

    public void setClient(ClientOrganisation clientOrganisation) {
        this.client = clientOrganisation;
    }

    public ClientUser client(ClientOrganisation clientOrganisation) {
        this.setClient(clientOrganisation);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClientUser)) {
            return false;
        }
        return id != null && id.equals(((ClientUser) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ClientUser{" +
            "id=" + getId() +
            ", landline='" + getLandline() + "'" +
            ", mobile='" + getMobile() + "'" +
            "}";
    }
}
