package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.resource.PageResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ParameterizedTypeReferences;
import org.innovateuk.ifs.competition.resource.AssessmentPeriodResource;
import org.innovateuk.ifs.crud.AbstractIfsCrudRestServiceImpl;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AssessmentPeriodRestServiceImpl is a utility for CRUD operations on {@link AssessmentPeriodResource}.
 * This class connects to the { org.innovateuk.ifs.competition.controller.AssessmentPeriodController}
 * through a REST call.
 */
@Service
public class AssessmentPeriodRestServiceImpl
        extends AbstractIfsCrudRestServiceImpl<AssessmentPeriodResource, Long>
        implements AssessmentPeriodRestService {

    private String assessmentPeriodRestURL = "/assessment-period";

    @Override
    public RestResult<List<AssessmentPeriodResource>> getAssessmentPeriodByCompetitionId(long competitionId) {
        return getWithRestResult(assessmentPeriodRestURL + "?competitionId=" + competitionId, ParameterizedTypeReferences.assessmentPeriodResourceListType());
    }
    @Override
    public RestResult<PageResource<AssessmentPeriodResource>> getAssessmentPeriodByCompetitionId(long competitionId, int page, int size) {
        return getWithRestResult(String.format("%s/?competitionId=%d&page=%d&size=%d", assessmentPeriodRestURL, competitionId, page, size), new ParameterizedTypeReference<PageResource<AssessmentPeriodResource>>() {});
    }

    @Override
    protected String getBaseUrl() {
        return assessmentPeriodRestURL;
    }

    @Override
    protected Class<AssessmentPeriodResource> getResourceClass() {
        return AssessmentPeriodResource.class;
    }

    @Override
    protected ParameterizedTypeReference<List<AssessmentPeriodResource>> getListTypeReference() {
        return new ParameterizedTypeReference<List<AssessmentPeriodResource>>() {};
    }
}