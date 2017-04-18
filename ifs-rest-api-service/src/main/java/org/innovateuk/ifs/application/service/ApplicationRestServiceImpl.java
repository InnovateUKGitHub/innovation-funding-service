package org.innovateuk.ifs.application.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.commons.service.ParameterizedTypeReferences;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Future;

import static org.innovateuk.ifs.application.service.Futures.adapt;

/**
 * ApplicationRestServiceImpl is a utility for CRUD operations on {@link ApplicationResource}.
 * This class connects to the {ApplicationController}
 * through a REST call.
 */
@Service
public class ApplicationRestServiceImpl extends BaseRestService implements ApplicationRestService {

    private String applicationRestURL = "/application";

    private String processRoleRestURL = "/processrole";

    private String questionStatusRestURL = "/questionStatus";

    @Override
    public RestResult<ApplicationResource> getApplicationById(Long applicationId) {
        return getWithRestResult(applicationRestURL + "/" + applicationId, ApplicationResource.class);
    }

    @Override
    public RestResult<List<ApplicationResource>> getApplicationsByUserId(Long userId) {
        return getWithRestResult(applicationRestURL + "/findByUser/" + userId, ParameterizedTypeReferences.applicationResourceListType());
    }

    @Override
    public RestResult<Void> saveApplication(ApplicationResource application) {
        return postWithRestResult(applicationRestURL + "/saveApplicationDetails/" + application.getId(), application, Void.class);
    }

    @Override
    public RestResult<Void> updateApplicationState(Long applicationId, ApplicationState state) {
        return putWithRestResult(applicationRestURL + "/updateApplicationState?applicationId=" + applicationId + "&state=" + state, Void.class);
    }

    // TODO DW - INFUND-1555 - remove usage of ObjectNode if possible
    @Override
    public Future<RestResult<Double>> getCompleteQuestionsPercentage(Long applicationId) {
        Future<RestResult<ObjectNode>> result = getWithRestResultAsync(applicationRestURL + "/getProgressPercentageByApplicationId/" + applicationId, ObjectNode.class);
        return adapt(result, n -> n.andOnSuccessReturn(jsonResponse -> jsonResponse.get("completedPercentage").asDouble()));
    }

    // TODO DW - INFUND-1555 - remove usages of the ObjectNode from the data side - replace with a dto
    @Override
    public RestResult<Boolean> isApplicationReadyForSubmit(Long applicationId) {
        RestResult<ObjectNode> result = getWithRestResult(applicationRestURL + "/applicationReadyForSubmit/" + applicationId, ObjectNode.class);
        return result.andOnSuccessReturn(jsonResponse -> jsonResponse.get("readyForSubmit").asBoolean(false));
    }

    @Override
    public RestResult<List<ApplicationResource>> getApplicationsByCompetitionIdAndUserId(Long competitionID, Long userID, UserRoleType role) {
        return getWithRestResult(applicationRestURL + "/getApplicationsByCompetitionIdAndUserId/" + competitionID + "/" + userID + "/" + role, ParameterizedTypeReferences.applicationResourceListType());
    }

    @Override
    public RestResult<Integer> getAssignedQuestionsCount(Long applicationId, Long assigneeId) {
        RestResult<Integer> count = getWithRestResult(questionStatusRestURL + "/getAssignedQuestionsCountByApplicationIdAndAssigneeId/" + applicationId + "/" + assigneeId, Integer.class);
        return count.andOnSuccessReturn(Integer::valueOf);
    }

    @Override
    public RestResult<ApplicationResource> createApplication(Long competitionId, Long userId, String applicationName) {

        // TODO DW - INFUND-1555 - heavy way to send just a single string...
        ApplicationResource application = new ApplicationResource();
        application.setName(applicationName);
        String url = applicationRestURL + "/createApplicationByName/" + competitionId + "/" + userId;

        return postWithRestResult(url, application, ApplicationResource.class);
    }

    @Override
    public RestResult<ApplicationResource> findByProcessRoleId(Long id) {
        return getWithRestResult(processRoleRestURL + "/" + id + "/application", ApplicationResource.class);
    }
}
