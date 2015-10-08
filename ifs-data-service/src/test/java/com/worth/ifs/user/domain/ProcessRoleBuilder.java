package com.worth.ifs.user.domain;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.Builder;

import java.util.function.Consumer;

/**
 * Created by dwatson on 08/10/15.
 */
public class ProcessRoleBuilder implements Builder<ProcessRole> {

    private final ProcessRole current;

    // for factory method and with() use
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

    public ProcessRoleBuilder withId(Long id) {
        return with(processRole -> processRole.setId(id));
    }

    public ProcessRoleBuilder withRole(Builder<Role> role) {
        return with(processRole -> processRole.setRole(role.build()));
    }

    public ProcessRoleBuilder withApplication(Builder<Application> application) {
        return with(processRole -> processRole.setApplication(application.build()));
    }

    @Override
    public ProcessRole build() {
        return current;
    }
}
