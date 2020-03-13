package org.innovateuk.ifs.acc;

import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.InviteOrganisation;
import org.innovateuk.ifs.invite.domain.ProjectInvite;
import org.innovateuk.ifs.project.core.domain.Project;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class AccInvite<T extends AccInvite<T>> extends ProjectInvite<T> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    private InviteOrganisation inviteOrganisation;

    public AccInvite() {
    }

    public AccInvite(final String name, final String email, final String hash, final InviteOrganisation inviteOrganisation, final Project project, final InviteStatus status) {
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
