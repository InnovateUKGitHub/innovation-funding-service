package org.innovateuk.ifs.question.service.controller.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionResource;
import org.innovateuk.ifs.question.service.QuestionSetupCompetitionRestService;
import org.springframework.beans.factory.annotation.Autowired;

public class QuestionSetupCompetitionServiceImpl implements QuestionSetupCompetitionService {

    @Autowired
    private QuestionSetupCompetitionRestService questionSetupCompetitionRestService;

    @Override
    public ServiceResult<CompetitionSetupQuestionResource> createDefaultQuestion(Long competitionId) {
        return questionSetupCompetitionRestService.addDefaultToCompetition(competitionId).toServiceResult();
    }

    @Override
    public ServiceResult<CompetitionSetupQuestionResource> getQuestion(final Long questionId) {
        return questionSetupCompetitionRestService.getByQuestionId(questionId).toServiceResult();
    }

    @Override
    public ServiceResult<Void> updateQuestion(CompetitionSetupQuestionResource competitionSetupQuestionResource) {
        return questionSetupCompetitionRestService.save(competitionSetupQuestionResource).toServiceResult();
    }

    @Override
    public ServiceResult<Void> deleteQuestion(Long questionId) {
        return questionSetupCompetitionRestService.deleteById(questionId).toServiceResult();
    }
}
