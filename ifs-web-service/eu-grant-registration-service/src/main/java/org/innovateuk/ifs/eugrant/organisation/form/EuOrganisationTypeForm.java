package org.innovateuk.ifs.eugrant.organisation.form;

import org.innovateuk.ifs.eugrant.EuOrganisationType;

import javax.validation.constraints.NotNull;

public class EuOrganisationTypeForm {
    @NotNull(message="{validation.standard.organisationtype.required}")
    private EuOrganisationType organisationType;

    public EuOrganisationType getOrganisationType() {
        return organisationType;
    }

    public void setOrganisationType(EuOrganisationType organisationType) {
        this.organisationType = organisationType;
    }

}
