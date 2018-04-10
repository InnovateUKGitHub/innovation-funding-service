package org.innovateuk.ifs.question.service.controller.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionResource;

public interface QuestionSetupCompetitionService {

    ServiceResult<CompetitionSetupQuestionResource> getQuestion(Long questionId);

    ServiceResult<Void> updateQuestion(CompetitionSetupQuestionResource question);

    ServiceResult<CompetitionSetupQuestionResource> createDefaultQuestion(Long competitionId);

    ServiceResult<Void> deleteQuestion(Long questionId);
}
