package org.innovateuk.ifs.organisation.resource;

import java.util.Objects;

public class OrganisationExecutiveOfficerResource {
    private Long id;
    private Long organisation;
    private String name;

    public OrganisationExecutiveOfficerResource() {

    }

    public OrganisationExecutiveOfficerResource(Long organisation, String name) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrganisationExecutiveOfficerResource that = (OrganisationExecutiveOfficerResource) o;
        return id.equals(that.id) &&
                organisation.equals(that.organisation) &&
                name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, organisation, name);
    }
}
