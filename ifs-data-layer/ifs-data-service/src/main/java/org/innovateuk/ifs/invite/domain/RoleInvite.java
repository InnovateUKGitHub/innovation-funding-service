package org.innovateuk.ifs.invite.domain;

import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.Role;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.ZonedDateTime;

@Entity
@DiscriminatorValue("ROLE")
/**
 * A user invite with a specific role.
 */
public class RoleInvite extends Invite<Role, RoleInvite> {

    @Column(name = "target_id")
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

    public RoleInvite sendOrResend(User sentBy, ZonedDateTime sentOn) {
        return doSend(sentBy, sentOn);
    }
}
