package org.innovateuk.ifs.application.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.innovateuk.ifs.application.mapper.IneligibleOutcomeMapper;
import org.innovateuk.ifs.application.resource.*;
import org.innovateuk.ifs.application.transactional.ApplicationDeletionService;
import org.innovateuk.ifs.application.transactional.ApplicationNotificationService;
import org.innovateuk.ifs.application.transactional.ApplicationProgressService;
import org.innovateuk.ifs.application.transactional.ApplicationService;
import org.innovateuk.ifs.commons.error.ValidationMessages;
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

    @Autowired
    private IneligibleOutcomeMapper ineligibleOutcomeMapper;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ApplicationNotificationService applicationNotificationService;

    @Autowired
    private ApplicationProgressService applicationProgressService;

    @Autowired
    private CrmService crmService;

    @Autowired
    private ApplicationDeletionService applicationDeletionService;

    @GetMapping("/{id}")
    public RestResult<ApplicationResource> getApplicationById(@PathVariable("id") final Long id) {
        return applicationService.getApplicationById(id).toGetResponse();
    }

    @DeleteMapping("/{id}")
    public RestResult<Void> delete(@PathVariable final long id) {
        return applicationDeletionService.deleteApplication(id).toDeleteResponse();
    }

    @PostMapping("/{id}/hide-for-user/{userId}")
    public RestResult<Void> delete(@PathVariable final long id, @PathVariable final long userId) {
        return applicationDeletionService.hideApplicationFromDashboard(ApplicationUserCompositeId.id(id, userId)).toDeleteResponse();
    }

    @GetMapping("/")
    public RestResult<List<ApplicationResource>> findAll() {
        return applicationService.findAll().toGetResponse();
    }

    @GetMapping("/find-by-user/{userId}")
    public RestResult<List<ApplicationResource>> findByUserId(@PathVariable("userId") final Long userId) {
        return applicationService.findByUserId(userId).toGetResponse();
    }

    @GetMapping("/wildcard-search-by-id")
    public RestResult<ApplicationPageResource> wildcardSearchById(@RequestParam(value = "searchString", defaultValue = "") String searchString,
                                                                  @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER) int pageIndex,
                                                                  @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        return applicationService.wildcardSearchById(searchString, PageRequest.of(pageIndex, pageSize)).toGetResponse();
    }

    @PostMapping("/save-application-details/{id}")
    public RestResult<ValidationMessages> saveApplicationDetails(@PathVariable("id") final Long id,
                                                                       @RequestBody ApplicationResource application) {

        return applicationService.saveApplicationDetails(id, application).toPostWithBodyResponse();
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

    @PutMapping("/{applicationId}/reopen-application")
    public RestResult<Void> reopenApplication(@PathVariable("applicationId") final Long applicationId) {
        return applicationService.reopenApplication(applicationId).toPutResponse();
    }

    @GetMapping("/application-ready-for-submit/{applicationId}")
    public RestResult<Boolean> applicationReadyForSubmit(@PathVariable("applicationId") final Long applicationId) {
        return RestResult.toGetResponse(applicationProgressService.applicationReadyForSubmit(applicationId));
    }

    @GetMapping("/get-applications-by-competition-id-and-user-id/{competitionId}/{userId}/{role}")
    public RestResult<List<ApplicationResource>> getApplicationsByCompetitionIdAndUserId(@PathVariable("competitionId") final Long competitionId,
                                                                                         @PathVariable("userId") final Long userId,
                                                                                         @PathVariable("role") final Role role) {

        return applicationService.getApplicationsByCompetitionIdAndUserId(competitionId, userId, role).toGetResponse();
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

    @PostMapping("/inform-ineligible/{applicationId}")
    public RestResult<Void> informIneligible(@PathVariable("applicationId") final long applicationId,
                                             @RequestBody ApplicationIneligibleSendResource applicationIneligibleSendResource) {
        return applicationNotificationService.informIneligible(applicationId, applicationIneligibleSendResource).toPostResponse();
    }

    // IFS-43 added to ease future expansion as application team members are expected to have access to the application team page, but the location of links to that page (enabled by tis method) is as yet unknown
    @GetMapping("/show-application-team/{applicationId}/{userId}")
    public RestResult<Boolean> showApplicationTeam(@PathVariable("applicationId") final Long applicationId,
                                                   @PathVariable("userId") final Long userId) {
        return applicationService.showApplicationTeam(applicationId, userId).toGetResponse();
    }

    @GetMapping("/get-latest-email-funding-date/{competitionId}")
    public RestResult<ZonedDateTime> getLatestEmailFundingDate(@PathVariable("competitionId") final Long competitionId) {
        return applicationService.findLatestEmailFundingDateByCompetitionId(competitionId).toGetResponse();
    }
}
