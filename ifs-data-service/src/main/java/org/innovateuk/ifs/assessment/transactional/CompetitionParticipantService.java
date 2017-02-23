package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.CompetitionParticipantResource;
import org.innovateuk.ifs.invite.resource.CompetitionParticipantRoleResource;
import org.springframework.security.access.prepost.PostFilter;

import java.util.List;

/**
 * Service for managing {@link org.innovateuk.ifs.invite.domain.CompetitionParticipant}s.
 */
public interface CompetitionParticipantService {

    @PostFilter("hasPermission(filterObject, 'READ')")
    @SecuredBySpring(
            value = "READ_COMPETITION_PARTICIPANT",
            description = "An Assessor can view a CompetitionParticipant provided that they are the same user as the CompetitionParticipant")
    ServiceResult<List<CompetitionParticipantResource>> getCompetitionParticipants(Long userId,
                                                                                   CompetitionParticipantRoleResource roleResource);
}
