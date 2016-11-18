package com.worth.ifs.assessment.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.invite.resource.CompetitionParticipantResource;
import com.worth.ifs.invite.resource.CompetitionParticipantRoleResource;
import com.worth.ifs.invite.resource.ParticipantStatusResource;

import java.util.List;

/**
 * REST service for retrieving {@link com.worth.ifs.invite.resource.CompetitionParticipantResource}s
 */
public interface CompetitionParticipantRestService {

    RestResult<List<CompetitionParticipantResource>> getParticipants(Long userId, CompetitionParticipantRoleResource role , ParticipantStatusResource status);
}
