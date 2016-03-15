package com.worth.ifs.application.service;

import com.worth.ifs.application.domain.QuestionStatus;
import com.worth.ifs.application.resource.QuestionStatusResource;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.worth.ifs.commons.service.ParameterizedTypeReferences.questionStatusListType;
import static com.worth.ifs.commons.service.ParameterizedTypeReferences.questionStatusResourceListType;
import static com.worth.ifs.util.CollectionFunctions.simpleJoiner;

/**
 *
 */
@Service
public class QuestionStatusRestServiceImpl extends BaseRestService implements QuestionStatusRestService {

    @Value("${ifs.data.service.rest.questionStatus}")
    String questionStatusRestURL;

    @Override
    public RestResult<List<QuestionStatus>> findQuestionStatusesByQuestionAndApplicationId(final Long questionId, final Long applicationId) {
        return getWithRestResult(questionStatusRestURL + "/findByQuestionAndApplication/" + questionId + "/" + applicationId, questionStatusListType());
    }

    @Override
    public RestResult<List<QuestionStatus>> findByQuestionAndApplicationAndOrganisation(Long questionId, Long applicationId, Long organisationId) {
        return getWithRestResult(questionStatusRestURL + "/findByQuestionAndApplicationAndOrganisation/" + questionId + "/" + applicationId + "/" + organisationId, questionStatusListType());
    }

    public RestResult<List<QuestionStatusResource>> findByApplicationAndOrganisation(Long applicationId, Long organisationId) {
        return getWithRestResult(questionStatusRestURL + "/findByApplicationAndOrganisation/" + applicationId + "/" + organisationId, questionStatusResourceListType());
    }

    @Override
    public RestResult<QuestionStatus> findQuestionStatusById(Long id) {
        return getWithRestResult(questionStatusRestURL + "/" + id, QuestionStatus.class);
    }

    @Override
    public RestResult<List<QuestionStatusResource>> getByQuestionIdAndApplicationIdAndOrganisationId(Long questionId, Long applicationId, Long organisationId){
        return getWithRestResult(questionStatusRestURL + "/findByQuestionAndApplicationAndOrganisation/" + questionId + "/" + applicationId + "/" + organisationId, questionStatusResourceListType());
    }

    @Override
    public RestResult<List<QuestionStatusResource>> getQuestionStatusesByQuestionIdsAndApplicationIdAndOrganisationId(List<Long> questionIds, Long applicationId, Long organisationId) {
        return getWithRestResult(questionStatusRestURL + "/findByQuestionIdsAndApplicationIdAndOrganisationId/" + simpleJoiner(questionIds, ",") + "/" + applicationId + "/" + organisationId, questionStatusResourceListType());
    }

    public RestResult<List<QuestionStatus>> getByIds(List<Long> ids){
        return getWithRestResult(questionStatusRestURL + "getByIds", questionStatusListType());
    }
}
