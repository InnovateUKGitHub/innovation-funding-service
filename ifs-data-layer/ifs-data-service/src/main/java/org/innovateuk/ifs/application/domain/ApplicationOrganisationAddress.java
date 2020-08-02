package org.innovateuk.ifs.application.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
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

    //for orm
    ApplicationOrganisationAddress() {}

    public ApplicationOrganisationAddress(OrganisationAddress organisationAddress, Application application) {
        this.organisationAddress = organisationAddress;
        this.application = application;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ApplicationOrganisationAddress that = (ApplicationOrganisationAddress) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(organisationAddress, that.organisationAddress)
                .append(application, that.application)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(organisationAddress)
                .append(application)
                .toHashCode();
    }
}