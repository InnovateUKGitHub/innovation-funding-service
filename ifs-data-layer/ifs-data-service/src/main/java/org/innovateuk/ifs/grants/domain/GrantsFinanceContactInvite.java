package org.innovateuk.ifs.grants.domain;

import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.InviteOrganisation;
import org.innovateuk.ifs.project.core.domain.Project;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Invite a user to be a system-wide monitoring officer.
 */
@Entity
@DiscriminatorValue("GRANTS_PROJECT_FINANCE_CONTACT")
public class GrantsFinanceContactInvite extends GrantsInvite<GrantsFinanceContactInvite> {

    public GrantsFinanceContactInvite() {
    }

    public GrantsFinanceContactInvite(final String name, final String email, final String hash, final InviteOrganisation inviteOrganisation, final Project project, final InviteStatus status) {
        super(name, email, hash, inviteOrganisation,project, status);
    }

}