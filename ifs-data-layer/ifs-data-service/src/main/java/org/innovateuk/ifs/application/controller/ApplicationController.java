package org.innovateuk.ifs.application.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.innovateuk.ifs.application.mapper.IneligibleOutcomeMapper;
import org.innovateuk.ifs.application.resource.*;
import org.innovateuk.ifs.application.transactional.ApplicationNotificationService;
import org.innovateuk.ifs.application.transactional.ApplicationProgressService;
import org.innovateuk.ifs.application.transactional.ApplicationService;
import org.innovateuk.ifs.commons.ZeroDowntime;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.crm.transactional.CrmService;
import org.innovateuk.ifs.user.resource.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * ApplicationController exposes Application data and operations through a REST API.
 */
@RestController
@RequestMapping("/application")
public class ApplicationController {

    private static final String DEFAULT_PAGE_NUMBER = "0";

    private static final String DEFAULT_PAGE_SIZE = "40";

    private static final String DEFAULT_SORT_BY = "id";

    private static final String PREVIOUS_APP_DEFAULT_FILTER = "ALL";

    private IneligibleOutcomeMapper ineligibleOutcomeMapper;

    private ApplicationService applicationService;

    private ApplicationNotificationService applicationNotificationService;

    private ApplicationProgressService applicationProgressService;

    private CrmService crmService;

    public ApplicationController() {}

    @Autowired
    public ApplicationController(IneligibleOutcomeMapper ineligibleOutcomeMapper, ApplicationService applicationService, ApplicationNotificationService applicationNotificationService, ApplicationProgressService applicationProgressService, CrmService crmService) {
        this.ineligibleOutcomeMapper = ineligibleOutcomeMapper;
        this.applicationService = applicationService;
        this.applicationNotificationService = applicationNotificationService;
        this.applicationProgressService = applicationProgressService;
        this.crmService = crmService;
    }

    @GetMapping("/{id}")
    public RestResult<ApplicationResource> getApplicationById(@PathVariable("id") final Long id) {
        return applicationService.getApplicationById(id).toGetResponse();
    }

    @GetMapping("/")
    public RestResult<List<ApplicationResource>> findAll() {
        return applicationService.findAll().toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "delete in h2020 sprint 6")
    @GetMapping("/findByUser/{userId}")
    public RestResult<List<ApplicationResource>> findByUserIdOld(@PathVariable("userId") final Long userId) {
        return applicationService.findByUserId(userId).toGetResponse();
    }

