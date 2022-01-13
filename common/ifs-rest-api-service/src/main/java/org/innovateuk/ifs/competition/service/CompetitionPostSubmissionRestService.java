package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionOpenQueryResource;
import org.innovateuk.ifs.competition.resource.SpendProfileStatusResource;

import java.util.List;

/**
 * Rest service for handling Feedback and assessing actions on Competitions
 */
public interface CompetitionPostSubmissionRestService {

    RestResult<Void> releaseFeedback(long competitionId);

    RestResult<List<CompetitionOpenQueryResource>> getCompetitionOpenQueries(long competitionId);

    RestResult<Long> getCompetitionOpenQueriesCount(long competitionId);

    RestResult<List<SpendProfileStatusResource>> getPendingSpendProfiles(long competitionId);

    RestResult<Long> countPendingSpendProfiles(long competitionId);

    RestResult<Void> closeAssessment(long competitionId);

    RestResult<Void> reopenAssessmentPeriod(long competitionId);
}
