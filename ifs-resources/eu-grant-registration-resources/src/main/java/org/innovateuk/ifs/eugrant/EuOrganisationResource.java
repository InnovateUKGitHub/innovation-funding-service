package org.innovateuk.ifs.eugrant;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class EuOrganisationResource {

    private String name;
    private EuOrganisationType organisationType;
    private String companiesHouseNumber;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EuOrganisationType getOrganisationType() {
        return organisationType;
    }

    public void setOrganisationType(EuOrganisationType organisationType) {
        this.organisationType = organisationType;
    }

    public String getCompaniesHouseNumber() {
        return companiesHouseNumber;
    }

    public void setCompaniesHouseNumber(String companiesHouseNumber) {
        this.companiesHouseNumber = companiesHouseNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        EuOrganisationResource that = (EuOrganisationResource) o;

        return new EqualsBuilder()
                .append(name, that.name)
                .append(organisationType, that.organisationType)
                .append(companiesHouseNumber, that.companiesHouseNumber)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(name)
                .append(organisationType)
                .append(companiesHouseNumber)
                .toHashCode();
    }
}
