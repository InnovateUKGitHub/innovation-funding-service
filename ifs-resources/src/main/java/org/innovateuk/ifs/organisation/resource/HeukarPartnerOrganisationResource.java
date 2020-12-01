package org.innovateuk.ifs.organisation.resource;

public class HeukarPartnerOrganisationResource {
    private Long id;
    private Long applicationId;
    private OrganisationTypeResource organisationTypeResource;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public OrganisationTypeResource getOrganisationTypeResource() {
        return organisationTypeResource;
    }

    public void setOrganisationTypeResource(OrganisationTypeResource organisationTypeResource) {
        this.organisationTypeResource = organisationTypeResource;
    }
}
