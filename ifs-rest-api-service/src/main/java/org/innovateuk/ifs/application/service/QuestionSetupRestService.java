package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;

import java.util.Map;


/**
 * Interface for getting and saving statuses on in setup for questions.
 */
public interface QuestionSetupRestService {

    RestResult<Void> markQuestionSetupComplete(long competitionId, CompetitionSetupSection parentSection, long questionId);

    RestResult<Void> markQuestionSetupIncomplete(long competitionId, CompetitionSetupSection parentSection, long questionId);

    RestResult<Map<Long, Boolean>> getQuestionStatuses(long competitionId, CompetitionSetupSection parentSection);
}
