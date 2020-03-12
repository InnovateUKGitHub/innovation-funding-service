package org.innovateuk.ifs.acc;

import org.innovateuk.ifs.competition.domain.ResendableInvite;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.Invite;
import org.innovateuk.ifs.invite.domain.InviteOrganisation;
import org.innovateuk.ifs.invite.domain.ProjectInvite;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.user.domain.User;

import javax.persistence.*;
import java.time.ZonedDateTime;

/**
 * Invite a user to be a system-wide monitoring officer.
 */
@Entity
@DiscriminatorValue("ACC_PROJECT_FINANCE_CONTACT")
public class AccFinanceContactInvite extends ProjectInvite<AccFinanceContactInvite> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    private InviteOrganisation inviteOrganisation;

    public AccFinanceContactInvite() {
    }

    public AccFinanceContactInvite(final String name, final String email, final String hash, final InviteOrganisation inviteOrganisation, final Project project, final InviteStatus status) {
        super(name, email, hash, project, status);
        this.inviteOrganisation = inviteOrganisation;
    }

    public InviteOrganisation getInviteOrganisation() {
        return inviteOrganisation;
    }

    public void setInviteOrganisation(InviteOrganisation inviteOrganisation) {
        this.inviteOrganisation = inviteOrganisation;
    }

}