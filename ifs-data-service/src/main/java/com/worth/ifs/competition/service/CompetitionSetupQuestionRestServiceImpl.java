package com.worth.ifs.competition.service;

import com.worth.ifs.application.resource.CompetitionSetupQuestionResource;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.competition.domain.Competition;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

/**
 * CompetitionsRestServiceImpl is a utility for CRUD operations on {@link Competition}.
 * This class connects to the {@link com.worth.ifs.competition.controller.CompetitionController}
 * through a REST call.
 */
@Service
public class CompetitionSetupQuestionRestServiceImpl extends BaseRestService implements CompetitionSetupQuestionRestService {

    @SuppressWarnings("unused")
    private static final Log LOG = LogFactory.getLog(CompetitionSetupQuestionRestServiceImpl.class);
    private String competitionsSetupRestURL = "/competitionSetupQuestion";

    @Override
    public RestResult<CompetitionSetupQuestionResource> getByQuestionId(Long questionId) {
        return getWithRestResult(competitionsSetupRestURL + "/" + questionId, CompetitionSetupQuestionResource.class);
    }

    @Override
    public RestResult<Void> save(CompetitionSetupQuestionResource competitionSetupQuestionResource) {
        return putWithRestResult(competitionsSetupRestURL + "/" + competitionSetupQuestionResource.getQuestionId(),
                competitionSetupQuestionResource,
                Void.class);
    }


}