package org.innovateuk.ifs.assessment.interview.controller;

import org.innovateuk.ifs.assessment.interview.transactional.InterviewPanelInviteService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Controller for managing Invites to Assessment Panels.
 */
@RestController
@RequestMapping("/interview-panel")
public class InterviewPanelController {

    private static final int DEFAULT_PAGE_SIZE = 20;

    @Autowired
    private InterviewPanelInviteService interviewPanelInviteService;

    @GetMapping("/available-applications/{competitionId}")
    public RestResult<AvailableApplicationPageResource> getAvailableApplications(
            @PathVariable long competitionId,
            @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = {"id"}, direction = Sort.Direction.ASC) Pageable pageable) {
        return interviewPanelInviteService.getAvailableApplications(competitionId, pageable).toGetResponse();
    }

    @GetMapping("/invited-applications/{competitionId}")
    public RestResult<InterviewPanelCreatedInvitePageResource> getCreates(
            @PathVariable long competitionId,
            @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = {"target.id"}, direction = Sort.Direction.ASC) Pageable pageable) {
        return interviewPanelInviteService.getCreatedApplications(competitionId, pageable).toGetResponse();
    }

    @GetMapping(value = "/available-application-ids/{competitionId}")
    public RestResult<List<Long>> getAvailableAssessorIds(@PathVariable long competitionId) {
        return interviewPanelInviteService.getAvailableAssessorIds(competitionId).toGetResponse();
    }

    @PostMapping("/assign-applications")
    public RestResult<Void> assignApplications(@Valid @RequestBody ExistingUserStagedInviteListResource existingUserStagedInvites) {
        return interviewPanelInviteService.assignApplications(existingUserStagedInvites.getInvites()).toPostWithBodyResponse();
    }
}