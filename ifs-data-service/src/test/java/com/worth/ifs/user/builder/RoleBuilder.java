package com.worth.ifs.user.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.resource.UserRoleType;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class RoleBuilder extends BaseBuilder<Role, RoleBuilder> {

    private RoleBuilder(List<BiConsumer<Integer, Role>> multiActions) {
        super(multiActions);
    }

    public static RoleBuilder newRole() {
        return new RoleBuilder(emptyList()).with(uniqueIds());
    }

    public static RoleBuilder newRole(UserRoleType roleType) {
        return newRole().withType(roleType);
    }

    @Override
    protected RoleBuilder createNewBuilderWithActions(List<BiConsumer<Integer, Role>> actions) {
        return new RoleBuilder(actions);
    }

    @Override
    protected Role createInitial() {
        return new Role();
    }

    public RoleBuilder withType(UserRoleType... types) {
        return withArray((type, role) -> role.setName(type.getName()), types);
    }

    public RoleBuilder withName(String... names) {
        return withArray((name, role) -> role.setName(name), names);
    }

    public RoleBuilder withUrl(String... urls) {
        return withArray((name, role) -> role.setUrl(name), urls);
    }
}
