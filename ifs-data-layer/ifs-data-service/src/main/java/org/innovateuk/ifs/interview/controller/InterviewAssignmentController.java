package org.innovateuk.ifs.interview.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.interview.resource.InterviewAssignmentKeyStatisticsResource;
import org.innovateuk.ifs.interview.transactional.InterviewAssignmentInviteService;
import org.innovateuk.ifs.invite.resource.AvailableApplicationPageResource;
import org.innovateuk.ifs.invite.resource.InterviewAssignmentStagedApplicationPageResource;
import org.innovateuk.ifs.invite.resource.StagedApplicationListResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Controller for managing application Invites to Interview Panels.
 */
@RestController
@RequestMapping("/interview-panel")
public class InterviewAssignmentController {

    private static final int DEFAULT_PAGE_SIZE = 20;

    private InterviewAssignmentInviteService interviewAssignmentInviteService;

    @Autowired
    public InterviewAssignmentController(InterviewAssignmentInviteService interviewAssignmentInviteService) {
        this.interviewAssignmentInviteService = interviewAssignmentInviteService;
    }


    @GetMapping("/available-applications/{competitionId}")
    public RestResult<AvailableApplicationPageResource> getAvailableApplications(
            @PathVariable long competitionId,
            @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = {"id"}, direction = Sort.Direction.ASC) Pageable pageable) {
        return interviewAssignmentInviteService.getAvailableApplications(competitionId, pageable).toGetResponse();
    }

    @GetMapping("/staged-applications/{competitionId}")
    public RestResult<InterviewAssignmentStagedApplicationPageResource> getStagedApplications(
            @PathVariable long competitionId,
            @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = {"target.id"}, direction = Sort.Direction.ASC) Pageable pageable) {
        return interviewAssignmentInviteService.getStagedApplications(competitionId, pageable).toGetResponse();
    }

    @GetMapping("/available-application-ids/{competitionId}")
    public RestResult<List<Long>> getAvailableApplicationIds(@PathVariable long competitionId) {
        return interviewAssignmentInviteService.getAvailableApplicationIds(competitionId).toGetResponse();
    }

    @PostMapping("/assign-applications")
    public RestResult<Void> assignApplications(@Valid @RequestBody StagedApplicationListResource stagedApplicationListResource) {
        return interviewAssignmentInviteService.assignApplications(stagedApplicationListResource.getInvites()).toPostWithBodyResponse();
    }

    @GetMapping("key-statistics/{competitionId}")
    public RestResult<InterviewAssignmentKeyStatisticsResource> getKeyStatistics(@PathVariable long competitionId) {
        return interviewAssignmentInviteService.getKeyStatistics(competitionId).toGetResponse();
    }
}