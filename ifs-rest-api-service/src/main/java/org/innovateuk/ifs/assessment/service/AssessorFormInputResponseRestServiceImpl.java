package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponseResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.commons.service.ParameterizedTypeReferences;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.assessorFormInputResponseResourceListType;
import static java.lang.String.format;

/**
 * AssessorFormInputResponseRestServiceImpl is a utility for CRUD operations on {org.innovateuk.ifs.assessment.domain.AssessorFormInputResponse}.
 * This class connects to the {org.innovateuk.ifs.assessment.controller.AssessorFormInputResponseController}
 * through a REST call.
 */
@Service
public class AssessorFormInputResponseRestServiceImpl extends BaseRestService implements AssessorFormInputResponseRestService {

    private String assessorFormInputResponseRestUrl = "/assessorFormInputResponse";

    protected void setAssessorFormInputResponseRestUrl(String assessorFormInputResponseRestUrl) {
        this.assessorFormInputResponseRestUrl = assessorFormInputResponseRestUrl;
    }

    @Override
    public RestResult<List<AssessorFormInputResponseResource>> getAllAssessorFormInputResponses(Long assessmentId) {
        return getWithRestResult(format("%s/assessment/%s", assessorFormInputResponseRestUrl, assessmentId), ParameterizedTypeReferences.assessorFormInputResponseResourceListType());
    }

    @Override
    public RestResult<List<AssessorFormInputResponseResource>> getAllAssessorFormInputResponsesByAssessmentAndQuestion(Long assessmentId, Long questionId) {
        return getWithRestResult(format("%s/assessment/%s/question/%s", assessorFormInputResponseRestUrl, assessmentId, questionId), ParameterizedTypeReferences.assessorFormInputResponseResourceListType());
    }

    @Override
    public RestResult<Void> updateFormInputResponse(AssessorFormInputResponseResource response) {
        return putWithRestResult(format("%s", assessorFormInputResponseRestUrl), response, Void.class);
    }
}
