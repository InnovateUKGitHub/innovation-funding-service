package org.innovateuk.ifs.application.service;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.application.resource.*;
import org.innovateuk.ifs.commons.error.ValidationMessages;
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

import static java.lang.String.format;
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

    private String questionStatusRestURL = "/question-status";

    @Override
    public RestResult<ApplicationResource> getApplicationById(Long applicationId) {
        return getWithRestResult(applicationRestURL + "/" + applicationId, ApplicationResource.class);
    }

    @Override
    public RestResult<List<ApplicationResource>> getApplicationsByUserId(Long userId) {
        return getWithRestResult(applicationRestURL + "/find-by-user/" + userId, ParameterizedTypeReferences.applicationResourceListType());
    }

    @Override
    public RestResult<ApplicationPageResource> wildcardSearchById(String searchString, int pageNumber, int pageSize) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        if (StringUtils.isNotBlank(searchString)) {
            params.put("searchString", singletonList(searchString));
        }

        String uriWithParams = buildPaginationUri(applicationRestURL + "/wildcard-search-by-id", pageNumber, pageSize, null, params);
        return getWithRestResult(uriWithParams, ApplicationPageResource.class);
    }

    @Override
    public RestResult<ValidationMessages> saveApplication(ApplicationResource application) {
        return postWithRestResult(applicationRestURL + "/save-application-details/" + application.getId(), application, ValidationMessages.class);
    }

    @Override
    public RestResult<Void> updateApplicationState(long applicationId, ApplicationState state) {
        return putWithRestResult(applicationRestURL + "/update-application-state?applicationId=" + applicationId + "&state=" + state, Void.class);
    }

    @Override
    public Future<RestResult<Double>> getCompleteQuestionsPercentage(Long applicationId) {
        return getWithRestResultAsync(applicationRestURL + "/get-progress-percentage-by-application-id/" + applicationId, Double.class);
    }

    @Override
    public RestResult<Boolean> isApplicationReadyForSubmit(Long applicationId) {
        return getWithRestResult(applicationRestURL + "/application-ready-for-submit/" + applicationId, Boolean.class);
    }

    @Override
    public RestResult<List<ApplicationResource>> getApplicationsByCompetitionIdAndUserId(Long competitionID, Long userID, Role role) {
        return getWithRestResult(applicationRestURL + "/get-applications-by-competition-id-and-user-id/" + competitionID + "/" + userID + "/" + role, ParameterizedTypeReferences.applicationResourceListType());
    }

    @Override
    public RestResult<Integer> getAssignedQuestionsCount(Long applicationId, Long assigneeId) {
        RestResult<Integer> count = getWithRestResult(questionStatusRestURL + "/get-assigned-questions-count-by-application-id-and-assignee-id/" + applicationId + "/" + assigneeId, Integer.class);
        return count.andOnSuccessReturn(Integer::valueOf);
    }

    @Override
    public RestResult<ApplicationResource> createApplication(long competitionId, long userId, long organisationId, String applicationName) {

        ApplicationResource application = new ApplicationResource();
        application.setName(applicationName);
        String url = format(applicationRestURL + "/create-application-by-name/%d/%d/%d", competitionId, userId, organisationId);

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
        return postWithRestResult(applicationRestURL + "/inform-ineligible/" + applicationId, applicationIneligibleSendResource, Void.class);
    }

    @Override
    public RestResult<Boolean> showApplicationTeam(Long applicationId, Long userId) {
        return getWithRestResult(applicationRestURL + "/show-application-team/" + applicationId + "/" + userId, Boolean.class);
    }

    @Override
    public RestResult<ZonedDateTime> getLatestEmailFundingDate(Long applicationId) {
        return getWithRestResult(applicationRestURL + "/get-latest-email-funding-date/" + applicationId, ZonedDateTime.class);
    }

    @Override
    public RestResult<Void> hideApplication(long applicationId, long userId) {
        return postWithRestResult(format("%s/%d/hide-for-user/%d", applicationRestURL, applicationId, userId), Void.class);
    }

    @Override
    public RestResult<Void> deleteApplication(long applicationId) {
        return deleteWithRestResult(format("%s/%d", applicationRestURL, applicationId), Void.class);
    }

    @Override
    public RestResult<Void> reopenApplication(long applicationId) {
        return putWithRestResult(format("%s/%d/%s", applicationRestURL, applicationId, "reopen-application"), Void.class);
    }

}
