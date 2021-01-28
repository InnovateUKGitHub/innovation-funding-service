package org.innovateuk.ifs.management.assessmentperiod.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competition.service.MilestoneRestService;
import org.innovateuk.ifs.management.assessmentperiod.model.ManageAssessmentPeriodsViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ManageAssessmentPeriodsPopulator {

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private MilestoneRestService milestoneRestService;

    public ManageAssessmentPeriodsViewModel populateModel(long competitionId) {

        CompetitionResource competitionResource = competitionRestService.getCompetitionById(competitionId).getSuccess();

        return new ManageAssessmentPeriodsViewModel(competitionResource);
    }



}
