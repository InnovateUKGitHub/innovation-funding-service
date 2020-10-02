package org.innovateuk.ifs.user.command;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.user.resource.Role;

import java.util.Optional;

public class GrantRoleCommand {

    private final long userId;
    private final Role targetRole;
    private final Optional<String> organisation;

    public GrantRoleCommand(long userId, Role targetRole, Optional<String> organisation) {
        this.userId = userId;
        this.targetRole = targetRole;
        this.organisation = organisation;
    }

    public long getUserId() {
        return userId;
    }

    public Role getTargetRole() {
        return targetRole;
    }

    public Optional<String> getOrganisation() {
        return organisation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        GrantRoleCommand that = (GrantRoleCommand) o;

        return new EqualsBuilder()
                .append(userId, that.userId)
                .append(targetRole, that.targetRole)
                .append(organisation, that.organisation)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(userId)
                .append(targetRole)
                .append(organisation)
                .toHashCode();
    }
}
