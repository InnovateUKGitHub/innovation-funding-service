package org.innovateuk.ifs.interview.model;

import org.innovateuk.ifs.competition.service.CompetitionKeyApplicationStatisticsRestService;
import org.innovateuk.ifs.interview.resource.InterviewAssignmentKeyStatisticsResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Invite assessors for Assessment Interview Invite view.
 */
@Component
abstract class InterviewApplicationsModelPopulator {

    @Autowired
    private CompetitionKeyApplicationStatisticsRestService competitionKeyApplicationStatisticsRestService;

    protected InterviewAssignmentKeyStatisticsResource getKeyStatistics(long competitionId) {
        return competitionKeyApplicationStatisticsRestService.getInterviewKeyStatisticsByCompetition(competitionId).getSuccess();
    }
}