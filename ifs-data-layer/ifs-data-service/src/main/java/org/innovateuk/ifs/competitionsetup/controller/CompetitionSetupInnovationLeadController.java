package org.innovateuk.ifs.competitionsetup.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.transactional.CompetitionSetupInnovationLeadService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/competition/setup")
public class CompetitionSetupInnovationLeadController {

    @Autowired
    private CompetitionSetupInnovationLeadService competitionSetupInnovationLeadService;

    @GetMapping("/{competitionId}/innovation-leads")
    public RestResult<List<UserResource>> findAvailableInnovationLeadsNotAssignedToCompetition(@PathVariable("competitionId") final long competitionId) {
        return competitionSetupInnovationLeadService.findInnovationLeads(competitionId).toGetResponse();
    }

    @GetMapping("/{competitionId}/innovation-leads/find-added")
    public RestResult<List<UserResource>> findInnovationLeadsAddedToCompetition(@PathVariable("competitionId") final long competitionId) {
        return competitionSetupInnovationLeadService.findAddedInnovationLeads(competitionId).toGetResponse();
    }

    @PostMapping("/{id}/add-innovation-lead/{innovationLeadUserId}")
    public RestResult<Void> addInnovationLead(@PathVariable("id") final long competitionId,
                                              @PathVariable("innovationLeadUserId") final long innovationLeadUserId) {

        return competitionSetupInnovationLeadService.addInnovationLead(competitionId, innovationLeadUserId).toPostResponse();
    }

    @PostMapping("/{id}/remove-innovation-lead/{innovationLeadUserId}")
    public RestResult<Void> removeInnovationLead(@PathVariable("id") final long competitionId,
                                                 @PathVariable("innovationLeadUserId") final long innovationLeadUserId) {

        return competitionSetupInnovationLeadService.removeInnovationLead(competitionId, innovationLeadUserId).toPostResponse();
    }
}
