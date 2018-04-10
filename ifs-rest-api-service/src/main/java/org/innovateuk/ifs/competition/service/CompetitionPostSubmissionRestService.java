package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.application.resource.ApplicationPageResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionOpenQueryResource;
import org.innovateuk.ifs.competition.resource.SpendProfileStatusResource;
import org.innovateuk.ifs.competition.resource.CompetitionSearchResultItem;

import java.util.List;

/**
 * Rest service for handling Feedback and assessing actions on Competitions
 */
public interface CompetitionPostSubmissionRestService {

    RestResult<Void> notifyAssessors(long competitionId);

    RestResult<Void> releaseFeedback(long competitionId);

    RestResult<List<CompetitionSearchResultItem>> findFeedbackReleasedCompetitions();

    RestResult<List<CompetitionOpenQueryResource>> getCompetitionOpenQueries(long competitionId);

    RestResult<Long> getCompetitionOpenQueriesCount(long competitionId);

    RestResult<List<SpendProfileStatusResource>> getPendingSpendProfiles(long competitionId);

    RestResult<Long> countPendingSpendProfiles(long competitionId);

    RestResult<Void> closeAssessment(long competitionId);
}
