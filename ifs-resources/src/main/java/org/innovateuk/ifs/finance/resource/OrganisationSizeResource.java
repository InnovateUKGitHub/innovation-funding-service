package org.innovateuk.ifs.finance.resource;

public class OrganisationSizeResource {

    private Long id;
    private String description;

    public OrganisationSizeResource() {}

    public OrganisationSizeResource(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
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
}
