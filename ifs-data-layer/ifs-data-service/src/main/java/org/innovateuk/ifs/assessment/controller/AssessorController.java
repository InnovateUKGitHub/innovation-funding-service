package org.innovateuk.ifs.assessment.controller;

import org.innovateuk.ifs.assessment.resource.AssessorProfileResource;
import org.innovateuk.ifs.assessment.transactional.AssessorService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.registration.resource.UserRegistrationResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Exposes CRUD operations through a REST API to manage assessor related data.
 */
@RestController
@RequestMapping("/assessor")
public class AssessorController {

    @Autowired
    private AssessorService assessorService;

    @Autowired
    private CompetitionService competitionService;

    @PostMapping("/register/{hash}")
    public RestResult<Void> registerAssessorByHash(@PathVariable("hash") String hash,
                                                   @Valid @RequestBody UserRegistrationResource userResource) {
        return assessorService.registerAssessorByHash(hash, userResource).toPostResponse();
    }

    @GetMapping("/profile/{assessorId}")
    public RestResult<AssessorProfileResource> getAssessorProfile(@PathVariable("assessorId") Long assessorId) {
        return assessorService.getAssessorProfile(assessorId).toGetResponse();
    }

    @PutMapping("/notify-assessors/competition/{competitionId}")
    public RestResult<Void> notifyAssessors(@PathVariable("competitionId") long competitionId) {
        return competitionService.notifyAssessors(competitionId)
                .andOnSuccess(() -> assessorService.notifyAssessorsByCompetition(competitionId))
                .toPutResponse();
    }

    @GetMapping("/has-applications-assigned/{assessorId}")
    public RestResult<Boolean> hasApplicationsAssigned(@PathVariable("assessorId") long assessorId) {
        return assessorService.hasApplicationsAssigned(assessorId).toGetResponse();
    }
}
