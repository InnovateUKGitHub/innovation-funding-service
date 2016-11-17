package com.worth.ifs.finance.domain;

import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.resource.OrganisationSize;

import javax.persistence.*;

@Entity
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public abstract class Finance {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="organisationId", referencedColumnName="id")
    private Organisation organisation;

    @Enumerated(EnumType.STRING)
    private OrganisationSize organisationSize;

    public Finance(Organisation organisation, OrganisationSize organisationSize) {
        this.organisation = organisation;
        this.organisationSize = organisationSize;
    }

    public Finance(Organisation organisation) {
        this.organisation = organisation;
    }

    public Finance(Long id, Organisation organisation) {
        this.id = id;
        this.organisation = organisation;
    }

    public Finance() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    public OrganisationSize getOrganisationSize() {
        return organisationSize;
    }

    public void setOrganisationSize(OrganisationSize organisationSize) {
        this.organisationSize = organisationSize;
    }
}
