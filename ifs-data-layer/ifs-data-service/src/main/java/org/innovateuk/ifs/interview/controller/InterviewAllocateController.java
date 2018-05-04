package org.innovateuk.ifs.interview.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.interview.resource.InterviewApplicationPageResource;
import org.innovateuk.ifs.interview.resource.InterviewAllocateOverviewPageResource;
import org.innovateuk.ifs.interview.transactional.InterviewAllocateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for allocating applications to assessors in Interview Panels.
 */
@RestController
@RequestMapping("/interview-panel")
public class InterviewAllocateController {

    private static final int DEFAULT_PAGE_SIZE = 20;

    @Autowired
    private InterviewAllocateService interviewAllocateService;

    @GetMapping("/allocate-overview/{competitionId}")
    public RestResult<InterviewAllocateOverviewPageResource> getAllocateApplicationsOverview(
            @PathVariable long competitionId,
            @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = "invite.name", direction = Sort.Direction.ASC) Pageable pageable) {
        return interviewAllocateService.getAllocateApplicationsOverview(competitionId, pageable).toGetResponse();
    }

    @GetMapping("/{competitionId}/allocated-applications/{assessorId}")
    public RestResult<InterviewApplicationPageResource> getAllocatedApplications(
            @PathVariable long competitionId,
            @PathVariable long assessorId,
            @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = "interviewAssignment.target.name", direction = Sort.Direction.ASC) Pageable pageable) {
        return interviewAllocateService.getAllocatedApplications(competitionId, assessorId, pageable).toGetResponse();
    }

    @GetMapping("/{competitionId}/unallocated-applications/{assessorId}")
    public RestResult<InterviewApplicationPageResource> getUnallocatedApplications(
            @PathVariable long competitionId,
            @PathVariable long assessorId,
            @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = "interviewAssignment.target.name", direction = Sort.Direction.ASC) Pageable pageable) {
        return interviewAllocateService.getUnallocatedApplications(competitionId, assessorId, pageable).toGetResponse();
    }
}
