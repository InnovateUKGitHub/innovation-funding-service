package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.springframework.stereotype.Service;

import java.util.Map;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.longStatusMap;

/**
 * CompetitionsRestServiceImpl is a utility for CRUD operations on {@link CompetitionResource}.
 * This class connects to the { org.innovateuk.ifs.competition.controller.CompetitionController}
 * through a REST call.
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
