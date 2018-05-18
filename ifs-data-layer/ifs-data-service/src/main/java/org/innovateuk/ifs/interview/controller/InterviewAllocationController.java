package org.innovateuk.ifs.interview.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.interview.resource.InterviewApplicationPageResource;
import org.innovateuk.ifs.interview.resource.InterviewAcceptedAssessorsPageResource;
import org.innovateuk.ifs.interview.transactional.InterviewAllocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller for allocating applications to assessors in Interview Panels.
 */
@RestController
@RequestMapping("/interview-panel")
public class InterviewAllocationController {

    private static final int DEFAULT_PAGE_SIZE = 20;

    @Autowired
    private InterviewAllocationService interviewAllocationService;

    @GetMapping("/allocate-assessors/{competitionId}")
    public RestResult<InterviewAcceptedAssessorsPageResource> getInterviewAcceptedAssessors(
            @PathVariable long competitionId,
            @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = "invite.name", direction = Sort.Direction.ASC) Pageable pageable) {
        return interviewAllocationService.getInterviewAcceptedAssessors(competitionId, pageable).toGetResponse();
    }

    @GetMapping("/{competitionId}/allocated-applications/{assessorId}")
    public RestResult<InterviewApplicationPageResource> getAllocatedApplications(
            @PathVariable long competitionId,
            @PathVariable long assessorId,
            @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = "target.id", direction = Sort.Direction.ASC) Pageable pageable) {
        return interviewAllocationService.getAllocatedApplications(competitionId, assessorId, pageable).toGetResponse();
    }

    @GetMapping("/{competitionId}/unallocated-applications/{assessorId}")
    public RestResult<InterviewApplicationPageResource> getUnallocatedApplications(
            @PathVariable long competitionId,
            @PathVariable long assessorId,
            @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = "target.id", direction = Sort.Direction.ASC) Pageable pageable) {
        return interviewAllocationService.getUnallocatedApplications(competitionId, assessorId, pageable).toGetResponse();
    }

    @GetMapping("/{competitionId}/unallocated-application-ids/{assessorId}")
    public RestResult<List<Long>> getUnallocatedApplicationIds(
            @PathVariable long competitionId,
            @PathVariable long assessorId) {
        return interviewAllocationService.getUnallocatedApplicationIds(competitionId, assessorId).toGetResponse();
    }
}
