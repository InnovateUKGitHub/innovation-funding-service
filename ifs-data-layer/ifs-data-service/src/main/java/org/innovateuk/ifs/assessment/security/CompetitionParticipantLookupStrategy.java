package org.innovateuk.ifs.assessment.security;

import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy;
import org.innovateuk.ifs.invite.domain.competition.CompetitionParticipant;
import org.innovateuk.ifs.invite.mapper.AssessmentParticipantMapper;
import org.innovateuk.ifs.invite.repository.CompetitionParticipantRepository;
import org.innovateuk.ifs.invite.resource.CompetitionParticipantResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Lookup strategy for {@link CompetitionParticipant}, used for permissioning.
 */
@Component
@PermissionEntityLookupStrategies
public class CompetitionParticipantLookupStrategy {

    @Autowired
    private CompetitionParticipantRepository competitionParticipantRepository;

    @Autowired
    private AssessmentParticipantMapper assessmentParticipantMapper;

    @PermissionEntityLookupStrategy
    public CompetitionParticipantResource getCompetitionParticipantResource(String inviteHash) {
        return assessmentParticipantMapper.mapToResource(competitionParticipantRepository.getByInviteHash(inviteHash));
    }
}
