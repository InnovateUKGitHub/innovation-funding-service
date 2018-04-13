package org.innovateuk.ifs.interview.security;

import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy;
import org.innovateuk.ifs.interview.domain.InterviewParticipant;
import org.innovateuk.ifs.interview.mapper.InterviewParticipantMapper;
import org.innovateuk.ifs.interview.repository.InterviewParticipantRepository;
import org.innovateuk.ifs.invite.resource.InterviewParticipantResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Lookup strategy for {@link InterviewParticipant}, used for permissioning.
 */
@Component
@PermissionEntityLookupStrategies
public class InterviewParticipantLookupStrategy {

    @Autowired
    private InterviewParticipantRepository interviewParticipantRepository;

    @Autowired
    private InterviewParticipantMapper interviewParticipantMapper;

    @PermissionEntityLookupStrategy
    public InterviewParticipantResource getInterviewParticipantResource(String inviteHash) {
        return interviewParticipantMapper.mapToResource(interviewParticipantRepository.getByInviteHash(inviteHash));
    }
}
