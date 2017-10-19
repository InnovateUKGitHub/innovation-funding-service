package org.innovateuk.ifs.invite.domain;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.user.domain.User;

import javax.persistence.*;
import java.io.Serializable;

import static org.innovateuk.ifs.invite.constant.InviteStatus.CREATED;


/**
 * An invitation for an assessor to an assessment panel.
 */
@Entity
@DiscriminatorValue("ASSESSMENT_PANEL")
public class AssessmentPanelInvite extends Invite<Competition, AssessmentPanelInvite> implements Serializable {

    @ManyToOne(optional = false)
    @JoinColumn(name = "target_id", referencedColumnName = "id")
    private Competition competition;

    public AssessmentPanelInvite() {
    }

    /**
     * An existing User invited to a Competition.
     */
    public AssessmentPanelInvite(final User existingUser, final String hash, Competition competition) {
        super(existingUser.getName(), existingUser.getEmail(), hash, CREATED);
        if (competition == null) {
            throw new NullPointerException("competition cannot be null");
        }
        this.competition = competition;
    }

    @Override
    public  Competition getTarget() {
        return competition;
    }

    @Override
    public void setTarget(Competition competition) {
        this.competition = competition;
    }

}
