package org.innovateuk.ifs.assessment.interview.controller;

import org.innovateuk.ifs.assessment.interview.transactional.InterviewPanelInviteService;
import org.innovateuk.ifs.assessment.transactional.AssessmentPanelInviteService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
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
@RequestMapping("/interview-panel-invite")
public class InterviewPanelInviteController {

    private static final int DEFAULT_PAGE_SIZE = 20;

    @Autowired
    private InterviewPanelInviteService interviewPanelInviteService;

    @GetMapping("/get-available-applications/{competitionId}")
    public RestResult<AvailableApplicationPageResource> getAvailableAssessors(
            @PathVariable long competitionId,
            @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = {"id", "name"}, direction = Sort.Direction.ASC) Pageable pageable) {
        return interviewPanelInviteService.getAvailableApplications(competitionId, pageable).toGetResponse();
    }
}