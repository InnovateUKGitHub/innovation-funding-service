package org.innovateuk.ifs.grants.domain;

import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.InviteOrganisation;
import org.innovateuk.ifs.invite.domain.ProjectInvite;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.project.core.domain.Project;

import javax.persistence.*;

@Entity
public class GrantsInvite<T extends GrantsInvite<T>> extends ProjectInvite<T> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    private Organisation organisation;

    public GrantsInvite() {
    }

    public GrantsInvite(final String name, final String email, final String hash, final Organisation organisation, final Project project, final InviteStatus status) {
        super(name, email, hash, project, status);
        this.organisation = organisation;
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }
}
