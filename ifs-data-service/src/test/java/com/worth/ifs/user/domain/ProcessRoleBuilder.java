package com.worth.ifs.user.domain;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.Builder;

import java.util.List;
import java.util.function.Consumer;

/**
 * Created by dwatson on 08/10/15.
 */
public class ProcessRoleBuilder extends BaseBuilder<ProcessRole> {

    private ProcessRoleBuilder() {
        super();
    }

    private ProcessRoleBuilder(List<Consumer<ProcessRole>> actions) {
        super(actions);
    }

    public static ProcessRoleBuilder newProcessRole() {
        return new ProcessRoleBuilder();
    }

    @Override
    protected ProcessRoleBuilder createNewBuilderWithActions(List<Consumer<ProcessRole>> actions) {
        return new ProcessRoleBuilder(actions);
    }

    @Override
    protected ProcessRole createInitial() {
        return new ProcessRole();
    }

    public ProcessRoleBuilder withId(Long id) {
        return with(processRole -> processRole.setId(id));
    }

    public ProcessRoleBuilder withRole(Builder<Role> role) {
        return with(processRole -> processRole.setRole(role.build()));
    }

    public ProcessRoleBuilder withApplication(Builder<Application> application) {
        return withApplication(application.build());
    }

    public ProcessRoleBuilder withApplication(Application application) {
        return with(processRole -> processRole.setApplication(application));
    }
}
