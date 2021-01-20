package org.innovateuk.ifs.management.assessmentperiod.model;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.assessmentperiod.form.AssessmentPeriodForm;

public class ManageAssessmentPeriodsViewModel {

    private long competitionId;
    AssessmentPeriodForm assessmentPeriodForm;

    public ManageAssessmentPeriodsViewModel(CompetitionResource competitionResource,
                                            AssessmentPeriodForm assessmentPeriodForm) {
        this.competitionId = competitionResource.getId();
        this.assessmentPeriodForm = assessmentPeriodForm;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public AssessmentPeriodForm getAssessmentPeriodForm() {
        return assessmentPeriodForm;
    }
}
