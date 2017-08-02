package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.Builder;
import org.innovateuk.ifs.invite.domain.RoleInvite;
import org.innovateuk.ifs.user.domain.Role;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class RoleInviteBuilder extends BaseInviteBuilder<Role, RoleInvite, RoleInviteBuilder>{

    private RoleInviteBuilder(List<BiConsumer<Integer, RoleInvite>> multiActions) {
        super(multiActions);
    }

    public static RoleInviteBuilder newRoleInvite() {
        return new RoleInviteBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected RoleInviteBuilder createNewBuilderWithActions(List<BiConsumer<Integer, RoleInvite>> actions) {
        return new RoleInviteBuilder(actions);
    }

    @Override
    protected RoleInvite createInitial() {
        return new RoleInvite();
    }

    public RoleInviteBuilder withRole(Builder<Role, ?> invite) {
        return withRole(invite.build());
    }

    public RoleInviteBuilder withRole(Role... roles) {
        return withTarget(roles);
    }
}
