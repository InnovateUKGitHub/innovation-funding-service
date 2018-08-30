package org.innovateuk.ifs.eugrant.organisation.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.eugrant.EuOrganisationType;

import javax.validation.constraints.NotNull;

public class OrganisationTypeForm {
    @NotNull(message="{validation.standard.organisationtype.required}")
    private EuOrganisationType organisationType;
    private boolean isLeadApplicant;

    public EuOrganisationType getOrganisationType() {
        return organisationType;
    }

    public void setOrganisationType(EuOrganisationType organisationType) {
        this.organisationType = organisationType;
    }

    public boolean isLeadApplicant() {
        return isLeadApplicant;
    }

    public void setLeadApplicant(boolean leadApplicant) {
        this.isLeadApplicant = leadApplicant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        OrganisationTypeForm that = (OrganisationTypeForm) o;

        return new EqualsBuilder()
                .append(isLeadApplicant, that.isLeadApplicant)
                .append(organisationType, that.organisationType)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(organisationType)
                .append(isLeadApplicant)
                .toHashCode();
    }
}
