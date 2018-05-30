package org.innovateuk.ifs.competitionsetup.assessor.viewmodel;

import org.innovateuk.ifs.competition.resource.AssessorCountOptionResource;
import org.innovateuk.ifs.competitionsetup.core.viewmodel.CompetitionSetupViewModel;
import org.innovateuk.ifs.competitionsetup.core.viewmodel.GeneralSetupViewModel;

import java.util.List;

public class AssessorViewModel extends CompetitionSetupViewModel {
    private List<AssessorCountOptionResource> assessorOptions;
    private boolean isAssessmentClosed;
    private boolean isSetupAndAfterNotifications;

    public AssessorViewModel(GeneralSetupViewModel generalSetupViewModel, List<AssessorCountOptionResource> assessorOptions, boolean isAssessmentClosed, boolean isSetupAndAfterNotifications) {
        this.generalSetupViewModel = generalSetupViewModel;
        this.assessorOptions = assessorOptions;
        this.isAssessmentClosed = isAssessmentClosed;
        this.isSetupAndAfterNotifications = isSetupAndAfterNotifications;
    }

    public List<AssessorCountOptionResource> getAssessorOptions() {
        return assessorOptions;
    }

    public boolean isAssessmentClosed() {
        return isAssessmentClosed;
    }

    public boolean isSetupAndAfterNotifications() {
        return isSetupAndAfterNotifications;
    }
}
