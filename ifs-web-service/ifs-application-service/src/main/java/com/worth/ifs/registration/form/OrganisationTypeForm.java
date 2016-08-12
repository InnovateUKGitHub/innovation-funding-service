package com.worth.ifs.registration.form;

import javax.validation.constraints.NotNull;

public class OrganisationTypeForm {
    @NotNull(message="{validation.standard.organisationtype.required}")
    private Long organisationType;

    public Long getOrganisationType() {
        return organisationType;
    }

    public void setOrganisationType(Long organisationType) {
        this.organisationType = organisationType;
    }
}
