package org.innovateuk.ifs.assessment.security;

import org.innovateuk.ifs.assessment.mapper.AssessmentParticipantMapper;
import org.innovateuk.ifs.assessment.repository.AssessmentParticipantRepository;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy;
import org.innovateuk.ifs.competition.domain.CompetitionParticipant;
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
    private AssessmentParticipantRepository assessmentParticipantRepository;

    @Autowired
    private AssessmentParticipantMapper assessmentParticipantMapper;

    @PermissionEntityLookupStrategy
    public CompetitionParticipantResource getCompetitionParticipantResource(String inviteHash) {
        return assessmentParticipantMapper.mapToResource(assessmentParticipantRepository.getByInviteHash(inviteHash));
    }
}
