package org.innovateuk.ifs.assessment.controller;

import org.innovateuk.ifs.assessment.transactional.CompetitionParticipantService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.CompetitionParticipantResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller for accessing Competition Participants.
 */
@RestController
@RequestMapping("/competitionparticipant")
public class CompetitionParticipantController {

    @Autowired
    private CompetitionParticipantService competitionParticipantService;

    @GetMapping("/user/{userId}")
    public RestResult<List<CompetitionParticipantResource>> getAssessorParticipants(@PathVariable("userId") long userId) {
        return competitionParticipantService.getCompetitionAssessors(userId).toGetResponse();
    }
}
