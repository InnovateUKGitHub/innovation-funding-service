package com.worth.ifs.application;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class ContributorsForm implements Serializable {

    @Valid
    private List<OrganisationInvite> organisations;

    public ContributorsForm() {
        organisations = new LinkedList<>();
    }

    public List<OrganisationInvite> getOrganisations() {
        return organisations;
    }

    public void setOrganisations(List<OrganisationInvite> organisations) {
        this.organisations = organisations;
    }
}

