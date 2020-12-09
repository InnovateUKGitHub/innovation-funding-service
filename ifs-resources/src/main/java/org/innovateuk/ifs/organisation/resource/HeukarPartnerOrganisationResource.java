package org.innovateuk.ifs.organisation.resource;

public class HeukarPartnerOrganisationResource {
    private Long id;
    private Long applicationId;

    private Long organisationTypeId;
    private String name;
    private String description;
//    private OrganisationTypeResource organisationTypeResource;

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

//    public OrganisationTypeResource getOrganisationTypeResource() {
//        return organisationTypeResource;
//    }
//
//    public void setOrganisationTypeResource(OrganisationTypeResource organisationTypeResource) {
//        this.organisationTypeResource = organisationTypeResource;
//    }


    public Long getOrganisationTypeId() {
        return organisationTypeId;
    }

    public void setOrganisationTypeId(Long organisationTypeId) {
        this.organisationTypeId = organisationTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
