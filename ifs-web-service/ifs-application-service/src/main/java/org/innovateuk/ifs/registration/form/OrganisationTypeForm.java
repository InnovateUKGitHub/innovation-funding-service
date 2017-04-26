package org.innovateuk.ifs.registration.form;

import javax.validation.constraints.NotNull;

public class OrganisationTypeForm {
    @NotNull(message="{validation.standard.organisationtype.required}")
    private Long organisationType;
    private boolean isLeadApplicant;

    public Long getOrganisationType() {
        return organisationType;
    }

    public void setOrganisationType(Long organisationType) {
        this.organisationType = organisationType;
    }

    public boolean isLeadApplicant() {
        return isLeadApplicant;
    }

    public void setLeadApplicant(boolean leadApplicant) {
        this.isLeadApplicant = leadApplicant;
    }
}
