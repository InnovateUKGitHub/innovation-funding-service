package org.innovateuk.ifs.organisation.resource;

import java.io.Serializable;
import java.util.Objects;

public class OrganisationExecutiveOfficerResource implements Serializable {

    private static final long serialVersionUID = 6318064923671344867L;

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
        return Objects.equals(id, that.id) &&
                Objects.equals(organisation, that.organisation) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, organisation, name);
    }
}
