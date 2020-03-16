package org.innovateuk.ifs.invite.domain;

import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.project.core.domain.Project;

import javax.persistence.*;

@Entity
@DiscriminatorValue("ACC_PROJECT")
public class AccUserInvite extends BaseUserInvite {

    public AccUserInvite() {
    }

    public AccUserInvite(final String name, final String email, final String hash, final Organisation organisation, final Project project, final InviteStatus status) {
        super(name, email, hash, organisation, project, status);
    }
}