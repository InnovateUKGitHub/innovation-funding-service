package org.innovateuk.ifs.interview.model;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionKeyApplicationStatisticsRestService;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.interview.resource.InterviewStatisticsResource;
import org.innovateuk.ifs.interview.viewmodel.InterviewViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Competition Interview Panel dashboard
 */
@Component
public class InterviewModelPopulator {

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private CompetitionKeyApplicationStatisticsRestService competitionKeyApplicationStatisticsRestService;

    public InterviewViewModel populateModel(long competitionId) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        InterviewStatisticsResource interviewStatisticsResource = competitionKeyApplicationStatisticsRestService
                .getInterviewStatisticsByCompetition(competitionId).getSuccess();
        return new InterviewViewModel(
                competition.getId(),
                competition.getName(),
                competition.getCompetitionStatus(),
                interviewStatisticsResource);
    }
}