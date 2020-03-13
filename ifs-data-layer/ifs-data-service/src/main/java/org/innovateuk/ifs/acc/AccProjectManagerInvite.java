package org.innovateuk.ifs.acc;

import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.InviteOrganisation;
import org.innovateuk.ifs.invite.domain.ProjectInvite;
import org.innovateuk.ifs.project.core.domain.Project;

import javax.persistence.*;

/**
 * Invite for a new project manager for a live project
 */
@Entity
@DiscriminatorValue("ACC_PROJECT_MANAGER")
public class AccProjectManagerInvite extends AccInvite<AccProjectManagerInvite> {

    public AccProjectManagerInvite() {
    }

    public AccProjectManagerInvite(final String name, final String email, final String hash, final InviteOrganisation inviteOrganisation, final Project project, final InviteStatus status) {
        super(name, email, hash, inviteOrganisation,project, status);
    }
}