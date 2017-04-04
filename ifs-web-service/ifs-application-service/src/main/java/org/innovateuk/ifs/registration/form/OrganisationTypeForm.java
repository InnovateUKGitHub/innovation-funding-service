package org.innovateuk.ifs.registration.form;

import javax.validation.constraints.NotNull;

public class OrganisationTypeForm {
    @NotNull(message="{validation.standard.organisationtype.required}")
    private Long organisationType;
    private boolean selectedByDefault;

    public Long getOrganisationType() {
        return organisationType;
    }

    public void setOrganisationType(Long organisationType) {
        this.organisationType = organisationType;
    }

    public boolean isSelectedByDefault() {
        return selectedByDefault;
    }

    public void setSelectedByDefault(boolean selectedByDefault) {
        this.selectedByDefault = selectedByDefault;
    }
}
