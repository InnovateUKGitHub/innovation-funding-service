package org.innovateuk.ifs.assessment.security;

import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy;
import org.innovateuk.ifs.invite.mapper.AssessmentPanelParticipantMapper;
import org.innovateuk.ifs.invite.repository.AssessmentPanelParticipantRepository;
import org.innovateuk.ifs.invite.resource.AssessmentPanelParticipantResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Lookup strategy for {@link org.innovateuk.ifs.invite.domain.AssessmentPanelParticipant}, used for permissioning.
 */
@Component
@PermissionEntityLookupStrategies
public class AssessmentPanelParticipantLookupStrategy {

    @Autowired
    private AssessmentPanelParticipantRepository assessmentPanelParticipantRepository;

    @Autowired
    private AssessmentPanelParticipantMapper assessmentPanelParticipantMapper;

    @PermissionEntityLookupStrategy
    public AssessmentPanelParticipantResource getAssessmentPanelParticipantResource(String inviteHash) {
        return assessmentPanelParticipantMapper.mapToResource(assessmentPanelParticipantRepository.getByInviteHash(inviteHash));
    }
}
