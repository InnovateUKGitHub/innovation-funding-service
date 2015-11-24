package com.worth.ifs.user.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.domain.UserRoleType;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

/**
 * Created by dwatson on 08/10/15.
 */
public class RoleBuilder extends BaseBuilder<Role, RoleBuilder> {

    private RoleBuilder(List<BiConsumer<Integer, Role>> multiActions) {
        super(multiActions);
    }

    public static RoleBuilder newRole() {
        return new RoleBuilder(emptyList()).with(uniqueIds());
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
}
