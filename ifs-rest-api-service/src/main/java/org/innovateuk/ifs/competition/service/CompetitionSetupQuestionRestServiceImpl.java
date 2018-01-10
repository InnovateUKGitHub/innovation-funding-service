package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.*;
import org.innovateuk.ifs.commons.service.*;
import org.innovateuk.ifs.competition.resource.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

/**
 * CompetitionsRestServiceImpl is a utility for CRUD operations on {@link Competition}.
 * This class connects to the {@link org.innovateuk.ifs.competition.controller.CompetitionController}
 * through a REST call.
 */
@Service
public class CompetitionSetupQuestionRestServiceImpl extends BaseRestService implements CompetitionSetupQuestionRestService {

    @SuppressWarnings("unused")
    private static final Log LOG = LogFactory.getLog(CompetitionSetupQuestionRestServiceImpl.class);
    private String competitionsSetupRestURL = "/competition-setup-question";

    @Override
    public RestResult<CompetitionSetupQuestionResource> getByQuestionId(Long questionId) {
        return getWithRestResult(competitionsSetupRestURL + "/getById/" + questionId, CompetitionSetupQuestionResource.class);
    }

    @Override
    public RestResult<Void> save(CompetitionSetupQuestionResource competitionSetupQuestionResource) {
        return putWithRestResult(competitionsSetupRestURL + "/save", competitionSetupQuestionResource, Void.class);
    }

    @Override
    public RestResult<CompetitionSetupQuestionResource> addDefaultToCompetition(Long competitionId) {
        return postWithRestResult(competitionsSetupRestURL + "/addDefaultToCompetition/" + competitionId, CompetitionSetupQuestionResource.class);
    }

    @Override
    public RestResult<Void> deleteById(Long questionId) {
        return deleteWithRestResult(competitionsSetupRestURL + "/deleteById/" + questionId, Void.class);
    }


}
