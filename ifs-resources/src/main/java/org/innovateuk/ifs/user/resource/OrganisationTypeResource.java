package org.innovateuk.ifs.user.resource;

public class OrganisationTypeResource {
    private Long id;
    private String name;
    private String description;
    private Boolean visibleInSetup;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getVisibleInSetup() {
        return visibleInSetup;
    }

    public void setVisibleInSetup(Boolean visibleInSetup) {
        this.visibleInSetup = visibleInSetup;
    }

    public Long getParentOrganisationType() {
        return this.parentOrganisationType;
    }

    public void setParentOrganisationType(Long parentOrganisationType) {
        this.parentOrganisationType = parentOrganisationType;
    }
}
