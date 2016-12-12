package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.commons.service.ParameterizedTypeReferences;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.questionStatusResourceListType;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleJoiner;

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
