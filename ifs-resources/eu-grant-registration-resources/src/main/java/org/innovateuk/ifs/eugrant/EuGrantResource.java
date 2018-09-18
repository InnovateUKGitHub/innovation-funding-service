package org.innovateuk.ifs.eugrant;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.UUID;

/**
 * Resource for an EU grant registration.
 */
public class EuGrantResource {

    private UUID id;

    private EuOrganisationResource organisation;

    private EuContactResource contact;

    private EuFundingResource funding;

    private boolean organisationComplete;

    private boolean contactComplete;

    private boolean fundingComplete;

    private String shortCode;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public EuOrganisationResource getOrganisation() {
        return organisation;
    }

    public void setOrganisation(EuOrganisationResource organisation) {
        this.organisation = organisation;
    }

    public EuContactResource getContact() {
        return contact;
    }

    public void setContact(EuContactResource contact) {
        this.contact = contact;
    }

    public EuFundingResource getFunding() {
        return funding;
    }

    public void setFunding(EuFundingResource funding) {
        this.funding = funding;
    }

    public boolean isOrganisationComplete() {
        return organisationComplete;
    }

    public void setOrganisationComplete(boolean organisationComplete) {
        this.organisationComplete = organisationComplete;
    }

    public boolean isContactComplete() {
        return contactComplete;
    }

    public void setContactComplete(boolean contactComplete) {
        this.contactComplete = contactComplete;
    }

    public boolean isFundingComplete() {
        return fundingComplete;
    }

    public void setFundingComplete(boolean fundingComplete) {
        this.fundingComplete = fundingComplete;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        EuGrantResource that = (EuGrantResource) o;

        return new EqualsBuilder()
                .append(organisationComplete, that.organisationComplete)
                .append(contactComplete, that.contactComplete)
                .append(fundingComplete, that.fundingComplete)
                .append(id, that.id)
                .append(organisation, that.organisation)
                .append(contact, that.contact)
                .append(funding, that.funding)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(organisation)
                .append(contact)
                .append(funding)
                .append(organisationComplete)
                .append(contactComplete)
                .append(fundingComplete)
                .toHashCode();
    }
}
