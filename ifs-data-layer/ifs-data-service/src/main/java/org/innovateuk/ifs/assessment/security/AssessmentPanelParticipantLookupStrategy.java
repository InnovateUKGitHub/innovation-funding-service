package org.innovateuk.ifs.assessment.security;

import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy;
import org.innovateuk.ifs.invite.domain.competition.AssessmentReviewPanelParticipant;
import org.innovateuk.ifs.invite.mapper.AssessmentReviewPanelParticipantMapper;
import org.innovateuk.ifs.invite.repository.AssessmentPanelParticipantRepository;
import org.innovateuk.ifs.invite.resource.AssessmentReviewPanelParticipantResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Lookup strategy for {@link AssessmentReviewPanelParticipant}, used for permissioning.
 */
@Component
@PermissionEntityLookupStrategies
public class AssessmentPanelParticipantLookupStrategy {

    @Autowired
    private AssessmentPanelParticipantRepository assessmentPanelParticipantRepository;

    @Autowired
    private AssessmentReviewPanelParticipantMapper assessmentReviewPanelParticipantMapper;

    @PermissionEntityLookupStrategy
    public AssessmentReviewPanelParticipantResource getAssessmentPanelParticipantResource(String inviteHash) {
        return assessmentReviewPanelParticipantMapper.mapToResource(assessmentPanelParticipantRepository.getByInviteHash(inviteHash));
    }
}
