package org.innovateuk.ifs.project.pendingpartner.viewmodel;

import org.innovateuk.ifs.application.forms.sections.yourorganisation.viewmodel.YourOrganisationViewModel;

public class ProjectYourOrganisationViewModel extends YourOrganisationViewModel {
    private final long projectId;
    private final String projectName;
    private final long organisationId;
    private final boolean h2020;
    private final boolean readOnly;

    public ProjectYourOrganisationViewModel(boolean showStateAidAgreement, boolean fundingSectionComplete, boolean h2020, long projectId, String projectName, long organisationId, boolean readOnly) {
        super(showStateAidAgreement, fundingSectionComplete, h2020);
        this.projectId = projectId;
        this.projectName = projectName;
        this.organisationId = organisationId;
        this.h2020 = h2020;
        this.readOnly = readOnly;
    }

    public long getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public long getOrganisationId() {
        return organisationId;
    }

    @Override
    public boolean isH2020() {
        return h2020;
    }

    public boolean isReadOnly() {
        return readOnly;
    }
}
