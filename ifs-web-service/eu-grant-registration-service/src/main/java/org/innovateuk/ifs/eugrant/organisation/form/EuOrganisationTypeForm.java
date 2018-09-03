package org.innovateuk.ifs.eugrant.organisation.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.eugrant.EuOrganisationType;

import javax.validation.constraints.NotNull;

public class EuOrganisationTypeForm {
    @NotNull(message="{validation.standard.organisationtype.required}")
    private EuOrganisationType organisationType;

    public EuOrganisationTypeForm() {}

    public EuOrganisationTypeForm(EuOrganisationType organisationType) {
        this.organisationType = organisationType;
    }

    public EuOrganisationType getOrganisationType() {
        return organisationType;
    }

    public void setOrganisationType(EuOrganisationType organisationType) {
        this.organisationType = organisationType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        EuOrganisationTypeForm that = (EuOrganisationTypeForm) o;

        return new EqualsBuilder()
                .append(organisationType, that.organisationType)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(organisationType)
                .toHashCode();
    }
}