    @GetMapping("/find-by-user/{userId}")
    public RestResult<List<ApplicationResource>> findByUserId(@PathVariable("userId") final Long userId) {
        return applicationService.findByUserId(userId).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "delete in h2020 sprint 6")
    @GetMapping("/wildcardSearchById")
    public RestResult<ApplicationPageResource> wildcardSearchByIdOld(@RequestParam(value = "searchString", defaultValue = "") String searchString,
                                                                  @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER) int pageIndex,
                                                                  @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        return applicationService.wildcardSearchById(searchString, new PageRequest(pageIndex, pageSize)).toGetResponse();
    }

    @GetMapping("/wildcard-search-by-id")
    public RestResult<ApplicationPageResource> wildcardSearchById(@RequestParam(value = "searchString", defaultValue = "") String searchString,
                                                                  @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER) int pageIndex,
                                                                  @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        return applicationService.wildcardSearchById(searchString, new PageRequest(pageIndex, pageSize)).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "delete in h2020 sprint 6")
    @PostMapping("/saveApplicationDetails/{id}")
    public RestResult<Void> saveApplicationDetailsOld(@PathVariable("id") final Long id,
                                                   @RequestBody ApplicationResource application) {

        return applicationService.saveApplicationDetails(id, application).toPostResponse();
    }

    @PostMapping("/save-application-details/{id}")
    public RestResult<Void> saveApplicationDetails(@PathVariable("id") final Long id,
                                                   @RequestBody ApplicationResource application) {

        return applicationService.saveApplicationDetails(id, application).toPostResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "delete in h2020 sprint 6")
    @GetMapping("/getProgressPercentageByApplicationId/{applicationId}")
    public RestResult<CompletedPercentageResource> getProgressPercentageByApplicationIdOld(@PathVariable("applicationId") final Long applicationId) {
        return applicationService.getProgressPercentageByApplicationId(applicationId).toGetResponse();
    }

    @GetMapping("/get-progress-percentage-by-application-id/{applicationId}")
    public RestResult<CompletedPercentageResource> getProgressPercentageByApplicationId(@PathVariable("applicationId") final Long applicationId) {
        return applicationService.getProgressPercentageByApplicationId(applicationId).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "delete in h2020 sprint 6")
    @PutMapping("/updateApplicationState")
    public RestResult<Void> updateApplicationStateOld(@RequestParam("applicationId") final Long id,
                                                   @RequestParam("state") final ApplicationState state) {

        ServiceResult<ApplicationResource> updateStatusResult = applicationService.updateApplicationState(id, state);

        if (updateStatusResult.isSuccess() && ApplicationState.SUBMITTED == state) {
            applicationService.saveApplicationSubmitDateTime(id, ZonedDateTime.now());
            applicationNotificationService.sendNotificationApplicationSubmitted(id);
        }

        return updateStatusResult.toPutResponse();
    }


    @PutMapping("/update-application-state")
    public RestResult<Void> updateApplicationState(@RequestParam("applicationId") final Long id,
                                                   @RequestParam("state") final ApplicationState state) {

        ServiceResult<ApplicationResource> updateStatusResult = applicationService.updateApplicationState(id, state);

        if (updateStatusResult.isSuccess() && ApplicationState.SUBMITTED == state) {
            applicationService.saveApplicationSubmitDateTime(id, ZonedDateTime.now());
            applicationNotificationService.sendNotificationApplicationSubmitted(id);
        }

        return updateStatusResult.toPutResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "delete in h2020 sprint 6")
    @GetMapping("/applicationReadyForSubmit/{applicationId}")
    public RestResult<Boolean> applicationReadyForSubmitOld(@PathVariable("applicationId") final Long applicationId) {
        return RestResult.toGetResponse(applicationProgressService.applicationReadyForSubmit(applicationId));
    }

    @GetMapping("/application-ready-for-submit/{applicationId}")
    public RestResult<Boolean> applicationReadyForSubmit(@PathVariable("applicationId") final Long applicationId) {
        return RestResult.toGetResponse(applicationProgressService.applicationReadyForSubmit(applicationId));
    }

    @ZeroDowntime(reference = "IFS-430", description = "delete in h2020 sprint 6")
    @GetMapping("/getApplicationsByCompetitionIdAndUserId/{competitionId}/{userId}/{role}")
    public RestResult<List<ApplicationResource>> getApplicationsByCompetitionIdAndUserIdOld(@PathVariable("competitionId") final Long competitionId,
                                                                                         @PathVariable("userId") final Long userId,
                                                                                         @PathVariable("role") final Role role) {

        return applicationService.getApplicationsByCompetitionIdAndUserId(competitionId, userId, role).toGetResponse();
    }

    @GetMapping("/get-applications-by-competition-id-and-user-id/{competitionId}/{userId}/{role}")
    public RestResult<List<ApplicationResource>> getApplicationsByCompetitionIdAndUserId(@PathVariable("competitionId") final Long competitionId,
                                                                                         @PathVariable("userId") final Long userId,
                                                                                         @PathVariable("role") final Role role) {

        return applicationService.getApplicationsByCompetitionIdAndUserId(competitionId, userId, role).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "delete in h2020 sprint 6")
    @PostMapping("/createApplicationByName/{competitionId}/{userId}/{organisationId}")
    public RestResult<ApplicationResource> createApplicationByApplicationNameForUserIdAndCompetitionIdOld(
            @PathVariable("competitionId") final long competitionId,
            @PathVariable("userId") final long userId,
            @PathVariable("organisationId") final long organisationId,
            @RequestBody JsonNode jsonObj) {

        String name = jsonObj.get("name").textValue();
        return applicationService.createApplicationByApplicationNameForUserIdAndCompetitionId(name, competitionId, userId, organisationId)
                .andOnSuccessReturn(result -> {
                    crmService.syncCrmContact(userId);
                    return result;
                })
                .toPostCreateResponse();
    }

    @PostMapping("/create-application-by-name/{competitionId}/{userId}/{organisationId}")
    public RestResult<ApplicationResource> createApplicationByApplicationNameForUserIdAndCompetitionId(
            @PathVariable("competitionId") final long competitionId,
            @PathVariable("userId") final long userId,
            @PathVariable("organisationId") final long organisationId,
            @RequestBody JsonNode jsonObj) {

        String name = jsonObj.get("name").textValue();
        return applicationService.createApplicationByApplicationNameForUserIdAndCompetitionId(name, competitionId, userId, organisationId)
                .andOnSuccessReturn(result -> {
                    crmService.syncCrmContact(userId);
                    return result;
                })
                .toPostCreateResponse();
    }

    @PostMapping("/{applicationId}/ineligible")
    public RestResult<Void> markAsIneligible(@PathVariable("applicationId") long applicationId,
                                             @RequestBody IneligibleOutcomeResource reason) {
        return applicationService
                .markAsIneligible(applicationId, ineligibleOutcomeMapper.mapToDomain(reason))
                .toPostWithBodyResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "delete in h2020 sprint 6")
    @PostMapping("/informIneligible/{applicationId}")
    public RestResult<Void> informIneligibleOld(@PathVariable("applicationId") final long applicationId,
                                             @RequestBody ApplicationIneligibleSendResource applicationIneligibleSendResource) {
        return applicationNotificationService.informIneligible(applicationId, applicationIneligibleSendResource).toPostResponse();
    }

    @PostMapping("/inform-ineligible/{applicationId}")
    public RestResult<Void> informIneligible(@PathVariable("applicationId") final long applicationId,
                                             @RequestBody ApplicationIneligibleSendResource applicationIneligibleSendResource) {
        return applicationNotificationService.informIneligible(applicationId, applicationIneligibleSendResource).toPostResponse();
    }

    @PostMapping("/{applicationId}/withdraw")
    public RestResult<Void> withdrawApplication(@PathVariable("applicationId") final long applicationId) {
        return applicationService.withdrawApplication(applicationId).toPostResponse();
    }

    // IFS-43 added to ease future expansion as application team members are expected to have access to the application team page, but the location of links to that page (enabled by tis method) is as yet unknown
    @ZeroDowntime(reference = "IFS-430", description = "delete in h2020 sprint 6")
    @GetMapping("/showApplicationTeam/{applicationId}/{userId}")
    public RestResult<Boolean> showApplicationTeamOld(@PathVariable("applicationId") final Long applicationId,
                                                   @PathVariable("userId") final Long userId) {
        return applicationService.showApplicationTeam(applicationId, userId).toGetResponse();
    }

    @GetMapping("/show-application-team/{applicationId}/{userId}")
    public RestResult<Boolean> showApplicationTeam(@PathVariable("applicationId") final Long applicationId,
                                                   @PathVariable("userId") final Long userId) {
        return applicationService.showApplicationTeam(applicationId, userId).toGetResponse();
    }

    @GetMapping("/{competitionId}/previous-applications")
    public RestResult<PreviousApplicationPageResource> findPreviousApplications(@PathVariable("competitionId") final Long competitionId,
                                                                                @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER) int pageIndex,
                                                                                @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE) int pageSize,
                                                                                @RequestParam(value = "sort", defaultValue = DEFAULT_SORT_BY) String sortField,
                                                                                @RequestParam(value = "filter", defaultValue = PREVIOUS_APP_DEFAULT_FILTER) String filter) {
        return applicationService.findPreviousApplications(competitionId, pageIndex, pageSize, sortField, filter).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "delete in h2020 sprint 6")
    @GetMapping("/getLatestEmailFundingDate/{competitionId}")
    public RestResult<ZonedDateTime> getLatestEmailFundingDateOld(@PathVariable("competitionId") final Long competitionId) {
        return applicationService.findLatestEmailFundingDateByCompetitionId(competitionId).toGetResponse();
    }

    @GetMapping("/get-latest-email-funding-date/{competitionId}")
    public RestResult<ZonedDateTime> getLatestEmailFundingDate(@PathVariable("competitionId") final Long competitionId) {
        return applicationService.findLatestEmailFundingDateByCompetitionId(competitionId).toGetResponse();
    }
}
