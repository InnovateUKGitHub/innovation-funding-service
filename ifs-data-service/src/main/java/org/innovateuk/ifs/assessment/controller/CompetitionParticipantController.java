package org.innovateuk.ifs.assessment.controller;

import org.innovateuk.ifs.assessment.transactional.CompetitionParticipantService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.CompetitionParticipantResource;
import org.innovateuk.ifs.invite.resource.CompetitionParticipantRoleResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for accessing Competition Participants.
 */
@RestController
@RequestMapping("/competitionparticipant")
public class CompetitionParticipantController {

    @Autowired
    private CompetitionParticipantService competitionParticipantService;

    @GetMapping("/user/{userId}/role/{role}")
    public RestResult<List<CompetitionParticipantResource>> getParticipants(@PathVariable("userId") Long userId,
                                                                               @PathVariable("role") CompetitionParticipantRoleResource roleResource) {
        return competitionParticipantService.getCompetitionParticipants(userId, roleResource).toGetResponse();
    }
}
