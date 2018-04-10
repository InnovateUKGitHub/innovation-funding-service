package org.innovateuk.ifs.question.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionResource;
import org.springframework.stereotype.Service;

/**
 * CompetitionsRestServiceImpl is a utility for CRUD operations on {@link CompetitionSetupQuestionResource}.
 * through a REST call.
 */
@Service
public class QuestionSetupCompetitionRestServiceImpl extends BaseRestService implements QuestionSetupCompetitionRestService {

    @SuppressWarnings("unused")
    private static final Log LOG = LogFactory.getLog(QuestionSetupCompetitionRestServiceImpl.class);
    private String questionRestURL = "/question";

    @Override
    public RestResult<CompetitionSetupQuestionResource> getByQuestionId(Long questionId) {
        return getWithRestResult(questionRestURL + "/getById/" + questionId, CompetitionSetupQuestionResource.class);
    }

    @Override
    public RestResult<Void> save(CompetitionSetupQuestionResource competitionSetupQuestionResource) {
        return putWithRestResult(questionRestURL + "/save", competitionSetupQuestionResource, Void.class);
    }

    @Override
    public RestResult<CompetitionSetupQuestionResource> addDefaultToCompetition(Long competitionId) {
        return postWithRestResult(questionRestURL + "/addDefaultToCompetition/" + competitionId, CompetitionSetupQuestionResource.class);
    }

    @Override
    public RestResult<Void> deleteById(Long questionId) {
        return deleteWithRestResult(questionRestURL + "/deleteById/" + questionId, Void.class);
    }
}
