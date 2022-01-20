package com.awsptm.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Type;

/**
 * A Visit.
 */
@Entity
@Table(name = "visit")
public class Visit implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "arrived")
    private ZonedDateTime arrived;

    @Column(name = "departed")
    private ZonedDateTime departed;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "description")
    private String description;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "actions")
    private String actions;

    @Column(name = "labour", precision = 21, scale = 2)
    private BigDecimal labour;

    @OneToMany(mappedBy = "visit")
    @JsonIgnoreProperties(value = { "visit" }, allowSetters = true)
    private Set<Material> materials = new HashSet<>();

    @OneToMany(mappedBy = "visit")
    @JsonIgnoreProperties(value = { "visit" }, allowSetters = true)
    private Set<Certificate> certificates = new HashSet<>();

    @OneToMany(mappedBy = "visit")
    @JsonIgnoreProperties(value = { "visit" }, allowSetters = true)
    private Set<Evidence> evidences = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "inspections", "visits", "createdBy", "assignedTo", "address" }, allowSetters = true)
    private Job job;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Visit id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getArrived() {
        return this.arrived;
    }

    public Visit arrived(ZonedDateTime arrived) {
        this.setArrived(arrived);
        return this;
    }

    public void setArrived(ZonedDateTime arrived) {
        this.arrived = arrived;
    }

    public ZonedDateTime getDeparted() {
        return this.departed;
    }

    public Visit departed(ZonedDateTime departed) {
        this.setDeparted(departed);
        return this;
    }

    public void setDeparted(ZonedDateTime departed) {
        this.departed = departed;
    }

    public String getDescription() {
        return this.description;
    }

    public Visit description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getActions() {
        return this.actions;
    }

    public Visit actions(String actions) {
        this.setActions(actions);
        return this;
    }

    public void setActions(String actions) {
        this.actions = actions;
    }

    public BigDecimal getLabour() {
        return this.labour;
    }

    public Visit labour(BigDecimal labour) {
        this.setLabour(labour);
        return this;
    }

    public void setLabour(BigDecimal labour) {
        this.labour = labour;
    }

    public Set<Material> getMaterials() {
        return this.materials;
    }

    public void setMaterials(Set<Material> materials) {
        if (this.materials != null) {
            this.materials.forEach(i -> i.setVisit(null));
        }
        if (materials != null) {
            materials.forEach(i -> i.setVisit(this));
        }
        this.materials = materials;
    }

    public Visit materials(Set<Material> materials) {
        this.setMaterials(materials);
        return this;
    }

    public Visit addMaterials(Material material) {
        this.materials.add(material);
        material.setVisit(this);
        return this;
    }

    public Visit removeMaterials(Material material) {
        this.materials.remove(material);
        material.setVisit(null);
        return this;
    }

    public Set<Certificate> getCertificates() {
        return this.certificates;
    }

    public void setCertificates(Set<Certificate> certificates) {
        if (this.certificates != null) {
            this.certificates.forEach(i -> i.setVisit(null));
        }
        if (certificates != null) {
            certificates.forEach(i -> i.setVisit(this));
        }
        this.certificates = certificates;
    }

    public Visit certificates(Set<Certificate> certificates) {
        this.setCertificates(certificates);
        return this;
    }

    public Visit addCertificates(Certificate certificate) {
        this.certificates.add(certificate);
        certificate.setVisit(this);
        return this;
    }

    public Visit removeCertificates(Certificate certificate) {
        this.certificates.remove(certificate);
        certificate.setVisit(null);
        return this;
    }

    public Set<Evidence> getEvidences() {
        return this.evidences;
    }

    public void setEvidences(Set<Evidence> evidences) {
        if (this.evidences != null) {
            this.evidences.forEach(i -> i.setVisit(null));
        }
        if (evidences != null) {
            evidences.forEach(i -> i.setVisit(this));
        }
        this.evidences = evidences;
    }

    public Visit evidences(Set<Evidence> evidences) {
        this.setEvidences(evidences);
        return this;
    }

    public Visit addEvidences(Evidence evidence) {
        this.evidences.add(evidence);
        evidence.setVisit(this);
        return this;
    }

    public Visit removeEvidences(Evidence evidence) {
        this.evidences.remove(evidence);
        evidence.setVisit(null);
        return this;
    }

    public Job getJob() {
        return this.job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public Visit job(Job job) {
        this.setJob(job);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Visit)) {
            return false;
        }
        return id != null && id.equals(((Visit) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Visit{" +
            "id=" + getId() +
            ", arrived='" + getArrived() + "'" +
            ", departed='" + getDeparted() + "'" +
            ", description='" + getDescription() + "'" +
            ", actions='" + getActions() + "'" +
            ", labour=" + getLabour() +
            "}";
    }
}
