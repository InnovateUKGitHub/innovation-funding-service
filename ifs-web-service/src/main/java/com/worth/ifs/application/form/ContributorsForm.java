package com.worth.ifs.application.form;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class ContributorsForm implements Serializable {
    private boolean triedToSave = false;
    private Long applicationId;

    @Valid
    private List<OrganisationInviteForm> organisations;

    public ContributorsForm() {
        organisations = new LinkedList<>();
    }

    public List<OrganisationInviteForm> getOrganisations() {
        return organisations;
    }

    public void setOrganisations(List<OrganisationInviteForm> organisations) {
        this.organisations = organisations;
    }

    public boolean isTriedToSave() {
        return triedToSave;
    }

    public void setTriedToSave(boolean triedToSave) {
        this.triedToSave = triedToSave;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }
}

