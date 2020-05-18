package org.innovateuk.ifs.registration.form;

import javax.validation.constraints.NotNull;

public class OrganisationInternationalForm {

    @NotNull(message = "{validation.standard.organisation.isInternational.required}")
    private Boolean international;

    public Boolean getInternational() {
        return international;
    }

    public void setInternational(Boolean international) {
        this.international = international;
    }
}