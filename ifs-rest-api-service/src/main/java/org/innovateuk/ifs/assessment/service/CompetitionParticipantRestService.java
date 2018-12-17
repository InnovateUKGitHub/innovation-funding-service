package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.CompetitionParticipantResource;

import java.util.List;

/**
 * REST service for retrieving {@link org.innovateuk.ifs.invite.resource.CompetitionParticipantResource}s
 */
public interface CompetitionParticipantRestService {

    RestResult<List<CompetitionParticipantResource>> getAssessorParticipants(long userId);
}
