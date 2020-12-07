package org.innovateuk.ifs.organisation.resource;

public class OrganisationExecutiveOfficerResource {
    private Long id;
    private Long organisation;
    private String name;

    public OrganisationExecutiveOfficerResource() {

    }

    public OrganisationExecutiveOfficerResource(Long organisation, String name) {
        this.id = id;
        this.organisation = organisation;
        this.name = name;
    }

    public OrganisationExecutiveOfficerResource(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Long organisation) {
        this.organisation = organisation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
