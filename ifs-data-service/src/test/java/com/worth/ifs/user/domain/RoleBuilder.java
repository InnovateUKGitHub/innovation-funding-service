package com.worth.ifs.user.domain;

import com.worth.ifs.application.domain.Builder;

import java.util.function.Consumer;

/**
 * Created by dwatson on 08/10/15.
 */
public class RoleBuilder implements Builder<Role> {

    private final Role current;

    private RoleBuilder(Role value) {
        this.current = value;
    }

    public static RoleBuilder newRole() {
        return new RoleBuilder(new Role());
    }

    @Override
    public RoleBuilder with(Consumer<Role> amendFunction) {
        Role newValue = new Role(current);
        amendFunction.accept(newValue);
        return new RoleBuilder(newValue);
    }

    public RoleBuilder withType(UserRoleType type) {
        return with(role -> role.setName(type.name()));
    }

    @Override
    public Role build() {
        return current;
    }
}
