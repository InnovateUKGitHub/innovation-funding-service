package org.innovateuk.ifs.question.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionResource;

public interface QuestionSetupCompetitionRestService {
    RestResult<CompetitionSetupQuestionResource> getByQuestionId(Long questionId);
    RestResult<Void> save(CompetitionSetupQuestionResource competitionSetupQuestionResource);
    RestResult<CompetitionSetupQuestionResource> addDefaultToCompetition(Long competitionId);
    RestResult<Void> deleteById(Long questionId);
}
