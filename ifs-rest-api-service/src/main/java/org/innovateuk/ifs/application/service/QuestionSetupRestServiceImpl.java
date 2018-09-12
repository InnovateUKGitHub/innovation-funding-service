package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.springframework.stereotype.Service;

import java.util.Map;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.longStatusMap;

/**
 * Implements {@link QuestionSetupRestService}
 */
@Service
public class QuestionSetupRestServiceImpl extends BaseRestService implements QuestionSetupRestService {

    private static final String QUESTION_SETUP_REST_URL = "/question/setup";

    @Override
    public RestResult<Void> markQuestionSetupComplete(long competitionId, CompetitionSetupSection parentSection, long questionId) {
        return putWithRestResult(String.format("%s/mark-as-complete/%d/%s/%d", QUESTION_SETUP_REST_URL, competitionId, parentSection, questionId), Void.class);
    }

    @Override
    public RestResult<Void> markQuestionSetupIncomplete(long competitionId, CompetitionSetupSection parentSection, long questionId) {
        return putWithRestResult(String.format("%s/mark-as-incomplete/%d/%s/%d", QUESTION_SETUP_REST_URL, competitionId, parentSection, questionId), Void.class);
    }

    @Override
    public RestResult<Map<Long, Boolean>> getQuestionStatuses(long competitionId, CompetitionSetupSection parentSection) {
        return getWithRestResult(String.format("%s/get-statuses/%d/%s", QUESTION_SETUP_REST_URL, competitionId, parentSection), longStatusMap());
    }
}
