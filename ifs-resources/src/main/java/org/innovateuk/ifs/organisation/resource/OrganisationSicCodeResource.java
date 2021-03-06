package org.innovateuk.ifs.organisation.resource;

import java.util.Objects;

public class OrganisationSicCodeResource {
    private Long id;
    private Long organisation;
    private String sicCode;

    public OrganisationSicCodeResource() {
    }


    public OrganisationSicCodeResource(String sicCode) {
        this.sicCode = sicCode;
    }

    public OrganisationSicCodeResource(Long organisation, String sicCode) {
        this.organisation = organisation;
        this.sicCode = sicCode;
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

    public String getSicCode() {
        return sicCode;
    }


    public void setSicCode(String sicCode) {
        this.sicCode = sicCode;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrganisationSicCodeResource that = (OrganisationSicCodeResource) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(organisation, that.organisation) &&
                Objects.equals(sicCode, that.sicCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, organisation, sicCode);
    }
}
