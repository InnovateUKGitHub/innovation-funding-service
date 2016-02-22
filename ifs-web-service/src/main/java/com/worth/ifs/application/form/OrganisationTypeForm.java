package com.worth.ifs.application.form;

import javax.validation.constraints.NotNull;

public class OrganisationTypeForm {
    @NotNull
    private Long organisationType;

    public Long getOrganisationType() {
        return organisationType;
    }

    public void setOrganisationType(Long organisationType) {
        this.organisationType = organisationType;
    }
}
