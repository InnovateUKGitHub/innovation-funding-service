package org.innovateuk.ifs.question.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;

import java.util.Map;


/**
 * Interface for getting and saving statuses on in setup for questions.
 */
public interface QuestionSetupCompetitionRestService {

    RestResult<CompetitionSetupQuestionResource> getByQuestionId(Long questionId);

    RestResult<Void> save(CompetitionSetupQuestionResource competitionSetupQuestionResource);

    RestResult<CompetitionSetupQuestionResource> addDefaultToCompetition(Long competitionId);

    RestResult<Void> deleteById(Long questionId);
}
