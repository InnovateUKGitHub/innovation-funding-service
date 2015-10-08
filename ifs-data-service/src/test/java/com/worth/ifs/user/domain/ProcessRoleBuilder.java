package com.worth.ifs.user.domain;

import com.worth.ifs.application.domain.Builder;

import java.util.function.Consumer;

/**
 * Created by dwatson on 08/10/15.
 */
public class ProcessRoleBuilder implements Builder<ProcessRole> {

    private final ProcessRole current;

    private ProcessRoleBuilder(ProcessRole value) {
        this.current = value;
    }

    public static ProcessRoleBuilder newProcessRole() {
        return new ProcessRoleBuilder(new ProcessRole());
    }

    @Override
    public ProcessRoleBuilder with(Consumer<ProcessRole> amendFunction) {
        ProcessRole newValue = new ProcessRole(current);
        amendFunction.accept(newValue);
        return new ProcessRoleBuilder(newValue);
    }

    public ProcessRoleBuilder withRole(Builder<Role> role) {
        return with(processRole -> processRole.setRole(role.build()));
    }

    @Override
    public ProcessRole build() {
        return current;
    }
}
