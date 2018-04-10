package org.innovateuk.ifs.question.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionResource;
import org.springframework.stereotype.Service;

/**
 * Implements {@link QuestionSetupCompetitionRestService}
 */
@Service
public class QuestionSetupCompetitionRestServiceImpl extends BaseRestService implements QuestionSetupCompetitionRestService {

    private static final String questionSetupRestURL = "/question-setup";

    @Override
    public RestResult<CompetitionSetupQuestionResource> getByQuestionId(Long questionId) {
        return getWithRestResult(questionSetupRestURL + "/getById/" + questionId, CompetitionSetupQuestionResource.class);
    }

    @Override
    public RestResult<Void> save(CompetitionSetupQuestionResource competitionSetupQuestionResource) {
        return putWithRestResult(questionSetupRestURL + "/save", competitionSetupQuestionResource, Void.class);
    }

    @Override
    public RestResult<CompetitionSetupQuestionResource> addDefaultToCompetition(Long competitionId) {
        return postWithRestResult(questionSetupRestURL + "/addDefaultToCompetition/" + competitionId, CompetitionSetupQuestionResource.class);
    }

    @Override
    public RestResult<Void> deleteById(Long questionId) {
        return deleteWithRestResult(questionSetupRestURL + "/deleteById/" + questionId, Void.class);
    }
}
