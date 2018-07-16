package org.innovateuk.ifs.finance.resource;

import org.innovateuk.ifs.commons.ZeroDowntime;

@ZeroDowntime(reference = "IFS-3818", description = "To support instances of older REST clients before " +
        "the OrganisationSize enum was introduced")
public class OrganisationSizeResource {

    private Long id;
    private String description;

    public OrganisationSizeResource() {}

    public OrganisationSizeResource(Long id, String description) {
        this.id = id;
        this.description = description;
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
