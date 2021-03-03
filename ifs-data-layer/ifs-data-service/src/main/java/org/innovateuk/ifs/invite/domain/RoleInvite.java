package org.innovateuk.ifs.invite.domain;

import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.organisation.domain.SimpleOrganisation;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.Role;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@DiscriminatorValue("ROLE")
/**
 * A user invite with a specific role.
 */
public class RoleInvite extends Invite<Role, RoleInvite> {

    @Column(name = "target_id")
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    private SimpleOrganisation simpleOrganisation;

    public RoleInvite() {
    }

    public RoleInvite(final String name, final String email, final String hash, final Role role, final InviteStatus status, SimpleOrganisation simpleOrganisation) {
        super(name, email, hash, status);
        this.role = role;
        this.simpleOrganisation = simpleOrganisation;
    }

    @Override
    public Role getTarget() {
        return role;
    }

    @Override
    public void setTarget(final Role role) {
        this.role = role;
    }

    public SimpleOrganisation getSimpleOrganisation() {
        return simpleOrganisation;
    }

    public void setSimpleOrganisation(SimpleOrganisation simpleOrganisation) {
        this.simpleOrganisation = simpleOrganisation;
    }

    public RoleInvite sendOrResend(User sentBy, ZonedDateTime sentOn) {
        return doSend(sentBy, sentOn);
    }
}
