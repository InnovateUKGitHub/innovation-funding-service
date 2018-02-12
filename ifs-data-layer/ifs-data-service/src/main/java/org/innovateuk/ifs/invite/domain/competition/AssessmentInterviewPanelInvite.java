package org.innovateuk.ifs.invite.domain.competition;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.user.domain.User;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import static org.innovateuk.ifs.invite.constant.InviteStatus.CREATED;

/**
 * An invitation for an assessor to an interview panel.
 */
@Entity
@DiscriminatorValue("INTERVIEW_PANEL")
public class AssessmentInterviewPanelInvite extends CompetitionInvite<AssessmentInterviewPanelInvite>  {

    public AssessmentInterviewPanelInvite() {
    }

    /**
     * An existing User invited to an Assessment Panel.
     */
    public AssessmentInterviewPanelInvite(final User existingUser, final String hash, Competition competition) {
        super(competition, existingUser.getName(), existingUser.getEmail(), hash, CREATED);
        if (competition == null) {
            throw new NullPointerException("competition cannot be null");
        }
    }
}