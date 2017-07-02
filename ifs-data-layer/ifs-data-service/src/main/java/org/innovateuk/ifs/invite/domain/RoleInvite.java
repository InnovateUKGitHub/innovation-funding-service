package org.innovateuk.ifs.invite.domain;

import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.user.domain.Role;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@DiscriminatorValue("ROLE")
/**
 * A user invite with a specific role.
 */
public class RoleInvite extends Invite<Role, RoleInvite> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id", referencedColumnName = "id")
    private Role role;

    public RoleInvite() {
    }

    public RoleInvite(final String name, final String email, final String hash, final Role role, final InviteStatus status) {
        super(name, email, hash, status);
        this.role = role;
    }

    @Override
    public Role getTarget() {
        return role;
    }

    @Override
    public void setTarget(final Role role) {
        this.role = role;
    }
}
