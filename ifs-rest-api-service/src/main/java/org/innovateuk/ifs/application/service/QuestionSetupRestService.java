package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;

import java.util.Map;


/**
 * Interface for CRUD operations on {@link CompetitionResource} related data.
 */
public interface QuestionSetupRestService {

    RestResult<Void> markQuestionSetupComplete(long competitionId, long questionId);

    RestResult<Void> markQuestionSetupInComplete(long competitionId, long questionId);

    RestResult<Map<Long, Boolean>> getQuestionStatuses(long competitionId, CompetitionSetupSection parentSection);
}
