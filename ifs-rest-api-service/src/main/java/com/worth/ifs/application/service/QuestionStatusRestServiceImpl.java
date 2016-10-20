package com.worth.ifs.application.service;

import com.worth.ifs.application.resource.QuestionStatusResource;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.worth.ifs.commons.service.ParameterizedTypeReferences.questionStatusResourceListType;
import static com.worth.ifs.util.CollectionFunctions.simpleJoiner;

/**
 *
 */
@Service
public class QuestionStatusRestServiceImpl extends BaseRestService implements QuestionStatusRestService {

    private String questionStatusRestURL = "/questionStatus";

    @Override
    public RestResult<List<QuestionStatusResource>> findQuestionStatusesByQuestionAndApplicationId(final Long questionId, final Long applicationId) {
        return getWithRestResult(questionStatusRestURL + "/findByQuestionAndApplication/" + questionId + "/" + applicationId, ParameterizedTypeReferences.questionStatusResourceListType());
    }

    @Override
    public RestResult<List<QuestionStatusResource>> findByQuestionAndApplicationAndOrganisation(Long questionId, Long applicationId, Long organisationId) {
        return getWithRestResult(questionStatusRestURL + "/findByQuestionAndApplicationAndOrganisation/" + questionId + "/" + applicationId + "/" + organisationId, ParameterizedTypeReferences.questionStatusResourceListType());
    }

    @Override
    public RestResult<List<QuestionStatusResource>> findByApplicationAndOrganisation(Long applicationId, Long organisationId) {
        return getWithRestResult(questionStatusRestURL + "/findByApplicationAndOrganisation/" + applicationId + "/" + organisationId, ParameterizedTypeReferences.questionStatusResourceListType());
    }

    @Override
    public RestResult<QuestionStatusResource> findQuestionStatusById(Long id) {
        return getWithRestResult(questionStatusRestURL + "/" + id, QuestionStatusResource.class);
    }

    @Override
    public RestResult<List<QuestionStatusResource>> getQuestionStatusesByQuestionIdsAndApplicationIdAndOrganisationId(List<Long> questionIds, Long applicationId, Long organisationId) {
        return getWithRestResult(questionStatusRestURL + "/findByQuestionIdsAndApplicationIdAndOrganisationId/" + CollectionFunctions.simpleJoiner(questionIds, ",") + "/" + applicationId + "/" + organisationId, ParameterizedTypeReferences.questionStatusResourceListType());
    }

}
