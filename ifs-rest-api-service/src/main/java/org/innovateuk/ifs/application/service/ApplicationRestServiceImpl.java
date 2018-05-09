package org.innovateuk.ifs.application.service;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.application.resource.ApplicationIneligibleSendResource;
import org.innovateuk.ifs.application.resource.ApplicationPageResource;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.resource.IneligibleOutcomeResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.commons.service.ParameterizedTypeReferences;
import org.innovateuk.ifs.user.resource.Role;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.Future;

import static java.util.Collections.singletonList;

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
    public RestResult<ApplicationPageResource> wildcardSearchById(String searchString, int pageNumber, int pageSize) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        if (StringUtils.isNotBlank(searchString)) {
            params.put("searchString", singletonList(searchString));
        }

        String uriWithParams = buildPaginationUri(applicationRestURL + "/wildcardSearchById", pageNumber, pageSize, null, params);
        return getWithRestResult(uriWithParams, ApplicationPageResource.class);
    }

    @Override
    public RestResult<Void> saveApplication(ApplicationResource application) {
        return postWithRestResult(applicationRestURL + "/saveApplicationDetails/" + application.getId(), application, Void.class);
    }

    @Override
    public RestResult<Void> updateApplicationState(Long applicationId, ApplicationState state) {
        return putWithRestResult(applicationRestURL + "/updateApplicationState?applicationId=" + applicationId + "&state=" + state, Void.class);
    }

    @Override
    public Future<RestResult<Double>> getCompleteQuestionsPercentage(Long applicationId) {
        return getWithRestResultAsync(applicationRestURL + "/getProgressPercentageByApplicationId/" + applicationId, Double.class);
    }

    @Override
    public RestResult<Boolean> isApplicationReadyForSubmit(Long applicationId) {
        return getWithRestResult(applicationRestURL + "/applicationReadyForSubmit/" + applicationId, Boolean.class);
    }

    @Override
    public RestResult<List<ApplicationResource>> getApplicationsByCompetitionIdAndUserId(Long competitionID, Long userID, Role role) {
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

    @Override
    public RestResult<Void> markAsIneligible(long applicationId, IneligibleOutcomeResource reason) {
        return postWithRestResult(applicationRestURL + "/" + applicationId + "/ineligible", reason, Void.class);
    }

    @Override
    public RestResult<Void> informIneligible(long applicationId, ApplicationIneligibleSendResource applicationIneligibleSendResource) {
        return postWithRestResult(applicationRestURL + "/informIneligible/" + applicationId, applicationIneligibleSendResource, Void.class);
    }

    @Override
    public RestResult<Void> withdrawApplication(long applicationId) {
        return postWithRestResult(applicationRestURL + "/withdraw/" + applicationId, Void.class);
    }

    @Override
    public RestResult<Boolean> showApplicationTeam(Long applicationId, Long userId) {
        return getWithRestResult(applicationRestURL + "/showApplicationTeam/" + applicationId + "/" + userId, Boolean.class);
    }

    @Override
    public RestResult<ZonedDateTime> getLatestEmailFundingDate(Long applicationId) {
        return getWithRestResult(applicationRestURL + "/getLatestEmailFundingDate/" + applicationId, ZonedDateTime.class);
    }

    @Override
    public RestResult<ApplicationPageResource> findUnsuccessfulApplications(Long competitionId, int pageNumber, int pageSize, String sortField) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        String uriWithParams = buildPaginationUri(applicationRestURL +  "/" + competitionId + "/unsuccessful-applications", pageNumber, pageSize, sortField, params);
        return getWithRestResult(uriWithParams, ApplicationPageResource.class);
    }
}
