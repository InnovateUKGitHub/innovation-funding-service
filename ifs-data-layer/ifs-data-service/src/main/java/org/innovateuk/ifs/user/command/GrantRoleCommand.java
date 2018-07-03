package org.innovateuk.ifs.user.command;

import org.innovateuk.ifs.user.resource.Role;

public class GrantRoleCommand {

    private final long userId;
    private final Role targetRole;

    public GrantRoleCommand(long userId, Role targetRole) {
        this.userId = userId;
        this.targetRole = targetRole;
    }

    public long getUserId() {
        return userId;
    }

    public Role getTargetRole() {
        return targetRole;
    }
}
