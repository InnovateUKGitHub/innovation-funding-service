package org.innovateuk.ifs.invite.domain.competition;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.user.domain.User;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import static org.innovateuk.ifs.invite.constant.InviteStatus.CREATED;

/**
 * An invitation for an assessor to an assessment panel.
 */
@Entity
@DiscriminatorValue("ASSESSMENT_PANEL")
public class AssessmentReviewInvite extends CompetitionInvite<AssessmentReviewInvite>  {

    public AssessmentReviewInvite() {
    }

    /**
     * An existing User invited to an Assessment Panel.
     */
    public AssessmentReviewInvite(final User existingUser, final String hash, Competition competition) {
        super(competition, existingUser.getName(), existingUser.getEmail(), hash, CREATED);
        if (competition == null) {
            throw new NullPointerException("competition cannot be null");
        }
    }
}