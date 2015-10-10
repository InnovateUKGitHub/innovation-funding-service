package com.worth.ifs.user.domain;

import com.worth.ifs.BaseBuilder;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by dwatson on 08/10/15.
 */
public class RoleBuilder extends BaseBuilder<Role> {

    private RoleBuilder() {
        super();
    }

    private RoleBuilder(List<BiConsumer<Integer, Role>> multiActions) {
        super(multiActions);
    }

    public static RoleBuilder newRole() {
        return new RoleBuilder();
    }

    @Override
    protected RoleBuilder createNewBuilderWithActions(List<BiConsumer<Integer, Role>> actions) {
        return new RoleBuilder(actions);
    }

    @Override
    protected Role createInitial() {
        return new Role();
    }

    public RoleBuilder withType(UserRoleType type) {
        return with(role -> role.setName(type.getName()));
    }
}
