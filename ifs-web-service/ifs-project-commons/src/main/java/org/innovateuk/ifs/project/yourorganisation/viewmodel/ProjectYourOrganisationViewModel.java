package org.innovateuk.ifs.project.yourorganisation.viewmodel;

import org.innovateuk.ifs.application.forms.sections.yourorganisation.viewmodel.YourOrganisationViewModel;

public class ProjectYourOrganisationViewModel extends YourOrganisationViewModel {
    private final long projectId;
    private final String projectName;
    private final long organisationId;
    private final boolean h2020;
    private final boolean readOnly;
    private final boolean showHints;
    private final boolean procurementCompetition;

    public ProjectYourOrganisationViewModel(boolean showStateAidAgreement, boolean fundingSectionComplete, boolean h2020, long projectId, String projectName, long organisationId, boolean readOnly, boolean showHints, boolean procurementCompetition) {
        super(showStateAidAgreement, fundingSectionComplete, h2020, procurementCompetition);
        this.projectId = projectId;
        this.projectName = projectName;
        this.organisationId = organisationId;
        this.h2020 = h2020;
        this.procurementCompetition = procurementCompetition;
        this.readOnly = readOnly;
        this.showHints = showHints;
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

    public boolean isShowHints() {
        return showHints;
    }

    @Override
    public boolean isH2020() {
        return h2020;
    }

    @Override
    public boolean isProcurementCompetition() {
        return procurementCompetition;
    }

    public boolean isReadOnly() {
        return readOnly;
    }
}
