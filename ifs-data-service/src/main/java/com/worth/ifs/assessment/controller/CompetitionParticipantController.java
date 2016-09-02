package com.worth.ifs.assessment.controller;

import com.worth.ifs.assessment.transactional.CompetitionParticipantService;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.invite.resource.CompetitionParticipantResource;
import com.worth.ifs.invite.resource.CompetitionParticipantRoleResource;
import com.worth.ifs.invite.resource.ParticipantStatusResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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

    @RequestMapping(value = "/user/{userId}/role/{role}/status/{status}", method = RequestMethod.GET)
    public RestResult<List<CompetitionParticipantResource>> getParticipants(@PathVariable("userId") Long userId,
                                                                            @PathVariable("role") CompetitionParticipantRoleResource roleResource,
                                                                            @PathVariable("status") ParticipantStatusResource statusResource) {

         return competitionParticipantService.getCompetitionParticipants(userId, roleResource, statusResource).toGetResponse();
    }
}
