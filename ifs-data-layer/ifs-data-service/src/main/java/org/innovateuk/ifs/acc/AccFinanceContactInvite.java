package org.innovateuk.ifs.acc;

import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.InviteOrganisation;
import org.innovateuk.ifs.project.core.domain.Project;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Invite a user to be a system-wide monitoring officer.
 */
@Entity
@DiscriminatorValue("ACC_PROJECT_FINANCE_CONTACT")
public class AccFinanceContactInvite extends AccInvite<AccFinanceContactInvite> {

    public AccFinanceContactInvite() {
    }

    public AccFinanceContactInvite(final String name, final String email, final String hash, final InviteOrganisation inviteOrganisation, final Project project, final InviteStatus status) {
        super(name, email, hash, inviteOrganisation,project, status);
    }

}