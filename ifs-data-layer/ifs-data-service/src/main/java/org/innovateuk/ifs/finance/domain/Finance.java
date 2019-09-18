package org.innovateuk.ifs.finance.domain;

import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.organisation.domain.Organisation;

import javax.persistence.*;

/**
 * Base class for high-level Organisational Finances belonging to different aspects of the IFS application
 */
@MappedSuperclass
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public abstract class Finance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="organisationId", referencedColumnName="id")
    private Organisation organisation;

    @Column(name = "organisation_size_id")
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

    public String getOrganisationName() {
        return organisation.getName();
    }
}
