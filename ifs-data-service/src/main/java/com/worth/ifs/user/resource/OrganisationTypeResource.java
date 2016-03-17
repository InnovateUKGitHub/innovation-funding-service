package com.worth.ifs.user.resource;

public class OrganisationTypeResource {
    private Long id;
    private String name;
    private Long parentOrganisationType;

    public OrganisationTypeResource(Long id, String name, Long parentOrganisationType) {
        this.id = id;
        this.name = name;
        this.parentOrganisationType = parentOrganisationType;
    }

    public OrganisationTypeResource() {
    	// no-arg constructor
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getParentOrganisationType() {
        return this.parentOrganisationType;
    }

    public void setParentOrganisationType(Long parentOrganisationType) {
        this.parentOrganisationType = parentOrganisationType;
    }
}
