package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.competition.resource.AssessmentPeriodResource;
import org.springframework.stereotype.Service;

/**
 * AssessmentPeriodRestServiceImpl is a utility for CRUD operations on {@link AssessmentPeriodResource}.
 * This class connects to the { org.innovateuk.ifs.competition.controller.AssessmentPeriodController}
 * through a REST call.
 */
@Service
public class AssessmentPeriodRestServiceImpl extends BaseRestService implements AssessmentPeriodRestService {

    private String assessmentPeriodRestURL = "/assessment-period";

    @Override
    public RestResult<AssessmentPeriodResource> getAssessmentPeriodByCompetitionIdAndIndex(Integer index, Long competitionId) {
        return getWithRestResult(assessmentPeriodRestURL + "/" + competitionId + "/get-by-index?index=" + index, AssessmentPeriodResource.class);
    }

    @Override
    public RestResult<AssessmentPeriodResource> create(Integer index, Long competitionId) {
        return postWithRestResult(assessmentPeriodRestURL + "/" + competitionId + "?index=" + index, AssessmentPeriodResource.class);
    }
}
