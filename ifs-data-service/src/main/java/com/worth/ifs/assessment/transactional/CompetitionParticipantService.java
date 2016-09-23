package com.worth.ifs.assessment.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.invite.resource.CompetitionParticipantResource;
import com.worth.ifs.invite.resource.CompetitionParticipantRoleResource;
import com.worth.ifs.invite.resource.ParticipantStatusResource;
import com.worth.ifs.commons.security.SecuredBySpring;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PostFilter;

import java.util.List;

/**
 * Service for managing {@link com.worth.ifs.invite.domain.CompetitionParticipant}s.
 */
public interface CompetitionParticipantService {

    @PostFilter("hasPermission(filterObject, 'READ')")
    @SecuredBySpring(value = "READ_COMPETITION_PARTICIPANT",
            description = "An Assessor can view a CompetitionParticipant provided that they are the same user as the CompetitionParticipant")
    ServiceResult<List<CompetitionParticipantResource>> getCompetitionParticipants(@P("user") Long userId,
                                                                                   @P("role") CompetitionParticipantRoleResource roleResource,
                                                                                   @P("status") ParticipantStatusResource statusResource);
}
