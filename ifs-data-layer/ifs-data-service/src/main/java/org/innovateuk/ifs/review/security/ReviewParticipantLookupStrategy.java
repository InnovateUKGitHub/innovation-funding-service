package org.innovateuk.ifs.review.security;

import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy;
import org.innovateuk.ifs.invite.domain.competition.ReviewParticipant;
import org.innovateuk.ifs.invite.mapper.AssessmentReviewPanelParticipantMapper;
import org.innovateuk.ifs.invite.repository.ReviewParticipantRepository;
import org.innovateuk.ifs.invite.resource.ReviewParticipantResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Lookup strategy for {@link ReviewParticipant}, used for permissioning.
 */
@Component
@PermissionEntityLookupStrategies
public class ReviewParticipantLookupStrategy {

    @Autowired
    private ReviewParticipantRepository reviewParticipantRepository;

    @Autowired
    private AssessmentReviewPanelParticipantMapper assessmentReviewPanelParticipantMapper;

    @PermissionEntityLookupStrategy
    public ReviewParticipantResource getAssessmentPanelParticipantResource(String inviteHash) {
        return assessmentReviewPanelParticipantMapper.mapToResource(reviewParticipantRepository.getByInviteHash(inviteHash));
    }
}
