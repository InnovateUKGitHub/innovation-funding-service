package org.innovateuk.ifs.management.assessmentperiod.model;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.competition.setup.milestone.form.MilestonesForm;

import java.util.List;

public class ManageAssessmentPeriodsViewModel {

    private final long competitionId;
    List<MilestonesForm> assessmentPeriods;


    public ManageAssessmentPeriodsViewModel(CompetitionResource competitionResource,
                                            List<MilestonesForm> assessmentPeriods) {
        this.competitionId = competitionResource.getId();
        this.assessmentPeriods = assessmentPeriods;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public List<MilestonesForm> getAssessmentPeriods() {
        return assessmentPeriods;
    }
}
