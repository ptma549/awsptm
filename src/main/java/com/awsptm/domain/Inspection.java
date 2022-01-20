package com.awsptm.domain;

import com.awsptm.domain.enumeration.Priority;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import javax.persistence.*;
import org.hibernate.annotations.Type;

/**
 * A Inspection.
 */
@Entity
@Table(name = "inspection")
public class Inspection implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private Priority priority;

    @Column(name = "created")
    private ZonedDateTime created;

    @Column(name = "occupiers_name")
    private String occupiersName;

    @Column(name = "occupiers_home_phone")
    private String occupiersHomePhone;

    @Column(name = "occupiers_work_phone")
    private String occupiersWorkPhone;

    @Column(name = "occupiers_mobile_phone")
    private String occupiersMobilePhone;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "work")
    private String work;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "access_instructions")
    private String accessInstructions;

    @Column(name = "updated")
    private ZonedDateTime updated;

    /**
     * when Jobs should start being scheduled from
     */
    @Column(name = "start")
    private LocalDate start;

    /**
     * when period between inspections
     */
    @Column(name = "frequency")
    private Duration frequency;

    @ManyToOne
    @JsonIgnoreProperties(value = { "inspections", "visits", "createdBy", "assignedTo", "address" }, allowSetters = true)
    private Job jobs;

    @ManyToOne
    @JsonIgnoreProperties(value = { "user", "jobs", "inspections", "client" }, allowSetters = true)
    private ClientUser createdBy;

    @ManyToOne
    @JsonIgnoreProperties(value = { "googleAddress", "jobs", "inspections" }, allowSetters = true)
    private Address address;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Inspection id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Priority getPriority() {
        return this.priority;
    }

    public Inspection priority(Priority priority) {
        this.setPriority(priority);
        return this;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public ZonedDateTime getCreated() {
        return this.created;
    }

    public Inspection created(ZonedDateTime created) {
        this.setCreated(created);
        return this;
    }

    public void setCreated(ZonedDateTime created) {
        this.created = created;
    }

    public String getOccupiersName() {
        return this.occupiersName;
    }

    public Inspection occupiersName(String occupiersName) {
        this.setOccupiersName(occupiersName);
        return this;
    }

    public void setOccupiersName(String occupiersName) {
        this.occupiersName = occupiersName;
    }

    public String getOccupiersHomePhone() {
        return this.occupiersHomePhone;
    }

    public Inspection occupiersHomePhone(String occupiersHomePhone) {
        this.setOccupiersHomePhone(occupiersHomePhone);
        return this;
    }

    public void setOccupiersHomePhone(String occupiersHomePhone) {
        this.occupiersHomePhone = occupiersHomePhone;
    }

    public String getOccupiersWorkPhone() {
        return this.occupiersWorkPhone;
    }

    public Inspection occupiersWorkPhone(String occupiersWorkPhone) {
        this.setOccupiersWorkPhone(occupiersWorkPhone);
        return this;
    }

    public void setOccupiersWorkPhone(String occupiersWorkPhone) {
        this.occupiersWorkPhone = occupiersWorkPhone;
    }

    public String getOccupiersMobilePhone() {
        return this.occupiersMobilePhone;
    }

    public Inspection occupiersMobilePhone(String occupiersMobilePhone) {
        this.setOccupiersMobilePhone(occupiersMobilePhone);
        return this;
    }

    public void setOccupiersMobilePhone(String occupiersMobilePhone) {
        this.occupiersMobilePhone = occupiersMobilePhone;
    }

    public String getWork() {
        return this.work;
    }

    public Inspection work(String work) {
        this.setWork(work);
        return this;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getAccessInstructions() {
        return this.accessInstructions;
    }

    public Inspection accessInstructions(String accessInstructions) {
        this.setAccessInstructions(accessInstructions);
        return this;
    }

    public void setAccessInstructions(String accessInstructions) {
        this.accessInstructions = accessInstructions;
    }

    public ZonedDateTime getUpdated() {
        return this.updated;
    }

    public Inspection updated(ZonedDateTime updated) {
        this.setUpdated(updated);
        return this;
    }

    public void setUpdated(ZonedDateTime updated) {
        this.updated = updated;
    }

    public LocalDate getStart() {
        return this.start;
    }

    public Inspection start(LocalDate start) {
        this.setStart(start);
        return this;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public Duration getFrequency() {
        return this.frequency;
    }

    public Inspection frequency(Duration frequency) {
        this.setFrequency(frequency);
        return this;
    }

    public void setFrequency(Duration frequency) {
        this.frequency = frequency;
    }

    public Job getJobs() {
        return this.jobs;
    }

    public void setJobs(Job job) {
        this.jobs = job;
    }

    public Inspection jobs(Job job) {
        this.setJobs(job);
        return this;
    }

    public ClientUser getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(ClientUser clientUser) {
        this.createdBy = clientUser;
    }

    public Inspection createdBy(ClientUser clientUser) {
        this.setCreatedBy(clientUser);
        return this;
    }

    public Address getAddress() {
        return this.address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Inspection address(Address address) {
        this.setAddress(address);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Inspection)) {
            return false;
        }
        return id != null && id.equals(((Inspection) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Inspection{" +
            "id=" + getId() +
            ", priority='" + getPriority() + "'" +
            ", created='" + getCreated() + "'" +
            ", occupiersName='" + getOccupiersName() + "'" +
            ", occupiersHomePhone='" + getOccupiersHomePhone() + "'" +
            ", occupiersWorkPhone='" + getOccupiersWorkPhone() + "'" +
            ", occupiersMobilePhone='" + getOccupiersMobilePhone() + "'" +
            ", work='" + getWork() + "'" +
            ", accessInstructions='" + getAccessInstructions() + "'" +
            ", updated='" + getUpdated() + "'" +
            ", start='" + getStart() + "'" +
            ", frequency='" + getFrequency() + "'" +
            "}";
    }
}
