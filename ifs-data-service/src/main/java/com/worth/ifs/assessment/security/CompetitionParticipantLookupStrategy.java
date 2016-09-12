package com.worth.ifs.assessment.security;

import com.worth.ifs.invite.mapper.CompetitionParticipantMapper;
import com.worth.ifs.invite.repository.CompetitionParticipantRepository;
import com.worth.ifs.invite.resource.CompetitionParticipantResource;
import com.worth.ifs.security.PermissionEntityLookupStrategies;
import com.worth.ifs.security.PermissionEntityLookupStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Lookup strategy for {@link com.worth.ifs.invite.domain.CompetitionParticipant}, used for permissioning.
 */
@Component
@PermissionEntityLookupStrategies
public class CompetitionParticipantLookupStrategy {

    @Autowired
    private CompetitionParticipantRepository competitionParticipantRepository;

    @Autowired
    private CompetitionParticipantMapper competitionParticipantMapper;

    @PermissionEntityLookupStrategy
    public CompetitionParticipantResource getCompetitionParticipantResource(String inviteHash) {
        return competitionParticipantMapper.mapToResource(competitionParticipantRepository.getByInviteHash(inviteHash));
    }
}
