package org.innovateuk.ifs.eugrant.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
public class EuGrant {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @OneToOne(cascade = CascadeType.ALL)
    private EuOrganisation organisation;

    @OneToOne(cascade = CascadeType.ALL)
    private EuContact contact;

    @OneToOne(cascade = CascadeType.ALL)
    private EuFunding funding;

    private boolean submitted;

    private String shortCode;

    public EuGrant() {
        this.submitted = false;
    }

    public void submit() {
        if (submitted) {
            throw new IllegalStateException("cannot resubmit an eugrant");
        }
        this.submitted = true;
        // TODO generate short code
    }

    public UUID getId() {
        return id;
    }

    public void setContact(EuContact contact) {
        this.contact = contact;
    }

    public EuContact getContact() {
        return contact;
    }

    public void setOrganisation(EuOrganisation organisation) {
        this.organisation = organisation;
    }

    public EuOrganisation getOrganisation() {
        return organisation;
    }

    public EuFunding getFunding() {
        return funding;
    }

    public void setFunding(EuFunding funding) {
        this.funding = funding;
    }

    public String getShortCode() {
        return shortCode;
    }
}