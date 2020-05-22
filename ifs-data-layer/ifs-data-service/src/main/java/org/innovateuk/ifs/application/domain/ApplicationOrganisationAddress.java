package org.innovateuk.ifs.application.domain;

import org.innovateuk.ifs.organisation.domain.OrganisationAddress;

import javax.persistence.*;

/**
 * Entity to link an organisation_address with an application.
 */
@Entity
public class ApplicationOrganisationAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private OrganisationAddress organisationAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    private Application application;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrganisationAddress getOrganisationAddress() {
        return organisationAddress;
    }

    public void setOrganisationAddress(OrganisationAddress organisationAddress) {
        this.organisationAddress = organisationAddress;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }
}