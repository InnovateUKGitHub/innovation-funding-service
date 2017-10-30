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

    private String questionSetupRestURL = "/question/setup";

    @Override
    public RestResult<Void> markQuestionSetupComplete(long competitionId, long questionId) {
        return putWithRestResult(String.format("%s/markAsComplete/%d/%d", questionSetupRestURL, questionId, competitionId), Void.class);
    }

    @Override
    public RestResult<Void> markQuestionSetupInComplete(long competitionId, long questionId) {
        return putWithRestResult(String.format("%s/markAsInComplete/%d/%d", questionSetupRestURL, questionId, competitionId), Void.class);
    }

    @Override
    public RestResult<Map<Long, Boolean>> getQuestionStatuses(long competitionId, CompetitionSetupSection parentSection) {
        return getWithRestResult(String.format("%s/getStatuses/%d/%s", questionSetupRestURL, competitionId, parentSection), longStatusMap());
    }
}
