package org.innovateuk.ifs.grants.domain;

import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.InviteOrganisation;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.project.core.domain.Project;

import javax.persistence.*;

/**
 * Invite for a new project manager for a live project
 */
@Entity
@DiscriminatorValue("GRANTS_PROJECT_MANAGER")
public class GrantsProjectManagerInvite extends GrantsInvite<GrantsProjectManagerInvite> {

    public GrantsProjectManagerInvite() {
    }

    public GrantsProjectManagerInvite(final String name, final String email, final String hash, final Organisation organisation, final Project project, final InviteStatus status) {
        super(name, email, hash, organisation,project, status);
    }
}