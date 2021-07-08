package org.innovateuk.ifs.project.financechecks.viewmodel;

public class ProjectOrganisationRowViewModel {

    private final Long organisationId;
    private final String organisationName;
    private final boolean lead;

    public ProjectOrganisationRowViewModel(Long organisationId, String organisationName, boolean lead) {
        this.organisationId = organisationId;
        this.organisationName = organisationName;
        this.lead = lead;
    }

    public Long getOrganisationId() {
        return organisationId;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public boolean isLead() {
        return lead;
    }
}
