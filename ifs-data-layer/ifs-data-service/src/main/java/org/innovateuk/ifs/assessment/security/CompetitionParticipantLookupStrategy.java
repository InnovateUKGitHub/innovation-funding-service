package org.innovateuk.ifs.assessment.security;

import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy;
import org.innovateuk.ifs.invite.mapper.CompetitionAssessmentParticipantMapper;
import org.innovateuk.ifs.invite.repository.CompetitionParticipantRepository;
import org.innovateuk.ifs.invite.resource.CompetitionParticipantResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Lookup strategy for {@link org.innovateuk.ifs.invite.domain.CompetitionParticipant}, used for permissioning.
 */
@Component
@PermissionEntityLookupStrategies
public class CompetitionParticipantLookupStrategy {

    @Autowired
    private CompetitionParticipantRepository competitionParticipantRepository;

    @Autowired
    private CompetitionAssessmentParticipantMapper competitionAssessmentParticipantMapper;

    @PermissionEntityLookupStrategy
    public CompetitionParticipantResource getCompetitionParticipantResource(String inviteHash) {
        return competitionAssessmentParticipantMapper.mapToResource(competitionParticipantRepository.getByInviteHash(inviteHash));
    }
}
