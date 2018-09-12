package org.innovateuk.ifs.user.command;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        GrantRoleCommand that = (GrantRoleCommand) o;

        return new EqualsBuilder()
                .append(userId, that.userId)
                .append(targetRole, that.targetRole)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(userId)
                .append(targetRole)
                .toHashCode();
    }
}
