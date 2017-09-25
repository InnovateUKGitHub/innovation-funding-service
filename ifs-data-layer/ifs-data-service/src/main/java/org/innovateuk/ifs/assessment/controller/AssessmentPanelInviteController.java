package org.innovateuk.ifs.assessment.controller;

import org.innovateuk.ifs.assessment.transactional.AssessmentPanelInviteService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.AssessorInviteSendResource;
import org.innovateuk.ifs.invite.resource.AssessorInvitesToSendResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.innovateuk.ifs.invite.resource.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import javax.validation.Valid;
import java.util.List;

/**
 * Controller for managing Invites to Assessment Panels.
 */


@RestController
@RequestMapping("/assessmentpanelinvite")
public class AssessmentPanelInviteController {


    private static final int DEFAULT_PAGE_SIZE = 20;

    @Autowired
    private AssessmentPanelInviteService assessmentPanelInviteService;

    @GetMapping("/getAllInvitesToSend/{competitionId}")
    public RestResult<AssessorInvitesToSendResource> getAllInvitesToSend(@PathVariable long competitionId) {
        return assessmentPanelInviteService.getAllInvitesToSend(competitionId).toGetResponse();
    }

    @PostMapping("/sendAllInvites/{competitionId}")
    public RestResult<Void> sendAllInvites(@PathVariable long competitionId,
                                           @RequestBody AssessorInviteSendResource assessorInviteSendResource) {
        return assessmentPanelInviteService.sendAllInvites(competitionId, assessorInviteSendResource).toPostResponse();
    }


    @GetMapping("/getCreatedInvites/{competitionId}")
    public RestResult<AssessorCreatedInvitePageResource> getCreatedInvites(
            @PathVariable long competitionId,
            @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = "name", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return assessmentPanelInviteService.getCreatedInvites(competitionId, pageable).toGetResponse();
    }

    @PostMapping("/inviteUsers")
    public RestResult<Void> inviteUsers(@Valid @RequestBody ExistingUserStagedInviteListResource existingUserStagedInvites) {
        return assessmentPanelInviteService.inviteUsers(existingUserStagedInvites.getInvites()).toPostWithBodyResponse();
    }

    @GetMapping("/getAvailableAssessors/{competitionId}")
    public RestResult<AvailableAssessorPageResource> getAvailableAssessors(
            @PathVariable long competitionId,
            @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = {"user.firstName", "user.lastName"}, direction = Sort.Direction.ASC) Pageable pageable) {
        return assessmentPanelInviteService.getAvailableAssessors(competitionId, pageable).toGetResponse();
    }

    @GetMapping(value = "/getAvailableAssessorIds/{competitionId}")
    public RestResult<List<Long>> getAvailableAssessorIds(
            @PathVariable long competitionId) {
        return assessmentPanelInviteService.getAvailableAssessorIds(competitionId).toGetResponse();
    }

}
