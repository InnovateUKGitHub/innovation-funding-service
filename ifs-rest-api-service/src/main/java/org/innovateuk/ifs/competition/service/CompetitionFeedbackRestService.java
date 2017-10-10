package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionSearchResultItem;

import java.util.List;

/**
 * TODO
 */
public interface CompetitionFeedbackRestService {

    RestResult<Void> notifyAssessors(long competitionId);

    RestResult<Void> releaseFeedback(long competitionId);

    RestResult<List<CompetitionSearchResultItem>> findFeedbackReleasedCompetitions();
}
