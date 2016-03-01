package com.worth.ifs.application.form;

import javax.validation.Valid;
import java.io.Serializable;

public class ContributorsForm implements Serializable {

    private boolean triedToSave = false;
    private Long applicationId;

    @Valid
    private OrganisationInviteFormList organisations;

    public ContributorsForm() {
        organisations = new OrganisationInviteFormList();
    }

    public OrganisationInviteFormList getOrganisations() {
        return organisations;
    }

    public void setOrganisations(OrganisationInviteFormList organisations) {
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

    public void merge(ContributorsForm contributorsFormCookie) {
        this.setTriedToSave(contributorsFormCookie.isTriedToSave());

        if(!contributorsFormCookie.getOrganisations().isEmpty()){
            contributorsFormCookie.getOrganisations().merge(contributorsFormCookie.getOrganisations());
        }
    }
}

