package org.innovateuk.ifs.management.competition.setup.closecompetition.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competition.service.MilestoneRestService;
import org.innovateuk.ifs.management.competition.setup.closecompetition.viewmodel.AlwaysOpenCloseCompetitionViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

import static org.innovateuk.ifs.competition.resource.MilestoneType.SUBMISSION_DATE;

@Component
public class AlwaysOpenCloseCompetitionViewModelPopulator {

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private MilestoneRestService milestoneRestService;

    public AlwaysOpenCloseCompetitionViewModel populate(Long competitionId) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        ZonedDateTime submissionDate = milestoneRestService.getMilestoneByTypeAndCompetitionId(SUBMISSION_DATE, competitionId).getSuccess().getDate();

        return new AlwaysOpenCloseCompetitionViewModel(competitionId,
                competition.getName(),
                false);
    }

}
