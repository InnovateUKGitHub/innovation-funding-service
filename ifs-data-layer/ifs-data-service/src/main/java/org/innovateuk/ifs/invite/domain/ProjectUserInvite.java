package org.innovateuk.ifs.invite.domain;

import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.project.core.domain.Project;

import javax.persistence.*;

@Entity
@DiscriminatorValue("PROJECT")
public class ProjectUserInvite extends ProjectInvite<ProjectUserInvite> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    private Organisation organisation;

    public ProjectUserInvite() {
    }

    public ProjectUserInvite(final String name, final String email, final String hash, final Organisation organisation, final Project project, final InviteStatus status) {
        super(name, email, hash, project, status);
        this.organisation = organisation;
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(final Organisation organisation) {
        this.organisation = organisation;
    }
}