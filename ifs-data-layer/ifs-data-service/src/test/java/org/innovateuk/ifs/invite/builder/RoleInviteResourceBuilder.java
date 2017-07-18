package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.invite.resource.RoleInviteResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

/**
 * Builder for role invite resource
 */
public class RoleInviteResourceBuilder extends BaseBuilder<RoleInviteResource, RoleInviteResourceBuilder> {

    @Override
    protected RoleInviteResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, RoleInviteResource>> actions) {
        return new RoleInviteResourceBuilder(actions);
    }

    @Override
    protected RoleInviteResource createInitial() {
        return new RoleInviteResource();
    }

    public static RoleInviteResourceBuilder newRoleInviteResource(){
        return new RoleInviteResourceBuilder(emptyList()).with(uniqueIds());
    }

    private RoleInviteResourceBuilder(List<BiConsumer<Integer, RoleInviteResource>> multiActions) {
        super(multiActions);
    }

    public RoleInviteResourceBuilder withName(String... names) {
        return withArraySetFieldByReflection("name", names);
    }

    public RoleInviteResourceBuilder withEmail(String... emails) {
        return withArraySetFieldByReflection("email", emails);
    }

    public RoleInviteResourceBuilder withRoleId(Long... roleIds) {
        return withArraySetFieldByReflection("roleId", roleIds);
    }

    public RoleInviteResourceBuilder withRoleName(String... roleNames) {
        return withArraySetFieldByReflection("roleName", roleNames);
    }

    public RoleInviteResourceBuilder withHash(String... hashes) {
        return withArraySetFieldByReflection("hash", hashes);
    }
}
