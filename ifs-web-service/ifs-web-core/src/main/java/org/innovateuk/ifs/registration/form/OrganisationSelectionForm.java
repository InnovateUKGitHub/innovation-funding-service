package org.innovateuk.ifs.registration.form;

import javax.validation.constraints.NotNull;

public class OrganisationSelectionForm {

    @NotNull(message = "{validation.standard.organisation.required}")
    private Long selectedOrganisationId;

    public Long getSelectedOrganisationId() {
        return selectedOrganisationId;
    }

    public void setSelectedOrganisationId(Long selectedOrganisationId) {
        this.selectedOrganisationId = selectedOrganisationId;
    }
}
