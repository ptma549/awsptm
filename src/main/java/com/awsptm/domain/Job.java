package com.awsptm.domain;

import com.awsptm.domain.enumeration.Priority;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Type;

/**
 * A Job.
 */
@Entity
@Table(name = "job")
public class Job implements Serializable {

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

    @Column(name = "client_order_id")
    private String clientOrderId;

    @Column(name = "assigned_at")
    private ZonedDateTime assignedAt;

    @Column(name = "scheduled")
    private ZonedDateTime scheduled;

    @Column(name = "completed")
    private ZonedDateTime completed;

    @Column(name = "invoice_number")
    private String invoiceNumber;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "fault")
    private String fault;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "access_instructions")
    private String accessInstructions;

    @Column(name = "updated")
    private ZonedDateTime updated;

    @OneToMany(mappedBy = "jobs")
    @JsonIgnoreProperties(value = { "jobs", "createdBy", "address" }, allowSetters = true)
    private Set<Inspection> inspections = new HashSet<>();

    @OneToMany(mappedBy = "job")
    @JsonIgnoreProperties(value = { "materials", "certificates", "evidences", "job" }, allowSetters = true)
    private Set<Visit> visits = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "user", "jobs", "inspections", "client" }, allowSetters = true)
    private ClientUser createdBy;

    @ManyToOne
    @JsonIgnoreProperties(value = { "user", "jobs" }, allowSetters = true)
    private Engineer assignedTo;

    @ManyToOne
    @JsonIgnoreProperties(value = { "googleAddress", "jobs", "inspections" }, allowSetters = true)
    private Address address;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Job id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Priority getPriority() {
        return this.priority;
    }

    public Job priority(Priority priority) {
        this.setPriority(priority);
        return this;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public ZonedDateTime getCreated() {
        return this.created;
    }

    public Job created(ZonedDateTime created) {
        this.setCreated(created);
        return this;
    }

    public void setCreated(ZonedDateTime created) {
        this.created = created;
    }

    public String getOccupiersName() {
        return this.occupiersName;
    }

    public Job occupiersName(String occupiersName) {
        this.setOccupiersName(occupiersName);
        return this;
    }

    public void setOccupiersName(String occupiersName) {
        this.occupiersName = occupiersName;
    }

    public String getOccupiersHomePhone() {
        return this.occupiersHomePhone;
    }

    public Job occupiersHomePhone(String occupiersHomePhone) {
        this.setOccupiersHomePhone(occupiersHomePhone);
        return this;
    }

    public void setOccupiersHomePhone(String occupiersHomePhone) {
        this.occupiersHomePhone = occupiersHomePhone;
    }

    public String getOccupiersWorkPhone() {
        return this.occupiersWorkPhone;
    }

    public Job occupiersWorkPhone(String occupiersWorkPhone) {
        this.setOccupiersWorkPhone(occupiersWorkPhone);
        return this;
    }

    public void setOccupiersWorkPhone(String occupiersWorkPhone) {
        this.occupiersWorkPhone = occupiersWorkPhone;
    }

    public String getOccupiersMobilePhone() {
        return this.occupiersMobilePhone;
    }

    public Job occupiersMobilePhone(String occupiersMobilePhone) {
        this.setOccupiersMobilePhone(occupiersMobilePhone);
        return this;
    }

    public void setOccupiersMobilePhone(String occupiersMobilePhone) {
        this.occupiersMobilePhone = occupiersMobilePhone;
    }

    public String getClientOrderId() {
        return this.clientOrderId;
    }

    public Job clientOrderId(String clientOrderId) {
        this.setClientOrderId(clientOrderId);
        return this;
    }

    public void setClientOrderId(String clientOrderId) {
        this.clientOrderId = clientOrderId;
    }

    public ZonedDateTime getAssignedAt() {
        return this.assignedAt;
    }

    public Job assignedAt(ZonedDateTime assignedAt) {
        this.setAssignedAt(assignedAt);
        return this;
    }

    public void setAssignedAt(ZonedDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }

    public ZonedDateTime getScheduled() {
        return this.scheduled;
    }

    public Job scheduled(ZonedDateTime scheduled) {
        this.setScheduled(scheduled);
        return this;
    }

    public void setScheduled(ZonedDateTime scheduled) {
        this.scheduled = scheduled;
    }

    public ZonedDateTime getCompleted() {
        return this.completed;
    }

    public Job completed(ZonedDateTime completed) {
        this.setCompleted(completed);
        return this;
    }

    public void setCompleted(ZonedDateTime completed) {
        this.completed = completed;
    }

    public String getInvoiceNumber() {
        return this.invoiceNumber;
    }

    public Job invoiceNumber(String invoiceNumber) {
        this.setInvoiceNumber(invoiceNumber);
        return this;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getFault() {
        return this.fault;
    }

    public Job fault(String fault) {
        this.setFault(fault);
        return this;
    }

    public void setFault(String fault) {
        this.fault = fault;
    }

    public String getAccessInstructions() {
        return this.accessInstructions;
    }

    public Job accessInstructions(String accessInstructions) {
        this.setAccessInstructions(accessInstructions);
        return this;
    }

    public void setAccessInstructions(String accessInstructions) {
        this.accessInstructions = accessInstructions;
    }

    public ZonedDateTime getUpdated() {
        return this.updated;
    }

    public Job updated(ZonedDateTime updated) {
        this.setUpdated(updated);
        return this;
    }

    public void setUpdated(ZonedDateTime updated) {
        this.updated = updated;
    }

    public Set<Inspection> getInspections() {
        return this.inspections;
    }

    public void setInspections(Set<Inspection> inspections) {
        if (this.inspections != null) {
            this.inspections.forEach(i -> i.setJobs(null));
        }
        if (inspections != null) {
            inspections.forEach(i -> i.setJobs(this));
        }
        this.inspections = inspections;
    }

    public Job inspections(Set<Inspection> inspections) {
        this.setInspections(inspections);
        return this;
    }

    public Job addInspection(Inspection inspection) {
        this.inspections.add(inspection);
        inspection.setJobs(this);
        return this;
    }

    public Job removeInspection(Inspection inspection) {
        this.inspections.remove(inspection);
        inspection.setJobs(null);
        return this;
    }

    public Set<Visit> getVisits() {
        return this.visits;
    }

    public void setVisits(Set<Visit> visits) {
        if (this.visits != null) {
            this.visits.forEach(i -> i.setJob(null));
        }
        if (visits != null) {
            visits.forEach(i -> i.setJob(this));
        }
        this.visits = visits;
    }

    public Job visits(Set<Visit> visits) {
        this.setVisits(visits);
        return this;
    }

    public Job addVisits(Visit visit) {
        this.visits.add(visit);
        visit.setJob(this);
        return this;
    }

    public Job removeVisits(Visit visit) {
        this.visits.remove(visit);
        visit.setJob(null);
        return this;
    }

    public ClientUser getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(ClientUser clientUser) {
        this.createdBy = clientUser;
    }

    public Job createdBy(ClientUser clientUser) {
        this.setCreatedBy(clientUser);
        return this;
    }

    public Engineer getAssignedTo() {
        return this.assignedTo;
    }

    public void setAssignedTo(Engineer engineer) {
        this.assignedTo = engineer;
    }

    public Job assignedTo(Engineer engineer) {
        this.setAssignedTo(engineer);
        return this;
    }

    public Address getAddress() {
        return this.address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Job address(Address address) {
        this.setAddress(address);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Job)) {
            return false;
        }
        return id != null && id.equals(((Job) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Job{" +
            "id=" + getId() +
            ", priority='" + getPriority() + "'" +
            ", created='" + getCreated() + "'" +
            ", occupiersName='" + getOccupiersName() + "'" +
            ", occupiersHomePhone='" + getOccupiersHomePhone() + "'" +
            ", occupiersWorkPhone='" + getOccupiersWorkPhone() + "'" +
            ", occupiersMobilePhone='" + getOccupiersMobilePhone() + "'" +
            ", clientOrderId='" + getClientOrderId() + "'" +
            ", assignedAt='" + getAssignedAt() + "'" +
            ", scheduled='" + getScheduled() + "'" +
            ", completed='" + getCompleted() + "'" +
            ", invoiceNumber='" + getInvoiceNumber() + "'" +
            ", fault='" + getFault() + "'" +
            ", accessInstructions='" + getAccessInstructions() + "'" +
            ", updated='" + getUpdated() + "'" +
            "}";
    }
}
