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
    public RestResult<List<QuestionStatusResource>> findQuestionStatusesByQuestionAndApplicationId(final long questionId, final long applicationId) {
        return getWithRestResult(questionStatusRestURL + "/findByQuestionAndApplication/" + questionId + "/" + applicationId, ParameterizedTypeReferences.questionStatusResourceListType());
    }

    @Override
    public RestResult<List<QuestionStatusResource>> findByQuestionAndApplicationAndOrganisation(long questionId, long applicationId, long organisationId) {
        return getWithRestResult(questionStatusRestURL + "/findByQuestionAndApplicationAndOrganisation/" + questionId + "/" + applicationId + "/" + organisationId, ParameterizedTypeReferences.questionStatusResourceListType());
    }

    @Override
    public RestResult<List<QuestionStatusResource>> findByApplicationAndOrganisation(long applicationId, long organisationId) {
        return getWithRestResult(questionStatusRestURL + "/findByApplicationAndOrganisation/" + applicationId + "/" + organisationId, ParameterizedTypeReferences.questionStatusResourceListType());
    }

    @Override
    public RestResult<QuestionStatusResource> findQuestionStatusById(long id) {
        return getWithRestResult(questionStatusRestURL + "/" + id, QuestionStatusResource.class);
    }

    @Override
    public RestResult<List<QuestionStatusResource>> getQuestionStatusesByQuestionIdsAndApplicationIdAndOrganisationId(List<Long> questionIds, long applicationId, long organisationId) {
        return getWithRestResult(questionStatusRestURL + "/findByQuestionIdsAndApplicationIdAndOrganisationId/" + CollectionFunctions.simpleJoiner(questionIds, ",") + "/" + applicationId + "/" + organisationId, ParameterizedTypeReferences.questionStatusResourceListType());
    }

    @Override
    public RestResult<List<ValidationMessages>> markAsComplete(long questionId, long applicationId, long markedAsCompleteById) {
        return putWithRestResult(questionStatusRestURL + "/mark-as-complete/" + questionId + "/" + applicationId + "/" + markedAsCompleteById, new ParameterizedTypeReference<List<ValidationMessages>>() {
        });
    }

    @Override
    public RestResult<Void> markAsInComplete(long questionId, long applicationId, long markedAsInCompleteById) {
        return putWithRestResult(questionStatusRestURL + "/mark-as-in-complete/" + questionId + "/" + applicationId + "/" + markedAsInCompleteById, Void.class);
    }

    @Override
    public RestResult<Void> assign(long questionId, long applicationId, long assigneeId, long assignedById) {
        return putWithRestResult(questionStatusRestURL + "/assign/" + questionId + "/" + applicationId + "/" + assigneeId + "/" + assignedById, Void.class);
    }

    @Override
    public RestResult<Void> updateNotification(long questionStatusId, boolean notify) {
        return putWithRestResult(questionStatusRestURL + "/update-notification/" + questionStatusId + "/" + notify, Void.class);
    }

    @Override
    public Future<Set<Long>> getMarkedAsComplete(long applicationId, long organisationId) {
        return adapt(restGetAsync(questionStatusRestURL + "/get-marked-as-complete/" + applicationId + "/" + organisationId, Long[].class), re -> new HashSet<>(asList(re.getBody())));
    }



}
