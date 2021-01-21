package org.innovateuk.ifs.management.assessmentperiod.model;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.assessmentperiod.form.AssessmentPeriodForm;

public class ManageAssessmentPeriodsViewModel {

    private long competitionId;

    public ManageAssessmentPeriodsViewModel(CompetitionResource competitionResource
                                            ) {
        this.competitionId = competitionResource.getId();
    }

    public long getCompetitionId() {
        return competitionId;
    }

}
