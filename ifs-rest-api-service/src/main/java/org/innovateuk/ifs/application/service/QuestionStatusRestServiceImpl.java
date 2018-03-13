package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.commons.service.ParameterizedTypeReferences;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.application.service.Futures.adapt;

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

    @Override
    public RestResult<List<ValidationMessages>> markAsComplete(Long questionId, Long applicationId, Long markedAsCompleteById) {
        return putWithRestResult(questionStatusRestURL + "/markAsComplete/" + questionId + "/" + applicationId + "/" + markedAsCompleteById, new ParameterizedTypeReference<List<ValidationMessages>>() {
        });
    }

    @Override
    public RestResult<Void> markAsInComplete(Long questionId, Long applicationId, Long markedAsInCompleteById) {
        return putWithRestResult(questionStatusRestURL + "/markAsInComplete/" + questionId + "/" + applicationId + "/" + markedAsInCompleteById, Void.class);
    }

    @Override
    public RestResult<Void> assign(Long questionId, Long applicationId, Long assigneeId, Long assignedById) {
        return putWithRestResult(questionStatusRestURL + "/assign/" + questionId + "/" + applicationId + "/" + assigneeId + "/" + assignedById, Void.class);
    }

    @Override
    public RestResult<Void> updateNotification(Long questionStatusId, Boolean notify) {
        return putWithRestResult(questionStatusRestURL + "/updateNotification/" + questionStatusId + "/" + notify, Void.class);
    }

    @Override
    public Future<Set<Long>> getMarkedAsComplete(Long applicationId, Long organisationId) {
        return adapt(restGetAsync(questionStatusRestURL + "/getMarkedAsComplete/" + applicationId + "/" + organisationId, Long[].class), re -> new HashSet<>(asList(re.getBody())));
    }



}
