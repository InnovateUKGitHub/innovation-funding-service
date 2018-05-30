package org.innovateuk.ifs.interview.model;


import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionKeyStatisticsRestService;
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
    private CompetitionService competitionService;

    @Autowired
    private CompetitionKeyStatisticsRestService competitionKeyStatisticsRestService;

    public InterviewViewModel populateModel(long competitionId) {
        CompetitionResource competition = competitionService.getById(competitionId);
        InterviewStatisticsResource interviewStatisticsResource = competitionKeyStatisticsRestService.getInterviewStatisticsByCompetition(competitionId).getSuccess();
        return new InterviewViewModel(
                competition.getId(),
                competition.getName(),
                competition.getCompetitionStatus(),
                interviewStatisticsResource);
    }
}