package org.innovateuk.ifs.application.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.innovateuk.ifs.application.mapper.IneligibleOutcomeMapper;
import org.innovateuk.ifs.application.resource.*;
import org.innovateuk.ifs.application.transactional.ApplicationNotificationService;
import org.innovateuk.ifs.application.transactional.ApplicationProgressService;
import org.innovateuk.ifs.application.transactional.ApplicationService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ServiceResult;
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

    @Autowired
    private IneligibleOutcomeMapper ineligibleOutcomeMapper;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ApplicationNotificationService applicationNotificationService;

    @Autowired
    private ApplicationProgressService applicationProgressService;

    @GetMapping("/{id}")
    public RestResult<ApplicationResource> getApplicationById(@PathVariable("id") final Long id) {
        return applicationService.getApplicationById(id).toGetResponse();
    }

    @GetMapping("/")
    public RestResult<List<ApplicationResource>> findAll() {
        return applicationService.findAll().toGetResponse();
    }

    @GetMapping("/findByUser/{userId}")
    public RestResult<List<ApplicationResource>> findByUserId(@PathVariable("userId") final Long userId) {
        return applicationService.findByUserId(userId).toGetResponse();
    }

    @GetMapping("/wildcardSearchById")
    public RestResult<ApplicationPageResource> wildcardSearchById(@RequestParam(value = "searchString", defaultValue = "") String searchString,
                                                                  @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER) int pageIndex,
                                                                  @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        return applicationService.wildcardSearchById(searchString, new PageRequest(pageIndex, pageSize)).toGetResponse();
    }

    @PostMapping("/saveApplicationDetails/{id}")
    public RestResult<Void> saveApplicationDetails(@PathVariable("id") final Long id,
                                                   @RequestBody ApplicationResource application) {

        return applicationService.saveApplicationDetails(id, application).toPostResponse();
    }

    @GetMapping("/getProgressPercentageByApplicationId/{applicationId}")
    public RestResult<CompletedPercentageResource> getProgressPercentageByApplicationId(@PathVariable("applicationId") final Long applicationId) {
        return applicationService.getProgressPercentageByApplicationId(applicationId).toGetResponse();
    }

    @PutMapping("/updateApplicationState")
    public RestResult<Void> updateApplicationState(@RequestParam("applicationId") final Long id,
                                                   @RequestParam("state") final ApplicationState state) {
        ServiceResult<ApplicationResource> updateStatusResult = applicationService.updateApplicationState(id, state);

        if (updateStatusResult.isSuccess() && ApplicationState.SUBMITTED == state) {
            applicationService.saveApplicationSubmitDateTime(id, ZonedDateTime.now());
            applicationNotificationService.sendNotificationApplicationSubmitted(id);
        }

        return updateStatusResult.toPutResponse();
    }

    @GetMapping("/applicationReadyForSubmit/{applicationId}")
    public RestResult<Boolean> applicationReadyForSubmit(@PathVariable("applicationId") final Long applicationId) {
        return RestResult.toGetResponse(applicationProgressService.applicationReadyForSubmit(applicationId));
    }

    @GetMapping("/getApplicationsByCompetitionIdAndUserId/{competitionId}/{userId}/{role}")
    public RestResult<List<ApplicationResource>> getApplicationsByCompetitionIdAndUserId(@PathVariable("competitionId") final Long competitionId,
                                                                                         @PathVariable("userId") final Long userId,
                                                                                         @PathVariable("role") final Role role) {

        return applicationService.getApplicationsByCompetitionIdAndUserId(competitionId, userId, role).toGetResponse();
    }

    @PostMapping("/createApplicationByName/{competitionId}/{userId}")
    public RestResult<ApplicationResource> createApplicationByApplicationNameForUserIdAndCompetitionId(
            @PathVariable("competitionId") final Long competitionId,
            @PathVariable("userId") final Long userId,
            @RequestBody JsonNode jsonObj) {

        String name = jsonObj.get("name").textValue();
        ServiceResult<ApplicationResource> applicationResult =
                applicationService.createApplicationByApplicationNameForUserIdAndCompetitionId(name, competitionId, userId);
        return applicationResult.toPostCreateResponse();
    }

    @PostMapping("/{applicationId}/ineligible")
    public RestResult<Void> markAsIneligible(@PathVariable("applicationId") long applicationId,
                                             @RequestBody IneligibleOutcomeResource reason) {
        return applicationService
                .markAsIneligible(applicationId, ineligibleOutcomeMapper.mapToDomain(reason))
                .toPostWithBodyResponse();
    }

    @PostMapping("/informIneligible/{applicationId}")
    public RestResult<Void> informIneligible(@PathVariable("applicationId") final long applicationId,
                                             @RequestBody ApplicationIneligibleSendResource applicationIneligibleSendResource) {
        return applicationNotificationService.informIneligible(applicationId, applicationIneligibleSendResource).toPostResponse();
    }

    @PostMapping("/withdraw/{applicationId}")
    public RestResult<Void> withdrawApplication(@PathVariable("applicationId") final long applicationId) {
        return applicationService.withdrawApplication(applicationId).toPostResponse();
    }


    // IFS-43 added to ease future expansion as application team members are expected to have access to the application team page, but the location of links to that page (enabled by tis method) is as yet unknown
    @GetMapping("/showApplicationTeam/{applicationId}/{userId}")
    public RestResult<Boolean> showApplicationTeam(@PathVariable("applicationId") final Long applicationId,
                                                   @PathVariable("userId") final Long userId) {
        return applicationService.showApplicationTeam(applicationId, userId).toGetResponse();
    }

    @GetMapping("/{competitionId}/unsuccessful-applications")
    public RestResult<ApplicationPageResource> findUnsuccessfulApplications(@PathVariable("competitionId") final Long competitionId,
                                                                            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER) int pageIndex,
                                                                            @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE) int pageSize,
                                                                            @RequestParam(value = "sort", defaultValue = DEFAULT_SORT_BY) String sortField) {
        return applicationService.findUnsuccessfulApplications(competitionId, pageIndex, pageSize, sortField).toGetResponse();
    }

    @GetMapping("/getLatestEmailFundingDate/{competitionId}")
    public RestResult<ZonedDateTime> getLatestEmailFundingDate(@PathVariable("competitionId") final Long competitionId) {
        return applicationService.findLatestEmailFundingDateByCompetitionId(competitionId).toGetResponse();
    }
}
