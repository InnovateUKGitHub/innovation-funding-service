package org.innovateuk.ifs.supporter.controller;

import org.innovateuk.ifs.supporter.resource.*;
import org.innovateuk.ifs.supporter.transactional.SupporterAssignmentService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/supporter")
public class SupporterAssignmentController {
    private static final int DEFAULT_PAGE_SIZE = 20;

    @Autowired
    private SupporterAssignmentService supporterAssignmentService;

    @GetMapping("/assignment/user/{userId}/application/{applicationId}")
    public RestResult<SupporterAssignmentResource> getAssignment(@PathVariable long userId, @PathVariable long applicationId) {
        return supporterAssignmentService.getAssignment(userId, applicationId).toGetResponse();
    }

    @GetMapping("/assignment/application/{applicationId}")
    public RestResult<List<SupporterAssignmentResource>> getAssignmentsByApplicationId(@PathVariable long applicationId) {
        return supporterAssignmentService.getAssignmentsByApplicationId(applicationId).toGetResponse();
    }

    @PostMapping("/user/{userId}/application/{applicationId}")
    public RestResult<SupporterAssignmentResource> assign(@PathVariable long userId, @PathVariable long applicationId) {
        return supporterAssignmentService.assign(userId, applicationId).toGetResponse();
    }

    @DeleteMapping("/user/{userId}/application/{applicationId}")
    public RestResult<Void> removeAssignment(@PathVariable long userId, @PathVariable long applicationId) {
        return supporterAssignmentService.removeAssignment(userId, applicationId).toGetResponse();
    }

    @PostMapping("assignment/{assignmentId}/decision")
    public RestResult<Void> decision(@PathVariable long assignmentId, @RequestBody SupporterDecisionResource decision) {
        return supporterAssignmentService.decision(assignmentId, decision).toGetResponse();
    }

    @PostMapping("assignment/{assignmentId}/edit")
    public RestResult<Void> edit(@PathVariable long assignmentId) {
        return supporterAssignmentService.edit(assignmentId).toGetResponse();
    }

    @PostMapping("assignment")
    public RestResult<Void> assign(@RequestBody AssignSupportersResource assignSupportersResource) {
        return supporterAssignmentService.assign(assignSupportersResource.getSupporterIds(), assignSupportersResource.getApplicationId()).toPostResponse();
    }

    @GetMapping("/competition/{competitionId}")
    public RestResult<ApplicationsForCofundingPageResource> findApplicationsNeedingSupporters(@PathVariable long competitionId,
                                                                                             @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = {"id"}, direction = Sort.Direction.ASC) Pageable pageable,
                                                                                             @RequestParam(defaultValue = "") String filter) {
        return supporterAssignmentService.findApplicationsNeedingSupporters(competitionId, filter, pageable).toGetResponse();
    }

    @GetMapping("/application/{applicationId}")
    public RestResult<SupportersAvailableForApplicationPageResource> findAvailableSupportersForApplication(@PathVariable long applicationId,
                                                                                                         @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = {"id"}, direction = Sort.Direction.ASC) Pageable pageable,
                                                                                                         @RequestParam(defaultValue = "") String filter) {
        return supporterAssignmentService.findAvailableSupportersForApplication(applicationId, filter, pageable).toGetResponse();
    }

    @GetMapping("/application/{applicationId}/userIds")
    public RestResult<List<Long>> findAvailableSupportersUserIdsForApplication(@PathVariable long applicationId,
                                                                              @RequestParam(defaultValue = "") String filter) {
        return supporterAssignmentService.findAvailableSupportersUserIdsForApplication(applicationId, filter).toGetResponse();
    }
}
