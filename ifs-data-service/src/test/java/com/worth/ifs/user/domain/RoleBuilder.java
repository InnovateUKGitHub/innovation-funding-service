package com.worth.ifs.user.domain;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.Builder;

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

    private RoleBuilder(List<Consumer<Role>> actions, List<BiConsumer<Integer, Role>> multiActions) {
        super(actions, multiActions);
    }

    public static RoleBuilder newRole() {
        return new RoleBuilder();
    }

    @Override
    protected RoleBuilder createNewBuilderWithActions(List<Consumer<Role>> actions, List<BiConsumer<Integer, Role>> multiActions) {
        return new RoleBuilder(actions, multiActions);
    }

    @Override
    protected Role createInitial() {
        return new Role();
    }

    public RoleBuilder withType(UserRoleType type) {
        return with(role -> role.setName(type.getName()));
    }
}
