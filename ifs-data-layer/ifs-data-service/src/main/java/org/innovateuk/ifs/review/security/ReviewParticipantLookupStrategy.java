package org.innovateuk.ifs.review.security;

import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy;
import org.innovateuk.ifs.invite.resource.ReviewParticipantResource;
import org.innovateuk.ifs.review.domain.ReviewParticipant;
import org.innovateuk.ifs.review.mapper.ReviewParticipantMapper;
import org.innovateuk.ifs.review.repository.ReviewParticipantRepository;
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
    private ReviewParticipantMapper reviewParticipantMapper;

    @PermissionEntityLookupStrategy
    public ReviewParticipantResource getAssessmentPanelParticipantResource(String inviteHash) {
        return reviewParticipantMapper.mapToResource(reviewParticipantRepository.getByInviteHash(inviteHash));
    }
}
