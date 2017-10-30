package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionOpenQueryResource;
import org.innovateuk.ifs.competition.resource.CompetitionSearchResultItem;

import java.util.List;

/**
 * Rest service for handling Feedback and assessing actions on Competitions
 */
public interface CompetitionFeedbackRestService {

    RestResult<Void> notifyAssessors(long competitionId);

    RestResult<Void> releaseFeedback(long competitionId);

    RestResult<List<CompetitionSearchResultItem>> findFeedbackReleasedCompetitions();

    RestResult<List<CompetitionOpenQueryResource>> getCompetitionOpenQueries(long competitionId);

    RestResult<Long> getCompetitionOpenQueriesCount(long competitionId);
}
